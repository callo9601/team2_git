package center;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;




public class CenterDAO {

	Connection con = null;
	PreparedStatement pstmt = null;
	String sql = null;
	ResultSet rs=null;
	private Connection getConnection() throws Exception{
		//context.xml 파일 사용하기 위해서 객체생성
		Context init = new InitialContext();
		//context.xml 자원의 이름을 불려오기(찾기) (자원의 저장위치/이름)
		DataSource ds = (DataSource)init.lookup("java:comp/env/jdbc/MysqlDB");
		//불러온 자원 javax.sql.DataSource => Connection 으로 변경
		con = ds.getConnection();
		return con;
	}
	//객체기억장소 해제 메서드()
		public void obClose() {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			if(con!=null) {
				try {
					con.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		public void insertCenter(CenterDTO centerDTO) {
			try {
				int num = 0;
				con = getConnection();
				sql= "select max(b_num) from center";
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if(rs.next()){
				num = rs.getInt("max(b_num)") + 1;
				}
				sql = "insert into center(b_num,t_num,u_num,admin_id,b_title,b_content,b_date,b_reply,secret) value(?,?,?,?,?,?,?,?,?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, num); pstmt.setInt(2, centerDTO.getT_num()); 
				pstmt.setInt(3, centerDTO.getU_num());
				pstmt.setString(4, centerDTO.getAdmin_id()); pstmt.setString(5, centerDTO.getB_title());
				pstmt.setString(6, centerDTO.getB_content()); 
				pstmt.setTimestamp(7, centerDTO.getDate()); pstmt.setInt(8, 0); pstmt.setInt(9, centerDTO.getSecret());
				pstmt.executeUpdate();
			} catch (Exception e) {
				System.out.println("예외발생");
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				System.out.println("마무리작업");
				obClose();
			}
		}//insertCenter()
		public CenterDTO getCenter(int b_num) {
			CenterDTO centerDTO = new CenterDTO();
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				sql= "select * from center where b_num=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, b_num);
				//4단계 실행 -> 결과 저장
				rs = pstmt.executeQuery();
				//5단계 결과 -> 글 하나 CenterDTO에 저장 => CenterList한칸에 글하나 작성
				if(rs.next()){
					//디비에서 열 => 멤버변수에 저장
					centerDTO.setB_num(rs.getInt("b_num"));
					centerDTO.setT_num(rs.getInt("t_num"));
					centerDTO.setU_num(rs.getInt("u_num"));
					centerDTO.setDate(rs.getTimestamp("b_date"));
					centerDTO.setB_title(rs.getString("b_title"));
					centerDTO.setB_content(rs.getString("b_content"));
//					centerDTO.setSecret(rs.getInt("secret"));
				}
			} catch (Exception e) {
				e.getMessage();
			}finally {
				obClose();
			}
			return centerDTO;
		}//getCenter()
		public List<CenterDTO> getCenterList(int startRow,int pageSize) {
			//게시판 글을 여러개 저장할 수 있는 배열
			//CenterDTO형만 저장할 수 있게 객체생성 => 제네릭타입
			List<CenterDTO> CenterList = new ArrayList<CenterDTO>();
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				//getCenterList(startRow,pageSize) 메서드 정의
				//select * from Center order by num desc limit 시작행-1, 가져올개수
				sql= "select * from center where b_reply = 0 order by b_num desc limit ?,?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, startRow-1);
				pstmt.setInt(2, pageSize);
				//4단계 실행 -> 결과 저장
				rs = pstmt.executeQuery();
				//5단계 결과 -> 글 하나 CenterDTO에 저장 => CenterList한칸에 글하나 작성
				while(rs.next()){
					CenterDTO centerDTO = new CenterDTO();
					//디비에서 열 => 멤버변수에 저장
					centerDTO.setB_num(rs.getInt("b_num"));
					centerDTO.setU_num(rs.getInt("u_num"));
					centerDTO.setDate(rs.getTimestamp("b_date"));
					centerDTO.setB_title(rs.getString("b_title"));
					centerDTO.setSecret(rs.getInt("secret"));
					//글 하나를 배열 한칸에 저장
					CenterList.add(centerDTO);
				}
			} catch (Exception e) {
				e.getMessage();
			}finally {
				obClose();
			}
			return CenterList;
		}
		public int getCenterCount() {
			int count=0;
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				//getBoardList(startRow,pageSize) 메서드 정의
				//select * from board order by num desc limit 시작행-1, 가져올개수
				sql= "select count(*) from center where b_reply = 0";
				pstmt = con.prepareStatement(sql);
				//4단계 실행 -> 결과 저장
				rs = pstmt.executeQuery();
				//5단계 결과 
				if(rs.next()) {
					count=rs.getInt("count(*)");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally {
				obClose();
			}
			return count;
		}
		public void insertReply(CenterDTO centerDTO) {
			try {
				int num = 0;
				con = getConnection();
				sql= "select max(b_reply) from center where b_num = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, centerDTO.getB_num());
				rs = pstmt.executeQuery();
				if(rs.next()){
				num = rs.getInt("max(b_reply)") + 1;
				}
				sql = "insert into center(b_num,b_title,b_content,b_date,b_reply) value(?,?,?,?,?)";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, centerDTO.getB_num()); pstmt.setString(2, centerDTO.getB_title());
				pstmt.setString(3, centerDTO.getB_content()); 
				pstmt.setTimestamp(4, centerDTO.getDate()); pstmt.setInt(5, num);
				pstmt.executeUpdate();
			} catch (Exception e) {
				System.out.println("예외발생");
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				System.out.println("마무리작업");
				obClose();
			}
		}//insertCenter()
		public List<CenterDTO> getReplyList(int b_num) {
			//게시판 글을 여러개 저장할 수 있는 배열
			//CenterDTO형만 저장할 수 있게 객체생성 => 제네릭타입
			List<CenterDTO> ReplyList = new ArrayList<CenterDTO>();
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				sql= "select * from center where b_num = ? and b_reply>0 order by b_reply";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, b_num);
				//4단계 실행 -> 결과 저장
				rs = pstmt.executeQuery();
				//5단계 결과 -> 글 하나 CenterDTO에 저장 => CenterList한칸에 글하나 작성
				while(rs.next()){
					CenterDTO centerDTO = new CenterDTO();
					//디비에서 열 => 멤버변수에 저장
					centerDTO.setDate(rs.getTimestamp("b_date"));
					centerDTO.setB_title(rs.getString("b_title"));
					centerDTO.setB_content(rs.getString("b_content"));
					centerDTO.setB_reply(rs.getInt("b_reply"));
					//글 하나를 배열 한칸에 저장
					ReplyList.add(centerDTO);
				}
			} catch (Exception e) {
				e.getMessage();
			}finally {
				obClose();
			}
			return ReplyList;
		}
		public int isReply(int b_num) {
			//답변여부 확인
			int num = 0;
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				sql= "select max(b_reply) from center where b_num = ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, b_num);
				rs = pstmt.executeQuery();
				if(rs.next()){
				if(rs.getInt("max(b_reply)")>0)
				{num=1;}
				}
			} catch (Exception e) {
				e.getMessage();
			}finally {
				obClose();
			}
			return num;
		}
		public void deleteCenter(int b_num) {
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				sql= "delete from center where b_num=?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, b_num);
				pstmt.executeUpdate();
			} catch (Exception e) {
				System.out.println("예외발생");
				e.getMessage();
			}finally {
				obClose();
			}
		}//deleteCenter
		public void deleteReply(int b_num,int b_reply) {
			try {
				//1,2단계 디비연결 메서드 호출
				con = getConnection();
				//3단계 sql구문
				sql= "delete from center where b_num=? and b_reply= ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setInt(1, b_num); pstmt.setInt(2, b_reply);
				pstmt.executeUpdate();
			} catch (Exception e) {
				System.out.println("예외발생");
				e.getMessage();
			}finally {
				obClose();
			}
		}//deleteReply
}

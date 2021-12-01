package board;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
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
	public void insertBoard(BoardDTO boardDTO) {
		try {
			int num = 0;
			con = getConnection();
			sql= "select max(num) from board";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()){
			num = rs.getInt("max(num)") + 1;
			}
			sql = "insert into board(num,name,pass,subject,content,readcount,date,file) value(?,?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num); pstmt.setString(2, boardDTO.getName()); 
			pstmt.setString(3, boardDTO.getPass());
			pstmt.setString(4, boardDTO.getSubject()); pstmt.setString(5, boardDTO.getContent());
			pstmt.setInt(6, boardDTO.getReadcount()); pstmt.setTimestamp(7, boardDTO.getDate());
			pstmt.setString(8, boardDTO.getFile());
			pstmt.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			obClose();
		}
	}//insertBoard()
	public List<BoardDTO> getBoardList(int startRow,int pageSize) {
		//게시판 글을 여러개 저장할 수 있는 배열
		//BoardDTO형만 저장할 수 있게 객체생성 => 제네릭타입
		List<BoardDTO> boardList = new ArrayList<BoardDTO>();
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			//getBoardList(startRow,pageSize) 메서드 정의
			//select * from board order by num desc limit 시작행-1, 가져올개수
			sql= "select * from board order by num desc limit ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startRow-1);
			pstmt.setInt(2, pageSize);
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 -> 글 하나 BoardDTO에 저장 => boardList한칸에 글하나 작성
			while(rs.next()){
				BoardDTO boardDTO = new BoardDTO();
				//디비에서 열 => 멤버변수에 저장
				boardDTO.setNum(rs.getInt("num"));
				boardDTO.setName(rs.getString("name"));
				boardDTO.setPass(rs.getString("pass"));
				boardDTO.setSubject(rs.getString("subject"));
				boardDTO.setContent(rs.getString("content"));
				boardDTO.setDate(rs.getTimestamp("date"));
				boardDTO.setReadcount(rs.getInt("readcount"));
				//글 하나를 배열 한칸에 저장
				boardList.add(boardDTO);
			}
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
		return boardList;
	}//getBoardList()
	public List<BoardDTO> getBoardList(int startRow,int pageSize,String search) {
		//게시판 글을 여러개 저장할 수 있는 배열
		//BoardDTO형만 저장할 수 있게 객체생성 => 제네릭타입
		List<BoardDTO> boardList = new ArrayList<BoardDTO>();
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			//getBoardList(startRow,pageSize) 메서드 정의
			//select * from board order by num desc limit 시작행-1, 가져올개수
			sql= "select * from board where subject like ? order by num desc limit ?,?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+search+"%");
			pstmt.setInt(2, startRow-1);
			pstmt.setInt(3, pageSize);
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 -> 글 하나 BoardDTO에 저장 => boardList한칸에 글하나 작성
			while(rs.next()){
				BoardDTO boardDTO = new BoardDTO();
				//디비에서 열 => 멤버변수에 저장
				boardDTO.setNum(rs.getInt("num"));
				boardDTO.setName(rs.getString("name"));
				boardDTO.setPass(rs.getString("pass"));
				boardDTO.setSubject(rs.getString("subject"));
				boardDTO.setContent(rs.getString("content"));
				boardDTO.setDate(rs.getTimestamp("date"));
				boardDTO.setReadcount(rs.getInt("readcount"));
				//글 하나를 배열 한칸에 저장
				boardList.add(boardDTO);
			}
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
		return boardList;
	}//getBoardList()
	public int getBoardCount() {
		int count=0;
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			//getBoardList(startRow,pageSize) 메서드 정의
			//select * from board order by num desc limit 시작행-1, 가져올개수
			sql= "select count(*) from board";
			pstmt = con.prepareStatement(sql);
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 
			if(rs.next()) {
				count=rs.getInt("count(*)");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return count;
	}
	public int getBoardCount(String search) {
		int count=0;
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			//getBoardList(startRow,pageSize) 메서드 정의
			//select * from board order by num desc limit 시작행-1, 가져올개수
			sql= "select count(*) from board where subject like ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, "%"+search+"%");
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 
			if(rs.next()) {
				count=rs.getInt("count(*)");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return count;
	}
	public BoardDTO getBoard(int num) {
		BoardDTO boardDTO = new BoardDTO();
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "select * from board where num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 -> 글 하나 BoardDTO에 저장 => boardList한칸에 글하나 작성
			if(rs.next()){
				//디비에서 열 => 멤버변수에 저장
				boardDTO.setNum(rs.getInt("num"));
				boardDTO.setName(rs.getString("name"));
				boardDTO.setPass(rs.getString("pass"));
				boardDTO.setSubject(rs.getString("subject"));
				boardDTO.setContent(rs.getString("content"));
				boardDTO.setDate(rs.getTimestamp("date"));
				boardDTO.setReadcount(rs.getInt("readcount"));
				boardDTO.setFile(rs.getString("file"));
			}
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
		return boardDTO;
	}//getBoard
	public BoardDTO numCheck(int num,String pass) {
		BoardDTO boardDTO = null;
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "select * from board where num=? and pass=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, pass);
			//4단계 실행 -> 결과 저장
			rs = pstmt.executeQuery();
			//5단계 결과 -> 글 하나 BoardDTO에 저장 => boardList한칸에 글하나 작성
			if(rs.next()){
				boardDTO = new BoardDTO();
				//디비에서 열 => 멤버변수에 저장
				boardDTO.setNum(rs.getInt("num"));
			}
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
		return boardDTO;
	}//numCheck
	public void updateBoard(BoardDTO boardDTO) {
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "update board set name=? ,subject=?,content=? where num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, boardDTO.getName());
			pstmt.setString(2, boardDTO.getSubject()); pstmt.setString(3, boardDTO.getContent()); 
			pstmt.setInt(4, boardDTO.getNum());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
	}//updateBoard
	public void fupdateBoard(BoardDTO boardDTO) {
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "update board set name=? ,subject=?,content=?,file=? where num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, boardDTO.getName());
			pstmt.setString(2, boardDTO.getSubject()); pstmt.setString(3, boardDTO.getContent()); 
			pstmt.setString(4, boardDTO.getFile());
			pstmt.setInt(5, boardDTO.getNum());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
	}//updateBoard
	public void deleteBoard(int num) {
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "delete from board where num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
	}//deleteBoard
	public void updateReadcount(int num) {
		try {
			//1,2단계 디비연결 메서드 호출
			con = getConnection();
			//3단계 sql구문
			sql= "update board set readcount = readcount+1 where num=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.getMessage();
		}finally {
			obClose();
		}
	}
}

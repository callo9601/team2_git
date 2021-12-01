package trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class MemberDAO {
	
	// Member관련 디비작업
	// 멤버변수
	String sql=null;
	Connection con=null;
	PreparedStatement pstmt =null;
	ResultSet rs=null;
		
	// 생성자
	// 멤버함수(메서드)
		
	//데이터베이스 연결(메서드)
	private Connection getConnection() throws Exception{		
		// 메서드를 호출하는 곳에서 예외처리하는 명령
		
		//1단계 디비연결하는 드라이버 불러오기
//		Class.forName("com.mysql.jdbc.Driver");
//		// 2단계 드라이버를 이용해서 디비에 연결
//		String dbUrl="jdbc:mysql://localhost:3306/team2";
//		String dbUser="root";
//		String dbPass="1234";
//		con=DriverManager.getConnection(dbUrl, dbUser, dbPass);
		
//		커넥션풀(Connection Pool)
		// DBCP(DataBase Connection Pool) API 사용 : 프로그램 설치 없이 사용
		// 서버에서 미리 디비연결을 하고 필요할때마다 자원의 이름을 불러서 사용
//		1. 수정최소화
//		2. 성능향상(속도빨라짐)
		
		// context.xml 파일 사용하기 위해서 객체생성
		Context init=new InitialContext();
		// context.xml 자원의 이름을 불러오기
		DataSource ds=(DataSource)init.lookup("java:comp/env/jdbc/MysqlDB");
		// 불러온 자원 javax.sql.DataSource => Connection으로 변경
		con=ds.getConnection();
		return con;
	}
		
	// 객체기억장소 해제 메서드()
	public void obclose() {
		if(rs!=null) {
			try {
				rs.close();
			} catch (Exception e2) {
				
			}
		}
		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (Exception e2) {
				
			}
		}
		if(con!=null) {
			try {
				con.close();
			} catch (Exception e2) {
				
			}
		}
	}

	// insertMember(바구니주소 저장할 변수) 메서드 정의
	public void insertMember(MemberDTO memberDTO) {
		try {
			
			System.out.println("확인");
			//1,2 디비연결
			con = getConnection();
			// 3단계 sql구문을 만들어서 실행할 준비 insert
			sql="insert into trainer(t_id,t_pass,t_name,t_nic,t_postcode,t_address,t_detailAddress,t_phone,t_email) values(?,?,?,?,?,?,?,?,?)";			
			pstmt =con.prepareStatement(sql);
			pstmt.setString(1, memberDTO.getT_id());
			pstmt.setString(2, memberDTO.getT_pass());
			pstmt.setString(3, memberDTO.getT_name());
			pstmt.setString(4, memberDTO.getT_nic());
			pstmt.setString(5, memberDTO.getT_postcode());
			pstmt.setString(6, memberDTO.getT_address());
			pstmt.setString(7, memberDTO.getT_detailAddress());
			pstmt.setString(8, memberDTO.getT_phone());
			pstmt.setString(9, memberDTO.getT_email());
			
			// 4단계 sql구문 실행 .executeUpdate() - insert, update, delete
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}
	}//insertMember()
	
	//userCheck(id,pass)
	// 리턴할형 MemberDTO userCheck(id,pass)메서드 정의
	public MemberDTO userCheck(String t_id,String t_pass) {
		MemberDTO memberDTO=null;
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 sql구문 만들고 실행할 준비 select where t_id=? and t_pass=?
			sql="select * from trainer where t_id=? and t_pass=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, t_id);
			pstmt.setString(2, t_pass);
			// 4단계 sql구문 실행 => 결과저장 내장객체 ResultSet
			rs=pstmt.executeQuery();
			// 5단계 
			if(rs.next()) {
				memberDTO=new MemberDTO();
				memberDTO.setT_id(rs.getString("t_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}
		return memberDTO;
	}//userCheck(t_id,t_pass)		
	
	//MemberDTO getMember(t_id)
	public MemberDTO getMember(String t_id) {
		MemberDTO memberDTO = null;
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 sql구문 만들고 실행할 준비 select where t_id=?
			sql="select * from trainer where t_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, t_id);
			// 4단계 sql구문 실행 => 결과저장 내장객체 ResultSet
			rs=pstmt.executeQuery();
			// 5단계
			if(rs.next()) {
				memberDTO=new MemberDTO();
				memberDTO.setT_id(rs.getString("t_id"));
				memberDTO.setT_pass(rs.getString("t_pass"));
				memberDTO.setT_name(rs.getString("t_name"));
				memberDTO.setT_nic(rs.getString("t_nic"));
				memberDTO.setT_postcode(rs.getString("t_postcode"));
				memberDTO.setT_address(rs.getString("t_address"));
				memberDTO.setT_detailAddress(rs.getString("t_detailAddress"));
				memberDTO.setT_phone(rs.getString("t_phone"));
				memberDTO.setT_email(rs.getString("t_email"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}finally {
			obclose();
		}
		return memberDTO;
	}// getMember()
	
	public void updateMember(MemberDTO memberDTO) {
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 update 수정 name 조건 id
			sql="update trainer set t_name=?, t_pass=?, t_nic=?, t_postcode=?, t_address=?, t_detailAddress=?, t_phone=?, t_email=? where t_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, memberDTO.getT_name());
			pstmt.setString(2, memberDTO.getT_pass());
			pstmt.setString(3, memberDTO.getT_nic());
			pstmt.setString(4, memberDTO.getT_postcode());
			pstmt.setString(5, memberDTO.getT_address());
			pstmt.setString(6, memberDTO.getT_detailAddress());
			pstmt.setString(7, memberDTO.getT_phone());
			pstmt.setString(8, memberDTO.getT_email());
			pstmt.setString(9, memberDTO.getT_id());
			// 4단계 실행
			pstmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}	
	}//updateMember()
	
	
	//userSearch(id)
	// 리턴할형 MemberDTO userSearch(id)메서드 정의
	public MemberDTO userSearch(String t_id) {
		MemberDTO memberDTO=null;
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 sql구문 만들고 실행할 준비 select where t_id=?
			sql="select * from trainer where t_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, t_id);
			// 4단계 sql구문 실행 => 결과저장 내장객체 ResultSet
			rs=pstmt.executeQuery();
			// 5단계 
			if(rs.next()) {
				memberDTO=new MemberDTO();
				memberDTO.setT_id(rs.getString("t_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}
		return memberDTO;
	}//userSearch(t_id)
	
	//트레이너 추가정보 넣기
	public void updatePlusMember(MemberDTO memberDTO) {
		try {
			
			con=getConnection();
			// 3단계 update 수정 name 조건 id
			sql="update trainer set t_intro=?, t_postcode=?, t_address=?, t_detailAddress=?, "
					+ "profile_photo=?, t_exp=? ,t_sns=?, t_program=?, video=? where t_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, memberDTO.getT_intro());
			pstmt.setString(2, memberDTO.getT_postcode());
			pstmt.setString(3, memberDTO.getT_address());
			pstmt.setString(4, memberDTO.getT_detailAddress());
			pstmt.setString(5, memberDTO.getProfile_photo());
			pstmt.setString(6, memberDTO.getT_exp());
			pstmt.setString(7, memberDTO.getT_sns());
			pstmt.setString(8, memberDTO.getT_program());
			pstmt.setString(9, memberDTO.getVideo());
			pstmt.setString(10, memberDTO.getT_id());
			// 4단계 실행
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}
	}//insertMember()
}
	

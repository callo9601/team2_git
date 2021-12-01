package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class user_MemberDAO {
	
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
	public void insertMember(user_MemberDTO user_memberDTO) {
		try {
			
			System.out.println("확인");
			//1,2 디비연결
			con = getConnection();
			// 3단계 sql구문을 만들어서 실행할 준비 insert
			sql="insert into user(u_id,u_pass,u_nic,u_name,u_email,u_phone) values(?,?,?,?,?,?)";			
			pstmt =con.prepareStatement(sql);
			pstmt.setString(1, user_memberDTO.getU_id());
			pstmt.setString(2, user_memberDTO.getU_pass());
			pstmt.setString(3, user_memberDTO.getU_nic());
			pstmt.setString(4, user_memberDTO.getU_name());
			pstmt.setString(5, user_memberDTO.getU_email());
			pstmt.setString(6, user_memberDTO.getU_phone());
			
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
	public user_MemberDTO userCheck(String u_id,String u_pass) {
		user_MemberDTO user_memberDTO=null;
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 sql구문 만들고 실행할 준비 select where u_id=? and u_pass=?
			sql="select * from user where u_id=? and u_pass=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, u_id);
			pstmt.setString(2, u_pass);
			// 4단계 sql구문 실행 => 결과저장 내장객체 ResultSet
			rs=pstmt.executeQuery();
			// 5단계 
			if(rs.next()) {
				user_memberDTO=new user_MemberDTO();
				user_memberDTO.setU_id(rs.getString("u_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}
		return user_memberDTO;
	}//userCheck(u_id,u_pass)		
	
	//MemberDTO getMember(u_id)
	public user_MemberDTO getMember(String u_id) {
		user_MemberDTO user_memberDTO = null;
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 sql구문 만들고 실행할 준비 select where u_id=?
			sql="select * from user where u_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, u_id);
			// 4단계 sql구문 실행 => 결과저장 내장객체 ResultSet
			rs=pstmt.executeQuery();
			// 5단계
			if(rs.next()) {
				user_memberDTO=new user_MemberDTO();
				user_memberDTO.setU_id(rs.getString("u_id"));
				user_memberDTO.setU_pass(rs.getString("u_pass"));
				user_memberDTO.setU_nic(rs.getString("u_nic"));
				user_memberDTO.setU_name(rs.getString("u_name"));
				user_memberDTO.setU_email(rs.getString("u_email"));
				user_memberDTO.setU_phone(rs.getString("u_phone"));
				
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}finally {
			obclose();
		}
		return user_memberDTO;
	}// getMember()
	
	public void updateMember(user_MemberDTO usermemberDTO) {
		try {
			//1,2 디비연결
			con=getConnection();
			// 3단계 update 수정 name 조건 id
			sql="update user set u_pass=?, u_nic=?,u_name=?, u_email=?, u_phone=? where u_id=?";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(2, usermemberDTO.getU_pass());
			pstmt.setString(1, usermemberDTO.getU_nic());
			pstmt.setString(3, usermemberDTO.getU_name());
			pstmt.setString(4, usermemberDTO.getU_email());
			pstmt.setString(5, usermemberDTO.getU_phone());

			// 4단계 실행
			pstmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			obclose();
		}	
	}//updateMember()
}
	

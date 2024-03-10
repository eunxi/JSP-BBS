package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	// 생성자를 만들어 하나의 객체로 만들었을 때, 자동으로 DB Connection 실행될 수 있도록 작성.
	public UserDAO() {
		try {
			String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
			String dbID = "sample";
			String dbPassword = "sample";
			
			// DB 에 접근할 수 있도록 해주는 매개체와 같은 라이브러리
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection(dbUrl, dbID, dbPassword);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int login(String userID, String userPassword) {
		// PreparedStatement 는 SQL Injection 과 같은 해킹에 방어하기 위해 사용.
		// 하나의 문장을 미리 준비 > ? 키워드를 통해 해당되는 내용을 아래에 구문에서 set > 여기서는 userId. (매개변수이자 데이터베이스 set 해주는 변수)
		String SQL = "SELECT userPassword FROM B_USER WHERE userID = ?";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(rs.getString(1).equals(userPassword))
					return 1; // 로그인 성공
				else
					return 0; // 비밀번호 불일치
			}
			return -1; // 아이디 없음
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -2; // 데이터베이스 오류
	}
	
	public int join(User user) {
		String SQL = "INSERT INTO B_USER VALUES (?, ?, ?, ?, ?)";
		
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, user.getUserID());
			pstmt.setString(2, user.getUserPassword());
			pstmt.setString(3, user.getUserName());
			pstmt.setString(4, user.getUserGender());
			pstmt.setString(5, user.getUserEmail());
			
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; // 데이터베이스 오류
	}

}

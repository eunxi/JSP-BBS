package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// 데이터 접근 객체의 약자 = DAO
public class BbsDAO {

	private Connection conn;
	private ResultSet rs;
	
	public BbsDAO() {
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
	
	// 게시판 글 작성할 때 서버 시간을 넣어주는 함수
	public String getDate() {
		// bbsDate 데이터타입이  DATE 라서 String 으로 받아주면 "ORA-01861: 리터럴이 형식 문자열과 일치하지 않음" 해당 오류 출력.
		// 이는 SELECT 했올 때 TO_CHAR(SYSDATE, 'YYYY-MM-DD') 로 변경해서 조회해주면 데이터타입 형식 지킬 수 있음.
		String SQL = "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD') FROM DUAL";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ""; // 데이터베이스 오류
	}
	
	// 게시물 번호 함수
	public int getNext() {
		String SQL = "SELECT bbsID FROM B_BBS ORDER BY bbsID DESC";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getInt(1) + 1;
			}
			
			return 1; // 첫번째 게시물인 경우
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; // 데이터베이스 오류
	}
	
	// 글 작성 함수
	public int write(String bbsTitle, String userID, String bbsContent) {
		String SQL = "INSERT INTO B_BBS VALUES (?, ?, ?, ?, ?, ?)"; 
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			
			pstmt.setInt(1, getNext());
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);
			
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; // 데이터베이스 오류	
	}
	
	// 게시판 목록
	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM (SELECT * FROM B_BBS WHERE bbsID <= ? AND bbsAvailable = 1 ORDER BY bbsID DESC ) WHERE ROWNUM <= 10"; 
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				list.add(bbs);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 페이징 처리를 위해서 존재하는 함수
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM B_BBS WHERE bbsID < ? AND bbsAvailable = 1"; 
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return true;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// 글 상세보기
	public Bbs getBbs(int bbsID) {
		String SQL = "SELECT * FROM B_BBS WHERE bbsID = ?"; 
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				
				return bbs;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// 게시글 수정
	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE B_BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?"; 
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			pstmt.setInt(3, bbsID);
			
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; // 데이터베이스 오류			
	}
	
	// 게시글 삭제
	public int delete(int bbsID) {
		String SQL = "UPDATE B_BBS SET bbsAvailable = 0 WHERE bbsID = ?"; 
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			
			pstmt.setInt(1, bbsID);
			
			return pstmt.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return -1; // 데이터베이스 오류	
	}
	
}

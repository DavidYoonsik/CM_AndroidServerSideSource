package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
//import com.google.android.gcm.server.Result;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/gcmregisterServlet")
public class gcmregisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String GOOGLE_API_KEY = "AIzaSyCnK3xHxHr_7WwrPsOvdzcqCHNKKiwdsyA";

	//DB
	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null;
	Statement stmt = null;
	ResultSet rst = null;
	String sql;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		dumpRequest(request, response);
	}

	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("사용자의 등록요청이 왔습니다.");
		dumpRequest(request, response);
	}


	private synchronized void dumpRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Enumeration<String> keys = request.getParameterNames();
		request.setCharacterEncoding("utf-8");

		response.setContentType("text/html; charset=utf-8");
		
		

		// DB 입력 및 저장
		try{
			String user_id = new String(request.getParameter("user_id"));

			String reg_id = new String(request.getParameter("reg_id"));
			
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			conn.setAutoCommit(false);			

			// 이전의 GCM 등록 계정을 삭제하고 새로운 값을 입력합니다.
			sql = " select * from chatinfo where user_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			
			while(rst.next()){
				sql = " delete from chatinfo where user_id = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.executeUpdate();
				conn.commit();
			}
			
			conn.setAutoCommit(false);			
			
			sql = "insert into chatinfo (user_id, reg_id) " + 
					"values (?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			pstmt.setString(2, reg_id);
			pstmt.executeUpdate();	
			
			conn.commit();
			

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				rst.close();
				pstmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

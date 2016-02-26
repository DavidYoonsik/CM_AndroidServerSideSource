package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registerServlet")
public class registerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");

		//DB
		String url = "jdbc:postgresql://localhost:5432/postgres";
		String className = "org.postgresql.Driver";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		String sql;

		String user_id = new String(request.getParameter("user_id"));
		String user_pw = new String(request.getParameter("user_pw"));

		System.out.println(user_id + ", " + user_pw);

		// DB 입력 및 저장
		try{
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			sql = " select * from userinfo where user_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();

			if (rst.next()) {					
				response.getWriter().print("fail");
				return;
			}else{
				conn.setAutoCommit(false);
				sql = "insert into userinfo (user_id, user_pw) values (?, ?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.setString(2, user_pw);
				pstmt.executeUpdate();		
				conn.commit();
				response.getWriter().print("success");
			}

			conn.setAutoCommit(false);

			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			if(rst.next()){
				return;
			}else{
				sql = " insert into test (user_name) values (?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.executeUpdate();
			}
			
			

			conn.commit();

		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");

		//DB
		String url = "jdbc:postgresql://localhost:5432/postgres";
		String className = "org.postgresql.Driver";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		String sql;

		String user_id = new String(request.getParameter("user_id"));
		String user_pw = new String(request.getParameter("user_pw"));
		String gender = new String(request.getParameter("gender"));
		String interest = request.getParameter("interest");

		System.out.println(user_id + ", " + user_pw + ", " + gender + ", " + interest);

		// DB 입력 및 저장
		try{
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			sql = " select * from userinfo where user_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();

			if (rst.next()) {					
				response.getWriter().print("fail");
				return;
			}else{
				conn.setAutoCommit(false);
				sql = "insert into userinfo (user_id, user_pw, gender, interests) values (?, ?, ?, ?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.setString(2, user_pw);
				pstmt.setString(3, gender);
				pstmt.setString(4, interest);
				pstmt.executeUpdate();
				conn.commit();
				//conn.close();
				response.getWriter().print("success");
			}

			conn.setAutoCommit(false);

			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			if(rst.next()){
				return;
			}else{
				sql = " insert into test (user_name) values (?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.executeUpdate();
			}
			
			/*sql = " alter table following add " + user_id + " varchar(300) default(0) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			sql = " alter table follower add " + user_id + " varchar(300) default(0) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();*/
			
			conn.commit();
			
			/*conn.setAutoCommit(false);
			
			sql = " select * from following where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			if(rst.next()){
				return;
			}else{
				sql = " insert into following (user_id) values (?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.executeUpdate();
			}
			
			sql = " select * from follower where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			if(rst.next()){
				return;
			}else{
				sql = " insert into follower (user_id) values (?) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_id);
				pstmt.executeUpdate();
			}

			conn.commit();*/

		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				rst.close();
				pstmt.close();
				conn.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
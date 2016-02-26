package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/userInspector")
public class userInspector extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		
		System.out.println(user_id + ", " + user_pw);

		// DB 입력 및 저장
		try{
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			sql = " select user_pw from userInfo where user_id = ? " ;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			
			if(rst.next()){
				System.out.println("통과");
				if(rst.getString("user_pw").equals(user_pw)){
					response.getWriter().print("success");
				}else{
					response.getWriter().print("fail");
				}
			}else{
				response.getWriter().print("fail");
			}
			conn.commit();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				rst.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

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

@WebServlet("/productDelete")
public class productDelete extends HttpServlet {
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
		String sql;

		String p_index = new String(request.getParameter("p_index"));
		String p_type = new String(request.getParameter("p_type"));

		System.out.println(p_index + ", " + p_type);

		// DB 입력 및 저장
		try{
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			sql = " delete from productlist where _index = ? and _type = ? " ;
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(p_index));
			pstmt.setString(2, p_type);
			pstmt.executeUpdate();

			response.getWriter().print("success");			
			conn.commit();

		}catch(Exception e){
			e.printStackTrace();
			response.getWriter().print("fail");
		}finally{
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

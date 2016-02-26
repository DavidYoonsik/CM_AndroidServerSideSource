package mobile.cross.itm.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/followDownload")
public class followDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String val;

	Date now = new Date();


	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;
	String column;
	ArrayList<String> user_name_list;



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.setCharacterEncoding("utf-8");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		downloadData(request, response);
	}

	private synchronized void downloadData(HttpServletRequest request, HttpServletResponse response) throws ServletException, UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");

		//request.getCharacterEncoding();
		String user_id = new String(request.getParameter("user_id"));
		String fol_id = new String(request.getParameter("fol_id"));

		try{
			response.setContentType("text/html; charset=utf-8"); // 한글 인코딩 처리
			/*
			 * DB에 저장하는 역할.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			// DB read the date
			conn.setAutoCommit(false);
			sql = " select * from following where user_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			//pstmt.setString(2, fol_id);
			rst = pstmt.executeQuery();
			
			while(rst.next()){
				column = fol_id.toLowerCase();
				String fol = rst.getString(column);
				if(fol.toString().trim().equalsIgnoreCase("0")){
					sql = " update following set " + column + " = ? where user_id = ? ";
					pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, "1");
					pstmt.setString(2, user_id);
					pstmt.executeUpdate();
				}else{
					// nothing
				}
			}
			
			

			sql = " select * from follower where user_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fol_id);
			//pstmt.setString(2, fol_id);
			rst = pstmt.executeQuery();
			
			while(rst.next()){
				column = user_id.toLowerCase();
				String fol = rst.getString(column);
				if(fol.toString().trim().equalsIgnoreCase("0")){
					sql = " update follower set " + column + " = ? where user_id = ? ";
					pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, "1");
					pstmt.setString(2, fol_id);
					pstmt.executeUpdate();
				}else{
					// nothing
				}
			}
			
			
			
			conn.commit();
			
			//System.out.println(ownerRec.toString());
			//System.out.println(otherRec.toString());

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print("success");

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				rst.close();
				pstmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

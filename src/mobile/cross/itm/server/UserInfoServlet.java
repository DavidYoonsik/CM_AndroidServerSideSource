package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UserInfoServlet")
@MultipartConfig
public class UserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rst = null;
	String sql;
	String user_id = "";
	
	String gender, interest, push, user_pic, user_say;
	int index;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{	 
			user_id = request.getParameter("user_id");
			/*
			 * DB 에서 내용을 읽어와 list 형태의 데이터에 저장합니다. json 형식의 데이터로 가공 후 안드로이드 클라이언트 쪽으로 보내줍니다.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			sql = " select * from userinfo where user_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			
			List<String> list = new ArrayList<String>();

			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				gender = rst.getString("gender");
				interest = rst.getString("interests");
				push = rst.getString("push");
				user_pic = rst.getString("user_pic");
				user_say = rst.getString("user_say");

				innerList.add(gender);
				innerList.add(interest);
				innerList.add(push);
				innerList.add(user_pic);
				innerList.add(user_say);
				list.addAll(innerList);
			}
			conn.commit();
			

			String json = new Gson().toJson(list);

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);

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
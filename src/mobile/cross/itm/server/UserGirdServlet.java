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
@WebServlet("/UserGridServlet")
@MultipartConfig
public class UserGirdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;
	String user_id = "";
	
	String path, name, message, email, pdate, price, type, status, method, user_pic;
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
			
			List<String> list = new ArrayList<String>();
			
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			
			conn.setAutoCommit(false);
			
			sql = " select * from productlist where email = ? order by _index desc ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, user_id);
			
			rst = pstmt.executeQuery();
			
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				name = rst.getString("name");
				message = rst.getString("message");
				path = rst.getString("path");
				email = rst.getString("email");
				pdate = rst.getString("pdate");
				price = rst.getString("price");
				index = rst.getInt("_index");
				type = rst.getString("_type");
				status = rst.getString("status");
				method = rst.getString("method");
				
				sql = " select user_pic from userinfo where user_id = ? ";
				pstmt2 = conn.prepareStatement(sql);
				pstmt2.setString(1, email);
				rst2 = pstmt2.executeQuery();
				rst2.next();
				user_pic = rst2.getString("user_pic");
				
				innerList.add(name);
				innerList.add(message);
				innerList.add(path);
				innerList.add(email);
				innerList.add(pdate);
				innerList.add(price);
				innerList.add(index+"");
				innerList.add(type);
				innerList.add(status);
				innerList.add(method);
				innerList.add(user_pic);
				list.addAll(innerList);
			}
			
			list.add(";");
			
			sql = " select * from following where user_id = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, user_id);
			
			rst = pstmt.executeQuery();
			
			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				
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
				rst2.close();
				pstmt2.close();
				rst.close();
				pstmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}

}
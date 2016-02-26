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
@WebServlet("/CategoryGridServlet")
@MultipartConfig
public class CategoryGirdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;
	String pro_category = "";
	
	String path, name, message, email, pdate, price, type, category, status, method, user_pic;
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
			pro_category = request.getParameter("pro_category");
			/*
			 * DB 에서 내용을 읽어와 list 형태의 데이터에 저장합니다. json 형식의 데이터로 가공 후 안드로이드 클라이언트 쪽으로 보내줍니다.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			if(pro_category.equals("All")){
				sql = " select * from productlist where _type = ? order by _index desc ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "ps");
			}else{
				sql = " select * from productlist where category = ? and _type = ? order by _index desc ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, pro_category);
				pstmt.setString(2, "ps");
			}
			
			rst = pstmt.executeQuery();
			
			List<String> list = new ArrayList<String>();

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
				category = rst.getString("category");
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
				innerList.add(category);
				innerList.add(status);
				innerList.add(method);
				innerList.add(user_pic);
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
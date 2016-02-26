package mobile.cross.itm.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/recDownload")
public class recDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String val;

	Date now = new Date();


	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;

	String vid;
	String vresponse;
	String vtime;
	String vpic;
	String column;
	
	String path, name = "", message = "", email = "", pdate = "", price = "", type = "", category = "", status = "", method = "", user_pic = "";
	
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
		String pindex = new String(request.getParameter("pindex"));
		String user_id = new String(request.getParameter("pid"));
		System.out.println("dfsdf");
		try{
			response.setContentType("text/html; charset=utf-8"); // 한글 인코딩 처리
			/*
			 * DB에 저장하는 역할.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			// DB read the date
			conn.setAutoCommit(false);
			
			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			
			List<String> list = new ArrayList<String>();
			
			while(rst.next()){
				column = "pi_"+pindex;
				String index = rst.getString(column);
				System.out.println(index + ", index : " + pindex + ", user_id : " + user_id);
				if(index.toString().trim().equals("0")){
					sql = " update test set " + column + " = ? where user_name = ? ";
					pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, "1");
					pstmt.setString(2, user_id);
					pstmt.executeUpdate();
				}else{
					
				}
			}
			conn.commit();
			
			sql = " select user_name from test where " + column + " = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "1");
			rst = pstmt.executeQuery();
			
			user_name_list = new ArrayList<String>();
			
			user_name_list.add(user_id);
			
			while(rst.next()){
				if(rst.getString(1).toString().equals(user_id)){
					
				}else{
					user_name_list.add(rst.getString(1));
				}
				
			}
			
			System.out.println(user_name_list.toString());
			
			ArrayList<String> list2 = new ArrayList<String>();
			for(int i = 0; i < user_name_list.size(); i++){
				
				sql = " select * from test where user_name = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_name_list.get(i).toString());
				rst = pstmt.executeQuery();
				while(rst.next()){
					if(i == 0){
						for(int j = 2; j <= 93; j++){
							list2.add(rst.getString(j));
						}
					}else{
						for(int j = 0; j < list2.size(); j++){
							if(list2.get(j).toString().equals("0") && rst.getString(j+2).toString().equals("1")){
								System.out.println("pi_"+ (j + 72));
								sql = " select * from productlist where _index = ? ";
								pstmt2 = conn.prepareStatement(sql);
								pstmt2.setInt(1, j+72);
								rst2 = pstmt.executeQuery();
								
								int index = 0;
								
								while(rst2.next()){
									List<String> list3 = new ArrayList<String>();
									
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
									user_pic = rst.getString("user_pic");
									
									list3.add(name);
									list3.add(message);
									list3.add(path);
									list3.add(email);
									list3.add(pdate);
									list3.add(price);
									list3.add(index+"");
									list3.add(type);
									list3.add(category);
									list3.add(status);
									list3.add(method);
									list3.add(user_pic);
									list.addAll(list3);
								}
								
							}
						}
					}
				}
			}
			
			System.out.println(list.toString());

			String json = new Gson().toJson(list); // ArrayList data change to the array format

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			response.getWriter().write(json); // respond to the android device

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				rst2.close();
				pstmt2.close();
				rst.close();
				pstmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

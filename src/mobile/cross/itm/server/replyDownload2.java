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

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/replyDownload2")
public class replyDownload2 extends HttpServlet {
	private static final long serialVersionUID = 1L;


	static final String GOOGLE_API_KEY = "AIzaSyCnK3xHxHr_7WwrPsOvdzcqCHNKKiwdsyA";

	Date now = new Date();


	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rst = null;
	String sql;

	String vid;
	String vresponse;
	String vtime;
	String vpic;

	String name, message, path, email, pdate, price, status, method, user_pic, reg_id;

	Message.Builder messageBuilder;
	Message messagE;

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
		String ptype = new String(request.getParameter("ptype"));
		String vid = new String(request.getParameter("vid"));
		String vresponse = new String(request.getParameter("vresponse"));
		String vtime = new String(request.getParameter("vtime"));
		String vpic = new String(request.getParameter("vpic"));

		try{
			response.setContentType("text/html; charset=utf-8"); // 한글 인코딩 처리

			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			// DB insert the data
			conn.setAutoCommit(false);

			sql = "insert into productreply(pindex, ptype, vid, vresponse, vtime, vpic) " + 
					"values (?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pindex);
			pstmt.setString(2, ptype);
			pstmt.setString(3, vid);
			pstmt.setString(4, vresponse);
			pstmt.setString(5, vtime);
			pstmt.setString(6, vpic);
			pstmt.executeUpdate();		
			conn.commit();

			// GCM push message sending
			conn.setAutoCommit(false);

			sql = " select * from productlist join chatinfo on productlist.email = chatinfo.user_id join userinfo on productlist.email = userinfo.user_id where productlist._index = ? and productlist._type = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(pindex));
			pstmt.setString(2, ptype);
			rst = pstmt.executeQuery();
			if(rst.next()){
				name = rst.getString("name");
				message = rst.getString("message");
				path = rst.getString("path");
				email = rst.getString("email");
				pdate = rst.getString("pdate");
				price = rst.getString("price");
				status = rst.getString("status");
				method = rst.getString("method");
				user_pic = rst.getString("user_pic");
				reg_id = rst.getString("reg_id").toString();

				if(!email.equals(vid)){
					messageBuilder = new Message.Builder();
					Sender sender = new Sender(GOOGLE_API_KEY);
					
					messageBuilder.addData("type", "reply");
					messageBuilder.addData("msg", vresponse);
					messageBuilder.addData("fro", vid);
					messageBuilder.addData("when", vtime);
					messageBuilder.addData("name", name);
					messageBuilder.addData("message", message);
					messageBuilder.addData("path", path);
					messageBuilder.addData("email", email);
					messageBuilder.addData("pdate", pdate);
					messageBuilder.addData("price", price);
					messageBuilder.addData("pindex", pindex);
					messageBuilder.addData("ptype", ptype);
					messageBuilder.addData("status", status);
					messageBuilder.addData("method", method);
					messageBuilder.addData("user_pic", user_pic);
					
					messagE = messageBuilder.build();
					sender.send(messagE, reg_id, 5);	 // rst.getString("reg_id").toString();
				}
				
				

			}else{
				System.out.println("REG_ID가 등록되어있지 않습니다.");
			}
			conn.commit();

			// DB read the date
			conn.setAutoCommit(false);
			sql = " select * from productreply join userinfo on productreply.vid = userinfo.user_id where productreply.pindex = ? and productreply.ptype = ? order by productreply.vtime desc ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pindex);
			pstmt.setString(2, ptype);
			rst = pstmt.executeQuery();

			List<String> list = new ArrayList<String>();

			while (rst.next()) {
				List<String> innerList = new ArrayList<String>();
				this.vid = rst.getString("vid");
				this.vresponse = rst.getString("vresponse");
				this.vtime = rst.getString("vtime");
				this.vpic = rst.getString("user_pic");
				innerList.add(this.vid);
				innerList.add(this.vresponse);
				innerList.add(this.vtime);
				innerList.add(this.vpic);
				list.addAll(innerList);
			}
			conn.commit();


			String json = new Gson().toJson(list); // ArrayList data change to the array format

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			response.getWriter().write(json); // respond to the android device

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

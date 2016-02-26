package mobile.cross.itm.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/chatServlet")
public class chatServlet extends HttpServlet {
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

	String reg_id;

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

		try{
			response.setContentType("text/html; charset=utf-8"); // 한글 인코딩 처리
			
			String user_id = request.getParameter("user_id");
			String user_pic = request.getParameter("user_pic");
			String other_id = request.getParameter("other_id");
			String msg = request.getParameter("msg");
			String time = request.getParameter("time");
			System.out.println(user_id + ", " + other_id + ", " + msg);
			String pro_name = request.getParameter("pro_name");
			String pro_detail = request.getParameter("pro_detail");
			String pro_image = request.getParameter("pro_image");
			String pro_time = request.getParameter("pro_time");
			String pro_price = request.getParameter("pro_price");
			String pro_index = request.getParameter("pro_index");
			String pro_type = request.getParameter("pro_type");
			String pro_status = request.getParameter("pro_status");
			String pro_method = request.getParameter("pro_method");

			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			sql = " select * from chatInfo where user_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, other_id);
			rst = pstmt.executeQuery();
			if(rst.next()){
				reg_id = rst.getString("reg_id");

				messageBuilder = new Message.Builder();
				Sender sender = new Sender(GOOGLE_API_KEY);

				messageBuilder.addData("type", "chat");
				
				messageBuilder.addData("msg", msg);
				messageBuilder.addData("fro", user_id);
				messageBuilder.addData("when", time);
				messageBuilder.addData("user_pic", user_pic);
				
				messageBuilder.addData("pro_name", pro_name);
				messageBuilder.addData("pro_detail", pro_detail);
				messageBuilder.addData("pro_image", pro_image);
				messageBuilder.addData("pro_time", pro_time);
				messageBuilder.addData("pro_price", pro_price);
				messageBuilder.addData("pro_index", pro_index);
				messageBuilder.addData("pro_type", pro_type);
				messageBuilder.addData("pro_status", pro_status);
				messageBuilder.addData("pro_method", pro_method);

				messagE = messageBuilder.build();
				sender.send(messagE, reg_id, 5);



			}else{
				System.out.println("REG_ID가 등록되어있지 않습니다.");
			}
			conn.commit();
			response.getWriter().write("success"); // respond to the android device

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

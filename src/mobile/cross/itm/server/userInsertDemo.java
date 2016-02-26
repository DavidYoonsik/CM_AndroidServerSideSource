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

/**
 * Servlet implementation class userInsertDemo
 */
@WebServlet("/userInsertDemo")
public class userInsertDemo extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;
	String[] arr = {";0", ";1", ";2", ";3", ";4",
			";0;1", ";0;2", ";0;3", ";0;4", 
			";0;1;2", ";0;1;3", ";0;1;4", ";1;2;3", ";1;2;4", ";2;3;4",
			";0;1;2;3", ";0;1;2;4", ";1;2;3;4", ";0;1;2;3;4",};

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			//conn.setAutoCommit(false);
			//sql = "insert into userinfo (user_id, user_pw) values (?, ?) ";
			//sql = " delete from userinfo where user_id = ? and user_pw = ? ";

			/*sql = " select * from userinfo ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			while(rst.next()){
				String a = rst.getString("user_id").toString().trim();
				//System.out.println(a);
				if(a.equalsIgnoreCase("cross")){

				}else if(a.equalsIgnoreCase("user002")){

				}else{
					sql = " alter table following add " + a + " varchar(300) default(0) ";
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.executeUpdate();
				}

			}*/

			//String a = rst.getString("_index") + "";
			/*for(int i = 178; i < 228; i++){
				sql = " alter table follower add pi_" + i + " varchar(300) default(0) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
			}*/

			sql = " select user_id from userinfo ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			while(rst.next()){
				String a = rst.getString("user_id");
				sql = " insert into follower (user_id) values ('" + a + "')";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
			}

			/*sql = " if not exists  alter table test add p6 varchar(300) ";
			sql = " select * from INFORMATION_SCHEMA.COLUMNS where table_name = ? and column_name = ?  ";			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "test");
			pstmt.setString(2, "p8");			
			rst = pstmt.executeQuery();			
			if(rst.next()){				

			}else{
				sql = " alter table test add p8 varchar(300) default(0) ";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
			}	
			 */
			//conn.commit();


		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				pstmt2.close();
				rst.close();
				pstmt.close();
				conn.close();
			} catch (Exception e) {

			}
		}
	}
}

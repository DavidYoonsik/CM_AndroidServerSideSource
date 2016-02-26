package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/test")
@MultipartConfig
public class test extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;
	int total;

	KeywordExtractor ke = new KeywordExtractor();


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			int index;
			String kkma;
			
			

			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			sql = " select * from productlist ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			while(rst.next()){
				index = rst.getInt("_index");
				//System.out.println(index);
				kkma = rst.getString("name") + " " + rst.getString("message");
				
				//System.out.println(kkma);
				
				sql = " insert into kkma (_index2) values ('" + index + "')";
				pstmt = conn.prepareStatement(sql);
				pstmt.executeUpdate();
				
				//System.out.println(index);
				
				

				KeywordList kl = ke.extractKeyword(kkma, true);
				for( int i = 0; i < kl.size(); i++ ) {
					Keyword kwrd = kl.get(i);
					
					sql = " select column_name from information_schema.columns where table_name = ? ";
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, "kkma");
					rst2 = pstmt2.executeQuery();				
					ArrayList<String> arr = new ArrayList<String>();
					
					while(rst2.next()){
						arr.add(rst2.getString(1));
					}
					System.out.println(arr.toString());
					
					int check = 0;
					
					for(int j = 1; j < arr.size(); j++){						
						if(kwrd.getString().toString().trim().equals(arr.get(j).toString().trim())){	
							System.out.println(kwrd.getString() + ", " + arr.get(j));
							check++;														
							break;
						}
					}

					if(check >0){
						sql = " update kkma set " + kwrd.getString() + " = ? where _index2 = ? " ;
						pstmt = conn.prepareStatement(sql);				
						pstmt.setString(1, kwrd.getCnt()+"");
						pstmt.setString(2, index+"");
						pstmt.executeUpdate();
					}else{
						if(kwrd.getString().startsWith("0") || kwrd.getString().startsWith("1") || kwrd.getString().startsWith("2") || kwrd.getString().startsWith("3")
								|| kwrd.getString().startsWith("4") || kwrd.getString().startsWith("5") || kwrd.getString().startsWith("6") || kwrd.getString().startsWith("7")
								|| kwrd.getString().startsWith("8") || kwrd.getString().startsWith("9")){
						}else{
							sql = " alter table kkma add " + kwrd.getString().toString().trim()+"" + " varchar(300) default(0) ";
							pstmt = conn.prepareStatement(sql);
							pstmt.executeUpdate();
							
							sql = " update kkma set " + kwrd.getString() + " = ? where _index2 = ? " ;
							pstmt = conn.prepareStatement(sql);				
							pstmt.setString(1, kwrd.getCnt()+"");
							pstmt.setString(2, index+"");
							pstmt.executeUpdate();
						}
						
						
						
					}
					//System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
				}

			}

			conn.commit();








		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
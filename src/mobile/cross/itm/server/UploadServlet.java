package mobile.cross.itm.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rst = null;
	String sql;
	int number = 0;
	String column, kkma;
	
	KeywordExtractor ke = new KeywordExtractor();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletContext().getRealPath("/images").toString();
		System.out.println(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.setCharacterEncoding("utf-8");
		upload(request,response);
	}
	
	

	private synchronized void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		
		FileOutputStream fos = null;
		InputStream is = null;
		
		try {
			response.setContentType("text/html; charset=utf-8");
			String pro_name = request.getParameter("param1");

			String pro_detail = request.getParameter("param2");
			
			String pro_price = request.getParameter("param3");
			
			String pro_host = request.getParameter("param4");
			
			String pro_date = request.getParameter("param5");
			
			String pro_type = request.getParameter("param6");
			
			String degree = request.getParameter("param7");
			
			String category = request.getParameter("param8");
			
			String status = request.getParameter("param9");
			
			String method = request.getParameter("param10");
			
			//System.out.println(category + ", " + status + ", " + method);

			Part p = request.getPart("file1");
			String fileName = fileName_parsing(p);
			String path = request.getServletContext().getRealPath("/images").toString();
			//System.out.println(path);
			//String path = "C:\\Users\\davidyu\\Documents\\workspace2\\servletTest\\WebContent\\images\\" + fileName;
			path += File.separator + fileName;
			//System.out.println(path);
			
			
			kkma = pro_name + " " + pro_detail;
			
			/*
			 * PostgreSQL에 내용을 담아 저장한다.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			sql = "insert into productlist(name, message, path, email, pdate, price, _type, category, status, method) " + 
					"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pro_name);
			pstmt.setString(2, pro_detail);
			pstmt.setString(3, "http://117.17.188.74:8080/CMservlet/images/"+fileName);
			pstmt.setString(4, pro_host);
			pstmt.setString(5, pro_date);
			pstmt.setString(6, pro_price);
			pstmt.setString(7, pro_type);
			pstmt.setString(8, category);
			pstmt.setString(9, status);
			pstmt.setString(10, method);
			pstmt.executeUpdate();			
			conn.commit();
			
			conn.setAutoCommit(false);
			sql = " select _index from productlist order by _index desc ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			rst.next();
			number = rst.getInt("_index");
			
			//System.out.println(number);
			
			sql = " alter table test add pi_" + number + " varchar(300) default(0) ";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			conn.commit();
			
			conn.setAutoCommit(false);
			
			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pro_host);
			rst = pstmt.executeQuery();
			while(rst.next()){
				column = "pi_"+number;
				String index = rst.getString(column);
				//System.out.println(index + ", index : " + number + ", user_id : " + pro_host);
				if(index.toString().trim().equals("0")){
					sql = " update test set " + column + " = ? where user_name = ? ";
					pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, "1");
					pstmt.setString(2, pro_host);
					pstmt.executeUpdate();
				}else{
					
				}
			}		
			
			conn.commit();
			
			// 형태소 분석기를 이곳에서 작동시킨다.
			
			conn.setAutoCommit(false);
			
			sql = " insert into kkma (_index2) values ('" + number + "')";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
			KeywordList kl = ke.extractKeyword(kkma, true);
			for( int i = 0; i < kl.size(); i++ ) {
				Keyword kwrd = kl.get(i);
				
				sql = " select column_name from information_schema.columns where table_name = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "kkma");
				rst = pstmt.executeQuery();				
				ArrayList<String> arr = new ArrayList<String>();
				
				while(rst.next()){
					arr.add(rst.getString(1));
				}
				System.out.println(arr.toString());
				
				int check = 0;
				
				for(int j = 1; j < arr.size(); j++){						
					if(kwrd.getString().toString().trim().equals(arr.get(j).toString().trim())){	
						//System.out.println(kwrd.getString() + ", " + arr.get(j));
						check++;														
						break;
					}
				}

				if(check >0){
					sql = " update kkma set " + kwrd.getString() + " = ? where _index2 = ? " ;
					pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, kwrd.getCnt()+"");
					pstmt.setString(2, number+"");
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
						pstmt.setString(2, number+"");
						pstmt.executeUpdate();
					}
					
					
					
				}
				//System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
			}
			
			
			conn.commit();

			/*
			 * 사진 파일을 외부에 저장한다. 이 때는 바이트를 이용한다. 위의 텍스트 또한 바이트로 처리 할 수 있다.
			 * This is , below, a source for storing picture data at the external place. For this case, use byte format(theme).
			 */
			File f = new File(path);
			fos = new FileOutputStream(f);
			is = p.getInputStream();
			byte[] buffer = new byte[is.available()];

			while ( is.read(buffer) != -1){
				fos.write(buffer);
			}

			System.out.println("File name ["+fileName+"] - Size ["+f.length()+"]");
			
			// 저정한 사진 파일을 다시 읽어들여 썸네일 아트를 실시한다.
			long st = System.currentTimeMillis();
			BufferedImage bi = ImageIO.read(f);
			Thumbnails.of(bi).rotate(Integer.parseInt(degree)).crop(Positions.CENTER).size(400, 420).toFile(f);
			//System.out.println((System.currentTimeMillis() - st)/100);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		finally{
			try{
				is.close();
				fos.close();
				rst.close();
				pstmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private synchronized String fileName_parsing(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		//System.out.println("contentDisp = " + contentDisp);
		String[] items = contentDisp.split(";");
		for (String s : items) {
			System.out.println("s = " + s);
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length()-1);
			}
		}
		return "";
	}
}
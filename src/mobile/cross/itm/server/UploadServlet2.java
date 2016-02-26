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

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet2")
@MultipartConfig
public class UploadServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rst = null;
	String sql;


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

			String pro_name = request.getParameter("name");
			String pro_detail = request.getParameter("detail");			
			String pro_price = request.getParameter("price");			
			String pro_host = request.getParameter("host");			
			String pro_date = request.getParameter("s");			
			String pro_type = request.getParameter("type");	
			String pro_index = request.getParameter("index");
			String degree = request.getParameter("degree");			
			String category = request.getParameter("category");			
			String status = request.getParameter("status");			
			String method = request.getParameter("method");

			System.out.println(category + ", " + status + ", " + method);

			Part p = request.getPart("file1");
			String fileName = FileName(p);
			String path = request.getServletContext().getRealPath("/images").toString();
			System.out.println(path);
			//String path = "C:\\Users\\davidyu\\Documents\\workspace2\\servletTest\\WebContent\\images\\" + fileName;
			path += File.separator + fileName;
			//System.out.println(path);

			/*
			 * PostgreSQL에 내용을 담아 저장한다.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");
			conn.setAutoCommit(false);
			
			sql = "update productlist set " +
					"name = ?, " +
					"message = ?, " +
					"path = ?, " +
					"email = ?, " +
					"pdate = ?, " +
					"price = ?, " +
					"_type = ?, " +
					"category = ?, " +
					"status = ?, " +
					"method = ? " + 
					"where _index = ? and _type = ?";
			
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
			pstmt.setInt(11, Integer.parseInt(pro_index));
			pstmt.setString(12, pro_type);
			
			pstmt.executeUpdate();			
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
			Thumbnails.of(bi).rotate(Integer.parseInt(degree)).crop(Positions.CENTER).size(700, 700).toFile(f);
			System.out.println((System.currentTimeMillis() - st)/100);
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
				pstmt.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private synchronized String FileName(Part part) {
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
package mobile.cross.itm.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@WebServlet("/replyDownload")
public class replyDownload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	class recKKMA{
		String index;
		float value;

		public recKKMA(String index, float f){
			this.index = index;
			this.value = f;
		}
	}

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
	
	public void SelectionSort(recKKMA[] arr){

		int minIndex;
		recKKMA temp;
		for(int i = 0; i < arr.length-1; i++){
			minIndex = i;
			for(int j = i+1; j < arr.length; j++){
				if(arr[j].value < arr[minIndex].value){
					minIndex = j;
				}
			}
			temp = arr[minIndex];
			arr[minIndex] = arr[i];
			arr[i] = temp;
		}

	}

	private synchronized void recMethod(int j, List list) throws SQLException{		

		sql = " select * from productlist join userInfo on productlist.email = userInfo.user_id and productlist._index = ? order by productlist._index desc ";
		pstmt2 = conn.prepareStatement(sql);
		pstmt2.setInt(1, j);
		rst2 = pstmt2.executeQuery();

		int index = 0;

		while(rst2.next()){
			List<String> list3 = new ArrayList<String>();

			name = rst2.getString("name");
			message = rst2.getString("message");
			path = rst2.getString("path");
			email = rst2.getString("email");
			pdate = rst2.getString("pdate");
			price = rst2.getString("price");
			index = rst2.getInt("_index");
			type = rst2.getString("_type");
			category = rst2.getString("category");
			status = rst2.getString("status");
			method = rst2.getString("method");
			user_pic = rst2.getString("user_pic");

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
			list.add(";");
		}
	}
	
	private synchronized void recMethod2(int j, List list) throws SQLException{		

		sql = " select * from productlist join userInfo on productlist.email = userInfo.user_id and productlist._index = ? order by productlist._index desc ";
		pstmt2 = conn.prepareStatement(sql);
		pstmt2.setInt(1, j);
		rst2 = pstmt2.executeQuery();

		int index = 0;

		while(rst2.next()){
			List<String> list3 = new ArrayList<String>();

			name = rst2.getString("name");
			message = rst2.getString("message");
			path = rst2.getString("path");
			email = rst2.getString("email");
			pdate = rst2.getString("pdate");
			price = rst2.getString("price");
			index = rst2.getInt("_index");
			type = rst2.getString("_type");
			category = rst2.getString("category");
			status = rst2.getString("status");
			method = rst2.getString("method");
			user_pic = rst2.getString("user_pic");

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
			list.add(":");
		}
	}

	private synchronized void downloadData(HttpServletRequest request, HttpServletResponse response) throws ServletException, UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");

		//request.getCharacterEncoding();
		String pindex = new String(request.getParameter("pindex"));
		String ptype = new String(request.getParameter("ptype"));
		String user_id = new String(request.getParameter("pid"));

		int total = 1;
		
		System.out.println(pindex);

		try{
			response.setContentType("text/html; charset=utf-8"); // 한글 인코딩 처리
			/*
			 * DB에 저장하는 역할.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			sql = " select count(*) from information_schema.columns where table_name = ? ";
			pstmt2 = conn.prepareStatement(sql);
			pstmt2.setString(1, "test");
			rst2 = pstmt2.executeQuery();
			rst2.next();
			total = rst2.getInt(1) - 1;

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
				// 조인구문으로 바꿀 껏!
				innerList.add(this.vid);
				innerList.add(this.vresponse);
				innerList.add(this.vtime);
				innerList.add(this.vpic);
				list.addAll(innerList);
			}			

			list.add(";");

			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			while(rst.next()){
				column = "pi_"+pindex;
				String index = rst.getString(column);
				//System.out.println(index + ", index : " + pindex + ", user_id : " + user_id);
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

			//System.out.println(user_name_list.toString());

			ArrayList<String> list2 = new ArrayList<String>();
			ArrayList<String> check = new ArrayList<String>();
			for(int i = 0; i < user_name_list.size(); i++){

				sql = " select * from test where user_name = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_name_list.get(i).toString());
				rst = pstmt.executeQuery();
				while(rst.next()){
					if(i == 0){
						for(int j = 2; j <= total ; j++){
							list2.add(rst.getString(j));
						}
					}else{
						for(int j = 0; j < list2.size(); j++){
							if(list2.get(j).toString().equals("0") && rst.getString(j+2).toString().equals("1")){
								//System.out.println("pi_"+ (j + 73));
								if(i == 1){
									check.add((j+178)+"");
									//System.out.println("pi_"+ (j + 178));
									recMethod(j+178, list);
								}else{
									int index = 0;
									for(int k = 0; k < check.size(); k++){
										if(check.get(k).toString().equals((j+178)+"")){
											index++;
											break;
										}/*else{
											System.out.println("pi_ else "+ (j + 73));
											check.add((j+73)+"");
											recMethod(j, list);
											break;
										}*/
									}
									if(index == 0){
										check.add((j+178)+"");
										recMethod(j+178, list);
									}
								}
							}
						}
					}
				}
			}

			// 형태소 분석을 통한 추천 알고리즘

			sql = " select count(*) from information_schema.columns where table_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "kkma");
			rst = pstmt.executeQuery();
			rst.next();
			total = rst.getInt(1)-1;

			sql = " select count(*) from kkma ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			rst.next();			
			recKKMA[] r = new recKKMA[rst.getInt(1)-1];
			//System.out.println("index check" + rst.getInt(1));
			
			ArrayList<String> ownerRec = new ArrayList<String>();	
			int sum1 = 0;
			
			sql = " select * from kkma where _index2 = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, pindex);
			rst = pstmt.executeQuery();			
			while(rst.next()){
				for(int i = 2; i <= total; i++){
					ownerRec.add(rst.getString(i));
				}
				
				//System.out.println(ownerRec.toString());
				
				for(int i = 0; i < ownerRec.size(); i++){
					sum1 += Math.pow(Float.parseFloat(ownerRec.get(i)), 2);
				}
				
				//System.out.println("sum1 : "+sum1);
			}
			

			sql = " select * from kkma ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();
			
			int k = 0;
			
			while(rst.next()){				
				
				int sum2 = 0, sum3 = 0;
				
				if(rst.getString("_index2").toString().equals(pindex)){					
					// null
				}else{
					ArrayList<String> otherRec = new ArrayList<String>();
					for(int i = 2; i <= total; i++){
						otherRec.add(rst.getString(i));
					}
					
					//System.out.println(otherRec.toString());
					
					for(int i = 0; i < otherRec.size(); i++){
						sum2 += Math.pow(Float.parseFloat(otherRec.get(i)), 2);
					}
					
					//System.out.println("sum2 : "+sum2);
					
					for(int i = 0; i < ownerRec.size(); i++){
						sum3 += Integer.parseInt(ownerRec.get(i)) * Integer.parseInt(otherRec.get(i));
					}
					
					//System.out.println("sum3 : "+sum3);
					
					//r = new recKKMA(rst.getString("_index2"), sum3/(sum1*sum2));
					//System.out.println((float)(sum1*sum2)/(sum3+1));
					
					r[k] = new recKKMA(rst.getString("_index2"), (float)(sum1*sum2)/(sum3+1));
					k++;
				}				
			}
			
			//System.out.println("index check" + k);
			
			SelectionSort(r);
			
			for(int i = 0; i < 4; i++){
				recMethod2(Integer.parseInt(r[i].index), list);
			}
			
			//System.out.println(ownerRec.toString());
			//System.out.println(otherRec.toString());

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

package mobile.cross.itm.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
@WebServlet("/DownloadServlet")
@MultipartConfig
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "jdbc:postgresql://localhost:5432/postgres";
	String className = "org.postgresql.Driver";
	Connection conn = null;
	PreparedStatement pstmt = null, pstmt2 = null;
	ResultSet rst = null, rst2 = null;
	String sql;

	String path, user_id, name = "", message = "", email = "", pdate = "", price = "", type = "", category = "", status = "", method = "", user_pic = "";

	ArrayList<String> dataSet;
	ArrayList<String> userList, otherList;
	ArrayList<HashMap<String, String>> minList;
	HashMap<String, String> minMap;

	RecList[] recList;

	Comparator<String> comparator = Collections.reverseOrder();

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	private synchronized void recMethod(int j, List<String> list) throws SQLException{		

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
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int total = 0;
		List<String> list = new ArrayList<String>();

		try{	

			user_id = request.getParameter("user_id");

			/*
			 * DB 에서 내용을 읽어와 list 형태의 데이터에 저장합니다. json 형식의 데이터로 가공 후 안드로이드 클라이언트 쪽으로 보내줍니다.
			 */
			Class.forName(className);
			conn = DriverManager.getConnection(url, "postgres", "admin");

			sql = " select count(*) from information_schema.columns where table_name = ? ";
			pstmt2 = conn.prepareStatement(sql);
			pstmt2.setString(1, "test");
			rst2 = pstmt2.executeQuery();
			rst2.next();
			total = rst2.getInt(1);
			System.out.println("Total : " + total);

			dataSet = new ArrayList<String>();			
			userList = new ArrayList<>();

			sql = " select * from test where user_name = ? ";
			pstmt2 = conn.prepareStatement(sql);
			pstmt2.setString(1, user_id);
			rst2 = pstmt2.executeQuery();

			while(rst2.next()){
				for(int j = 2; j <= total ; j++){
					userList.add(rst2.getString(j));
				}
			}


			sql = " select * from test ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();


			while(rst.next()){
				if(rst.getString("user_name").toString().equals(user_id)){

				}else{
					otherList = new ArrayList<>();
					String user_name = rst.getString("user_name");
					sql = " select * from test where user_name = ? ";
					pstmt2 = conn.prepareStatement(sql);
					pstmt2.setString(1, user_name);
					rst2 = pstmt2.executeQuery();
					rst2.next();
					for(int j = 2; j <= total ; j++){
						otherList.add(rst2.getString(j));
					}

					int sum = 0;

					for(int i = 0; i < userList.size(); i++){
						sum += Math.abs(Integer.parseInt(userList.get(i)) - Integer.parseInt(otherList.get(i)));

					}

					dataSet.add(user_name);
					dataSet.add(sum+"");
				}
			}

			/*for(int i = 0; i < dataSet.size(); i++){
				System.out.print(dataSet.get(i) + " ");
			}
			System.out.println();*/

			recList = new RecList[dataSet.size()/2];

			for(int i = 0; i < dataSet.size()/2; i++){
				String val = (String) dataSet.get(2*i+1);
				recList[i] = new RecList((String)dataSet.get(2*i), Integer.parseInt(val));
			}

			RecList rec = new RecList(null, 0);
			while(true){
				int change = 0;
				for(int i = 0; i < recList.length-1; i++){				
					if(recList[i].dis > recList[i+1].dis){
						rec = recList[i];
						recList[i] = recList[i+1];
						recList[i+1] = rec;
						change++;
					}
				}
				if(change == 0){
					break;
				}
			}

			ArrayList<String> list5 = new ArrayList<String>();
			ArrayList<String> check = new ArrayList<String>();
			sql = " select * from test where user_name = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_id);
			rst = pstmt.executeQuery();
			while(rst.next()){
				for(int j = 2; j <= total ; j++){
					list5.add(rst.getString(j));
				}
			}
			
			//System.out.println("List5 : " + list5.toString());

			for(int i = 0; i < recList.length; i++){
				System.out.println(recList[i].dis);
				//System.out.println(recList.length);
				sql = " select * from test where user_name = ? ";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, recList[i].user_id.toString());
				rst = pstmt.executeQuery();
				while(rst.next()){
					for(int j = 0; j < list5.size(); j++){
						if(list5.get(j).toString().equals("0") && rst.getString(j+2).toString().equals("1")){
							//System.out.println("pi_"+ (j + 73));
							if(i == 0){
								check.add((j+178)+"");
								System.out.println("pi_"+ (j + 178));
								//recMethod(j+178, list);
							}else{
								int index = 0;
								for(int k = 0; k < check.size(); k++){
									if(check.get(k).toString().equals((j+178)+"")){
										index++;
										break;
									}else{
										//uncheck.add((j+178)+"");
									}
								}
								if(index == 0){
									check.add((j+178)+"");
									//recMethod(j+178, list);
								}else{
									//uncheck.add((j+178)+"");
								}
							}
						}else{
							
						}
					}
					//uncheck.add((j+178)+"");
				}
			}
			Collections.sort(check, comparator);
			//Collections.sort(uncheck, comparator);
			for(int i = 0; i < check.size(); i++){
				System.out.print("check : "+check.get(i) + " ");
				recMethod(Integer.parseInt(check.get(i)), list);
			}			

			//conn.setAutoCommit(false);

			sql = " select * from productlist join userInfo on productlist.email = userInfo.user_id order by productlist._index desc ";
			pstmt = conn.prepareStatement(sql);
			rst = pstmt.executeQuery();

			int index = 0;

			while (rst.next()) {
				boolean flag = true;
				List<String> list2 = new ArrayList<String>();
				index = rst.getInt("_index");
				name = rst.getString("name");
				message = rst.getString("message");
				path = rst.getString("path");
				email = rst.getString("email");
				pdate = rst.getString("pdate");
				price = rst.getString("price");						
				type = rst.getString("_type");
				category = rst.getString("category");
				status = rst.getString("status");
				method = rst.getString("method");
				user_pic = rst.getString("user_pic");



				for(int i = 0; i < check.size(); i++){
					if(check.get(i).toString().equals(index+"")){
						//System.out.println("if : "+check.get(i).toString() + ", " + index);
						flag = false;
						break;
					}
				}
				
				if(flag){
					//list.add(":");
					list2.add(name);
					list2.add(message);
					list2.add(path);
					list2.add(email);
					list2.add(pdate);
					list2.add(price);
					list2.add(index+"");
					list2.add(type);
					list2.add(category);
					list2.add(status);
					list2.add(method);
					list2.add(user_pic);
					list.addAll(list2);
				}
				
				//System.out.println(list.toString());
			}

			//conn.commit();

			//System.out.println(list.toString());

			String json = new Gson().toJson(list);
			//System.out.println(list.get(0));
			//System.out.println(json);
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
	class RecList{
		String user_id;
		int dis;

		public RecList(String user_id, int dis){
			this.user_id = user_id;
			this.dis = dis;
		}
	}
}
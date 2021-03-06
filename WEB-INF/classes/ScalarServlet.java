import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.*;
import java.io.*;
import java.sql.*;
import java.io.OutputStreamWriter;

import oracle.sql.*;
import oracle.jdbc.*;
import javax.servlet.*;
import javax.servlet.http.*;

import util.Db;

public class ScalarServlet extends HttpServlet {

	private HttpSession session;
	public void doGet(HttpServletRequest request, 
		HttpServletResponse response)
		throws ServletException, IOException {
		
		Db database = new Db();
		database.connect_db();
		String id = request.getParameter( "id" );
		String user = request.getParameter( "user" );
		String fromdate = request.getParameter( "fromdate" );
		String todate = request.getParameter( "todate" );
		String location = request.getParameter( "location" );
		String value = request.getParameter( "value" );
		String query = null;
		
		response.setContentType("text/html");
		response.setHeader("Content-Disposition","attachment;filename= Scalar.csv");
		
		try{
			OutputStream out = response.getOutputStream();
			Writer w = new OutputStreamWriter(out, "UTF-8");
			
			if (user.equals("yes")) {
				String[] datef = fromdate.split("T");
	        	String[] datet = todate.split("T");
	        	String date1 = null;
	        	String date2 = null;
	        	if (datef[1].split(":").length != 3) {
	        		date1 = datef[0] +" "+datef[1] + ":00";
	    		}
	    		else {
	    			date1 = datef[0] +" "+datef[1];
	    		}
	        	if (datet[1].split(":").length != 3) {
	        		date2 = datet[0] +" "+datet[1] + ":00";
	    		}
	    		else {
	    			date2 = datet[0] +" "+datet[1];
	    		}
	        	if(database.isNumber( value )){
					query = "SELECT s.sensor_id,s.date_created,s.value "
							+ "FROM scalar_data s "
							+ "JOIN subscriptions su on s.sensor_id = su.sensor_id "
							+ "JOIN sensors se on s.sensor_id = se.sensor_id "
							+ "WHERE su.person_id =" + id
							+ "AND s.value ="+ Float.parseFloat(value)
									+ "AND se.location LIKE '%"+location+"' "
											+ "AND date_created BETWEEN TO_DATE('"+date1+"', 'YYYY-MM-DD HH24:MI:SS') "
													+ "AND TO_DATE('"+date2+"', 'YYYY-MM-DD HH24:MI:SS') "
															+ "ORDER BY s.sensor_id";
				}
	        	else if(value.isEmpty()){
					query = "SELECT s.sensor_id,s.date_created,s.value "
							+ "FROM scalar_data s "
							+ "JOIN subscriptions su on s.sensor_id = su.sensor_id "
							+ "JOIN sensors se on s.sensor_id = se.sensor_id "
							+ "WHERE su.person_id =" + id
							+ "AND se.location LIKE '%"+location+"' "
									+ "AND date_created BETWEEN TO_DATE('"+date1+"', 'YYYY-MM-DD HH24:MI:SS') "
											+ "AND TO_DATE('"+date2+"', 'YYYY-MM-DD HH24:MI:SS') "
													+ "ORDER BY s.sensor_id";
				}
	        	else {
					query = "SELECT s.sensor_id,s.date_created,s.value "
							+ "FROM scalar_data s "
							+ "JOIN subscriptions su on s.sensor_id = su.sensor_id "
							+ "JOIN sensors se on s.sensor_id = se.sensor_id "
							+ "WHERE su.person_id =" + id
							+ "AND s.value =null "
							+ "AND se.location LIKE '%"+location+"' "
									+ "AND date_created BETWEEN TO_DATE('"+date1+"', 'YYYY-MM-DD HH24:MI:SS') "
											+ "AND TO_DATE('"+date2+"', 'YYYY-MM-DD HH24:MI:SS') "
													+ "ORDER BY s.sensor_id";
				}
			}
			else {
				query = "SELECT sensor_id,date_created,value FROM scalar_data";
			}
		
			ResultSet rs = database.execute_stmt(query);
			while (rs.next()) {
				String[] dateTime = String.valueOf(rs.getTimestamp(2)).split(" ");
				String[] date = dateTime[0].split("-");
				String[] time = dateTime[1].split("\\.");
				String dateFormated = date[2] + "/"+ date[1]+ "/"+ date[0] + " " + time[0];

				
				w.write(rs.getString(1) +","+ dateFormated +","+ String.valueOf(rs.getDouble(3))+"\n");
			}
			w.flush();
			w.close();
			database.close_db();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			}
	}
}
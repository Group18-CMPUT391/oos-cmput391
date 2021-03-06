<!--
Search page allows to user to enter a search query 
-->

<%@	page import="util.Db" %>
<%@ page import="util.User" %> 

<% 
	User user = null;
	Db database = new Db();
	database.connect_db();
		
	String status = null;
	 String error = null; 
	
	try {
	   status = (String) session.getAttribute("status");
	   user = (User) session.getAttribute("user");
	   error = (String) session.getAttribute("err");  
	   //username = (String) session.getAttribute("username");
	} catch (NullPointerException e) {
	   e.printStackTrace();
	}
    
    if (user == null) {
        status = "Please log in before searching";
        session.setAttribute("status", status);
        response.sendRedirect("/oos-cmput391/login.jsp");
    }
%>

<html>
  <head>
    <title>Search</title>
    <center>
    	<jsp:include page="includes/header.jsp"/>
    </center>
  </head>
  <body>
	<div style="text-align:center">
  		<div style="display:inline-block">
		    <form name="searchform" action="searchservlet" method="get">
		      <table>
		        <tr>
		          <th>Keyword(s)/Value: </th>
		          <td>
		            <input name="query" placeholder="Enter search query.."></input>
		          </td>
		        </tr>
		        <tr>
		          <th>Location: </th>
		          <td>
		            <input name="location" placeholder="Enter location.."></input>
		          </td>
		        </tr>
		        <tr><th>Sensor Type:</th>
	              	<td>
	              		<select name="sensor_type">
	              			<option value="empty"></option>
	              			<option value="a">Audio</option>
							<option value="i">Image</option>
							<option value="s">Scalar Value</option>
	              		</select>
	              	</td>
	            </tr>
		        <tr>
		          <th>*Date: </th>
		          <td>
		            <input name="fromdate" type="datetime-local" step="1" required="required" />
		            to 
		            <input name="todate" type="datetime-local" step="1" required="required" />
		          </td>
		        </tr>
		        <tr>
		          <th> </th>
		          <td>
		            <input type="submit" value="Search" name="search"/>
		          </td>
		        </tr>
		      </table>
		    </form>
		</div>
	</div>
	<center><%if (error != null) {
				out.println(error); 
				session.removeAttribute("err");
			  }%></center>
  </body>
  <footer>
<center><jsp:include page="includes/footer.jsp"/></center>
</footer> 
</html>

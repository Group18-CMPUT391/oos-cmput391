import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import util.Db;
import util.User;


// Servlet for changing info in Users table
public class UpdatePassServlet extends HttpServlet {
   
	/**
	 * Handles HTTP POST request when pressing button
	 */
	public void doPost( HttpServletRequest request, 
			HttpServletResponse response )
					throws ServletException, IOException {
		// Invoke doGet to process this request
		doGet( request, response );
	}
	
    /**
	 * Handles HTTP GET request from form's values
	 */
	public void doGet( HttpServletRequest request, HttpServletResponse response ) 
						throws ServletException, IOException {
		HttpSession session;
	    String message = "";
	    Db database;
	    User user;
	    long person_id;
	    String user_name;
	    String password;

	    
		// Set the content type to HTML
		response.setContentType( "text/html" );
		// Get the output stream from the response object
		PrintWriter out = response.getWriter();
		// Get the session (Create a new one if required)
		session = request.getSession(true);
	
		// Check if the user is logged in
		user = (User) session.getAttribute("user");
		if (user == null) {
			String errormsg = "Please login before accessing account info";
		    session.setAttribute("status", errormsg);
		    response.sendRedirect("/oos-cmput391/login.jsp");
		    return;
		}

		database = new Db();
		database.connect_db();

		person_id = Long.parseLong(request.getParameter("person_ids"));

		user_name = database.getUser_name(person_id);
		password = database.getPassword(user_name);

		// GETs from form's input
		String n_user_name = request.getParameter("username");
		String n_password = request.getParameter("pass");
		String confirm_pw = request.getParameter("pass2");
		
		// Compare with database
		if (!user_name.equals(n_user_name)) {
		    // Check if the user_name already exists:
		    if (database.userExists(n_user_name)) {
		    	message = message + "Username '" + n_user_name + "' already taken by another user.<br>";
		    } else {
		    	database.updateUsername(user_name, n_user_name);
		    	message = message + "Username changed from '" + user_name + "' to '" + n_user_name + "'<br>";
		    	// Important, to pass the user object with changed values
		    	user.setUser_name(n_user_name);
		    }
		}
		if ((n_password.equals(confirm_pw)) & (n_password.length() > 0)) {
			database.updatePassword(user_name, n_password);
	    	message = message + "Password changed<br>";
		}
		
	    database.close_db();
		session.setAttribute("status", message);
	    session.setAttribute("user", user);
		if (request.getParameter("person_ids") != null) {
		    response.sendRedirect("/oos-cmput391/change_info.jsp?updateType=pass&selected=" + person_id);
	    } else {
	    	response.sendRedirect("/oos-cmput391/change_info.jsp?updateType=pass");
	    }
	}
}
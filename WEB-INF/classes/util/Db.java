package util;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import oracle.sql.*;
import oracle.jdbc.*;

import util.Role;
import util.User;
import util.Photo;

public class Db {
	static final String USERNAME = "wkchoi";
	static final String PASSWORD = "Kingfreak95";
	// JDBC driver name and database URL
	static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	static final String DB_URL = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";

	private Connection conn;
	private Statement stmt;

	public int connect_db() {
		try {
			Class drvClass = Class.forName(DRIVER_NAME);
			DriverManager.registerDriver((Driver) drvClass.newInstance());
			this.conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
			this.stmt = conn.createStatement();
			return 1;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void close_db() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param query
	 * @return ResultSet
	 */
	public ResultSet execute_stmt(String query) {
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Performs the update query execution.
	 * Returns 0 when unsuccessful
	 * @param query
	 * @return Integer
	 */
	public Integer execute_update(String query) {
		try {
			return stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Fetches all information in database relating to username
	 * @param String username
	 * @returns User
	 */
	public User get_user(String username) {
		ResultSet rs_users;
		ResultSet rs_persons;
		String query_users = "select * from users "
			+ "where user_name = '" + username + "'";
		String query_persons = "select * from persons "
			+ "where user_name = '" + username + "'";
		rs_users = execute_stmt(query_users);
		rs_persons = execute_stmt(query_persons);
		return user_from_resultset(rs_users, rs_persons);
	}

	/**
	 * Returns the username's matching password
	 * @param String username
	 * @returns String password
	 */
	public String get_password(String username) {
		String tPassword = "";
		String query = "select password from users where user_name = '" + 
			username + "'";
		ResultSet rs = execute_stmt(query);
		try {
			while(rs != null && rs.next()) {
				tPassword = (rs.getString(1)).trim();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return tPassword;
	}

	/**
	 * Returns a User object from two resultsets
	 * resulting from the user and persons tables
	 * @param ResultSet rs_user
	 * @param ResultSet rs_person
	 * @returns User
	 */
	public User user_from_resultset(ResultSet rs_user, ResultSet rs_person) {
		String user_name = null;
		String password = null;
		String date = null;
		String email = null;
		String fname = null;
		String lname = null;
		String phone = null;
		String address = null;
		User user = null;
		String role = null;
		
		// Get data from rs_user
		try {
			while (rs_user.next()) { 
				user_name = rs_user.getString("user_name");
				password = rs_user.getString("password");
				date = rs_user.getString("date_registered");
				role = rs_user.getString("role");
			}
			while (rs_person.next()) {
				email = rs_person.getString("email");
				fname = rs_person.getString("first_name");
				lname = rs_person.getString("last_name");
				phone = rs_person.getString("phone");
				address = rs_person.getString("address");
			}
			user = new User(user_name, email, fname, lname, phone, address,
					role, date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * Checks if a user with specified username exists within the database
	 * Returns true if username exists, otherwise returns false
	 *
	 * @param String username
	 * @return boolean
	 */
	public boolean userExists(String username) {
		String userquery = "select * from users where user_name='" + 
			username + "'";
		ResultSet rset = execute_stmt(userquery);
		try {
			if (!rset.next()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Adds a user to the database, given a specified username and password
	 * @param String username, String password
	 * @return Integer
	 */
	public Integer addUser(String username, String password) {
		String query = "insert into users values ('" + 
			username + "', '" + 
			password + "', sysdate)";
		return execute_update(query);
	}

	/**
	 * Checks if a person with a given email address already exists
	 * @param String email
	 * @return boolean
	 */
	public boolean emailExists(String email) {
		String query = "select email from persons where email = '" + 
			email + "'";
		ResultSet rs = execute_stmt(query);
		try {
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Set the email of a user. Handles if does not exist.
	 * @param String username, String email
	 * @return Integer
	 */
	public Integer setEmail(String username, String email) {
		String checkquery = "select user_name, email from persons where " + 
			"user_name = '" + username + "'";
		ResultSet rscheck = execute_stmt(checkquery);
		try {
			if (rscheck.next()) {
				// update the email
				String updatequery = "update persons set email = '" + email + 
					"' where user_name = '" + username + "'";
				return execute_update(updatequery);
			} else {
				// add a row into the database
				String addquery = "insert into persons (user_name, email)" + 
					"values ('" + username + "', '" + email + "')";
				return execute_update(addquery);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}

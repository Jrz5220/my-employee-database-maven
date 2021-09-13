package com.felix.MyEmployeeDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
	private static String dbhost = "jdbc:mysql://<db-endpoint>.us-east-1.rds.amazonaws.com:3306/<db-table>";
	private static String username = "<user>";
	private static String password = "<password>";
	private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	private static Connection myConn = null;

	/**
	 * Creates a connection to a database URL
	 * @return A Connection object connected to a database, or null if a database access error occurs
	 */
	public static Connection createNewDbConnection() {
		try {
			// specifies which jdbc driver to make connections with.
			// com.mysql.cj.jdbc.Driver (from MySQL Connector/J) is the class that implements the java.sql.Driver interface.
			Class.forName(jdbcDriver).newInstance();
			myConn = DriverManager.getConnection(dbhost, username, password);
		} catch(InstantiationException e) {
			System.out.println("Cannot instantiate class: " + jdbcDriver);
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			System.out.println("Illegal Access: class is not accessible");
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			System.out.println(jdbcDriver + " class could not be located");
			e.printStackTrace();
		} catch(SQLException e) {
			System.out.println("Cannot create database connection");
			System.out.println("SQLException: " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
			e.printStackTrace();
		}
		return myConn;
	}

}

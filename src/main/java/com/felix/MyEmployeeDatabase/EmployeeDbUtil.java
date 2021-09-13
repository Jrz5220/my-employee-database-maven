package com.felix.MyEmployeeDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDbUtil {
	
	public EmployeeDbUtil() {
	}

	/**
	 * Gets all employees from the employee database table
	 * @return A List containing all employee objects from the database table
	 * @throws SQLException if a database access error occurs
	 */
	public List<Employee> getEmployees() throws SQLException {
		List<Employee> emps = new ArrayList<>();
		
		//JDBC objects
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			//get database connection
			myConn = DBconnection.createNewDbConnection();
			
			validateConnection(myConn);
			
			//create a SQL statement
			String sql = "select * from employee";
			myStmt = myConn.createStatement();
			
			//execute query
			myRs = myStmt.executeQuery(sql);
			
			while(myRs.next()) {
				//process result set
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String dept = myRs.getString("department");
				String email = myRs.getString("email");
				Employee emp = new Employee(id, firstName, lastName, dept, email);
				emps.add(emp);
			}
		}
		finally {
			//close JDBC objects
			close(myConn, myStmt, myRs);
		}
		return emps;
	}
	
	/**
	 * Adds a new employee to the employee database table. The employee must not contain a duplicate email.
	 * @param newEmp The Employee object to add to the database table
	 * @throws SQLException if a database access error occurs
	 */
	public void addEmployee(Employee newEmp) throws SQLException {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			myConn = DBconnection.createNewDbConnection();
			
			validateConnection(myConn);
			
			String sql = "insert into employee (first_name, last_name, department, email) values (?, ?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);
			
			//set the parameters for the prepared statement
			myStmt.setString(1, newEmp.getFirstName());
			myStmt.setString(2, newEmp.getLastName());
			myStmt.setString(3, newEmp.getDepartment());
			myStmt.setString(4, newEmp.getEmail());
			
			myStmt.execute();
		}
		finally {
			close(myConn, myStmt, null);
		}
	}
	
	/**
	 * Gets an employee from the database table whose id matches the specified id
	 * @param theEmpId The id of the employee to search for
	 * @return The Employee object found in the database, or null if the employee doesn't exist
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	public Employee getEmployee(int theEmpId) throws SQLException {
		Employee emp = null;
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		try {
			myConn = DBconnection.createNewDbConnection();
			
			validateConnection(myConn);
			
			String sql = "select * from employee where id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, theEmpId);
			
			myRs = myStmt.executeQuery();
			
			if(myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String department = myRs.getString("department");
				String email = myRs.getString("email");
				emp = new Employee(id, firstName, lastName, department, email);
			}
		} finally {
			close(myConn, myStmt, myRs);
		}
		return emp;
	}
	
	/**
	 * Updates an employee's data in the employee database table
	 * @param theEmp The Employee to update
	 * @throws SQLException if a database access error occurs
	 */
	public void updateEmployee(Employee theEmp) throws SQLException {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = DBconnection.createNewDbConnection();
			
			validateConnection(myConn);
			
			String sql = "update employee set first_name=?, last_name=?, department=?, email=? where id=?";
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setString(1, theEmp.getFirstName());
			myStmt.setString(2, theEmp.getLastName());
			myStmt.setString(3, theEmp.getDepartment());
			myStmt.setString(4, theEmp.getEmail());
			myStmt.setInt(5, theEmp.getId());
			
			myStmt.execute();
		}
		finally {
			close(myConn, myStmt, null);
		}
	}
	
	/**
	 * Deletes the employee data from the database whose id matches the specified id
	 * @param theEmpId The id of the employee to delete
	 * @throws SQLException if a database access error occurs
	 */
	public void deleteEmployee(int theEmpId) throws SQLException {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = DBconnection.createNewDbConnection();
			
			validateConnection(myConn);
			
			String sql = "delete from employee where id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, theEmpId);
			myStmt.execute();
		}
		finally {
			close(myConn, myStmt, null);
		}
	}
	
	/**
	 * Gets all the employees from the database whose first or last name contain the specified substring
	 * @param theSearchName The substring to search for in the employee's first or last name
	 * @return A List of Employee objects that contain the specified substring in their first or last name
	 * @throws SQLException if a database access error occurs
	 */
	public List<Employee> searchEmployees(String theSearchName) throws SQLException {
		List<Employee> emps = new ArrayList<>();
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		try {
			myConn = DBconnection.createNewDbConnection();
			validateConnection(myConn);
			String sql = "select * from employee where first_name like '%" + theSearchName + "%' or last_name like '%" + theSearchName + "%'";
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);
			while(myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String dept = myRs.getString("department");
				String email = myRs.getString("email");
				Employee emp = new Employee(id, firstName, lastName, dept, email);
				emps.add(emp);
			}
		} finally {
			close(myConn, myStmt, myRs);
		}
		return emps;
	}
	
	/**
	 * Sorts Employee objects alphabetically in a List according to the specified sort field.
	 * @param theSortField An integer representing the sort field to use. If the integer doesn't match any sort field, the employees will be sorted by last name.
	 * @return A List of alphabetically sorted Employee objects according to the specified sort field
	 * @throws SQLException if a database access error occurs
	 */
	public List<Employee> sortEmployees(int theSortField) throws SQLException {
		List<Employee> emps = new ArrayList<>();
		String theFieldName;
		
		switch(theSortField) {
		case SortUtils.FIRST_NAME:
			theFieldName = "first_name";
			break;
		case SortUtils.LAST_NAME:
			theFieldName = "last_name";
			break;
		case SortUtils.DEPARTMENT:
			theFieldName = "department";
			break;
		case SortUtils.EMAIL:
			theFieldName = "email";
			break;
		default:
			theFieldName = "last_name";
		}
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = DBconnection.createNewDbConnection();
			validateConnection(myConn);
			String sql = "select * from employee order by " + theFieldName;
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);
			while(myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String dept = myRs.getString("department");
				String email = myRs.getString("email");
				Employee emp = new Employee(id, firstName, lastName, dept, email);
				emps.add(emp);
			}
		} finally {
			close(myConn, myStmt, myRs);
		}
		return emps;
	}
	
	/**
	 * Returns the number of employees in the database table
	 * @return The number of employees in the database
	 * @throws SQLException if a database access error occurs
	 */
	public int numOfEmployees() throws SQLException {
		int numOfEmps = 0;
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = DBconnection.createNewDbConnection();
			validateConnection(myConn);
			String sql = "select count(*) from employee";
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);
			if(myRs.next()) {
				numOfEmps = myRs.getInt(1);
			}
		} finally {
			close(myConn, myStmt, myRs);
		}
		return numOfEmps;
	}
	
	/**
	 * Checks if the employee email already exists in the database
	 * @param email The email to check for
	 * @return true if the email already exists, false if not
	 */
	public boolean isDuplicate(String email) {
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			myConn = DBconnection.createNewDbConnection();
			String sql = "select * from employee where email = '" + email + "'";
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);
			return myRs.next();
		} catch(SQLException e) {
			return false;
		} finally {
			close(myConn, myStmt, myRs);
		}
	}
	
	private void validateConnection(Connection conn) throws SQLException {
		if(conn == null)
			throw new SQLException("Not connected to a database");
	}
	
	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if(myRs != null)
				myRs.close();
			if(myStmt != null) 
				myStmt.close();
			if(myConn != null)
				myConn.close();		//doesn't close the database connection, just makes it an available connection in the connection pool
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}

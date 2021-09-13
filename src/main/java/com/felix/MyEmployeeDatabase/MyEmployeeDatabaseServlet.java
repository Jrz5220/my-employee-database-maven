package com.felix.MyEmployeeDatabase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyEmployeeDatabaseServlet
 */
@WebServlet("/MyEmployeeDatabaseServlet")
public class MyEmployeeDatabaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private EmployeeDbUtil empDbUtil;
	
	@Override
    public void init() throws ServletException {
    	empDbUtil = new EmployeeDbUtil();
    }
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyEmployeeDatabaseServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String theCommand = request.getParameter("command");
		if(theCommand == null)
			theCommand = "list";
		try {
			switch(theCommand) {
			case "load":
				loadEmployee(request, response);
				break;
			case "search":
				searchEmployees(request, response);
				break;
			case "sort":
				sortEmployees(request, response);
				break;
			case "delete":
				deleteEmployee(request, response);
				break;
			default:
				listEmployees(request, response, null);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			forwardError(request, response, "/index.jsp", "dbError", "Could not not connect to database");
		}
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String theCommand = request.getParameter("command");
		if(theCommand == null)
			theCommand = "list";
		try {
			switch(theCommand) {
			case "list":
				listEmployees(request, response, null);
				break;
			case "update":
				updateDatabase(request, response);
				break;
			case "load":
				loadEmployee(request, response);
				break;
			default:
				listEmployees(request, response, null);
				break;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			forwardError(request, response, "/index.jsp", "dbError", "Could not not connect to database");
		}
	}
	
	private void listEmployees(HttpServletRequest request, HttpServletResponse response, List<Employee> employees) throws ServletException, IOException, SQLException {
		// if employees list is not provided, get employees from database
		if(employees == null)
			employees = empDbUtil.getEmployees();
				
		// add employees to the request
		request.setAttribute("employees", employees);
				
		// send to the JSP page
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-employees.jsp");
		dispatcher.forward(request, response);
	}
	
	private void loadEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		int empId = 0;
		Employee theEmp = null;
		String theEmpId = request.getParameter("empId");
		try {
			if(theEmpId != null && !theEmpId.equals(""))
				empId = Integer.parseInt(theEmpId);
			theEmp = empDbUtil.getEmployee(empId);
			request.setAttribute("theEmployee", theEmp);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/employee-form.jsp");
			dispatcher.forward(request, response);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			forwardError(request, response, "/employee-form.jsp", "dbUpdateError", "Invalid employee id. Could not load employee from database");
		}
	}
	
	private void searchEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		List<Employee> emps = null;
		String theSearchName = request.getParameter("theSearchName").trim().replaceAll("\\s+", "-").toUpperCase();
		if(theSearchName != null && !theSearchName.equals(""))
			emps = empDbUtil.searchEmployees(theSearchName);
		listEmployees(request, response, emps);
	}

	private void sortEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		try {
			int sortField = Integer.parseInt(request.getParameter("sort"));
			List<Employee> emps = empDbUtil.sortEmployees(sortField);
			listEmployees(request, response, emps);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			forwardError(request, response, "/index.jsp", "dbError", "Could not sort employees in database");
		}
	}
	
	private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		int empId = 0;
		String theEmpId = request.getParameter("empId");
		try {
			if(theEmpId != null && !theEmpId.equals(""))
				empId = Integer.parseInt(theEmpId);
			empDbUtil.deleteEmployee(empId);
			listEmployees(request, response, null);
		} catch(NumberFormatException e) {
			e.printStackTrace();
			forwardError(request, response, "/index.jsp", "dbError", "Invalid employee id. Could not delete employee from database");
		}
	}
	
	private void updateDatabase(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		int theId;
		Employee emp;
		String empId = request.getParameter("empId");
		String firstName = request.getParameter("firstName").trim().replaceAll("\\s+", "-").toUpperCase();
		String lastName = request.getParameter("lastName").trim().replaceAll("\\s+", "-").toUpperCase();
		String department = request.getParameter("deptRadio");
		String username = firstName.toLowerCase() + "." + lastName.toLowerCase();
		String domain = "@" + department.toLowerCase() + ".company.com";
		if(department.equals("other")) {
			department = "-";
			domain = "@company.com";
		}
		String email = username + domain;
		
		int emailNum = 1;
		while(empDbUtil.isDuplicate(email)) {
			email = username + emailNum + domain;
			emailNum++;
		}
		
		try {
			if(empId == null || empId.equals("")) {
				if(dbIsFull()) {
					forwardError(request, response, "/employee-form.jsp", "dbUpdateError", "Database is full. Max 10 entries allowed.");
				} else {
					// add new employee
					emp = new Employee(firstName, lastName, department, email);
					empDbUtil.addEmployee(emp);
					listEmployees(request, response, null);
				}
			} else {
				// update existing employee
				theId = Integer.parseInt(empId);
				emp = new Employee(theId, firstName, lastName, department, email);
				empDbUtil.updateEmployee(emp);
				listEmployees(request, response, null);
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
			forwardError(request, response, "/employee-form.jsp", "dbUpdateError", "Invalid employee id. Could not update employee.");
		}
	}
	
	private void forwardError(HttpServletRequest request, HttpServletResponse response, String forwardPage, String errorAttribute, String errorMsg) throws ServletException, IOException {
		request.setAttribute(errorAttribute, errorMsg);
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		dispatcher.forward(request, response);
	}
	
	private boolean dbIsFull() throws SQLException {
		return empDbUtil.numOfEmployees() >= 10;
	}

}

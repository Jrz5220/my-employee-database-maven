package com.felix.MyEmployeeDatabase;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AppExceptionHandler
 */
@WebServlet("/AppExceptionHandler")
public class AppExceptionHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public AppExceptionHandler() {
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processError(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processError(request, response);
	}

	private void processError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("processing error...");
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		if(servletName == null)
			servletName = "Unknown";
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if(requestUri == null)
			requestUri = "Unknown";
		if(statusCode != 500) {
			request.setAttribute("errorTitle", "Error Details");
			request.setAttribute("statusCode", statusCode);
			request.setAttribute("requestUri", requestUri);
		} else {
			request.setAttribute("errorTitle", "Exception Details");
			request.setAttribute("servletName", servletName);
			request.setAttribute("exceptionName", throwable.getClass().getName());
			request.setAttribute("requestUri", requestUri);
			String exceptionMsg = throwable.getMessage();
			if(exceptionMsg == null || exceptionMsg.equals(""))
				exceptionMsg = "The server encountered an unexpected condition that prevented it from fulfilling the request.";
			request.setAttribute("exceptionMsg", throwable.getMessage());
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
		dispatcher.forward(request, response);
	}

}

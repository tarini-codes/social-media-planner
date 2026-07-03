package com.smp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddPostServlet
 */
@WebServlet("/AddPostServlet")
public class AddPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddPostServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String platform = request.getParameter("Platform");
		String caption = request.getParameter("caption");
		String date = request.getParameter("schedule_date");
		
		
		if(caption == null || caption.trim().isEmpty()){
			
			response.setContentType("text/html");
			response.getWriter().println("<h3>Caption cannot be empty! please go back and try again</h3>");
			
		}else {
			
			try {
				
				Properties props = new Properties();
				InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
				props.load(input);
				
				Class.forName(props.getProperty("db.driver"));
				Connection con = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
				PreparedStatement ps = con.prepareStatement("INSERT INTO posts(platform, caption, scheduled_date) VALUES(?, ?, ?)");
				
				ps.setString(1, platform);
				ps.setString(2, caption);
				ps.setString(3, date);
				
				ps.executeUpdate();
				
				response.sendRedirect("dashboard.jsp");
				
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}

}

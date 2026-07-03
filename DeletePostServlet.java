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
 * Servlet implementation class DeletePostServlet
 */
@WebServlet("/DeletePostServlet")
public class DeletePostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
     * @see HttpServlet#HttpServlet()
     */
    public DeletePostServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String postId = request.getParameter("id");
		
		try {
			
			Properties props = new Properties();
			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
			props.load(input);
			
			Class.forName(props.getProperty("db.driver"));
			Connection con = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
			PreparedStatement ps = con.prepareStatement("DELETE FROM posts WHERE id = ?");
			
			ps.setString(1, postId);
			
			ps.executeUpdate();
			
			con.close();
			
			response.sendRedirect("dashboard.jsp");
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
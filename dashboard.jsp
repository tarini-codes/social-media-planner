<%@ page import="java.sql.*" %>
	<!DOCTYPE html>
	<html lang="en">

	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">

		<style>
			body {
				background-color: #f4f6f9;
				font-family: Arial, sans-serif;
			}

			form {
				background-color: white;
				padding: 20px;
				max-width: 450px;
				margin: 20px auto;
				border-radius: 8px;
				box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
			}

			label {
				font-weight: bold;
				display: block;
				margin-top: 10px;
			}

			select,
			textarea,
			input {
				width: 100%;
				padding: 8px;
				margin-top: 5px;
				border: 1px solid #ccc;
				border-radius: 4px;
				box-sizing: border-box;
			}

			button {
				background-color: #0073b1;
				color: white;
				border: none;
				padding: 10px 15px;
				border-radius: 4px;
				cursor: pointer;
				margin-top: 15px;
			}

			table {
				width: 90%;
				max-width: 900px;
				margin: 3px auto;
				border-collapse: collapse;
				background-color: white;
				box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
				border-radius: 8px;
				overflow: hidden;
			}

			th,
			td {
				padding: 12px 15px;
				text-align: left;
				border-bottom: 1px solid #dddddd;
			}

			th {
				background-color: #0073b1;
				color: white;
				font-weight: bold;
				text-transform: capitalize;
			}

			tr:hover {
				background-color: #f1f3f5;
				cursor: pointer;
			}
		</style>

		<title>Social Media Planner</title>

	</head>

	<body>

		<form onsubmit="return validateForm()" action="AddPostServlet" method="POST">

			<label>Choose Social Media Platform</label> <select name="Platform">
				<option value="linkedin">Linkedin</option>
				<option value="naukri">Naukri</option>
				<option value="twitter">Twitter</option>
				<option value="instagram">Instagram</option>
				<option value="youtube">Youtube</option>
			</select> <br> <label>Caption:</label>
			<textarea name="caption" rows="4" cols="40"></textarea>

			<br> <label>Schedule Date:</label> <input type="date" name="schedule_date"></input> <br>

			<button type="submit">Submit</button>

		</form>

		<h2 style="text-align: center; color: #333; margin-top: 20px;">Scheduled
			Posts</h2>

		<table>

			<tr>

				<th>Id</th>
				<th>Platform</th>
				<th>Caption</th>
				<th>Schedule Date</th>
				<th>Action</th>

			</tr>

			<% try{ Class.forName("com.mysql.cj.jdbc.Driver"); Connection
				con=DriverManager.getConnection("jdbc:mysql://localhost:3306/post_planner","YOUR_DB_ID","YOUR_DB_PASSWORD"); Statement
				st=con.createStatement(); ResultSet rs=st.executeQuery("select * from posts"); while(rs.next()){ %>

				<tr>

					<td>
						<%= rs.getInt("id") %>
					</td>
					<td>
						<%= rs.getString("platform") %>
					</td>
					<td>
						<%= rs.getString("caption") %>
					</td>
					<td>
						<%= rs.getString("scheduled_date") %>
					</td>
					<td>
					    <a href="DeletePostServlet?id=<%= rs.getInt("id") %>" 
					       style="color: red; font-weight: bold; text-decoration: none;" 
					       class="delete-btn"
					       onclick="return confirm('Are you sure you want to delete this?');">
					       Delete
					    </a>
					</td>
				</tr>
				<% } }catch(Exception e){ e.printStackTrace(); } %>

		</table>

		<script>

			function validateForm() {

				var cap = document.getElementsByName("caption")[0].value;

				if (cap == "") {

					alert("Please enter a caption!");

					return false;

				}

				var todaysDate = new Date();

				todaysDate.setHours(0, 0, 0, 0);

				var dateOfUser = new Date(document.getElementsByName("schedule_date")[0].value);

				if (dateOfUser < todaysDate) {

					alert("You cannot schedule a post in the past!");

					return false;
				}
				return true;
			}

		</script>

	</body>

	</html>
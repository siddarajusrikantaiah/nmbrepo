<%@ page import="java.security.Principal" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.logging.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Uploader</title>
</head>
<body>

	<form action="/Nmbservice.html">
		<table align="center" style="top:300px">
			<tr>
				<td>
				
						<input type="submit" value="Upload File">
										
				</td>
			</tr>
			<tr>
				<td>
				<%
				 	UserService userService = UserServiceFactory.getUserService();
					String requestUri = request.getRequestURI();
					Principal userPrincipal = request.getUserPrincipal();
				 	if (userPrincipal == null) {
				       String loginLink = userService.createLoginURL(requestUri); %>
				       <a href="<%= loginLink %>">Sign In</a></p>  <%
				    }else {
					String logoutLink = userService.createLogoutURL(requestUri);   
				    %> <a href="<%= logoutLink %>">Log Out</a></p> <% 
				    }
				 %>				
				</td>
			</tr>
		</table>

	</form>

</body>
</html>
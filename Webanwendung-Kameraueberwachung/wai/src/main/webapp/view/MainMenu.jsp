<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.*, java.util.*" %>

<%
    // Hole das User-Objekt aus dem Request
    User user = (User) request.getAttribute("user");
String role = "";
ArrayList<String> locations = new ArrayList<>();


if(user!=null){
   locations = user.getStandorte();
   role = user.getRolle();
}
%>

<html>
<head>
    <title>Standortübersicht</title>
</head>
<body>
    <h2>Standorte von Rolle: <%= role %></h2>

    <table border="1">
        <tr>
            <th>Standortname</th>
            <th>Rolle</th>
        </tr>
        <%
            for (String loc : locations) {
        %>
            <tr>
                <td><%= loc%></td>
                <td><%= role %></td>
            </tr>
        <%
            }
        %>
    </table>
</body>
</html>

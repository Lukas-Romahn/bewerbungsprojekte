<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Benutzer Hinzufügen</title>
    <style>
        .local {
            font-family: Arial, sans-serif;
            background-color: #f1f1f1;
            padding: 40px;
        }

        .local h1 {
            color: #333;
            margin-bottom: 20px;
        }

        .local #mainForm {
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            max-width: 600px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .local label {
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
            margin-top: 20px;
        }

        .local input[type="text"],
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        .local .checkbox-container {
            border: 1px solid #ccc;
            padding: 10px;
            width: 100%;
            height: 200px;
            overflow-y: auto;
            background-color: #f9f9f9;
            border-radius: 8px;
        }

        .local .auswahl-label {
            display: block;
            padding: 8px;
            margin: 5px 0;
            background-color: #eee;
            border: 1px solid #ccc;
            border-radius: 5px;
            cursor: pointer;
        }

        .local input[type="checkbox"]:checked + .auswahl-label {
            background-color: #cce5ff;
            border-color: #66b0ff;
        }

        .local input[type="checkbox"] {
            display: none;
        }

        .local button[type="submit"] {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 20px;
        }

        .local button[type="submit"]:hover {
            background-color: #45a049;
        }

        .local #standorte {
            display: block;
        }
        .local  #cancelBtn{
        background-color: #dc3545;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 20px;
        }
        .local #cancelForm{
        }
    </style>

    <script>
        window.addEventListener("DOMContentLoaded", function () {
            const rollenDropdown = document.getElementById("Rollen");
            const standorteDiv = document.getElementById("standorte");

            rollenDropdown.addEventListener("change", function () {
                const rolle = this.value.toLowerCase();
                if (rolle === "admin" || rolle === "chef") {
                    standorteDiv.style.display = "none";
                } else {
                    standorteDiv.style.display = "block";
                }
            });
        });
    </script>
</head>
<body>
<jsp:include page="/view/Navbar.jsp"></jsp:include>
<div class="local">
<h1>Benutzer hinzufügen</h1>

<form method="post" id="mainForm">
    <label for="username">Benutzername</label>
    <input type="text" name="username" id="username">

    <label for="password">Passwort</label>
    <input type="text" name="password" id="password">

    <label for="Rollen">Rolle auswählen</label>
    <select id="Rollen" name="rollen">
        <%
            List<String> rollen = (List<String>) request.getAttribute("rollen");
            for (String s : rollen) {
        %>
            <option value="<%=s%>"><%=s%></option>
        <% } %>
    </select>

    <div id="standorte">
        <label for="standorte">Standorte auswählen</label>
        <div class="checkbox-container">
            <%
                ArrayList<String> std = (ArrayList<String>) request.getAttribute("standorte");
                for (String s : std) {
            %>
                <input type="checkbox" id="<%=s%>" name="standorte" value="<%=s%>"/>
                <label class="auswahl-label" for="<%=s%>"><%=s%></label>
            <% } %>
        </div>
    </div>

    <button type="submit" name="aktion" value="userAdded">Hinzufügen</button>
</form>
<form method="post" action="/wai/admin/userManager" id="cancelForm">
	<button id="cancelBtn" type="submit" name="aktion" value="cancel">Abbrechen</button>

</form>

</div>
</body>
</html>

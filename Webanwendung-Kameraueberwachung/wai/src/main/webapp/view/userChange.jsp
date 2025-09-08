<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Benutzer bearbeiten</title>
    <style>
    	body{
    	            background-color: #f1f1f1;
    	}
    
        .userEdit {
            font-family: Arial, sans-serif;
            background-color: #f1f1f1;
            padding: 40px;
        }

        .userEdit h1 {
            color: #333;
            margin-bottom: 20px;
        }

        .userEdit .localForm {
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            max-width: 600px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .userEdit label {
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
            margin-top: 20px;
        }

        .userEdit input[type="text"],
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        .userEdit .checkbox-container {
            border: 1px solid #ccc;
            padding: 10px;
            width: 100%;
            height: 200px;
            overflow-y: auto;
            background-color: #f9f9f9;
            border-radius: 8px;
        }

        .userEdit .auswahl-label {
            display: block;
            padding: 8px;
            margin: 5px 0;
            background-color: #eee;
            border: 1px solid #ccc;
            border-radius: 5px;
            cursor: pointer;
        }

        .userEdit input[type="checkbox"]:checked + .auswahl-label {
            background-color: #cce5ff;
            border-color: #66b0ff;
        }

        .userEdit input[type="checkbox"] {
            display: none;
        }

       .userEdit  button[type="submit"] {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 20px;
        }

       .userEdit button[type="submit"]:hover {
            background-color: #0056b3;
        }

        .userEdit #standorte {
            display: block;
        }
        .userEdit #cancelBtn{
        background-color: #dc3545;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 20px;
        }
    </style>
    

   <script>
    window.addEventListener("DOMContentLoaded", function () {
        const rollenDropdown = document.getElementById("Rollen");
        const standorteDiv = document.getElementById("standorte");

        const selectedRolle = "<%= request.getAttribute("selectedRolle") %>";

        rollenDropdown.value = selectedRolle;

        // Sichtbarkeit direkt beim Laden setzen
        const rolle = selectedRolle.toLowerCase();
        if (rolle === "admin" || rolle === "chef") {
            standorteDiv.style.display = "none";
        } else {
            standorteDiv.style.display = "block";
        }

        // Für spätere Benutzeränderungen
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
<div class="userEdit">

<h1>Benutzer <%= request.getAttribute("username") %> bearbeiten</h1>

<form method="post" class="localForm">
    <input type="hidden" name="userid" value="<%= request.getAttribute("userid") %>" />

    <label for="Rollen">Rolle auswählen</label>
    <select id="Rollen" name="rollen">
        <%
            List<String> rollen = (List<String>) request.getAttribute("rollen");
            for (String rolle : rollen) {
        %>
            <option value="<%= rolle %>"><%= rolle %></option>
        <% } %>
    </select>

    <div id="standorte">
        <label for="standorte">Standorte auswählen</label>
        <div class="checkbox-container">
            <%
                ArrayList<String> alleStandorte = (ArrayList<String>) request.getAttribute("standorte");
                ArrayList<String> selected = (ArrayList<String>) request.getAttribute("selectedStandorte");
                for (String s : alleStandorte) {
                    boolean checked = selected != null && selected.contains(s);
            %>
                <input type="checkbox" id="<%= s %>" name="standorte" value="<%= s %>" <%= checked ? "checked" : "" %> />
                <label class="auswahl-label" for="<%= s %>"><%= s %></label>
            <% } %>
        </div>
    </div>

    <button type="submit" name="aktion" value="userEdited">Änderung speichern</button>
</form>
<form method="post" action="/wai/admin/userManager" >
	<button id="cancelBtn" type="submit" name="aktion" value="cancel">Abbrechen</button>

</form>

</div>

</body>
</html>

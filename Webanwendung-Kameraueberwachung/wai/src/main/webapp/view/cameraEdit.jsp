<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Camera" %>

<!DOCTYPE html>
<html>
<head>
<jsp:include page="/view/Navbar.jsp"></jsp:include>

    <meta charset="UTF-8">
    <title>Benutzer bearbeiten</title>
    <style>
    
    	body {
    	
    	        background-color: #f1f1f1;
    	
    	}
    
        .cameraEdit {
            font-family: Arial, sans-serif;
            background-color: #f1f1f1;
            padding: 40px;
        }

       .cameraEdit h1 {
            color: #333;
            margin-bottom: 20px;
        }

        .cameraEdit .localForm {
            background-color: #fff;
            padding: 30px;
            border-radius: 12px;
            max-width: 600px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .cameraEdit label {
            font-weight: bold;
            display: block;
            margin-bottom: 8px;
            margin-top: 20px;
        }

        .cameraEdit input[type="text"],
        select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 6px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        .cameraEdit .checkbox-container {
            border: 1px solid #ccc;
            padding: 10px;
            width: 100%;
            height: 200px;
            overflow-y: auto;
            background-color: #f9f9f9;
            border-radius: 8px;
        }

        .cameraEdit .auswahl-label {
            display: block;
            padding: 8px;
            margin: 5px 0;
            background-color: #eee;
            border: 1px solid #ccc;
            border-radius: 5px;
            cursor: pointer;
        }

        .cameraEdit input[type="checkbox"]:checked + .auswahl-label {
            background-color: #cce5ff;
            border-color: #66b0ff;
        }

        .cameraEdit input[type="checkbox"] {
            display: none;
        }

        .cameraEdit .speichern{
            background-color: #007bff;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 20px;
        }

        .cameraEdit button[type="submit"]:hover {
            background-color: #0056b3;
        }

        .cameraEdit #standorte {
            display: block;
        }
        
        .cameraEdit .btns{
        	display:flex;
			align-items: center;
			gap:1rem;
			        
        }
        .cameraEdit #cancelBtn{
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
        const kid = "<%= request.getAttribute("kid") %>";
        const name = "<%= (request.getAttribute("name") + "").replace("\"", "\\\"") %>";
        const domain = "<%= (request.getAttribute("domain") + "").replace("\"", "\\\"") %>";

        // setze Werte ins Formular
        document.getElementById("name").value = name;
        document.getElementById("domain").value = domain;

        document.getElementById("submitBtn").addEventListener("click", function (e) {
            e.preventDefault(); // verhindert Standardverhalten

            const form = document.createElement('form');
            form.class = 'localForm'
            form.method = 'POST';
            form.action = '/wai/admin/cameraManager';

            const inputAktion = document.createElement('input');
            inputAktion.type = 'hidden';
            inputAktion.name = 'aktion';
            inputAktion.value = 'edited';
            form.appendChild(inputAktion);

            const cameraData = {
                kid: kid,
                name: document.getElementById("name").value,
                domain: document.getElementById("domain").value,
                location: document.getElementById("Standort").value,
                status: document.getElementById("Status").value,
            };

            const jsonData = document.createElement('input');
            jsonData.type = 'hidden';
            jsonData.name = 'camera';
            jsonData.value = JSON.stringify(cameraData);
            form.appendChild(jsonData);

            document.body.appendChild(form);
            form.submit();
        });
    });
</script>


</head>
<body>
<div class="cameraEdit">

<h1>Kamera <%= request.getAttribute("name") %> bearbeiten</h1>


   <label for="name">Name</label>
  <input type="text" name="name" id="name" value="<%= request.getAttribute("name") %>">

    <label for="domain">Domain</label>
<input type="text" name="domain" id="domain" value="<%= request.getAttribute("domain") %>">




    <label for="Standort">Standort auswählen</label>
    <select id="Standort" name="Standort">
        <%
            List<String> Standorte = (List<String>) request.getAttribute("Standorte");
            for (String standort : Standorte) {
        %>
<option value="<%= standort %>" <%= standort.equals(request.getAttribute("location")) ? "selected" : "" %>><%= standort %></option>
        <% } %>
    </select>
    
    
    <label for="Status">Status auswählen</label>
    <select id="Status" name="Status">
        <%
            List<String> Stati = (List<String>) request.getAttribute("Stati");
            for (String status : Stati) {
        %>
<option value="<%= status %>" <%= status.equals(request.getAttribute("status")) ? "selected" : "" %>><%= status %></option>
        <% } %>
    </select>

    
	<div class="btns">
    	<button id="submitBtn" type="button" class="speichern">Änderung speichern</button>
    	
    	<form method="post" action="/wai/admin/cameraManager">
    		<button id="cancelBtn" type="submit" name="aktion" value="cancel" >Abbrechen</button>
    	</form>
    </div>
</div>
</body>
</html>

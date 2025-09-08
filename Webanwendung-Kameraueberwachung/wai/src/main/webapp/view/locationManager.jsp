<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList" %>
<%@ include file="./components/notifier.html" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/location.css">
<title>Standortverwaltung</title>

<style>
    /* Einfaches Styling */
    .Standort-block { margin-bottom: 10px; }
    #editFormContainer { margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px; }
    
    .modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  font-family: sans-serif;
}

.modal-content {
  background: #fff;
  padding: 24px 32px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0,0,0,0.2);
  max-width: 400px;
}

.modal-content p {
  font-size: 18px;
  margin-bottom: 24px;
  color: #333;
}

.modal-content button {
  padding: 10px 20px;
  margin: 0 8px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  color: #fff;
  transition: background-color 0.2s ease;
}

/* Ja-Button: Rot */
#confirmYes {
  background-color: #e53935;
}

#confirmYes:hover {
  background-color: #c62828;
}

/* Nein-Button: Blau */
#confirmNo {
  background-color: #1e88e5;
}

#confirmNo:hover {
  background-color: #1565c0;
}
</style>



</head>

<div id="confirmModal" class="modal" style="display:none;">
  <div class="modal-content">
    <p id="confirmText"></p>
    <button id="confirmYes">Ja</button>
    <button id="confirmNo">Nein</button>
  </div>
</div>


<body>
<jsp:include page="/view/Navbar.jsp"></jsp:include>

<div class="page-a-wrapper">


<h1>Standortliste</h1>

<%
ArrayList<String> standorte = new ArrayList<>();
if (request.getAttribute("standorte") != null) {
    standorte = (ArrayList<String>) request.getAttribute("standorte");
}
String errorMessage = (String) request.getAttribute("error");
%>



<!-- Formular für Hinzufügen und Bearbeiten -->
<div id="editFormContainer">
    <h2 id="formTitle">Neuen Standort hinzufügen</h2>

    <form method="post" action="/wai/admin/locationManager" id="locationForm">
        <input type="hidden" name="aktion" value="locationadd" id="aktionInput">
        <input type="hidden" name="oldName" id="oldNameInput" value="">

        <label for="nameInput">Name:</label>
        <input type="text" id="nameInput" name="name" required>

        <button type="submit" id="submitBtn">Hinzufügen</button>
        <button type="button" onclick="resetForm()">Abbrechen</button>
    </form>

    	<% if (errorMessage != null) { %>
        	<p style="color:red;"><%= errorMessage %></p>
    	<% } %>
	</div>
	<div id="standortContainer">
	
    <% for (String s : standorte) { %>
        <div class="Standort-block">
            <span><%= s %></span>
            <div class="button-group">

            <button type="button" onclick="editLocation('<%= s %>')">Bearbeiten</button>

            <form class="deleteForm" method="post" action="/wai/admin/locationManager" style="display:inline;">
                <input type="hidden" name="_method" value="DELETE">
                <input type="hidden" name="aktion" value="locationdelete">
                <input type="hidden" name="name" value="<%= s %>">
                <button class="deletebtn" type="submit">Löschen</button>
            </form>
            </div>
        </div>
    <% } %>

	</div>
	
</div>
<div id="notification" class="notification hidden"></div>

<script>

let deleteFormToSubmit = null; // globale Variable zum Merken des aktiven Formulars

//Modal-Elemente
const confirmModal = document.getElementById("confirmModal");
const confirmText = document.getElementById("confirmText");
const confirmYes = document.getElementById("confirmYes");
const confirmNo = document.getElementById("confirmNo");

//Alle Delete-Formulare abfangen
document.querySelectorAll(".deleteForm").forEach(form => {
 form.addEventListener("submit", function (e) {
     e.preventDefault(); // Standard-Submit verhindern
     deleteFormToSubmit = form; // das aktuelle Formular merken
     confirmText.innerText = "Möchtest du diesen Standort wirklich löschen?";
     confirmModal.style.display = "flex";
 });
});

async function sleep (ms) { return new Promise(resolve => setTimeout(resolve, ms))}
document.getElementById("locationForm").addEventListener("submit", async function(event) {
      event.preventDefault();
      showNotification(document.getElementById("locationForm").dataset.notificationText);
      await sleep(200);
      this.submit();

})

//Modal-Buttons
confirmYes.addEventListener("click", async function () {
 if (deleteFormToSubmit) {
    showNotification("Standort gelöscht");;
    await sleep(200);
    deleteFormToSubmit.submit(); // Formular absenden
    deleteFormToSubmit = null;
 }
 confirmModal.style.display = "none"; });

confirmNo.addEventListener("click", function () {
 deleteFormToSubmit = null;
 confirmModal.style.display = "none";
});

<% String error="null";
if(request.getAttribute("error")!=null) {
	error=request.getAttribute("error").toString();
}

%>
const error="<%=error%>"
if(error!=="null"){
	showNotification(error,"error");
}


function editLocation(name) {
	document.getElementById('formTitle').innerText = 'Standort bearbeiten';
    document.getElementById('aktionInput').value = 'locationedit';
    document.getElementById('oldNameInput').value = name;
    document.getElementById('nameInput').value = name;
    document.getElementById('submitBtn').innerText = 'Speichern';
    document.getElementById("locationForm").dataset.notificationText = "Standort geändert";
}


function resetForm() {
    document.getElementById('formTitle').innerText = 'Neuen Standort hinzufügen';
    document.getElementById('aktionInput').value = 'locationadd';
    document.getElementById('oldNameInput').value = '';
    document.getElementById('nameInput').value = '';
    document.getElementById('submitBtn').innerText = 'Hinzufügen';
    document.getElementById("locationForm").dataset.notificationText = "Standort hinzugefügt";
}
      
window.onload = function () {
  resetForm();
}
      
</script>

</body>
</html>

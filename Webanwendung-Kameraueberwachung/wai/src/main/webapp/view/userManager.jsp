<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Personenliste</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userManager.css">
    <style>
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
   <script>
   
   

   function showConfirm(message) {
       return new Promise((resolve) => {
           const modal = document.getElementById('confirmModal');
           const confirmText = document.getElementById('confirmText');
           const yesButton = document.getElementById('confirmYes');
           const noButton = document.getElementById('confirmNo');

           confirmText.textContent = message;
           modal.style.display = 'flex';

           const cleanup = () => {
               modal.style.display = 'none';
               yesButton.removeEventListener('click', onYes);
               noButton.removeEventListener('click', onNo);
           };

           const onYes = () => {
               cleanup();
               resolve(true);
           };

           const onNo = () => {
               cleanup();
               resolve(false);
           };

           yesButton.addEventListener('click', onYes);
           noButton.addEventListener('click', onNo);
       });
   }
   
   let allePersonen = [];

   async function deleteUser(userId, username) {
	   const confirmed = await showConfirm(`Möchtest du ${username} wirklich löschen?`);
		if (!confirmed) return;

       fetch('/wai/admin/userManager?userId=' + encodeURIComponent(userId), {
           method: 'DELETE'
       })
       .then(response => {
           if (response.ok) {
               showNotification(`Benutzer ${username} wurde gelöscht.`);
   			allePersonen = allePersonen.filter(p => p.Id !== userId);
   			 renderPersonenliste(allePersonen);
           } else {
               showNotification(`Fehler beim Löschen von ${username}.`, true);
           }
       })
       .catch(error => {
           showNotification(`Fehler beim Löschen von ${username}.`, true);
       });
   }

   function editUser(userId) {
       const form = document.createElement('form');
       form.method = 'POST';
       form.action = '/wai/admin/userManager';

       const inputAktion = document.createElement('input');
       inputAktion.type = 'hidden';
       inputAktion.name = 'aktion';
       inputAktion.value = 'edit';
       form.appendChild(inputAktion);

       const inputUserId = document.createElement('input');
       inputUserId.type = 'hidden';
       inputUserId.name = 'userId';
       inputUserId.value = userId;
       form.appendChild(inputUserId);

       document.body.appendChild(form);
       form.submit();
   }

   function renderPersonenliste(personen, rolleFilter = "", standortFilter = "") {
       const container = document.getElementById('personenliste');
       container.innerHTML = '';

       personen.forEach(person => {
           const rolle = person.rolle?.toLowerCase();

           if (rolleFilter && rolle !== rolleFilter) return;
           if (rolle === 'standortleiter' && standortFilter) {
               if (!person.standorte || !person.standorte.includes(standortFilter)) return;
           }

           const div = document.createElement('div');
           div.className = 'user-block';
           div.setAttribute('data-userid', person.Id);

           const name = document.createElement('p');
           name.innerHTML = `<strong>Name:</strong> ${'${'}person.username}`;
           div.appendChild(name);

           const role = document.createElement('p');
           role.innerHTML = `<strong>Rolle:</strong>  ${'${'}person.rolle}`;
           div.appendChild(role);

           if (person.standorte && person.standorte.length > 0) {
               const standortList = document.createElement('ul');
               person.standorte.forEach(ort => {
                   const li = document.createElement('li');
                   li.textContent = ort;
                   standortList.appendChild(li);
               });
               div.appendChild(standortList);
           }

           const actions = document.createElement('div');
           actions.className = 'user-actions';

           const editBtn = document.createElement('button');
           editBtn.textContent = 'Bearbeiten';
           editBtn.onclick = () => editUser(person.Id);
           actions.appendChild(editBtn);

           const deleteBtn = document.createElement('button');
           deleteBtn.textContent = 'Löschen';
           deleteBtn.onclick = () => deleteUser(person.Id, person.username);
           actions.appendChild(deleteBtn);

           div.appendChild(actions);
           container.appendChild(div);
       });
   }
  
 
window.onload=function(){
   <%
   String error="";
   if(request.getAttribute("error")!=null){
   	error=request.getAttribute("error").toString();
   }%>
   const error="<%=error%>";
   if (error!=="null") {
       showNotification(error, true);

       // Später erneut versuchen
       setTimeout(() => {
            // nochmal fetch ausführen
            fetchUsers();
       }, 3000);
       return;
   }else{
   	fetchUsers();
   }
   
document.getElementById("textSearch").addEventListener("input",function(){
	   
	   fetchUsers(this.value);
	   });
};


  function fetchUsers(name){
	  const params = new URLSearchParams();

	  params.append("info", "ajax");

	  if(name!=null){

		  params.append("name", name);
	  }
       fetch('/wai/admin/userManager?'+params.toString())
           .then(response => response.json())
           .then(data => {
   			if (!data.success) {
   			           showNotification("Fehler beim Laden der Benutzer:  Sie sind nicht im Intranet", true);
   			           return;
   			       }
               allePersonen = data.users || [];

               const rollenFilter = document.getElementById('rollenFilter');
               const standortFilterWrapper = document.getElementById('standortFilterWrapper');
               const standortFilter = document.getElementById('standortFilter');

               const standortleiter = data.users.filter(p => p.rolle?.toLowerCase() === 'standortleiter');
               const alleStandorte = new Set();

               standortleiter.forEach(p => {
                   if (Array.isArray(p.standorte)) {
                       p.standorte.forEach(ort => alleStandorte.add(ort));
                   }
               });

               standortFilter.innerHTML = '<option value="">-- Alle Standorte --</option>';
               alleStandorte.forEach(ort => {
                   const option = document.createElement('option');
                   option.value = ort;
                   option.textContent = ort;
                   standortFilter.appendChild(option);
               });

               const applyFilter = () => {
                   const rolle = rollenFilter.value;
                   const standort = standortFilter.value;
                   renderPersonenliste(allePersonen, rolle, standort);
               };

               rollenFilter.addEventListener('change', () => {
                   const rolle = rollenFilter.value;
                   if (rolle === 'standortleiter') {
                       standortFilterWrapper.style.display = 'inline';
                   } else {
                       standortFilterWrapper.style.display = 'none';
                       standortFilter.value = '';
                   }
                   applyFilter();
               });

               standortFilter.addEventListener('change', applyFilter);

               // Initial anzeigen
               renderPersonenliste(allePersonen);
           })
           .catch(error => {
   showNotification("Fehler beim Laden der Benutzer: Netzwerkfehler", true);
           });
   };
   function showNotification(message, isError = false) {
       const notification = document.getElementById('notification');
       notification.textContent = message;
       notification.className = 'notification' + (isError ? ' error' : '');
       
       // Anzeigen
       notification.classList.remove('hidden');

       // Nach 3 Sekunden ausblenden
       setTimeout(() => {
           notification.classList.add('hidden');
       }, 3000);
   };


   
   
  

   
   
   </script>

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

<div class="userManager">
<h2>Personenliste</h2>
    

<div id="filterContainer" style="margin-bottom: 20px;">
    <label for="rollenFilter"><strong>Nach Rolle filtern:</strong></label>
    <select id="rollenFilter">
        <option value="">-- Alle Rollen --</option>
        <option value="admin">Admin</option>
        <option value="chef">Chef</option>
        <option value="standortleiter">Standortleiter</option>
    </select>

    <span id="standortFilterWrapper" style="display:none; margin-left:20px;">
        <label for="standortFilter"><strong>Standort:</strong></label>
        <select id="standortFilter">
            <option value="">-- Alle Standorte --</option>
        </select>
    </span>
    <div class="filter-group">
                <label for="textSearch">Name:</label>
                <input type="text" id="textSearch" placeholder="Nutzer suchen..."/>
            </div>
</div>

<form method="post" action="/wai/admin/userManager">
    <button type="submit" name="aktion" value="userAdd" id="userAdd">Benutzer hinzufügen</button>
</form>




<div id="personenliste">
    <p>Lade Daten...</p>
</div>
</div>

<div id="notification" class="notification hidden"></div>


</body>
</html>

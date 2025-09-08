<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.Camera" %>
<%@ page isELIgnored="true" %>
<%@ include file="./components/tableItemManager.html" %>
<%@ include file="./components/optionItem.html" %>
<%@ include file="./components/notifier.html" %>
<!DOCTYPE html>


<html lang="de">

<head>
    <meta charset="UTF-8">
    <title>Kameras einsehen</title>
    <script type="text/javascript" src="./../javascript/prototype.js"></script>
	<link rel="stylesheet" href="./../css/cameraManager.css">

	
</head>
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

<div id="confirmModal" class="modal" style="display:none;">
  <div class="modal-content">
    <p id="confirmText"></p>
    <button id="confirmYes">Ja</button>
    <button id="confirmNo">Nein</button>
  </div>
</div>

<body>

<jsp:include page="/view/Navbar.jsp"></jsp:include>

<div class="table-wrapper">

    <div class="horizontal">
        <h2>Kameraliste</h2>
        <div class="filter-container">
            <div class="filter-group">
                <label for="standortFilter">Standort:</label>
                <select id="standortFilter">
                    <option value="">-- Alle Standorte --</option>
                </select>
            </div>
            
            <div class="filter-group">
                <label for="statusFilter">Status:</label>
                <select id="statusFilter">
                    <option value="">-- Alle --</option>
                    <option value="aktiv">Aktiv</option>
                    <option value="passiv">Passiv</option>
                    <option value="wartung">Wartung</option>
                </select>
            </div>
            
            <div class="filter-group">
                <label for="textSearch">Name:</label>
                <input type="text" id="textSearch" placeholder="Kamera suchen..."/>
            </div>
          
            <div class="button-group">
                <button type="button" id="toggleSelectionBtn" >Auswahlmodus aktivieren</button>
                <form method="post" action="/wai/admin/cameraManager" style="display:inline;">
                    <button type="submit" name="aktion" value="cameraAdd">Kamera hinzufügen</button>
                </form>
            </div>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>Name</th>
            <th>IP-Adresse</th>
            <th>Standort</th>
            <th>Status</th>
            <th>Aktionen</th>
            <th class="selection-column">Auswahl</th>
        </tr>
        </thead>
        <tbody id="cameraTableBody"></tbody>
    </table>

    <div class="paging">
       <button class="pagination-nav" id="previousPage" onclick="loadPage(-1)">&lt;</button>
            <span id="pageInfo"></span>
            <button class="pagination-nav" id="nextPage" onclick="loadPage(1)">&gt;</button>
    </div>

    <div style="text-align:right;">
        <button type="button" id="deleteSelectedBtn">Ausgewählte löschen</button>
    </div>
</div>

<%-- <div id="notification" class="notification">
    <h1>Test</h1>
</div>

 --%>

<script>
	let cameraName="";
    let auswahlAktiv = false;

   window.onload = () => populate();

    document.getElementById("toggleSelectionBtn").addEventListener("click", function () {
        auswahlAktiv = !auswahlAktiv;
        document.body.classList.toggle("selection-active", auswahlAktiv); // Hier wird die CSS-Klasse gesetzt
        this.classList.toggle("active", auswahlAktiv);

        this.textContent = auswahlAktiv
            ? "Auswahlmodus deaktivieren"
            : "Auswahlmodus aktivieren";
    });


    document.getElementById("deleteSelectedBtn").addEventListener("click", () => {
        const selected = document.querySelectorAll(".cameraCheckbox:checked");

        if (selected.length === 0) {
            showNotification("Keine Kameras ausgewählt", "warning");
            return;
        }
        console.log("eventlistener");

        showConfirm("Möchtest du die ausgewählten Kameras wirklich löschen, alle Bilder werden ebenfalls gelöscht?", () => {
        	  const cameras = [];
        	  selected.forEach((cb) => {
        	      cameras.push(cb.dataset.id);
        	  });

        	  const params = new URLSearchParams();
        	  cameras.forEach(id => params.append("cameras", id));

        	  new Ajax.Request("/wai/admin/cameraManager?" + params.toString(), {
        	      method: "DELETE",
        	      onSuccess: ({ responseText }) => {
        	          const response = JSON.parse(responseText);

        	          if (response.response === "success") {
        	              showNotification("Kameras gelöscht", "success");
        	              reloadCurrentPage();
        	          } else {
        	              if (response.message === "sql") {
        	                  showNotification("Sie sind nicht im Netz", "error");
        	              } else {
        	                  showNotification("Fehler beim Löschen", "error");
        	              }
        	          }
        	      },
        	      onFailure: function () {
        	          showNotification("Server nicht erreichbar", "error");
        	      }
        	  });
        	});

        const params = new URLSearchParams();
        cameras.forEach(id => params.append("cameras", id));
        new Ajax.Request("/wai/admin/cameraManager?" + params.toString(), {
            method: "DELETE",
            onSuccess: ({ responseText }) => {
                const response = JSON.parse(responseText);

                if (response.response === "success") {
                    showNotification("Kameras gelöscht", "success");
                    reloadCurrentPage();
                } else {
                    // Fehlerantwort vom Server mit message auswerten
                    if (response.message === "sql") {
                        showNotification("Sie sind nicht im Netz", "error");
                    } else {
                        showNotification("Fehler beim Löschen", "error");
                    }
                }
            },
            onFailure: function () {
                showNotification("Server nicht erreichbar", "error");
            }
        });

    });
    
    document.getElementById("standortFilter").addEventListener("change",function(){
    	updateResult();
    	
    });
    document.getElementById("statusFilter").addEventListener("change",function(){
    	updateResult();
    	
    });
    cameraName=document.getElementById("textSearch").addEventListener("input",function(){
   
		updateResult();
    });
    
    
    
    function editCamera(camera){
    	const form = document.createElement('form');
	    form.method = 'POST';
	    form.action = '/wai/admin/cameraManager';

	    const inputAktion = document.createElement('input');
	    inputAktion.type = 'hidden';
	    inputAktion.name = 'aktion';
	    inputAktion.value = 'edit';
	    form.appendChild(inputAktion);

	    const jsonData = document.createElement('input');
	    jsonData.type = 'hidden';
	    jsonData.name = 'camera';
	    jsonData.value = JSON.stringify(camera);
	    form.appendChild(jsonData);

	    document.body.appendChild(form);
	    form.submit();
			
        

    }
    function updateResult(page){
         
    	 const locationSelect=document.getElementById("standortFilter");
		 let location = locationSelect.selectedIndex ? locationSelect.options[locationSelect.selectedIndex].text : null;
         if(!locationSelect.value){
             location = null ;
          }
         const status = document.getElementById("statusFilter").value;
         
         const cameraName=document.getElementById("textSearch");

          let data = {
              name: cameraName.value,
              location: location,
              page: page,
              status:status
          }
          
          data = Object.fromEntries(Object.entries(data).filter(([_, v]) => v != null && v != ""));
          new Ajax.Request("/wai/getCameras", {
              method: 'GET',
              parameters: data,
              onSuccess: (response ) => {
                  response = JSON.parse(response.responseText);
                 
                  if(response.error){
                	  showNotification("Fehler beim Laden der Kameradaten", "error");
                	  return;
                  }
                  fillCameraTable(response);
                  // Begrenzungen vom Server verwenden
                  const totalPages = parseInt(response.maxPages, 10);
                  const current = parseInt(response.page, 10);

                  if (current > totalPages) return; // z.B. Fehlerhafte Anfrage

                  // UI aktualisieren
                  const pageInfo = document.getElementById("pageInfo");
                  pageInfo.dataset.page = current;
                  pageInfo.dataset.maxPages = totalPages;
                  pageInfo.textContent = `${current} / ${totalPages}`;

                  // Buttons korrekt aktivieren/deaktivieren
                  document.getElementById("previousPage").disabled = current <= 1;
                  document.getElementById("nextPage").disabled = current >= totalPages;
              },
              onFailure: () => {
            	  showNotification("server nicht erreichbar beim Laden der Kameradaten", "error");
              }
          });
    }

    function loadPage(page) {
        const currentPage = document.getElementById("pageInfo").dataset.page;
        updateResult(parseInt(currentPage) + parseInt(page));
    }

    function deleteCamera(cameraId) {
    	  showConfirm("Möchtest du die ausgewählten Kameras wirklich löschen, alle Bilder werden ebenfalls gelöscht?", () => {
    	    new Ajax.Request("/wai/admin/cameraManager?cameras=" + encodeURIComponent(cameraId), {
    	      method: "DELETE",
    	      onSuccess: ({ responseText }) => {
    	        const response = JSON.parse(responseText);

    	        if (response.response == "success") {
    	            showNotification("Kamera gelöscht", "success");
					populate();
    	        } else {
    	          if (response.message === "sql") {
    	            showNotification("Sie sind nicht im Netz", "error");
    	          } else {
    	            showNotification("Fehler beim Löschen", "error");
    	          }
    	        }
    	      },
    	      onFailure: () => {
    	        showNotification("Server nicht erreichbar", "error");
    	      }
    	    });
    	  });
    	}
    
    function populate(){
    	
         new Ajax.Request("/wai/search", {
            method: 'GET',
 
            onSuccess: (response) => {
                response = JSON.parse(response.responseText);
                
                if (response.error) {
                    showNotification(response.error, "error");
                    return;
                }

                populateLocation(response);
            },
            onFailure: () => {
                showNotification("Server nicht erreichbar beim Laden der Standorte", "error");
            }
        });
    };
    function populateLocation(data){
    	
    	 const locationSelect=document.getElementById("standortFilter")
    	 if(locationSelect.value != ""){return }
         locationSelect.innerHTML = '<option value="">Ort...</option>';
         data.locations.forEach((location, index) => {
             const option = document.createElement('option');
             option.value = index + 1;
             option.textContent = location;
             locationSelect.appendChild(option);
         });
         if(data.locations.length == 1){
             locationSelect.value = 1;
         }
         updateResult();
    }

    function fillCameraTable(data) {
        const tbody = document.getElementById("cameraTableBody");
        tbody.innerHTML = '';
        let cameras =data.cameras;

        cameras.forEach(cam => {
            let node = document.getElementById("tableItemManager").content.cloneNode(true);

            // Checkbox-Daten füllen
            const checkbox = node.querySelector(".cameraCheckbox");
            checkbox.dataset.name = cam.name;
            checkbox.dataset.domain = cam.domain;
            checkbox.dataset.location = cam.location;
            checkbox.dataset.status = cam.status;
            checkbox.dataset.id = cam.kid;

            // Zellen füllen
            node.querySelector("#name").textContent = cam.name;
            node.querySelector("#domain").textContent = cam.domain;
            node.querySelector("#location").textContent = cam.location;
            node.querySelector("#status").textContent = cam.status;

            // Event Listener für Button "Bearbeiten"
            const btnEdit = node.querySelector(".btn-edit");
            btnEdit.textContent="bearbeiten";
            
            btnEdit.addEventListener("click", () => {
                editCamera({
                    kid: cam.kid,
                    name: cam.name,
                    domain: cam.domain,
                    location: cam.location,
                    status: cam.status,
                    createdTimestamp: null
                });
            });

            // Event Listener für Button "Löschen"
            const btnDelete = node.querySelector(".btn-delete");
            btnDelete.textContent="löschen";
            btnDelete.addEventListener("click", () => {
                deleteCamera(cam.kid);
            });

            tbody.appendChild(node);
        });
    }
    
    function showConfirm(message, onConfirm) {
    	  const modal = document.getElementById("confirmModal");
    	  const confirmText = document.getElementById("confirmText");
    	  const yesBtn = document.getElementById("confirmYes");
    	  const noBtn = document.getElementById("confirmNo");

    	  confirmText.textContent = message;
    	  modal.style.display = "flex";

    	  // Vorherige Eventlistener entfernen
    	  const newYesBtn = yesBtn.cloneNode(true);
    	  yesBtn.parentNode.replaceChild(newYesBtn, yesBtn);

    	  newYesBtn.addEventListener("click", () => {
    	    modal.style.display = "none";
    	    onConfirm();
    	  });

    	  noBtn.onclick = () => {
    	    modal.style.display = "none";
    	  };
    	}
    
    function reloadCurrentPage() {
    	  const currentPage = document.getElementById("pageInfo").dataset.page;
    	  updateResult(currentPage);
    	}
    

</script>

</body>
</html>

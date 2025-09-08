<!DOCTYPE html>
<html lang="de">
<head>
<jsp:include page="/view/NavbarNormal.jsp"></jsp:include>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="text/javascript" src="./javascript/prototype.js"></script>
    <%@ page isELIgnored="true" %>
    <%@ include file="./components/pictureTableItem.html" %>
    <title>Inkrementeller Date Selektor</title>
    <style>
    body{
                background-color: #f5f5f5;
    
    }
        .local {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        
         .local .date-selector {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
         .local .selector-group {
            margin-top: 10px;
            margin-bottom: 10px;
        }
        
         .local label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        
         .local select, input{
            width: 100%;
            padding: 12px;
            font-size: 16px;
            border: 2px solid #ddd;
            border-radius: 8px;
            background-color: white;
            transition: all 0.3s ease;
            box-sizing: border-box;
            font-family: inherit;
        }
        
         .local select:disabled {
            background-color: #f8f8f8;
            color: #999;
            border-color: #eee;
            cursor: not-allowed;
        }
        
         .local select:focus {
            border-color: #4CAF50;
            outline: none;
        }
        
         .local select:enabled:hover {
            border-color: #999;
        }
        
         .local .result {
            margin-top: 30px;
            padding: 20px;
            background-color: #e8f5e8;
            border-radius: 8px;
            border-left: 4px solid #4CAF50;
        }
        
         .local .horizontal {
            display: flex;
            gap: 10px;
        }
         .local .horizontal > * {
            flex: 1;
        }
        .local  .table-wrapper {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
            position: relative;
        }

         .local .table-container {
            background: #e8f5e8;
            border-left: 4px solid #4CAF50;
            padding: 15px;
        }

         .local table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

         .local th, td {
            border: 1px solid #666;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
        }

         .local th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }

        .local  td {
            background-color: white;
        }

         .local .image-cell {
            width: 80px;
            text-align: center;
        }

         .local .image-cell img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 4px;
        }

        .local  .paging{
            display: flex;
            justify-content: flex-end;
            align-items: center;
            margin-top: 12px;
            gap: 8px;
            font-family: inherit;
        }

         .local .pagination-nav {
            background-color: #f8f9fa;
            border: 1px solid #ddd;
            padding: 6px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            color: #555;
            text-decoration: none;
            transition: all 0.2s;
            min-width: 35px;
            text-align: center;
        }

         .local .pagination-nav:hover {
            background-color: #e9ecef;
            border-color: #adb5bd;
            color: #333;
        }

         .local .pagination-nav:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .local  .page-info {
            font-size: 14px;
            color: #666;
            margin: 0 12px;
            font-weight: normal;
            padding: 6px 0;
        }

        .local  .status-active {
            color: #28a745;
            font-weight: 500;
        }
        .local .filter-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            background: white;
            border-radius: 8px 8px 0 0;
            border-bottom: 1px solid #e0e0e0;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .local  .filter-header:hover {
            background-color: #f8f9fa;
        }

       .local   .filter-header h3 {
            margin: 0;
            color: #333;
            font-size: 16px;
            font-weight: 600;
        }

         .local .collapse-btn {
            background: none;
            border: none;
            font-size: 18px;
            color: #666;
            cursor: pointer;
            transition: transform 0.3s ease;
            padding: 0;
            min-width: 20px;
        }

         .local .collapse-btn.collapsed {
            transform: rotate(-90deg);
        }

         .local .filter-content {
            max-height: 200px;
            overflow: hidden;
            transition: max-height 0.3s ease;
            background: white;
            border-radius: 0 0 8px 8px;
            place-items: center;
        }

         .local .filter-content.collapsed {
            max-height: 0;
        }
    </style>
</head>
<body>
	<div class="local">
    <div class="date-selector">
        <h2>Homepage</h2>
        
        <div class="horizontal">
            <div class="selector-group">
                <label for="selectLocation">Ort:</label>
                <select id="selectLocation" >
                    <option value="">Ort...</option>
                </select>
            </div>
            <div class="selector-group">
                <label for="selectName">Name:</label>
                <input type="text" id="textSearch"/>
            </div>
        </div>
        
        <div class="filter-header" onclick="toggleFilters()">
            <h3>Erweiterte Filter</h3>
            <button class="collapse-btn" id="collapse-btn">&#9650</button>
        </div>

        <div class="horizontal filter-content" id="filter-content">

            <div class="selector-group">
                <label for="selectStatus">Status:</label>
                <select id="selectStatus" enabled>
                    <option value="">Status...</option>
                    <option value="">Aktiv</option>
                    <option value="">Passiv</option>
                    <option value="">Wartung</optrion>
                </select>
            </div>
            
            <div class="selector-group">
                <label for="selectYear">Jahr:</label>
                <select id="selectYear" disabled>
                    <option value="">Jahr...</option>
                </select>
            </div>
            
            <div class="selector-group">
                <label for="selectMonth">Monat:</label>
                <select id="selectMonth" disabled>
                    <option value="">Monat...</option>
                </select>
            </div>
            
            <div class="selector-group">
                <label for="selectDay">Tag:</label>
                <select id="selectDay" disabled>
                    <option value="">Tag...</option>
                </select>
            </div>
        </div>
            
        <div class="result" id="result" style="display: none;">
            <table border = "1">
                <thead>
                <tr>
                    <th>Kamera</th>
                    <th>Standort</th>
                    <th>Status</th>
                    <th>Image</th>
                </tr> 
                </thead>
                <tbody id="cameraTableBody">
                </tbody>
            </table>
        </div>
        <div class="paging">
            <button class="pagination-nav" id="previousPage" onclick="loadPage(-1)">&lt;</button>
            <span id="pageInfo"></span>
            <button class="pagination-nav" id="nextPage" onclick="loadPage(1)">&gt;</button>
        </div>
    </div>
</div>
    <script>
        
        class IncrementalDateSelector {
            constructor() {
                this.cameraName = document.getElementById('textSearch');
                this.locationSelect = document.getElementById('selectLocation');
                this.yearSelect = document.getElementById('selectYear');
                this.monthSelect = document.getElementById('selectMonth');
                this.daySelect = document.getElementById('selectDay');
                this.resultDiv = document.getElementById('result');
                this.selectedDateP = document.getElementById('selectedDate');
                this.statusSelect = document.getElementById('selectStatus');
               this.from = "";
               this.to = "";
            }
            
            init() {
                this.populate({})
                this.addEventListeners();
            }
            
            populateLocation(data){
               if(this.locationSelect.value != ""){return }
                this.locationSelect.innerHTML = '<option value="">Ort...</option>';
                data.locations.forEach((location, index) => {
                    const option = document.createElement('option');
                    option.value = index + 1;
                    option.textContent = location;
                    this.locationSelect.appendChild(option);
                });
                if(data.locations.length == 1){
                    this.locationSelect.value = 1;
                }
                this.updateResult();
            }
            populateYears(data) {
               if(this.yearSelect.value != ""){return }
                this.yearSelect.innerHTML = '<option value="">Jahr...</option>';
                let years = data.timestamps.map(v => new Date(v).getFullYear());
                years = [...new Set(years)];

                years.forEach((timestamp, index) => {
                    const option = document.createElement('option');
                    option.value = index + 1;
                    option.textContent = timestamp;
                    this.yearSelect.appendChild(option);
                });
            }
            
            populateMonths(data) {
               if(this.monthSelect.value != ""){return }
                // Monat-Selector leeren
                this.monthSelect.innerHTML = '<option value="">Monat...</option>';
                
                let months = data.timestamps.map(v => new Date(v).getMonth() + 1);
                months = [...new Set(months)];

                months.forEach((timestamp, index) => {
                    const option = document.createElement('option');
                    option.value = index + 1;
                    option.textContent = timestamp;
                    this.monthSelect.appendChild(option);
                });
            }
            
            populateDays(data) {
               if(this.daySelect.value != ""){return }
                // Tag-Selector leeren
                this.daySelect.innerHTML = '<option value="">Tag...</option>';
                let days = data.timestamps.map(v => new Date(v).getDate());
                days= [...new Set(days)];
                
                days.forEach((timestamp, index) => {
                    const option = document.createElement('option');
                    option.value = index + 1;
                    option.textContent = timestamp;
                    this.daySelect.appendChild(option);
                });
            }
            
            addEventListeners() {
                this.statusSelect.addEventListener('change', () => {
                    this.updateResult();
                });
                this.cameraName.addEventListener('input', () => {
                    this.updateResult();
                });
                this.locationSelect.addEventListener('change', () => {
                    const selectedLocation = this.locationSelect.value;
                    const selectedLocationText = this.locationSelect.options[this.locationSelect.selectedIndex].text;
                    
                    if(selectedLocation){
                        
                        this.yearSelect.disabled = false;
                        this.yearSelect.innerHTML = '<option value="">Jahr...</option>';
                        this.populate()
                    }else{
                        
                        this.from = "";
                        this.to = "";
                        this.yearSelect.disabled = true
                        this.yearSelect.innerHTML = '<option value="">Jahr...</option>';
                    }

                    this.monthSelect.disabled = true;
                    this.daySelect.disabled = true;
                    this.monthSelect.innerHTML = '<option value="">Monat...</option>';
                    this.daySelect.innerHTML = '<option value="">Tag...</option>';
                    this.updateResult();

                });
                this.yearSelect.addEventListener('change', () => {
                    const selectedYear = this.yearSelect.value;
                    const selectedYearText = this.yearSelect.options[this.yearSelect.selectedIndex].text;                    

                    if (selectedYear) {
                        // Monat-Selector aktivieren und befüllen
                        this.monthSelect.disabled = false;
                        this.monthSelect.innerHTML = '<option value="">Monat...</option>';
                        this.from = selectedYearText;
                        this.to = Number(selectedYearText) + 1 ;
                        this.populate();
                        
                    }else{

                        this.from = "";
                        this.to = ""
                        // Beide nachfolgenden Selektoren deaktivieren
                        this.monthSelect.disabled = true;
                        this.daySelect.disabled = true;
                        this.monthSelect.innerHTML = '<option value="">Monat...</option>';
                        this.daySelect.innerHTML = '<option value="">Tag...</option>';
                    }
                        this.daySelect.disabled = true;
                        this.daySelect.innerHTML = '<option value="">Tag...</option>';
                    
                    this.updateResult();
                });
                
                this.monthSelect.addEventListener('change', () => {
                    const selectedMonth = this.monthSelect.value;
                    const selectedYear = this.yearSelect.value;
                    const selectedYearText = parseInt(this.yearSelect.options[this.yearSelect.selectedIndex].text);
                    const selectedMonthText = parseInt(this.monthSelect.options[this.monthSelect.selectedIndex].text);
                    
                    if (selectedYear && selectedMonth) {
                        // Tag-Selector aktivieren und befüllen
                        this.daySelect.disabled = false;
                        this.from = selectedYearText + "-" + selectedMonthText.toString("10").padStart(2, "0")
                        this.to = selectedYearText + Math.floor((selectedMonthText+1)/12) + "-" + ((selectedMonthText+1)%12).toString("10").padStart(2, "0");
                        this.populate();
                    } else{
                        this.from = selectedYearText;
                        this.to = selectedYearText+1;
                        this.daySelect.disabled = true;
                    }
                        this.daySelect.innerHTML = '<option value="">Tag...</option>';
                    
                    this.updateResult();
                });
                
                this.daySelect.addEventListener('change', () => {
                    const selectedYear = this.yearSelect.value;
                    const selectedMonth= this.monthSelect.value;
                    const selectedDay = this.daySelect.value;
                    const selectedYearText = parseInt(this.yearSelect.options[this.yearSelect.selectedIndex].text);
                    const selectedMonthText = parseInt(this.monthSelect.options[this.monthSelect.selectedIndex].text);
                    if (selectedYear && selectedMonth && selectedDay) {
                        
                        const selectedDayText= parseInt(this.daySelect.options[this.daySelect.selectedIndex].text);
                        this.from = selectedYearText + "-" + selectedMonthText.toString("10").padStart(2, "0") + "-" + selectedDayText.toString("10").padStart(2, "0");
                        this.to = selectedYearText + Math.floor((selectedMonthText)/12) + "-" + (selectedMonthText%12 + Math.floor((selectedDayText+1)/30)).toString("10").padStart(2, "0") + "-" + ((selectedDayText+1) % 30).toString("10").padStart(2, "0");
                    }else {
                        this.from = selectedYearText + "-" + selectedMonthText.toString("10").padStart(2, "0");
                        this.to = selectedYearText + Math.floor((selectedMonthText)/12) + "-" + ((selectedMonthText+1)%12).toString("10").padStart(2, "0");
                    }
                    
                    this.updateResult();
                });
                                                            
            }
            
            updateResult(page) {
                let location = this.locationSelect.options[this.locationSelect.selectedIndex].text ? this.locationSelect.options[this.locationSelect.selectedIndex].text : null;
                let status = this.statusSelect.options[this.statusSelect.selectedIndex].text ? this.statusSelect.options[this.statusSelect.selectedIndex].text : null;

                if(!this.locationSelect.value){
                   location = null ;
                }

                if(!this.statusSelect.value){
                   status = null ;
                }
                let data = {
                    name: this.cameraName.value,
                    location: this.locationSelect.selectedIndex ? this.locationSelect.options[this.locationSelect.selectedIndex].text : null,
                    from: this.from,
                    to: this.to,
                    page: page,
                    status: this.statusSelect.selectedIndex ? this.statusSelect.options[this.statusSelect.selectedIndex].text.toLowerCase() : null,
                }
                
                data = Object.fromEntries(Object.entries(data).filter(([_, v]) => v != null && v != ""));
                new Ajax.Request("/wai/getCameras", {
                    method: 'GET',
                    parameters: data,
                    onSuccess: (response ) => {
                        response = JSON.parse(response.responseText);
                        if(response.cameras.length > 0){
                            this.resultDiv.style.display= "block";
                        }else {
                            this.resultDiv.style.display = "none";
                        }
                        this.populateDevices(response);
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
                    }
                });
                
            }
            async populate(){
                let data = {
                    location: this.locationSelect.selectedIndex ? this.locationSelect.options[this.locationSelect.selectedIndex].text : null,
                    from: this.from,
                    to: this.to,
                }

                data = Object.fromEntries(Object.entries(data).filter(([_, v]) => v != null && v != ""));
                await new Ajax.Request("/wai/search", {
                    method: 'GET',
                    parameters: data,
                    onSuccess: (response) => {
                        response = JSON.parse(response.responseText);
                        console.log(this);
                        this.populateLocation(response);
                        this.populateYears(response);
                        this.populateMonths(response);
                        this.populateDays(response);
                    }
                });
             }
            populateDevices(response){
                const tbody = document.getElementById("cameraTableBody");
                
                tbody.innerHTML = '';
                response.cameras.forEach(device => {
                    console.log(device);
                    let node = document.getElementById("PictureTableItem").content.cloneNode(true);

                    node.querySelector("#location").textContent = device.location;
                    node.querySelector("#kamera").textContent = device.name;
                    node.querySelector("#status").textContent = device.status;
                    node.querySelector("#image").src = 'data:image/png;base64,' + response?.Images[device.kid];
                    node.querySelector("#image").width = 100;
                    const item = node.querySelector("tr");
                    item.dataset.id = device.kid;
                    item.addEventListener('click', function(){
                        const searchparams = new URLSearchParams();
                        searchparams.append("kid", this.dataset.id);
                        searchparams.append("kName", this.querySelector("#kamera").textContent);
                      
                        const yearSelect = document.getElementById("selectYear");
                        searchparams.append("year", yearSelect.selectedIndex > 0 ? yearSelect.options[yearSelect.selectedIndex].text : null);

                        const monthSelect = document.getElementById("selectMonth");
                        searchparams.append("month", monthSelect.selectedIndex > 0 ? monthSelect.options[monthSelect.selectedIndex].text : null);

                        const daySelect = document.getElementById("selectDay");
                        searchparams.append("day", daySelect.selectedIndex > 0 ? daySelect.options[daySelect.selectedIndex].text : null);

                        window.location.href = "/wai/gallery?" + searchparams.toString();
                    });
                    tbody.appendChild(node);
                });
            }
        }

        const selector = new IncrementalDateSelector();
        selector.init();
        let filtersCollapsed = true;
        document.getElementById("filter-content").classList.add('collapsed');
        
        function loadPage(page){
            const currentPage = document.getElementById("pageInfo").dataset.page;
            selector.updateResult(parseInt(currentPage) + parseInt(page));
        }

        function toggleFilters() {
           const filterContent = document.getElementById('filter-content');
           const collapseBtn = document.getElementById('collapse-btn');
           
           filtersCollapsed = !filtersCollapsed;
           
           if (filtersCollapsed) {
               filterContent.classList.add('collapsed');
               collapseBtn.classList.add('collapsed');
               collapseBtn.textContent = "\u25bc";
           } else {
               filterContent.classList.remove('collapsed');
               collapseBtn.classList.remove('collapsed');
               collapseBtn.textContent = "\u25bc";
           }
       }
    </script>
</body>
</html>
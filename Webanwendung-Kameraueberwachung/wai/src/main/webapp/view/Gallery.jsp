<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page isELIgnored="true"%>
<!DOCTYPE html>
<html lang="de">

<head>
<jsp:include page="/view/NavbarNormal.jsp"></jsp:include>

    <meta charset="UTF-8">
    <script type="text/javascript" src="./javascript/prototype.js"></script>
    <title>Bildergalerie</title>
    <style>
    	body{
    	            background-color: #f5f5f5;
    	
    	}
    
        .local {
            font-family: Arial, sans-serif;
            max-width: 1200px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }

        .local .filter-section {
            background: white;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .local .horizontal {
            display:flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
        }

        .local #kamera {
            display: flex;
            gap: 10px;
        }
        .local select {
            width: 150px;
            padding: 8px;
            margin-right: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }

        .local .gallery {
            display: flex;
			justify-content: center;
            flex-wrap: wrap;
            gap: 10px;
        }

        .local .thumbnail {
            width: 120px;
            height: 90px;
            object-fit: cover;
            cursor: pointer;
            border-radius: 5px;
            box-shadow: 0 1px 5px rgba(0,0,0,0.2);
        }

        .local .modal {
            display: none;
            position: fixed;
            z-index: 99;
            left: 0; top: 0;
            width: 100%; height: 100%;
            background: rgba(0,0,0,0.8);
            justify-content: center;
            align-items: center;
        }

.local .modal img {
	max-width: 90%;
	max-height: 90%;
	border-radius: 10px;
} 

.image-container {
	display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.15);
  padding: 10px;
  overflow: hidden;
}

.image-container img {
	border-radius: 5px;
  width: 100%;
  height: 120px;
  object-fit: cover;
  cursor: pointer;
}

.image-container .caption {
	margin-top: 8px;
  font-size: 0.85rem;
  color: #555;
  text-align: center;
  min-height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}
        .paging{
            display: flex;
            justify-content: flex-end;
            align-items: center;
            margin-top: 12px;
            gap: 8px;
            font-family: inherit;
        }

        .pagination-nav {
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

        .pagination-nav:hover {
            background-color: #e9ecef;
            border-color: #adb5bd;
            color: #333;
        }

        .pagination-nav:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .page-info {
            font-size: 14px;
            color: #666;
            margin: 0 12px;
            font-weight: normal;
            padding: 6px 0;
        }
</style>
</head>
<body>
<div class="local">
<div class="filter-section">
    <div class="horizontal">
        <h2>Bildergalerie</h2>
        <div class="horizontal" id="kamera" > 
            <h2>Kamera:</h2>
            <p id="kameraName"></p>
        </div>
    </div>

		<label for="yearSelect">Jahr:</label> <select id="yearSelect" disabled>
			<option value="">Jahr...</option>
		</select> <label for="monthSelect">Monat:</label> <select id="monthSelect"
			disabled>
			<option value="">Monat...</option>
		</select> <label for="daySelect">Tag:</label> <select id="daySelect" disabled>
			<option value="">Tag...</option>
		</select> <label for="fromHourSelect">Von Stunde:</label> <select
			id="fromHourSelect" disabled>
			<option value="">Von...</option>
		</select> <label for="toHourSelect">Bis Stunde:</label> <select
			id="toHourSelect" disabled>
			<option value="">Bis...</option>
		</select>
	</div>

	<div class="gallery" id="gallery"></div>

	<div class="modal" id="imageModal" onclick="this.style.display='none'">
		<img id="modalImage" src="" alt="Großansicht">
	</div>

<div class="paging">
    <button class="pagination-nav" id="previousPage" onclick="loadPage(-1)">&lt;</button>
    <span id="pageInfo"></span>
    <button class="pagination-nav" id="nextPage" onclick="loadPage(1)">&gt;</button>
</div>
</div>
</div>

	<script>
    class GalleryDateSelector {
        constructor(data) {

            this.yearSelect = document.getElementById("yearSelect");
            this.monthSelect = document.getElementById("monthSelect");
            this.daySelect = document.getElementById("daySelect");
            this.galleryDiv = document.getElementById("gallery");
            this.fromHourSelect = document.getElementById("fromHourSelect");
            this.toHourSelect = document.getElementById("toHourSelect");

            this.location = this.getQueryParam("location");
            this.camera = this.getQueryParam("kid");
            this.year = this.getQueryParam("year");
            this.month = this.getQueryParam("month");
            this.day = this.getQueryParam("day");
            
         // Disable initially (kann auch direkt true gesetzt werden)
            this.yearSelect.disabled = false;
            this.monthSelect.disabled = true;
            this.daySelect.disabled = true;
            this.fromHourSelect.disabled = true;
            this.toHourSelect.disabled = true;
            
            this.pageSelect = document.getElementById("pageInfo");
            this.addEventListeners();
            
         // Aktivieren je nachdem, ob year/month gesetzt sind
            if (this.year) {
                this.monthSelect.disabled = false;
            }
            if (this.month) {
                this.daySelect.disabled = false;
            }
            
			this.populateWithDataFromConstructor();
        }
									
		async populateWithDataFromConstructor(){
	         // Einmaliger Aufruf mit URL-Parametern, dabei month und day ggf. mit führenden Nullen
        	await this.populate({
                location: this.location,
                camera: this.camera,
                year: this.year,
                month: this.month ? this.month.toString().padStart(2, '0') : null,
                day: this.day ? this.day.toString().padStart(2, '0') : null,
            }, true);
									
			console.log("HALLO WELT");
			 // Jahr aus URL setzen
			if (this.year) {
				for (let i = 0; i < this.yearSelect.options.length; i++) {
					if (this.yearSelect.options[i].text === this.year) {
						this.yearSelect.selectedIndex = i;
						this.monthSelect.disabled = false;
						break;
					}else{
						this.monthSelect.disabled = true;
						this.daySelect.disabled = true;
					}
				}
			}
      
			// Monat aus URL setzen
			if (this.month) {
			  for (let i = 0; i < this.monthSelect.options.length; i++) {
				if (this.monthSelect.options[i].text === this.month.padStart(2, "0")) {
				  this.monthSelect.selectedIndex = i;
				  this.daySelect.disabled = false;
				  break;
				}else{
					this.daySelect.disabled = true;
				}
			  }
			}

			// Tag aus URL setzen
			if (this.day) {
			  for (let i = 0; i < this.daySelect.options.length; i++) {
				if (this.daySelect.options[i].text === this.day.padStart(2, "0")) {
				  this.daySelect.selectedIndex = i;
				  break;
				}
			  }
			}
		}

        getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(param);
        }

	  addEventListeners() {

		  this.yearSelect.addEventListener('change', () => {
		    var year = this.yearSelect.value;
		    if (year) {

		      this.monthSelect.disabled = false;
		    } else {

		      this.monthSelect.disabled = true;
		    }
		    this.daySelect.disabled = true;
		    this.monthSelect.innerHTML = '<option value="">Monat...</option>';
		    this.daySelect.innerHTML = '<option value="">Tag...</option>';
		      this.populate();
		  });

		  this.monthSelect.addEventListener('change', ()=> {
		    var year = this.yearSelect.value;
		    var month = this.monthSelect.value;
		    if (year && month) {
		      this.daySelect.disabled = false;
		    } else if (year) {
		      this.daySelect.disabled = true;
		    }
		      this.daySelect.innerHTML = '<option value="">Tag...</option>';
		      this.populate();
		  });

		  this.daySelect.addEventListener('change', () => {
			  var daySelected = this.daySelect.value !== "";
	          this.fromHourSelect.disabled = !daySelected;
	          this.toHourSelect.disabled = !daySelected;
			  this.resetHourFilters();
	          this.populate();
		  });
		  
		  this.fromHourSelect.addEventListener('change', () => this.populate());
		  this.toHourSelect.addEventListener('change', () => this.populate());
		}
	  resetDropdown(select, placeholder) {
		  select.innerHTML = `<option value="">${placeholder}</option>`;
	  }
	  
	  resetHourFilters() {
	        this.resetDropdown(this.fromHourSelect, "Von...");
	        this.resetDropdown(this.toHourSelect, "Bis...");
	  }

	  async populate(data, populateAll) {
        let params = {
            location : this.location,
            camera : this.camera,
            year: this.yearSelect.value ? this.yearSelect.options[this.yearSelect.selectedIndex].text : null,
            month: this.monthSelect.value ? this.monthSelect.options[this.monthSelect.selectedIndex].text : null,
            day: this.daySelect.value ?  this.daySelect.options[this.daySelect.selectedIndex].text : null,
            fromHour: this.fromHourSelect.value ? parseInt(this.fromHourSelect.options[this.fromHourSelect.selectedIndex].text) : null,
            toHour: this.toHourSelect.value ? parseInt(this.toHourSelect.options[this.toHourSelect.selectedIndex].text) : null,
            page: this.pageSelect.dataset.page
        }
                                    
        if(data != null){
            params = data;
        }
        
      params = Object.fromEntries(Object.entries(params).filter(([_, v]) => v != null && v != "" && v != "null"));
		  
		  const searchParams = new URLSearchParams();
		  for(const [key, value] of Object.entries(params)){
			searchParams.append(key, value);
		  }
		
		  if(populateAll === true){

			await fetch("/wai/getPictures?" + searchParams)
			.then(response => response.json())
			.then(response => {

				 this.resetDropdown(this.yearSelect, "Jahr...");
				 response.years.forEach((year, index) => {
				   const option = document.createElement("option");
				   option.value = index + 1
				   option.textContent = year;
				   this.yearSelect.appendChild(option);
				 });
		
				 this.resetDropdown(this.monthSelect, "Monat...");
				 response.months.forEach((month, index) => {
				   const option = document.createElement("option");
				   option.value = index + 1;
				   option.textContent = month;
				   this.monthSelect.appendChild(option);
				 })


				 this.resetDropdown(this.daySelect, "Tag...");
				 response.days.forEach((day, index) => {
				   const option = document.createElement("option");
				   option.value = index + 1;
				   option.textContent = day;
				   this.daySelect.appendChild(option);
				 });
					
				if (this.daySelect.selectedIndex > 0 && this.fromHourSelect.selectedIndex == 0 && this.toHourSelect.selectedIndex == 0) {
						this.populateHourFilters(response.hours);
				}
					this.populateGallery(response.images);
					updatePageInfo(response.page, response.maxPages);
				});
		  }else {

			await fetch("/wai/getPictures?" + searchParams)
			.then(response => response.json())
			.then(response => {
				this.populateFilters(response.years, response.months, response.days, response.hours);
				this.populateGallery(response.images);
				updatePageInfo(response.page, response.maxPages);
			});
                                  
		  }
            
	  }

	  populateFilters(years, months, days, hours) {
	    // hier z.B. Jahr-Select aktualisieren, falls noch leer
	    if (this.yearSelect.options.length <= 1) {
	      this.resetDropdown(this.yearSelect, "Jahr...");
	      years.forEach((year, index) => {
	        const option = document.createElement("option");
          option.value = index + 1
          option.textContent = year;
	        this.yearSelect.appendChild(option);
	      });
	    }
	
	 // Monat-Select
	    if (this.yearSelect.selectedIndex > 0 && this.monthSelect.options.length <= 1) {
	      this.resetDropdown(this.monthSelect, "Monat...");
	      months.forEach((month, index) => {
	        const option = document.createElement("option");
	        option.value = index + 1;
	        option.textContent = month;
	        this.monthSelect.appendChild(option);
	      });
	    }


	    // Tag-Select
	    if (this.yearSelect.selectedIndex > 0 && this.monthSelect.selectedIndex > 0 && this.daySelect.options.length <= 1) {
	      this.resetDropdown(this.daySelect, "Tag...");
	      days.forEach((day, index) => {
	        const option = document.createElement("option");
	        option.value = index + 1;
	        option.textContent = day;
	        this.daySelect.appendChild(option);
	      });
	    }

		if (this.daySelect.selectedIndex > 0 && this.fromHourSelect.selectedIndex == 0 && this.toHourSelect.selectedIndex == 0) {
			this.populateHourFilters(hours);
		}
	}
	  
	populateHourFilters(hours) {

		
		this.resetDropdown(this.fromHourSelect, "Von...");
		this.resetDropdown(this.toHourSelect, "Bis...");

		 // Stundenliste sortieren
		 const sortedHours = hours.sort((a, b) => a - b)

		 sortedHours.forEach(hour => {
		const label = hour.toString().padStart(2, '0') + ":00"

		const fromOption = document.createElement("option");
		fromOption.value = hour;
		fromOption.textContent = label;
		this.fromHourSelect.appendChild(fromOption)

		const toOption = document.createElement("option");
		toOption.value = hour;
		toOption.textContent = label;
		this.toHourSelect.appendChild(toOption);
		 })

		 this.fromHourSelect.disabled = sortedHours.length === 0;
		 this.toHourSelect.disabled = sortedHours.length === 0

		 // Listener anpassen: Beim Ändern von "Von" mögliche "Bis" Optionen filtern
		 this.fromHourSelect.addEventListener('change', () => {
		const fromValue = parseInt(this.fromHourSelect.value);
		this.resetDropdown(this.toHourSelect, "Bis...")

		sortedHours.forEach(hour => {
			 if (hour >= fromValue) {
			const label = hour.toString().padStart(2, '0') + ":00";
			const option = document.createElement("option");
			option.value = hour;
			option.textContent = label;
			this.toHourSelect.appendChild(option);
			}
		});
	});
	}
	  populateGallery(images) {
		  this.galleryDiv.innerHTML = '';

		  images.forEach(img => {
		    const date = new Date(img.timestamp);
		    const hour = date.getHours();

		    const container = document.createElement('div');
		    container.className = 'image-container';

		    const imgElem = document.createElement('img');
		    imgElem.src = 'data:image/png;base64,' + img.full;
		    imgElem.addEventListener('click', () => {
		      const modal = document.getElementById("imageModal");
		      const modalImage = document.getElementById("modalImage");
		      modalImage.src = imgElem.src;
		      modal.style.display = "flex";
		    });

		    const captionElem = document.createElement('div');
		    captionElem.className = 'caption';
		    captionElem.textContent = "Aufgenommen am: " + date.toLocaleString();

		    container.appendChild(imgElem);
		    container.appendChild(captionElem);

		    this.galleryDiv.appendChild(container);
		  });

		}
    }

    document.addEventListener('DOMContentLoaded', () => {
        const searchparams = new URLSearchParams(window.location.search);
        const cameraId = searchparams.get("kid");
        const cameraName = searchparams.get("kName");
        
        if(cameraName != null && cameraId != null){
            document.getElementById("kamera").style.display = "block";
            document.getElementById("kameraName").textContent = cameraName;
        }else {
            
            document.getElementById("kamera").style.display = "none";
        }
    });

    const selector = new GalleryDateSelector();
    function loadPage(page){
        document.getElementById("pageInfo").dataset.page = parseInt(document.getElementById("pageInfo").dataset.page) + page;
        selector.populate();
    }
	
	function updatePageInfo(page, maxPages){
          // Begrenzungen vom Server verwenden
		const pageSelect = document.getElementById("pageInfo");
        const totalPages = parseInt(maxPages, 10);
        const current = parseInt(page, 10);

        if (current > totalPages) return; // z.B. Fehlerhafte Anfrage

        // UI aktualisieren
        pageSelect.dataset.page = current;
        pageSelect.dataset.maxPages = totalPages;
        pageSelect.textContent = `${current} / ${totalPages}`;

        // Buttons korrekt aktivieren/deaktivieren
        document.getElementById("previousPage").disabled = current <= 1;
        document.getElementById("nextPage").disabled = current >= totalPages;
	}
</script>
</body>
</html>
let allePersonen = [];

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
        name.innerHTML = `<strong>Name:</strong> ${person.username}`;
        div.appendChild(name);

        const role = document.createElement('p');
        role.innerHTML = `<strong>Rolle:</strong> ${person.rolle}`;
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


window.onload = function () {
    fetch('/wai/admin/userManager?info=ajax')
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


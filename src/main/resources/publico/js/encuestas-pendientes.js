
// Función para crear contenedores de registros
function loadPendingRecordsDashboard() {
    localforage.getItem('surveyData').then(students => {
        const container = document.getElementById('pendingStudentsList');
        container.innerHTML = ''; // Limpiar el contenedor

        (students || []).forEach((student, index) => {
            // Crear un contenedor para cada registro
            const encuestaContainer = document.createElement('div');
            encuestaContainer.className = 'encuesta-container';

            // Contenido del registro
            encuestaContainer.innerHTML = `
                <h3>${student.nombre}</h3>
                <p><strong>Sector:</strong> ${student.sector}</p>
                <p><strong>Nivel Escolar:</strong> ${student.nivelEscolar}</p>
                <div>
                    <button onclick="sendPendingStudent(${index})" class="btn btn-success btn-sm">
                        <i class="fas fa-cloud-upload-alt"></i> Enviar
                    </button>
                    <button onclick="editPendingStudent(${index})" class="btn btn-warning btn-sm">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button onclick="deletePendingStudent(${index})" class="btn btn-danger btn-sm">
                        <i class="fas fa-trash"></i> Eliminar
                    </button>
                </div>
            `;

            // Agregar el contenedor al listado
            container.appendChild(encuestaContainer);
        });
    });
}

function sendPendingStudent(index) {
    localforage.getItem('surveyData').then(records => {
        records = records || [];
        const student = records[index];
        return fetch('/encuestas/crear', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams(student)
        }).then(response => {
            if (response.ok) {
                records.splice(index, 1);
                return localforage.setItem('surveyData', records);
            } else {
                alert("Error al enviar registro: " + response.statusText);
            }
        });
    }).then(() => window.location.reload());
}



function sendAllPendingStudents() {
    localforage.getItem('surveyData').then(students => {
        if (!students || students.length === 0) {
            alert("No hay encuestas pendientes para enviar.");
            return;
        }

        // Función recursiva para enviar los registros uno por uno
        const sendNextStudent = (index) => {
            if (index >= students.length) {
                alert("Todas las encuestas han sido enviadas correctamente.");
                window.location.reload(); // Recargar la página para actualizar la lista
                return;
            }

            const student = students[index];
            fetch('/encuestas/crear', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: new URLSearchParams(student)
            }).then(response => {
                if (response.ok) {
                    // Si el envío es exitoso, eliminar el registro de la lista local
                    students.splice(index, 1);
                    localforage.setItem('surveyData', students).then(() => {
                        // Enviar el siguiente registro
                        sendNextStudent(index);
                    });
                } else {
                    alert(`Error al enviar el registro de ${student.nombre}: ${response.statusText}`);
                    // Continuar con el siguiente registro incluso si hay un error
                    sendNextStudent(index + 1);
                }
            }).catch(error => {
                alert(`Error de red al enviar el registro de ${student.nombre}: ${error.message}`);
                // Continuar con el siguiente registro incluso si hay un error
                sendNextStudent(index + 1);
            });
        };

        // Comenzar a enviar los registros desde el primero
        sendNextStudent(0);
    });
}

function deletePendingStudent(index) {
    localforage.getItem('surveyData').then(records => {
        records = records || [];
        records.splice(index, 1);
        return localforage.setItem('surveyData', records);
    }).then(() => loadPendingRecordsDashboard());
}

function editPendingStudent(index) {
    window.location.href = "/Encuesta/Pendientes/editar/" + index;
}

window.addEventListener('load', loadPendingRecordsDashboard);
const API_BASE_URL = 'http://localhost:8080/api';

// Verificar autenticación al cargar
window.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacion();
    cargarListaCargues();
    configurarDragAndDrop();
});

function verificarAutenticacion() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

function cerrarSesion() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Configurar drag and drop
function configurarDragAndDrop() {
    const fileLabel = document.getElementById('fileLabel');

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        fileLabel.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        fileLabel.addEventListener(eventName, () => {
            fileLabel.classList.add('drag-over');
        }, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        fileLabel.addEventListener(eventName, () => {
            fileLabel.classList.remove('drag-over');
        }, false);
    });

    fileLabel.addEventListener('drop', (e) => {
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            document.getElementById('fileInput').files = files;
            archivoSeleccionado();
        }
    }, false);
}

// Cuando se selecciona un archivo
function archivoSeleccionado() {
    const fileInput = document.getElementById('fileInput');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    const btnSubir = document.getElementById('btnSubir');

    if (fileInput.files.length > 0) {
        const file = fileInput.files[0];
        fileName.textContent = `${file.name} (${formatBytes(file.size)})`;
        fileInfo.classList.add('show');
        btnSubir.disabled = false;
    } else {
        fileInfo.classList.remove('show');
        btnSubir.disabled = true;
    }
}

// Formatear tamaño de archivo
function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// Limpiar selección
function limpiarSeleccion() {
    document.getElementById('fileInput').value = '';
    document.getElementById('fileInfo').classList.remove('show');
    document.getElementById('btnSubir').disabled = true;
    document.getElementById('resultadoSection').classList.add('hidden');
    document.getElementById('errorMessage').classList.add('hidden');
}

// Subir archivo
async function subirArchivo() {
    if (!verificarAutenticacion()) return;

    const fileInput = document.getElementById('fileInput');

    if (fileInput.files.length === 0) {
        mostrarError('Por favor selecciona un archivo');
        return;
    }

    const file = fileInput.files[0];

    // Validar extensión
    if (!file.name.endsWith('.xlsx') && !file.name.endsWith('.xls')) {
        mostrarError('El archivo debe ser de tipo Excel (.xlsx o .xls)');
        return;
    }

    // Validar tamaño (10MB máximo)
    if (file.size > 10 * 1024 * 1024) {
        mostrarError('El archivo no debe superar los 10MB');
        return;
    }

    mostrarLoading(true);
    ocultarError();

    const formData = new FormData();
    formData.append('file', file);

    const token = localStorage.getItem('token');
    const apikey = localStorage.getItem('apikey');

    try {
        const response = await fetch(`${API_BASE_URL}/cargue/archivo`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-API-KEY': apikey
            },
            body: formData
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.mensaje || error.error || 'Error al procesar archivo');
        }

        const resultado = await response.json();
        mostrarResultado(resultado);
        cargarListaCargues();

    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarLoading(false);
    }
}

// Mostrar resultado del cargue
function mostrarResultado(resultado) {
    const resultadoSection = document.getElementById('resultadoSection');
    const resultadoContent = document.getElementById('resultadoContent');

    const html = `
        <div class="resultado-card">
            <div class="resultado-header">
                <h3>✅ Cargue Completado</h3>
                <span class="estado-badge estado-procesado">ID: ${resultado.idCargue}</span>
            </div>

            <p style="margin-bottom: 20px; color: #666;">
                📅 Fecha: ${new Date(resultado.fechaCargue).toLocaleString('es-CO')}
            </p>

            <div class="stats-grid">
                <div class="stat-item">
                    <div class="stat-number">${resultado.totalRegistros}</div>
                    <div class="stat-label">Total</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number" style="color: #28a745;">${resultado.registrosProcesados}</div>
                    <div class="stat-label">✅ Procesados</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number" style="color: #dc3545;">${resultado.registrosConError}</div>
                    <div class="stat-label">❌ Con Errores</div>
                </div>
            </div>

            <div style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                <strong>📝 Mensaje:</strong> ${resultado.mensaje}
            </div>

            <button onclick="verDetallesCargue(${resultado.idCargue})" class="btn-primary" style="margin-top: 20px;">
                🔍 Ver Detalles del Cargue
            </button>
        </div>
    `;

    resultadoContent.innerHTML = html;
    resultadoSection.classList.remove('hidden');
}

// Cargar lista de cargues
async function cargarListaCargues() {
    if (!verificarAutenticacion()) return;

    const token = localStorage.getItem('token');
    const apikey = localStorage.getItem('apikey');

    try {
        const response = await fetch(`${API_BASE_URL}/cargue/listar`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-API-KEY': apikey
            }
        });

        if (!response.ok) throw new Error('Error al cargar lista de cargues');

        const data = await response.json();
        mostrarListaCargues(data.cargues);

    } catch (error) {
        console.error('Error:', error);
    }
}

// Mostrar lista de cargues
function mostrarListaCargues(cargues) {
    const listaCargues = document.getElementById('listaCargues');

    if (cargues.length === 0) {
        listaCargues.innerHTML = '<p style="color: #666;">No hay cargues realizados aún</p>';
        return;
    }

    let html = '<div style="display: flex; flex-direction: column; gap: 10px;">';

    cargues.forEach(idCargue => {
        html += `
            <div style="display: flex; justify-content: space-between; align-items: center; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                <div>
                    <strong>ID Cargue:</strong> ${idCargue}
                    <br>
                    <small style="color: #666;">Fecha: ${formatearIdCargue(idCargue)}</small>
                </div>
                <button onclick="verDetallesCargue(${idCargue})" class="btn-secondary">
                    🔍 Ver Detalles
                </button>
            </div>
        `;
    });

    html += '</div>';
    listaCargues.innerHTML = html;
}

// Formatear ID de cargue a fecha legible
function formatearIdCargue(idCargue) {
    const str = idCargue.toString();
    const year = str.substring(0, 4);
    const month = str.substring(4, 6);
    const day = str.substring(6, 8);
    const hour = str.substring(8, 10);
    const minute = str.substring(10, 12);

    return `${day}/${month}/${year} ${hour}:${minute}`;
}

// Ver detalles de un cargue
async function verDetallesCargue(idCargue) {
    if (!verificarAutenticacion()) return;

    mostrarLoading(true);

    const token = localStorage.getItem('token');
    const apikey = localStorage.getItem('apikey');

    try {
        const response = await fetch(`${API_BASE_URL}/cargue/${idCargue}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'X-API-KEY': apikey
            }
        });

        if (!response.ok) throw new Error('Error al consultar cargue');

        const registros = await response.json();
        mostrarDetallesEnModal(registros, idCargue);

    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarLoading(false);
    }
}

// Mostrar detalles en modal o sección expandida
function mostrarDetallesEnModal(registros, idCargue) {
    const resultadoSection = document.getElementById('resultadoSection');
    const resultadoContent = document.getElementById('resultadoContent');

    let html = `
        <div class="resultado-card">
            <div class="resultado-header">
                <h3>📊 Detalles del Cargue ${idCargue}</h3>
                <span class="estado-badge estado-procesado">Total: ${registros.length} registros</span>
            </div>

            <div style="overflow-x: auto;">
                <table class="tabla-registros">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Conductor</th>
                            <th>Vehículo</th>
                            <th>Código Ruta</th>
                            <th>Ubicación</th>
                            <th>Orden</th>
                            <th>Estado</th>
                            <th>Observación</th>
                        </tr>
                    </thead>
                    <tbody>
    `;

    registros.forEach(reg => {
        const estadoClass = `estado-${reg.estado.toLowerCase()}`;
        html += `
            <tr>
                <td>${reg.id}</td>
                <td>${reg.conductorId}</td>
                <td>${reg.vehiculoId}</td>
                <td>${reg.codigoRuta}</td>
                <td>${reg.ubicacion}</td>
                <td>${reg.ordenParada}</td>
                <td><span class="estado-badge ${estadoClass}">${reg.estado}</span></td>
                <td class="observacion-cell">${reg.observacion || '-'}</td>
            </tr>
        `;
            });

            html += `
                            </tbody>
                        </table>
                    </div>

                    <button onclick="document.getElementById('resultadoSection').classList.add('hidden')" class="btn-secondary" style="margin-top: 20px;">
                        ✖️ Cerrar
                    </button>
                </div>
            `;

            resultadoContent.innerHTML = html;
            resultadoSection.classList.remove('hidden');
            resultadoSection.scrollIntoView({ behavior: 'smooth' });
        }

        // Mostrar/ocultar loading
        function mostrarLoading(mostrar) {
            const loading = document.getElementById('loading');
            if (mostrar) {
                loading.classList.remove('hidden');
            } else {
                loading.classList.add('hidden');
            }
        }

        // Mostrar error
        function mostrarError(mensaje) {
            const errorDiv = document.getElementById('errorMessage');
            errorDiv.textContent = `❌ ${mensaje}`;
            errorDiv.classList.remove('hidden');

            setTimeout(() => {
                errorDiv.classList.add('hidden');
            }, 5000);
        }

        // Ocultar error
        function ocultarError() {
            const errorDiv = document.getElementById('errorMessage');
            errorDiv.classList.add('hidden');
        }

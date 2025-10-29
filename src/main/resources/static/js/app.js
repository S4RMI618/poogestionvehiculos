// Configuración de la API
const API_BASE_URL = 'http://localhost:8080/api';

// Variables globales
let map;
let markers = [];
let polyline;

// Verificar autenticación al cargar la página
window.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacion();
    mostrarNombreUsuario();
});

// Verificar si el usuario está autenticado
function verificarAutenticacion() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// Mostrar nombre de usuario
function mostrarNombreUsuario() {
    const userName = localStorage.getItem('userName');
    if (userName) {
        const header = document.querySelector('.header');
        const userInfo = document.createElement('p');
        userInfo.style.marginTop = '10px';
        userInfo.innerHTML = `👤 Bienvenido, <strong>${userName}</strong> | <a href="#" onclick="cerrarSesion()" style="color: white; text-decoration: underline;">Cerrar Sesión</a>`;
        header.appendChild(userInfo);
    }
}

// Cerrar sesión
function cerrarSesion() {
    localStorage.removeItem('token');
    localStorage.removeItem('apikey');
    localStorage.removeItem('login');
    localStorage.removeItem('userName');
    window.location.href = '/login.html';
}

// Función principal para buscar ruta
async function buscarRuta() {
    if (!verificarAutenticacion()) return;

    const codigoRuta = document.getElementById('codigoRuta').value.trim();

    if (!codigoRuta) {
        mostrarError('Por favor, ingresa un código de ruta');
        return;
    }

    // Mostrar loading y ocultar contenido anterior
    mostrarLoading(true);
    ocultarSecciones();

    const token = localStorage.getItem('token');
    const apikey = localStorage.getItem('apikey');

    try {
        const response = await fetch(`${API_BASE_URL}/trayectos/ruta/${codigoRuta}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                'X-API-KEY': apikey
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('No se encontró ninguna ruta con ese código');
            } else if (response.status === 401 || response.status === 403) {
                // Token expirado o inválido
                localStorage.clear();
                window.location.href = '/login.html';
                return;
            } else {
                throw new Error('Error al buscar la ruta');
            }
        }

        const data = await response.json();
        mostrarRuta(data);

    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarLoading(false);
    }
}

// Mostrar información de la ruta
function mostrarRuta(data) {
    // Mostrar información básica
    document.getElementById('rutaCodigo').textContent = data.codigoRuta;
    document.getElementById('rutaConductor').textContent = data.conductorNombre;
    document.getElementById('rutaVehiculo').textContent = data.vehiculoPlaca;
    document.getElementById('totalParadas').textContent = data.trayectos.length;

    // Mostrar secciones
    document.getElementById('rutaInfo').classList.remove('hidden');
    document.getElementById('mapSection').classList.remove('hidden');
    document.getElementById('trayectosSection').classList.remove('hidden');

    // Mostrar trayectos en lista
    mostrarTrayectos(data.trayectos);

    // Inicializar mapa
    inicializarMapa(data.trayectos);
}

// Mostrar lista de trayectos
function mostrarTrayectos(trayectos) {
    const trayectosList = document.getElementById('trayectosList');
    trayectosList.innerHTML = '';

    trayectos.forEach((trayecto, index) => {
        const card = document.createElement('div');
        card.className = 'trayecto-card';

        const tipoParada = index === 0 ? '🏁 Inicio' :
                          index === trayectos.length - 1 ? '🏁 Final' :
                          `🚏 Parada ${index}`;

        const coordenadasHTML = trayecto.latitud && trayecto.longitud ? `
            <div class="trayecto-coordenadas">
                📍 Coordenadas: ${trayecto.latitud.toFixed(6)}, ${trayecto.longitud.toFixed(6)}
            </div>
        ` : `
            <div class="trayecto-coordenadas" style="background: rgba(255, 68, 68, 0.1);">
                ⚠️ Sin coordenadas disponibles
            </div>
        `;

        card.innerHTML = `
            <div class="trayecto-header">
                <div class="trayecto-number">${index + 1}</div>
                <div class="trayecto-ubicacion">${trayecto.ubicacion}</div>
            </div>
            <div class="trayecto-details">
                <div><strong>${tipoParada}</strong></div>
                ${coordenadasHTML}
            </div>
        `;

        trayectosList.appendChild(card);
    });
}

// Inicializar Google Maps con rutas reales
function inicializarMapa(trayectos) {
    // Filtrar trayectos con coordenadas
    const trayectosConCoordenadas = trayectos.filter(t => t.latitud && t.longitud);

    if (trayectosConCoordenadas.length === 0) {
        document.getElementById('map').innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: #f5f5f5; border-radius: 10px;">
                <p style="color: #666; font-size: 1.1rem;">⚠️ No hay coordenadas disponibles para mostrar el mapa</p>
            </div>
        `;
        return;
    }

    // Limpiar marcadores anteriores
    limpiarMapa();

    // Centro del mapa (primer trayecto)
    const primerTrayecto = trayectosConCoordenadas[0];
    const centroMapa = {
        lat: primerTrayecto.latitud,
        lng: primerTrayecto.longitud
    };

    // Crear mapa
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 13,
        center: centroMapa,
        mapTypeId: 'roadmap',
        styles: [
            {
                featureType: 'poi',
                elementType: 'labels',
                stylers: [{ visibility: 'off' }]
            }
        ]
    });

    // Crear DirectionsService y DirectionsRenderer
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
        suppressMarkers: true, // No mostrar los marcadores por defecto
        polylineOptions: {
            strokeColor: '#667eea',
            strokeOpacity: 0.8,
            strokeWeight: 5
        }
    });

    // Si hay más de una parada, calcular la ruta
    if (trayectosConCoordenadas.length > 1) {
        calcularRutaReal(trayectosConCoordenadas, directionsService, directionsRenderer);
    }

    // Crear marcadores personalizados
    trayectosConCoordenadas.forEach((trayecto, index) => {
        const position = {
            lat: trayecto.latitud,
            lng: trayecto.longitud
        };

        // Determinar el ícono según la posición
        let icon;
        let labelText;

        if (index === 0) {
            // Inicio - Verde
            icon = {
                url: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
                scaledSize: new google.maps.Size(40, 40)
            };
            labelText = '🚩';
        } else if (index === trayectosConCoordenadas.length - 1) {
            // Final - Rojo
            icon = {
                url: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
                scaledSize: new google.maps.Size(40, 40)
            };
            labelText = '🏁';
        } else {
            // Intermedio - Azul
            icon = {
                url: 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png',
                scaledSize: new google.maps.Size(35, 35)
            };
            labelText = (index + 1).toString();
        }

        const marker = new google.maps.Marker({
            position: position,
            map: map,
            title: trayecto.ubicacion,
            label: {
                text: labelText,
                color: 'white',
                fontWeight: 'bold',
                fontSize: '14px'
            },
            icon: icon,
            animation: google.maps.Animation.DROP
        });

        // Info Window con más información
        const tipoParada = index === 0 ? '🏁 Inicio de Ruta' :
                          index === trayectosConCoordenadas.length - 1 ? '🏁 Final de Ruta' :
                          `🚏 Parada Intermedia ${index}`;

        const infoWindow = new google.maps.InfoWindow({
            content: `
                <div style="padding: 15px; max-width: 300px;">
                    <h3 style="margin: 0 0 10px 0; color: #667eea; font-size: 1.1rem;">${tipoParada}</h3>
                    <div style="margin-bottom: 8px;">
                        <strong style="color: #555;">📍 Ubicación:</strong><br>
                        <span style="color: #333;">${trayecto.ubicacion}</span>
                    </div>
                    <div style="margin-bottom: 8px;">
                        <strong style="color: #555;">🗺️ Coordenadas:</strong><br>
                        <span style="color: #333; font-family: monospace;">
                            ${trayecto.latitud.toFixed(6)}, ${trayecto.longitud.toFixed(6)}
                        </span>
                    </div>
                    <div style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
                        <span style="color: #667eea; font-weight: bold;">Parada ${index + 1} de ${trayectosConCoordenadas.length}</span>
                    </div>
                </div>
            `
        });

        marker.addListener('click', () => {
            // Cerrar otros InfoWindows
            markers.forEach(m => {
                if (m.infoWindow) {
                    m.infoWindow.close();
                }
            });
            infoWindow.open(map, marker);
        });

        // Guardar el InfoWindow en el marcador
        marker.infoWindow = infoWindow;

        markers.push(marker);
    });
}

// Calcular ruta real usando Directions API
function calcularRutaReal(trayectos, directionsService, directionsRenderer) {
    if (trayectos.length < 2) return;

    // Origen (primer trayecto)
    const origen = new google.maps.LatLng(trayectos[0].latitud, trayectos[0].longitud);

    // Destino (último trayecto)
    const ultimoIndex = trayectos.length - 1;
    const destino = new google.maps.LatLng(trayectos[ultimoIndex].latitud, trayectos[ultimoIndex].longitud);

    // Waypoints (paradas intermedias)
    const waypoints = [];
    for (let i = 1; i < ultimoIndex; i++) {
        waypoints.push({
            location: new google.maps.LatLng(trayectos[i].latitud, trayectos[i].longitud),
            stopover: true
        });
    }

    // Configurar la solicitud
    const request = {
        origin: origen,
        destination: destino,
        waypoints: waypoints,
        optimizeWaypoints: false, // Mantener el orden de las paradas
        travelMode: google.maps.TravelMode.DRIVING
    };

    // Hacer la solicitud a Directions API
    directionsService.route(request, (result, status) => {
        if (status === 'OK') {
            directionsRenderer.setDirections(result);
            mostrarInstrucciones(result);

            // Obtener información de la ruta
            const route = result.routes[0];
            let distanciaTotal = 0;
            let duracionTotal = 0;

            route.legs.forEach(leg => {
                distanciaTotal += leg.distance.value; // en metros
                duracionTotal += leg.duration.value; // en segundos
            });

            // Mostrar información de la ruta
            mostrarInfoRuta(distanciaTotal, duracionTotal, route.legs.length);

            console.log('✅ Ruta calculada exitosamente');
            console.log('📏 Distancia total:', (distanciaTotal / 1000).toFixed(2), 'km');
            console.log('⏱️ Duración estimada:', Math.round(duracionTotal / 60), 'minutos');

        } else {
            console.warn('⚠️ No se pudo calcular la ruta:', status);
            console.log('Usando línea directa en su lugar');

            // Fallback: usar polilínea simple
            usarPolilineaSimple(trayectos);
        }
    });
}

// Mostrar información adicional de la ruta
function mostrarInfoRuta(distanciaMetros, duracionSegundos, numeroTramos) {
    const distanciaKm = (distanciaMetros / 1000).toFixed(2);
    const duracionMinutos = Math.round(duracionSegundos / 60);
    const horas = Math.floor(duracionMinutos / 60);
    const minutos = duracionMinutos % 60;

    let duracionTexto;
    if (horas > 0) {
        duracionTexto = `${horas}h ${minutos}min`;
    } else {
        duracionTexto = `${minutos} min`;
    }

    // Agregar la información al card de info
    const infoCard = document.querySelector('.info-grid');

    // Verificar si ya existe para no duplicar
    if (!document.getElementById('rutaDistancia')) {
        const distanciaItem = document.createElement('div');
        distanciaItem.className = 'info-item';
        distanciaItem.innerHTML = `
            <span class="label">📏 Distancia Total:</span>
            <span id="rutaDistancia" class="value">${distanciaKm} km</span>
        `;

        const duracionItem = document.createElement('div');
        duracionItem.className = 'info-item';
        duracionItem.innerHTML = `
            <span class="label">⏱️ Duración Estimada:</span>
            <span id="rutaDuracion" class="value">${duracionTexto}</span>
        `;

        infoCard.appendChild(distanciaItem);
        infoCard.appendChild(duracionItem);
    }
}

// Fallback: usar polilínea simple si falla Directions API
function usarPolilineaSimple(trayectos) {
    const path = trayectos.map(t => ({
        lat: t.latitud,
        lng: t.longitud
    }));

    polyline = new google.maps.Polyline({
        path: path,
        geodesic: true,
        strokeColor: '#667eea',
        strokeOpacity: 0.6,
        strokeWeight: 3,
        icons: [{
            icon: {
                path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
                scale: 3,
                strokeColor: '#667eea'
            },
            offset: '100%',
            repeat: '100px'
        }]
    });

    polyline.setMap(map);
}

// Limpiar mapa
function limpiarMapa() {
    markers.forEach(marker => {
        if (marker.infoWindow) {
            marker.infoWindow.close();
        }
        marker.setMap(null);
    });
    markers = [];
    if (polyline) {
        polyline.setMap(null);
        polyline = null;
    }
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

// Mostrar mensaje de error
function mostrarError(mensaje) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = `❌ ${mensaje}`;
    errorDiv.classList.remove('hidden');

    setTimeout(() => {
        errorDiv.classList.add('hidden');
    }, 5000);
}

// Ocultar todas las secciones de contenido
function ocultarSecciones() {
    document.getElementById('rutaInfo').classList.add('hidden');
    document.getElementById('mapSection').classList.add('hidden');
    document.getElementById('trayectosSection').classList.add('hidden');
    document.getElementById('errorMessage').classList.add('hidden');
}

// Mostrar instrucciones paso a paso
function mostrarInstrucciones(result) {
    const panelInstrucciones = document.getElementById('panelInstrucciones');
    const instruccionesLista = document.getElementById('instruccionesLista');

    if (!result || !result.routes || !result.routes[0]) return;

    instruccionesLista.innerHTML = '';
    panelInstrucciones.classList.remove('hidden');

    const route = result.routes[0];
    let pasoNumero = 1;

    route.legs.forEach((leg, legIndex) => {
        leg.steps.forEach((step, stepIndex) => {
            const instruccion = document.createElement('div');
            instruccion.className = 'instruccion-item';
            instruccion.innerHTML = `
                <strong>Paso ${pasoNumero}:</strong> ${step.instructions}
                <br><small>${step.distance.text} - ${step.duration.text}</small>
            `;
            instruccionesLista.appendChild(instruccion);
            pasoNumero++;
        });
    });
}

// Event listener para Enter en el input
document.getElementById('codigoRuta').addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        buscarRuta();
    }
});
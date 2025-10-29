const API_BASE_URL = 'http://localhost:8080/api';

// Event listener para el formulario
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    await iniciarSesion();
});

// Función de login
async function iniciarSesion() {
    const login = document.getElementById('login').value.trim();
    const password = document.getElementById('password').value.trim();
    const apikey = document.getElementById('apikey').value.trim();

    if (!login || !password || !apikey) {
        mostrarError('Todos los campos son obligatorios');
        return;
    }

    // Mostrar loading
    mostrarLoading(true);
    ocultarError();

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                login: login,
                password: password,
                apikey: apikey
            })
        });

        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Credenciales incorrectas');
            } else {
                throw new Error('Error al iniciar sesión');
            }
        }

        const data = await response.json();

        // Guardar datos en localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('apikey', data.apikey);
        localStorage.setItem('login', data.login);
        localStorage.setItem('userName', data.persona.nombres + ' ' + data.persona.apellidos);

        // Redirigir a la página principal
        window.location.href = '/index.html';

    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarLoading(false);
    }
}

// Mostrar/ocultar loading
function mostrarLoading(mostrar) {
    const btnText = document.getElementById('btnText');
    const btnLoader = document.getElementById('btnLoader');
    const btnLogin = document.querySelector('.btn-login');

    if (mostrar) {
        btnText.classList.add('hidden');
        btnLoader.classList.remove('hidden');
        btnLogin.disabled = true;
    } else {
        btnText.classList.remove('hidden');
        btnLoader.classList.add('hidden');
        btnLogin.disabled = false;
    }
}

// Mostrar error
function mostrarError(mensaje) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = `❌ ${mensaje}`;
    errorDiv.classList.remove('hidden');
}

// Ocultar error
function ocultarError() {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.classList.add('hidden');
}

// Verificar si ya está logueado
window.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (token) {
        window.location.href = '/index.html';
    }
});
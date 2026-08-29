// Authentication and Authorization Utility

const API_URL = '/api';

// Save auth details to local storage
function saveAuth(data) {
    localStorage.setItem('gym_token', data.token);
    localStorage.setItem('gym_userId', data.userId);
    localStorage.setItem('gym_username', data.username);
    localStorage.setItem('gym_email', data.email);
    localStorage.setItem('gym_roles', JSON.stringify(data.roles));
    localStorage.setItem('gym_profileId', data.memberOrTrainerId || '');
}

// Clear local storage on logout
function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Retrieve details
function getToken() {
    return localStorage.getItem('gym_token');
}

function getRoles() {
    try {
        return JSON.parse(localStorage.getItem('gym_roles')) || [];
    } catch (e) {
        return [];
    }
}

function getProfileId() {
    return localStorage.getItem('gym_profileId');
}

function getUserId() {
    return localStorage.getItem('gym_userId');
}

function getUsername() {
    return localStorage.getItem('gym_username');
}

// Verify page access based on roles
function checkAuth(requiredRole) {
    const token = getToken();
    const roles = getRoles();

    if (!token) {
        window.location.href = '/login.html';
        return false;
    }

    if (requiredRole && !roles.includes(requiredRole)) {
        // Redirect to appropriate dashboard if role is incorrect
        if (roles.includes('ROLE_ADMIN')) {
            window.location.href = '/admin/dashboard.html';
        } else if (roles.includes('ROLE_TRAINER')) {
            window.location.href = '/trainer/dashboard.html';
        } else if (roles.includes('ROLE_MEMBER')) {
            window.location.href = '/member/dashboard.html';
        } else {
            logout();
        }
        return false;
    }
    return true;
}

// Fetch wrapper that automatically appends JWT Bearer token
async function fetchWithAuth(endpoint, options = {}) {
    const token = getToken();
    
    // Set headers
    if (!options.headers) {
        options.headers = {};
    }
    
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }
    
    if (!(options.body instanceof FormData) && !options.headers['Content-Type']) {
        options.headers['Content-Type'] = 'application/json';
    }

    try {
        const response = await fetch(`${API_URL}${endpoint}`, options);
        
        if (response.status === 401 || response.status === 403) {
            // Token expired or access denied
            console.error('Session expired or unauthorized access.');
            logout();
            return null;
        }
        
        return response;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Toast notification helper
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container') || createToastContainer();
    
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0 show m-2`;
    toast.role = 'alert';
    toast.ariaLive = 'assertive';
    toast.ariaAtomic = 'true';
    
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'} me-2"></i>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 500);
    }, 4000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

// Render dynamic navbar header profile indicators
function loadNavbarProfile() {
    const usernameElement = document.getElementById('nav-profile-username');
    if (usernameElement) {
        usernameElement.textContent = localStorage.getItem('gym_username') || 'Profile';
    }
}

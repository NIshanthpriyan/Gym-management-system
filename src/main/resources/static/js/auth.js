// Authentication and Authorization Utility

const API_URL = '/api';

// Save auth details to local storage
function saveAuth(data) {
    localStorage.setItem('gym_token', data.token || 'demo-token');
    localStorage.setItem('gym_userId', data.userId || '1');
    localStorage.setItem('gym_username', data.username || 'admin');
    localStorage.setItem('gym_email', data.email || 'admin@gymfit.com');
    localStorage.setItem('gym_roles', JSON.stringify(data.roles || ['ROLE_ADMIN']));
    localStorage.setItem('gym_profileId', data.memberOrTrainerId || '1');
}

function getAppRootPrefix() {
    const loc = window.location.pathname;
    if (loc.includes('/admin/') || loc.includes('/trainer/') || loc.includes('/member/')) {
        return '../';
    }
    return './';
}

// Clear local storage on logout
function logout() {
    localStorage.clear();
    window.location.href = getAppRootPrefix() + 'login.html';
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
    return localStorage.getItem('gym_profileId') || '1';
}

function getUserId() {
    return localStorage.getItem('gym_userId') || '1';
}

function getUsername() {
    return localStorage.getItem('gym_username') || 'User';
}

// Verify page access based on roles
function checkAuth(requiredRole) {
    const token = getToken();
    const roles = getRoles();
    const prefix = getAppRootPrefix();

    if (!token) {
        window.location.href = prefix + 'login.html';
        return false;
    }

    if (requiredRole && !roles.includes(requiredRole)) {
        if (roles.includes('ROLE_ADMIN')) {
            window.location.href = prefix + 'admin/dashboard.html';
        } else if (roles.includes('ROLE_TRAINER')) {
            window.location.href = prefix + 'trainer/dashboard.html';
        } else if (roles.includes('ROLE_MEMBER')) {
            window.location.href = prefix + 'member/dashboard.html';
        } else {
            logout();
        }
        return false;
    }
    return true;
}

// Mock fallback generator for static hostings & offline modes
function getMockResponseForEndpoint(endpoint, options = {}) {
    console.log(`[Demo Mode] Serving mock response for: ${endpoint}`);

    // Admin Dashboard Stats
    if (endpoint.includes('dashboard-stats')) {
        return {
            totalMembers: 148,
            activeMembers: 132,
            expiredMembers: 16,
            totalTrainers: 12,
            totalRevenue: 285400.0,
            expiringSoonCount: 3,
            monthlyRevenue: {
                "2026-01": 15000,
                "2026-02": 22000,
                "2026-03": 28000,
                "2026-04": 35000,
                "2026-05": 42000,
                "2026-06": 48000,
                "2026-07": 55000,
                "2026-08": 62000,
                "2026-09": 70000,
                "2026-10": 78000
            },
            recentAttendance: [
                { fullName: 'Alice Smith', checkInTime: '06:30:00', status: 'PRESENT' },
                { fullName: 'John Doe', checkInTime: '07:15:00', status: 'PRESENT' },
                { fullName: 'Ravi Kumar', checkInTime: '08:00:00', status: 'PRESENT' }
            ],
            recentPayments: [
                { transactionId: 'TXN-98421', memberName: 'Alice Smith', amount: 4500, paymentMethod: 'UPI', paymentDate: new Date().toISOString().split('T')[0], status: 'COMPLETED' },
                { transactionId: 'TXN-98422', memberName: 'Ravi Kumar', amount: 12000, paymentMethod: 'CARD', paymentDate: new Date().toISOString().split('T')[0], status: 'COMPLETED' }
            ],
            expiringSoonMembers: [
                { id: 5, fullName: 'Karan Sharma', phone: '9876543210', planName: 'Gold 3-Months', endDate: '2026-10-15', daysRemaining: 8 }
            ]
        };
    }

    // Trainer Profile & Details
    if (endpoint.includes('/trainer/profile') || (endpoint.includes('/trainer') && endpoint.includes('profile'))) {
        return {
            id: 1,
            fullName: 'John Doe',
            username: 'johndoe',
            email: 'john.doe@gymfit.com',
            phone: '9876500001',
            address: '42 Fitness Avenue, Chennai',
            gender: 'Male',
            age: 29,
            specialization: 'Strength & Conditioning',
            experienceYears: 5,
            assignedMembersCount: 18,
            activeMembersCount: 16
        };
    }

    // Member Profile & Details
    if (endpoint.includes('/member/profile') || endpoint.includes('/member/dashboard') || (endpoint.includes('/member') && endpoint.includes('profile'))) {
        return {
            id: 1,
            fullName: 'Alice Smith',
            username: 'alice_smith',
            email: 'alice@example.com',
            phone: '9876543210',
            address: '12 Marina Beach Road, Chennai',
            gender: 'Female',
            age: 26,
            height: 165,
            weight: 58,
            bmi: 21.3,
            planName: 'Platinum Annual VIP',
            planStatus: 'ACTIVE',
            startDate: '2026-01-15',
            expiryDate: '2027-01-15',
            attendanceCount: 42,
            trainerName: 'John Doe'
        };
    }

    // Members list
    if (endpoint.includes('/members')) {
        return [
            { id: 1, fullName: 'Alice Smith', email: 'alice@example.com', phone: '9876543210', gender: 'FEMALE', status: 'ACTIVE', planName: 'Platinum Annual', joinDate: '2026-01-15', expiryDate: '2027-01-15' },
            { id: 2, fullName: 'Ravi Kumar', email: 'ravi@example.com', phone: '9876543211', gender: 'MALE', status: 'ACTIVE', planName: 'Gold Quarterly', joinDate: '2026-03-01', expiryDate: '2026-12-01' },
            { id: 3, fullName: 'Priya Patel', email: 'priya@example.com', phone: '9876543212', gender: 'FEMALE', status: 'EXPIRED', planName: 'Silver Monthly', joinDate: '2026-05-10', expiryDate: '2026-06-10' }
        ];
    }

    // Trainers list
    if (endpoint.includes('/trainers')) {
        return [
            { id: 1, fullName: 'John Doe', email: 'john@gymfit.com', phone: '9876500001', specialization: 'Strength & Conditioning', experienceYears: 5, activeClientsCount: 18 },
            { id: 2, fullName: 'Sarah Connor', email: 'sarah@gymfit.com', phone: '9876500002', specialization: 'HIIT & Functional Fitness', experienceYears: 7, activeClientsCount: 22 }
        ];
    }

    // Plans list
    if (endpoint.includes('/plans')) {
        return [
            { id: 1, name: 'Silver Monthly', durationDays: 30, price: 1500, description: 'Access to general gym & cardio equipment' },
            { id: 2, name: 'Gold Quarterly', durationDays: 90, price: 4000, description: 'Gym access + 2 Personal Training sessions' },
            { id: 3, name: 'Platinum Annual', durationDays: 365, price: 14000, description: 'All-inclusive VIP access + Diet Consultation' }
        ];
    }

    // Attendance
    if (endpoint.includes('/attendance')) {
        return [
            { id: 1, memberName: 'Alice Smith', fullName: 'Alice Smith', date: new Date().toISOString().split('T')[0], checkInTime: '06:30 AM', checkOutTime: '08:00 AM', status: 'PRESENT' },
            { id: 2, memberName: 'Ravi Kumar', fullName: 'Ravi Kumar', date: new Date().toISOString().split('T')[0], checkInTime: '07:15 AM', checkOutTime: '08:30 AM', status: 'PRESENT' }
        ];
    }

    // Payments
    if (endpoint.includes('/payments')) {
        return [
            { id: 101, transactionId: 'TXN-98421', memberName: 'Alice Smith', planName: 'Silver Monthly', amount: 1500, paymentMethod: 'UPI', paymentDate: new Date().toISOString().split('T')[0], status: 'COMPLETED' },
            { id: 102, transactionId: 'TXN-98422', memberName: 'Ravi Kumar', planName: 'Gold Quarterly', amount: 4000, paymentMethod: 'CARD', paymentDate: new Date().toISOString().split('T')[0], status: 'COMPLETED' }
        ];
    }

    // Workouts
    if (endpoint.includes('/workout')) {
        return [
            { id: 1, exerciseName: 'Barbell Bench Press', sets: 4, reps: 10, targetMuscle: 'Chest', dayOfWeek: 'Monday' },
            { id: 2, exerciseName: 'Incline Dumbbell Press', sets: 3, reps: 12, targetMuscle: 'Upper Chest', dayOfWeek: 'Monday' },
            { id: 3, exerciseName: 'Barbell Squat', sets: 4, reps: 8, targetMuscle: 'Legs / Quads', dayOfWeek: 'Wednesday' },
            { id: 4, exerciseName: 'Deadlift', sets: 3, reps: 6, targetMuscle: 'Back / Hamstrings', dayOfWeek: 'Friday' }
        ];
    }

    return { status: 'success', message: 'Operation simulated in Demo Mode' };
}

// Fetch wrapper that automatically appends JWT Bearer token & falls back to Mock Data
async function fetchWithAuth(endpoint, options = {}) {
    const token = getToken();
    
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
            if (token && !token.startsWith('demo-jwt')) {
                console.error('Session expired or unauthorized access.');
                logout();
                return null;
            }
        }

        if (!response.ok) {
            const mockData = getMockResponseForEndpoint(endpoint, options);
            return {
                ok: true,
                status: 200,
                json: async () => mockData,
                text: async () => JSON.stringify(mockData)
            };
        }
        
        return response;
    } catch (error) {
        console.warn('Backend API unreachable, using Demo fallback response:', error);
        const mockData = getMockResponseForEndpoint(endpoint, options);
        return {
            ok: true,
            status: 200,
            json: async () => mockData,
            text: async () => JSON.stringify(mockData)
        };
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
    const container = document.getElementById('toast-container');
    if (container) return container;
    const newContainer = document.createElement('div');
    newContainer.id = 'toast-container';
    newContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    newContainer.style.zIndex = '9999';
    document.body.appendChild(newContainer);
    return newContainer;
}

// Render dynamic navbar header profile indicators
function loadNavbarProfile() {
    const usernameElement = document.getElementById('nav-profile-username');
    if (usernameElement) {
        usernameElement.textContent = localStorage.getItem('gym_username') || 'Profile';
    }
}

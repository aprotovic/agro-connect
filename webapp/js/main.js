// ===== AGRO-CONNECT MAIN JAVASCRIPT =====
// Common utilities and functions

// API Base URL
const API_BASE = '';

// Utility: Show alert message
function showAlert(containerId, type, message) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertClass = type === 'success' ? 'alert-success' :
        type === 'error' ? 'alert-error' :
            type === 'warning' ? 'alert-warning' : 'alert-info';
    const icon = type === 'success' ? 'check-circle' :
        type === 'error' ? 'exclamation-circle' :
            type === 'warning' ? 'exclamation-triangle' : 'info-circle';

    container.innerHTML = `
        <div class="alert ${alertClass}">
            <i class="fas fa-${icon}"></i>
            <span>${message}</span>
        </div>
    `;

    if (type !== 'info') {
        setTimeout(() => {
            container.innerHTML = '';
        }, 5000);
    }
}

// Utility: Format currency
function formatCurrency(amount) {
    return 'ETB ' + parseFloat(amount).toFixed(2);
}

// Utility: Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Utility: Get status badge
function getStatusBadge(status) {
    const statusMap = {
        'pending': 'badge-warning',
        'confirmed': 'badge-info',
        'processing': 'badge-info',
        'shipped': 'badge-info',
        'delivered': 'badge-success',
        'cancelled': 'badge-error',
        'available': 'badge-success',
        'out_of_stock': 'badge-warning',
        'discontinued': 'badge-error',
        'paid': 'badge-success',
        'failed': 'badge-error'
    };

    const badgeClass = statusMap[status.toLowerCase()] || 'badge-info';
    return `<span class="badge ${badgeClass}">${status}</span>`;
}

// Utility: Get category icon
function getCategoryIcon(category) {
    const iconMap = {
        'Grains': '🌾',
        'Vegetables': '🥬',
        'Fruits': '🍎',
        'Pulses': '🫘',
        'Spices': '🌶️',
        'Cash Crops': '🌿',
        'General': '📦'
    };
    return iconMap[category] || '📦';
}

// Utility: Modal functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
    }
});

// Utility: Check authentication
function checkAuth(userType) {
    const userData = localStorage.getItem(userType);
    if (!userData) {
        window.location.href = 'login.html';
        return null;
    }
    return JSON.parse(userData);
}

// Utility: Logout
function logout(userType) {
    localStorage.removeItem(userType);
    window.location.href = 'login.html';
}

// Utility: API call wrapper
async function apiCall(endpoint, options = {}) {
    try {
        const response = await fetch(API_BASE + endpoint, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                ...options.headers
            },
            ...options
        });

        return await response.json();
    } catch (error) {
        console.error('API call error:', error);
        throw new Error('Connection error. Please check if the server is running.');
    }
}

// Utility: Form data builder
function buildFormData(formData) {
    return Object.keys(formData)
        .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(formData[key])}`)
        .join('&');
}

// Utility: Validate phone number
function validatePhone(phone) {
    const phoneRegex = /^[0-9]{10}$/;
    return phoneRegex.test(phone);
}

// Utility: Validate email
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Loading spinner
function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '<div class="spinner"></div>';
    }
}

function hideLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '';
    }
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Initialize tooltips and other UI enhancements
document.addEventListener('DOMContentLoaded', () => {
    // Theme Toggle Logic
    const theme = localStorage.getItem('theme');
    if (theme === 'dark') {
        document.body.classList.add('dark-mode');
        updateThemeIcon(true);
    }

    // Add ripple effect to buttons
    document.querySelectorAll('.btn').forEach(button => {
        button.addEventListener('click', function (e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;

            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');

            this.appendChild(ripple);

            setTimeout(() => ripple.remove(), 600);
        });
    });
});

// Theme Toggle Function
function toggleTheme() {
    document.body.classList.toggle('dark-mode');
    const isDark = document.body.classList.contains('dark-mode');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    updateThemeIcon(isDark);
}

function updateThemeIcon(isDark) {
    const icon = document.getElementById('theme-icon');
    if (icon) {
        icon.className = isDark ? 'fas fa-sun' : 'fas fa-moon';
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        showAlert,
        formatCurrency,
        formatDate,
        getStatusBadge,
        getCategoryIcon,
        openModal,
        closeModal,
        checkAuth,
        logout,
        apiCall,
        buildFormData,
        validatePhone,
        validateEmail,
        showLoading,
        hideLoading,
        debounce
    };
}

// Employee Management System - Client-side JavaScript
document.addEventListener('DOMContentLoaded', function() {
    console.log('Employee Management System loaded');

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Form validation enhancement
    const forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            const submitBtn = form.querySelector('button[type="submit"]');

            // Add loading state to submit button
            if (submitBtn) {
                submitBtn.classList.add('btn-loading');
                submitBtn.disabled = true;
                setTimeout(() => {
                    submitBtn.classList.remove('btn-loading');
                    submitBtn.disabled = false;
                }, 2000);
            }

            // Client-side validation
            const nameInput = form.querySelector('input[name="name"]');
            const ageInput = form.querySelector('input[name="age"]');
            const salaryInput = form.querySelector('input[name="salary"]');

            let isValid = true;

            // Name validation
            if (nameInput && nameInput.value.trim() === '') {
                showFieldError(nameInput, 'Name is required');
                isValid = false;
            } else if (nameInput && nameInput.value.trim().length < 2) {
                showFieldError(nameInput, 'Name must be at least 2 characters');
                isValid = false;
            } else if (nameInput) {
                clearFieldError(nameInput);
            }

            // Age validation
            if (ageInput && (ageInput.value < 18 || ageInput.value > 65)) {
                showFieldError(ageInput, 'Age must be between 18 and 65');
                isValid = false;
            } else if (ageInput && ageInput.value === '') {
                showFieldError(ageInput, 'Age is required');
                isValid = false;
            } else if (ageInput) {
                clearFieldError(ageInput);
            }

            // Salary validation
            if (salaryInput && salaryInput.value < 1000) {
                showFieldError(salaryInput, 'Salary must be at least 1000');
                isValid = false;
            } else if (salaryInput && salaryInput.value === '') {
                showFieldError(salaryInput, 'Salary is required');
                isValid = false;
            } else if (salaryInput) {
                clearFieldError(salaryInput);
            }

            if (!isValid) {
                event.preventDefault();
                if (submitBtn) {
                    submitBtn.classList.remove('btn-loading');
                    submitBtn.disabled = false;
                }
            }
        });
    });

    // Real-time search functionality
    const searchInput = document.querySelector('input[name="query"]');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                if (this.value.length >= 2 || this.value.length === 0) {
                    // Auto-submit search form after 500ms delay
                    this.form.submit();
                }
            }, 500);
        });
    }

    // Confirm delete with detailed message
    const deleteLinks = document.querySelectorAll('a[onclick*="confirm"]');
    deleteLinks.forEach(function(link) {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const employeeName = this.closest('tr').querySelector('td:nth-child(2) span').textContent;

            const result = confirm(
                `⚠️ DELETE CONFIRMATION\n\n` +
                `Are you sure you want to delete employee "${employeeName}"?\n\n` +
                `This action cannot be undone and will permanently remove:\n` +
                `• Employee record\n` +
                `• All associated data\n\n` +
                `Click OK to proceed or Cancel to abort.`
            );

            if (result) {
                window.location.href = this.href;
            }
        });
    });

    // Highlight edited row
    const editLinks = document.querySelectorAll('a[href*="/edit/"]');
    editLinks.forEach(function(link) {
        link.addEventListener('click', function() {
            const row = this.closest('tr');
            if (row) {
                row.classList.add('editing');
            }
        });
    });

    // Format salary inputs
    const salaryInputs = document.querySelectorAll('input[name="salary"]');
    salaryInputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            const value = parseFloat(this.value);
            if (!isNaN(value)) {
                this.value = value.toFixed(2);
            }
        });
    });

    // Show success animation for form submissions
    const successAlert = document.querySelector('.alert-success');
    if (successAlert) {
        successAlert.style.animation = 'fadeInUp 0.5s ease-out';
    }

    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Helper functions
    function showFieldError(input, message) {
        input.classList.add('is-invalid');

        // Remove existing error message
        const existingError = input.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }

        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        input.parentNode.appendChild(errorDiv);

        // Focus on first invalid field
        if (document.querySelector('.is-invalid') === input) {
            input.focus();
        }
    }

    function clearFieldError(input) {
        input.classList.remove('is-invalid');
        const errorDiv = input.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    // Add smooth scrolling to anchors
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    console.log('✅ Employee Management System initialized successfully');
});

// Global utility functions
window.EmployeeManagement = {

    // Format currency
    formatCurrency: function(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    },

    // Show notification
    showNotification: function(message, type = 'success') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alertDiv.innerHTML = `
            <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-triangle'}-fill me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(alertDiv);

        // Auto-remove after 3 seconds
        setTimeout(() => {
            alertDiv.remove();
        }, 3000);
    },

    // Validate employee data
    validateEmployee: function(name, age, salary) {
        const errors = [];

        if (!name || name.trim().length < 2) {
            errors.push('Name must be at least 2 characters');
        }

        if (!age || age < 18 || age > 65) {
            errors.push('Age must be between 18 and 65');
        }

        if (!salary || salary < 1000) {
            errors.push('Salary must be at least 1000');
        }

        return errors;
    }
};
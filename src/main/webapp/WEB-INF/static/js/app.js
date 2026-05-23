document.querySelectorAll('.btn-toggle-pass').forEach(btn => {
    btn.addEventListener('click', () => {
        const input = btn.closest('.input-wrapper').querySelector('input');
        const icon  = btn.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'bi bi-eye-slash';
        } else {
            input.type = 'password';
            icon.className = 'bi bi-eye';
        }
    });
});

/* ── Password strength meter ─────────────────────────────────── */
const strengthInput = document.getElementById('password');
if (strengthInput) {
    const bar  = document.getElementById('strengthBar');
    const text = document.getElementById('strengthText');

    strengthInput.addEventListener('input', () => {
        const val = strengthInput.value;
        let score = 0;

        if (val.length >= 8)                          score++;
        if (/[A-Z]/.test(val))                        score++;
        if (/[0-9]/.test(val))                        score++;
        if (/[^A-Za-z0-9]/.test(val))                 score++;

        bar.className = 'strength-bar';
        if (val.length === 0) {
            bar.style.width = '0';
            text.textContent = '';
        } else if (score <= 1) {
            bar.classList.add('strength-weak');
            bar.style.background = '#e74c3c';
            bar.style.width = '33%';
            text.textContent = 'Mật khẩu yếu';
            text.style.color = '#e74c3c';
        } else if (score === 2 || score === 3) {
            bar.style.background = '#f39c12';
            bar.style.width = '66%';
            text.textContent = 'Mật khẩu trung bình';
            text.style.color = '#f39c12';
        } else {
            bar.style.background = '#00C896';
            bar.style.width = '100%';
            text.textContent = 'Mật khẩu mạnh';
            text.style.color = '#00C896';
        }
    });
}

/* ── Confirm password realtime match ────────────────────────── */
const confirmInput = document.getElementById('confirmPassword');
if (confirmInput && strengthInput) {
    confirmInput.addEventListener('input', () => {
        const matchMsg = document.getElementById('matchMsg');
        if (!matchMsg) return;
        if (confirmInput.value === strengthInput.value) {
            matchMsg.textContent = '✓ Mật khẩu khớp';
            matchMsg.style.color = '#00C896';
            confirmInput.classList.remove('is-invalid');
        } else {
            matchMsg.textContent = '✗ Mật khẩu chưa khớp';
            matchMsg.style.color = '#e74c3c';
        }
    });
}

/* ── Button loading state khi submit form ────────────────────── */
document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', () => {
        const btn = form.querySelector('.btn-submit');
        if (btn) btn.classList.add('loading');
    });
});

/* ── Toast notifications ─────────────────────────────────────── */
function showToast(message, type = 'success', duration = 3500) {
    let stack = document.querySelector('.toast-stack');
    if (!stack) {
        stack = document.createElement('div');
        stack.className = 'toast-stack';
        document.body.appendChild(stack);
    }

    const toast = document.createElement('div');
    toast.className = `toast-item toast-${type}`;

    const icon = type === 'success' ? 'bi-check-circle' : 'bi-exclamation-circle';
    toast.innerHTML = `<i class="bi ${icon}"></i><span>${message}</span>`;
    stack.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideIn .3s ease reverse';
        setTimeout(() => toast.remove(), 280);
    }, duration);
}

/* ── Auto-dismiss alert sau 5 giây ──────────────────────────── */
document.querySelectorAll('.alert-app').forEach(alert => {
    setTimeout(() => {
        alert.style.transition = 'opacity .4s ease';
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 400);
    }, 5000);
});

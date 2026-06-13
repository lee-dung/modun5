/* ============================================================
   WALLETS — JavaScript
   ============================================================ */

/* ── Xoá ví: mở modal confirm ───────────────────────────────── */
function openDeleteModal(btn) {
    const id   = btn.getAttribute('data-id');
    const name = btn.getAttribute('data-name');

    // Điền tên ví vào modal
    document.getElementById('deleteWalletName').textContent = name;

    // Set action của form xoá (POST /wallets/{id}/delete)
    document.getElementById('deleteForm').action = `/wallets/${id}/delete`;

    // Mở modal
    document.getElementById('deleteModal').classList.add('open');
    document.body.style.overflow = 'hidden'; // Chặn scroll background
}

function closeDeleteModal(event) {
    // Nếu click vào backdrop (không phải modal-box) thì đóng
    if (event && event.target !== document.getElementById('deleteModal')) return;

    document.getElementById('deleteModal').classList.remove('open');
    document.body.style.overflow = '';
}

// Đóng modal bằng phím Escape
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeDeleteModal();
});

// Cho phép click vào .modal-backdrop để đóng
document.getElementById('deleteModal')?.addEventListener('click', function(e) {
    if (e.target === this) closeDeleteModal();
});


/* ── Form preview: cập nhật realtime ───────────────────────── */
const nameInput    = document.getElementById('name');
const balanceInput = document.getElementById('balance');
const currencyInput = document.getElementById('currency');
const descInput    = document.getElementById('description');

function formatVnd(num) {
    if (isNaN(num) || num === '') return '0 ₫';
    return Number(num).toLocaleString('vi-VN') + ' ₫';
}

function updatePreview() {
    const previewName     = document.getElementById('previewName');
    const previewBalance  = document.getElementById('previewBalance');
    const previewCurrency = document.getElementById('previewCurrency');
    const previewDesc     = document.getElementById('previewDesc');

    if (!previewName) return; // Không có trang form thì bỏ qua

    if (nameInput)     previewName.textContent    = nameInput.value || 'Tên ví';
    if (balanceInput)  previewBalance.textContent  = formatVnd(balanceInput.value);
    if (currencyInput) previewCurrency.textContent = currencyInput.value || 'VND';

    if (descInput) {
        if (descInput.value.trim()) {
            previewDesc.textContent = descInput.value;
            previewDesc.style.display = 'block';
        } else {
            previewDesc.style.display = 'none';
        }
    }
}

nameInput?.addEventListener('input', updatePreview);
balanceInput?.addEventListener('input', updatePreview);
currencyInput?.addEventListener('change', updatePreview);
descInput?.addEventListener('input', updatePreview);

// Cập nhật icon khi chọn radio
document.querySelectorAll('input[name="icon"]').forEach(radio => {
    radio.addEventListener('change', function() {
        const previewIcon = document.getElementById('previewIcon');
        if (previewIcon) {
            // Xoá tất cả class bi-* cũ
            previewIcon.className = previewIcon.className.replace(/bi-\S+/, '');
            previewIcon.classList.add('bi', 'bi-' + this.value);
        }
    });
});

// Chạy lần đầu khi load trang (fill preview với giá trị hiện tại khi sửa)
updatePreview();


/* ── Format số tiền khi blur input balance ──────────────────── */
balanceInput?.addEventListener('blur', function() {
    const val = parseFloat(this.value);
    if (!isNaN(val) && val >= 0) {
        this.value = Math.round(val); // Làm tròn, không có số thập phân
    }
});

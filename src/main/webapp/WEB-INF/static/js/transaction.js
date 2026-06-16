/* ── Modal xác nhận xoá ─────────────────────────────────────── */
function openDeleteModal(btn) {
    const id     = btn.getAttribute('data-id');
    const amount = btn.getAttribute('data-amount');

    document.getElementById('deleteAmount').textContent = amount;
    document.getElementById('deleteForm').action = '/transactions/' + id + '/delete';
    document.getElementById('deleteModal').classList.add('open');
    document.body.style.overflow = 'hidden';
}

function closeModal(event) {
    if (event && event.target !== document.getElementById('deleteModal')) return;
    document.getElementById('deleteModal').classList.remove('open');
    document.body.style.overflow = '';
}

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        document.getElementById('deleteModal').classList.remove('open');
        document.body.style.overflow = '';
    }
});

/* ── Format số tiền realtime trong form ─────────────────────── */
const amountInput = document.getElementById('amount');
if (amountInput) {
    amountInput.addEventListener('input', () => {
        const preview = document.getElementById('amountPreview');
        if (!preview) return;
        const val = parseFloat(amountInput.value);
        if (!isNaN(val) && val > 0) {
            preview.textContent = val.toLocaleString('vi-VN') + ' ₫';
            preview.style.color = 'var(--mint-d)';
        } else {
            preview.textContent = '0 ₫';
            preview.style.color = 'var(--s400)';
        }
    });
}

USE expense_db;

INSERT INTO categories (user_id, name, icon, color, type) VALUES
-- Chi tiêu
(NULL, 'Ăn uống',       'bowl',          '#FF6B6B', 'EXPENSE'),
(NULL, 'Di chuyển',     'car',           '#4ECDC4', 'EXPENSE'),
(NULL, 'Mua sắm',       'shopping-cart', '#45B7D1', 'EXPENSE'),
(NULL, 'Giải trí',      'music',         '#96CEB4', 'EXPENSE'),
(NULL, 'Y tế',          'heart-pulse',   '#FFEAA7', 'EXPENSE'),
(NULL, 'Giáo dục',      'book',          '#DDA0DD', 'EXPENSE'),
(NULL, 'Hóa đơn',       'file-invoice',  '#98D8C8', 'EXPENSE'),
(NULL, 'Khác',          'dots',          '#B0B0B0', 'EXPENSE'),
-- Thu nhập
(NULL, 'Lương',         'briefcase',     '#6BCB77', 'INCOME'),
(NULL, 'Thưởng',        'gift',          '#FFD93D', 'INCOME'),
(NULL, 'Thu nhập khác', 'plus-circle',   '#4D96FF', 'INCOME');
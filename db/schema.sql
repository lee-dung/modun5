CREATE DATABASE IF NOT EXISTS expense_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE expense_db;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash
    full_name   VARCHAR(100),
    avatar_url  VARCHAR(255),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS wallets (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    name         VARCHAR(100)   NOT NULL,
    balance      DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    currency     VARCHAR(10)    NOT NULL DEFAULT 'VND',
    icon         VARCHAR(50)    DEFAULT 'wallet',   -- tên icon Bootstrap/Tabler
    description  VARCHAR(255),
    is_default   BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NULL,               -- NULL = danh mục hệ thống (mặc định)
    name        VARCHAR(100) NOT NULL,
    icon        VARCHAR(50)  DEFAULT 'tag',
    color       VARCHAR(20)  DEFAULT '#6c757d',  -- Hex color cho UI
    type        ENUM('EXPENSE', 'INCOME', 'BOTH') NOT NULL DEFAULT 'EXPENSE',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    wallet_id    BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    amount       DECIMAL(15, 2) NOT NULL,
    type         ENUM('EXPENSE', 'INCOME') NOT NULL DEFAULT 'EXPENSE',
    note         VARCHAR(500),
    transaction_date  DATE      NOT NULL,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_txn_user     FOREIGN KEY (user_id)     REFERENCES users(id)        ON DELETE CASCADE,
    CONSTRAINT fk_txn_wallet   FOREIGN KEY (wallet_id)   REFERENCES wallets(id)      ON DELETE CASCADE,
    CONSTRAINT fk_txn_category FOREIGN KEY (category_id) REFERENCES categories(id)   ON DELETE RESTRICT,

    INDEX idx_txn_user_date  (user_id, transaction_date),
    INDEX idx_txn_wallet     (wallet_id),
    INDEX idx_txn_category   (category_id)
);

CREATE TABLE IF NOT EXISTS budgets (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    amount_limit DECIMAL(15, 2) NOT NULL,
    month        TINYINT        NOT NULL,
    year         SMALLINT       NOT NULL,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uq_budget (user_id, category_id, month, year),
    CONSTRAINT fk_budget_user     FOREIGN KEY (user_id)     REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

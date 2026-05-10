USE erss;

SET @schema_name := DATABASE();

SET @wallet_balance_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'users'
    AND column_name = 'wallet_balance'
);

SET @sql_wallet_balance := IF(
  @wallet_balance_exists = 0,
  'ALTER TABLE users ADD COLUMN wallet_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00 AFTER banned_at',
  'SELECT 1'
);
PREPARE stmt_wallet_balance FROM @sql_wallet_balance;
EXECUTE stmt_wallet_balance;
DEALLOCATE PREPARE stmt_wallet_balance;

CREATE TABLE IF NOT EXISTS system_wallet (
  id BIGINT PRIMARY KEY,
  escrow_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS payout_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  amount DECIMAL(12, 2) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  released_at DATETIME DEFAULT NULL,
  released_by BIGINT DEFAULT NULL,
  release_note VARCHAR(255) DEFAULT '',
  UNIQUE KEY uk_payout_requests_order_id (order_id),
  KEY idx_payout_requests_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO system_wallet (id, escrow_balance)
SELECT 1, 0.00
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM system_wallet WHERE id = 1
);

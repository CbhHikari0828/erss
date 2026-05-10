USE erss;

CREATE TABLE IF NOT EXISTS wallet_withdrawals (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  amount DECIMAL(12, 2) NOT NULL,
  channel VARCHAR(32) NOT NULL,
  destination_account VARCHAR(128) DEFAULT '',
  note VARCHAR(255) DEFAULT '',
  status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME DEFAULT NULL,
  KEY idx_wallet_withdrawals_user (user_id, requested_at),
  KEY idx_wallet_withdrawals_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

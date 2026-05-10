USE erss;

CREATE TABLE IF NOT EXISTS system_transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category VARCHAR(32) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  description VARCHAR(255) DEFAULT '',
  reference_id VARCHAR(64) DEFAULT '',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_sys_tx_category (category),
  KEY idx_sys_tx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

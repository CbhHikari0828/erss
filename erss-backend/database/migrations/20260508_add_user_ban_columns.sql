SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'banned'
);
SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE users ADD COLUMN banned TINYINT(1) NOT NULL DEFAULT 0 AFTER shop_registered',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'ban_reason'
);
SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE users ADD COLUMN ban_reason VARCHAR(255) DEFAULT '''' AFTER banned',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
  SELECT COUNT(*)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'banned_at'
);
SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE users ADD COLUMN banned_at DATETIME DEFAULT NULL AFTER ban_reason',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

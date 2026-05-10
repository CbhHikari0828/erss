USE erss;

SET @schema_name := DATABASE();

SET @rental_enabled_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'books'
    AND column_name = 'rental_enabled'
);

SET @sql_rental_enabled := IF(
  @rental_enabled_exists = 0,
  'ALTER TABLE books ADD COLUMN rental_enabled TINYINT(1) NOT NULL DEFAULT 0 AFTER original_price',
  'SELECT 1'
);
PREPARE stmt_rental_enabled FROM @sql_rental_enabled;
EXECUTE stmt_rental_enabled;
DEALLOCATE PREPARE stmt_rental_enabled;

SET @rent_price_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'books'
    AND column_name = 'rent_price'
);

SET @sql_rent_price := IF(
  @rent_price_exists = 0,
  'ALTER TABLE books ADD COLUMN rent_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER rental_enabled',
  'SELECT 1'
);
PREPARE stmt_rent_price FROM @sql_rent_price;
EXECUTE stmt_rent_price;
DEALLOCATE PREPARE stmt_rent_price;

SET @rental_deposit_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'books'
    AND column_name = 'rental_deposit'
);

SET @sql_rental_deposit := IF(
  @rental_deposit_exists = 0,
  'ALTER TABLE books ADD COLUMN rental_deposit DECIMAL(10, 2) NOT NULL DEFAULT 0.00 AFTER rent_price',
  'SELECT 1'
);
PREPARE stmt_rental_deposit FROM @sql_rental_deposit;
EXECUTE stmt_rental_deposit;
DEALLOCATE PREPARE stmt_rental_deposit;

UPDATE books
SET rent_price = GREATEST(1.00, ROUND(price / 5, 0))
WHERE rent_price = 0.00
  AND status = 'RENTED';

UPDATE books
SET rental_deposit = GREATEST(10.00, ROUND(price * 1.5, 0))
WHERE rental_deposit = 0.00
  AND status = 'RENTED';

UPDATE books
SET rental_enabled = 1
WHERE status = 'RENTED';

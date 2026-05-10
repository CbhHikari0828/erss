USE erss;

SET @schema_name := DATABASE();

SET @return_delivery_method_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = @schema_name
    AND table_name = 'rental_orders'
    AND column_name = 'return_delivery_method'
);

SET @sql_return_delivery_method := IF(
  @return_delivery_method_exists = 0,
  'ALTER TABLE rental_orders ADD COLUMN return_delivery_method VARCHAR(16) DEFAULT '''' AFTER deposit_amount',
  'SELECT 1'
);
PREPARE stmt_return_delivery_method FROM @sql_return_delivery_method;
EXECUTE stmt_return_delivery_method;
DEALLOCATE PREPARE stmt_return_delivery_method;

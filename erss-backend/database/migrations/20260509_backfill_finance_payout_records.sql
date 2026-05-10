USE erss;

INSERT INTO payout_requests (
  order_id,
  seller_id,
  amount,
  status,
  requested_at,
  released_at,
  release_note
)
SELECT
  o.id,
  o.seller_id,
  CASE
    WHEN o.order_type = 'rental'
      THEN COALESCE(o.unit_price, 0) * COALESCE(NULLIF(CAST(REGEXP_REPLACE(COALESCE(o.rental_period, ''), '[^0-9]', '') AS UNSIGNED), 0), 7)
    ELSE COALESCE(o.unit_price, 0) * COALESCE(o.quantity, 1)
  END,
  CASE
    WHEN o.status = 'COMPLETED' OR o.escrow_status = 'ESCROW_RELEASED' THEN 'PAID'
    WHEN o.status = 'PAYOUT_PENDING' OR o.escrow_status = 'ESCROW_RELEASE_PENDING' THEN 'PENDING'
    ELSE 'ESCROW_HOLDING'
  END,
  COALESCE(o.updated_at, o.created_at, NOW()),
  CASE
    WHEN o.status = 'COMPLETED' OR o.escrow_status = 'ESCROW_RELEASED' THEN COALESCE(o.updated_at, NOW())
    ELSE NULL
  END,
  CASE
    WHEN o.status = 'COMPLETED' OR o.escrow_status = 'ESCROW_RELEASED' THEN '历史订单自动补录'
    ELSE ''
  END
FROM trade_orders o
LEFT JOIN payout_requests p ON p.order_id = o.id
WHERE p.id IS NULL
  AND o.escrow_status IN ('ESCROW_HOLDING', 'ESCROW_RELEASE_PENDING', 'ESCROW_RELEASED');

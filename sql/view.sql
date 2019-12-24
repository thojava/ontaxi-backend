drop view driver_transaction;
CREATE VIEW driver_transaction AS
  SELECT version, concat('P', id) as id, driver, amount, payment_type, reason, created_by, modified_by, created_datetime, last_updated_datetime FROM driver_payment
  UNION
  select version, concat('B', id) as id, accepted_by, actual_total_fee, 'UC', concat('Phí Order Số', id) as reason, created_by, modified_by, created_datetime, last_updated_datetime from booking where status = 'C';

drop view driver_transaction;
CREATE VIEW driver_transaction AS
  SELECT version, concat('P', id) as id, driver, amount, payment_type, reason, created_by, modified_by, created_datetime, last_updated_datetime FROM driver_payment
  UNION
  select booking.version, concat('B', booking.id) as id, driver.id, actual_total_fee, 'UC', concat('Phí Order Số ', booking.id) as reason, booking.created_by, booking.modified_by, booking.created_datetime, booking.last_updated_datetime from booking
  inner join driver on booking.accepted_by = driver.email where booking.status = 'C';

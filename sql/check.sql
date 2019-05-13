select * from (
select id, email,
  (select coalesce(sum(driver_payment.amount), 0) from driver_payment where driver_payment.driver = driver.email) as total_payment,
(select coalesce(sum(total_fee), 0) from booking where accepted_by = driver.email and status = 'A') +
(select coalesce(sum(actual_total_fee),0) from booking where accepted_by = driver.email and status = 'C') as total_fee,
  (select coalesce(sum(driver_payment.amount),0) from driver_payment where driver_payment.driver = driver.email and driver_payment.payment_type = 'RC') -
  (select coalesce(sum(driver_payment.amount),0) from driver_payment where driver_payment.driver = driver.email and driver_payment.payment_type = 'UC') -
  (select coalesce(sum(total_fee),0) from booking where accepted_by = driver.email and status = 'A') -
  (select coalesce(sum(actual_total_fee),0) from booking where accepted_by = driver.email and status = 'C') as should_be_amount,
  amount
from driver) as total_table where should_be_amount - amount > 0.1 or amount - should_be_amount > 0.1;

/*Update the in correct amount driver*/
update driver set amount = (select coalesce(sum(driver_payment.amount),0) from driver_payment where driver_payment.driver = driver.email and driver_payment.payment_type = 'RC') -
                           (select coalesce(sum(driver_payment.amount),0) from driver_payment where driver_payment.driver = driver.email and driver_payment.payment_type = 'UC') -
                           (select coalesce(sum(total_fee),0) from booking where accepted_by = driver.email and status = 'A') -
                           (select coalesce(sum(actual_total_fee),0) from booking where accepted_by = driver.email and status = 'C');
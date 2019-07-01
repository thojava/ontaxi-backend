package vn.ontaxi.common.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByStatus(String status);

    List<Booking> findByMobile(String phoneNumber, Sort sort);

    List<Booking> findByStatusAndCreatedBy(String status, String created_by);

    List<Booking> findByStatusOrderByDepartureTimeAsc(String status);

    List<Booking> findByStatusOrderByDepartureTimeDesc(String status);

    List<Booking> findByStatusAndSurveyIdAndIsLaterPaidOrderByDepartureTimeDesc(String status, long surveyId, String laterPaid);

    List<Booking> findByStatusAndSurveyIdGreaterThanOrderByDepartureTimeDesc(String status, long surveyId);

    List<Booking> findByStatusAndArrivalTimeBetweenOrderByDepartureTimeDesc(String status, Date startTime, Date endTime);

    List<Booking> findByStatusAndArrivalTimeBetweenOrderByDepartureTimeAsc(String status, Date startTime, Date endTime);

    List<Booking> findByStatusAndIsLaterPaidAndPaidToDriverOrderByDepartureTimeDesc(String status, String laterPaid, String paidToDriver);

    List<Booking> findByStatusAndCreatedByOrderByDepartureTimeDesc(String status, String created_by);

    List<Booking> findByStatusAndCreatedByOrderByDepartureTimeAsc(String status, String created_by);

    List<Booking> findByStatusAndCreatedByAndArrivalTimeBetweenOrderByDepartureTimeDesc(String status, String created_by,
                                                                                        Date startTime, Date endTime);

    List<Booking> findByStatusAndCreatedByAndArrivalTimeBetweenOrderByDepartureTimeAsc(String status, String created_by,
                                                                                       Date startTime, Date endTime);

    @Query("SELECT DISTINCT u.acceptedByDriver FROM Booking u WHERE u.isLaterPaid = 'Y' AND u.arrivalTime between :startTime AND :endTime")
    List<Driver> getLaterPaidDriversInPeriod(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Query("SELECT DISTINCT u.acceptedByDriver FROM Booking u WHERE u.isLaterPaid = 'Y'")
    List<Driver> getLaterPaidDrivers();

    List<Booking> findAllByOrderByIdDesc();

    List<Booking> findByAcceptedByDriver(Driver driver);
    List<Booking> findByAcceptedByDriver_Email(String email);

    List<Booking> findByDepartureTimeBetween(Date startDate, Date endDate);

    Long countByStatus(String status);

    Long countByStatusAndDebtor(String status, String debtor);

    @Query("SELECT sum(u.actual_total_distance) FROM Booking u WHERE u.status = 'C'")
    double getTotalCompletedKM();

    @Query("SELECT sum(u.actual_total_distance) FROM Booking u WHERE u.status = 'C' AND u.createdBy = :createdBy ")
    double getTotalCompletedKM(@Param("createdBy") String createdBy);

    @Query("SELECT sum(u.actual_total_price) FROM Booking u WHERE u.status = 'C'")
    double getTotalCompletedPrice();

    @Query("SELECT sum(u.actual_total_price) FROM Booking u WHERE u.status = 'C' AND u.createdBy = :createdBy ")
    double getTotalCompletedPrice(@Param("createdBy") String createdBy);

    @Query("SELECT sum(u.actual_total_fee) FROM Booking u WHERE u.status = 'C'")
    double getTotalCompletedFee();

    @Query("SELECT sum(u.actual_total_fee) FROM Booking u WHERE u.status = 'C' AND u.createdBy = :createdBy ")
    double getTotalCompletedFee(@Param("createdBy") String createdBy);

    Long countByStatusAndDebtorAndArrivalTimeBetween(String status, String debtor, Date startTime, Date endTime);

    Long countByStatusAndArrivalTimeBetween(String status, Date startTime, Date endTime);
}

package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.Checkin;
import event_booking_system.demo.enums.CheckinStatus;
import event_booking_system.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<Checkin, String> {
    Optional<Checkin> findByOrderId(String orderId);

    Optional<Checkin> findByQrCode(String qrCode);

    List<Checkin> findByStatus(CheckinStatus status);

    boolean existsByQrCodeAndStatus(String qrCode, CheckinStatus status);
}

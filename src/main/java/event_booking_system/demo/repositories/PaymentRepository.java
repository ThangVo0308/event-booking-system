package event_booking_system.demo.repositories;

import event_booking_system.demo.entities.Payment;
import event_booking_system.demo.enums.PaymentStatus;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Observed
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findByOrderId(String orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByOrderIdAndStatus(String orderId, PaymentStatus status);
}

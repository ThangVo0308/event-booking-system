package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Payment;
import event_booking_system.demo.enums.PaymentStatus;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.repositories.PaymentRepository;
import event_booking_system.demo.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static event_booking_system.demo.exceptions.CommonErrorCode.PAYMENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(Payment payment) {
        Payment existingPayment = paymentRepository.findById(payment.getId())
                .orElseThrow(() -> new AppException(PAYMENT_NOT_FOUND, NOT_FOUND));
        existingPayment.setOrder(payment.getOrder());
        existingPayment.setPrice(payment.getPrice());
        existingPayment.setStatus(payment.getStatus());
        return paymentRepository.save(existingPayment);
    }

    @Override
    public void deletePayment(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new AppException(PAYMENT_NOT_FOUND, NOT_FOUND);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public Optional<Payment> findPaymentById(String id) {
        return Optional.ofNullable(paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(PAYMENT_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findPaymentByOrder(Order order) {
        return Optional.ofNullable(paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new AppException(PAYMENT_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Payment> findPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    @Override
    public List<Payment> findPaymentsByOrderAndStatus(Order order, PaymentStatus status) {
        return paymentRepository.findByOrderIdAndPaymentStatus(order.getId(), status);
    }
}
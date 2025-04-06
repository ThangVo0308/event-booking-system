package event_booking_system.demo.service;

import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Payment;
import event_booking_system.demo.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    // Tạo mới một payment
    Payment createPayment(Payment payment);

    Payment updatePayment(Payment payment);

    void deletePayment(String id);

    Payment findPaymentById(String id);

    List<Payment> findAllPayments();

    List<Payment> findPaymentsByStatus(PaymentStatus status);

    List<Payment> findPaymentsByOrderAndStatus(Order order, PaymentStatus status);
}
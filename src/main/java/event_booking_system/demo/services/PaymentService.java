package event_booking_system.demo.services;

import event_booking_system.demo.dtos.responses.vnpay.VNPayResponse;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Payment;
import event_booking_system.demo.enums.PaymentStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {

    Payment findById(String id);

    VNPayResponse createVNPayPayment(String orderId, HttpServletRequest request);

    boolean verifyVNPayPayment(Map<String, String> params, String orderId, String secureHash);

    Payment create(String orderId);
}
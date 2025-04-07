package event_booking_system.demo.services.impls;

import event_booking_system.demo.configs.VNPayConfig;
import event_booking_system.demo.dtos.responses.vnpay.VNPayResponse;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.entities.Payment;
import event_booking_system.demo.enums.OrderStatus;
import event_booking_system.demo.enums.PaymentMethod;
import event_booking_system.demo.enums.PaymentStatus;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.exceptions.CommonErrorCode;
import event_booking_system.demo.exceptions.order.OrderErrorCode;
import event_booking_system.demo.exceptions.order.OrderException;
import event_booking_system.demo.exceptions.payment.PaymentErrorCode;
import event_booking_system.demo.exceptions.payment.PaymentException;
import event_booking_system.demo.repositories.PaymentRepository;
import event_booking_system.demo.service.PaymentService;
import event_booking_system.demo.services.OrderService;
import event_booking_system.demo.vnpay.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static event_booking_system.demo.components.Translator.getLocalizedMessage;
import static event_booking_system.demo.exceptions.CommonErrorCode.PAYMENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;

    VNPayConfig vnPayConfig;

    OrderService orderService;

    @Override
    public Payment findById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, "Payment"));
    }

    @Override
    public VNPayResponse createVNPayPayment(long amount, String orderId, HttpServletRequest request) {
        long vnpAmount = amount * 100L;

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(vnpAmount));

        vnpParamsMap.put("vnp_TxnRef", orderId);

        vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));

        String queryUrl = VNPayUtils.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtils.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtils.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return new VNPayResponse("ok", getLocalizedMessage("payment_success"), paymentUrl);
    }

    @Override
    public boolean verifyVNPayPayment(Map<String, String> params, String orderId, String secureHash) {
        // Filter out the vnp_SecureHash parameter
        Map<String, String> filteredParams = params.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("vnp_SecureHash"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Recalculate the hash
        String calculatedHash = VNPayUtils.hmacSHA512(vnPayConfig.getSecretKey(), VNPayUtils.getPaymentURL(filteredParams, false));

        // Compare secure hashes
        if (!calculatedHash.equals(secureHash)) {
            return false;
        }

        // Get payment status from params
        String paymentStatus = params.get("vnp_ResponseCode");
        Double price = Double.parseDouble(params.get("vnp_Amount")) / 100;

        // Fetch the order using the orderId
        Order order = orderService.findOrderById(orderId);

        PaymentStatus paymentStatusEnum = "00".equals(paymentStatus) ? PaymentStatus.SUCCESS : PaymentStatus.PENDING;

        Payment payment = Payment.builder()
                .method(PaymentMethod.VNPAY)
                .price(price)
                .order(order)
                .status(paymentStatusEnum)
                .createdBy(order.getUser().getId())
                .build();

        paymentRepository.save(payment);

        paymentRepository.findById(payment.getId())
                .orElseThrow(() -> new AppException(CommonErrorCode.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND, "Payment"));

        return "00".equals(paymentStatus);
    }

    @Override
    public Payment create(double amount, String orderId) {
        Order order = orderService.findOrderById(orderId);

        if (amount <= 0) throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_INVALID, HttpStatus.BAD_REQUEST);
        if (order == null) throw new PaymentException(PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND, HttpStatus.NOT_FOUND);
        if (order.getStatus() != OrderStatus.PENDING) throw new OrderException(OrderErrorCode.BOOKING_CHECK_PENDING, HttpStatus.BAD_REQUEST);

        Payment payment = Payment.builder()
                .price(amount)
                .order(order)
                .method(PaymentMethod.CASH)
                .status(PaymentStatus.PENDING)
                .createdBy(order.getUser().getId())
                .build();

        return paymentRepository.save(payment);
    }
}
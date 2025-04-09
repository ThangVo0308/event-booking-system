package event_booking_system.demo.controllers;

import event_booking_system.demo.dtos.requests.payments.PaymentRequest;
import event_booking_system.demo.dtos.requests.vnpay.VNPayRequest;
import event_booking_system.demo.dtos.responses.payment.PaymentResponse;
import event_booking_system.demo.dtos.responses.vnpay.VNPayResponse;
import event_booking_system.demo.entities.OrderItem;
import event_booking_system.demo.mappers.OrderItemMapper;
import event_booking_system.demo.mappers.PaymentMapper;
import event_booking_system.demo.services.OrderItemService;
import event_booking_system.demo.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Payment APIs")
public class PaymentController {

    PaymentService paymentService;

    OrderItemService orderItemService;

    OrderItemMapper orderItemMapper = OrderItemMapper.INSTANCE;

    PaymentMapper paymentMapper = PaymentMapper.INSTANCE;

    // Card number: 9704198526191432198
    // Owner name: NGUYEN VAN A
    // Date: 07/15
    // Bank code: Base on card's type in API card:
    // https://sandbox.vnpayment.vn/apis/vnpay-demo
    @Operation(summary = "Create VNPay Payment", description = "Create VNPay Payment")
    @PostMapping("/vnpay")
    public ResponseEntity<VNPayResponse> createVNPayPayment(@RequestBody @Valid VNPayRequest payRequest,
                                                            HttpServletRequest request) {
//        long amount = payRequest.amount();
        String orderId = payRequest.orderId();

        VNPayResponse vnPayResponse = paymentService.createVNPayPayment(orderId, request);

        return ResponseEntity.ok(vnPayResponse);
    }

    @Operation(summary = "Create Payment", description = "Create Payment")
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody @Valid PaymentRequest request) {
        String orderId = request.orderId();
        return ResponseEntity.ok(paymentMapper.toPaymentResponse(paymentService.create(orderId)));
    }

    @Operation(summary = "VNPay Callback", description = "Handle VNPay payment callback")
    @GetMapping("/vn-pay-callback")
    public ResponseEntity<String> vnPayCallback(@RequestParam Map<String, String> params, HttpServletRequest request) {
        log.info("VNPay Callback received with params: {}", params);

        String orderId = params.get("vnp_TxnRef");
        String secureHash = params.get("vnp_SecureHash");

        boolean isVerified = paymentService.verifyVNPayPayment(params, orderId, secureHash);

        if (isVerified) {
            return ResponseEntity.ok("Payment processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Payment verification failed");
        }
    }


    @Operation(summary = "Get sport field price & bookingId", description = "Get sport field price & bookingId when user clicks on payment form", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/payment-info/{bookingId}")
    public ResponseEntity<Map<String, Object>> getPaymentInfo(@PathVariable String bookingItemsID) {
        OrderItem orderItem = orderItemService.findById(bookingItemsID);
        double totalPrice = orderItem.getPrice();
        return ResponseEntity.ok(
                Map.of("totalPrice", totalPrice, "bookingId", orderItemMapper.toOrderItemResponse(orderItem)));
    }

}
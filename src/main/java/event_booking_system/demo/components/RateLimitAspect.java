package event_booking_system.demo.components;

import com.nimbusds.jwt.SignedJWT;
import event_booking_system.demo.annotations.RateLimit;
import event_booking_system.demo.enums.RateLimitKeyType;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class RateLimitAspect {
    private final HttpServletRequest request;

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
    }

    private Map<String, Map<String, AtomicInteger>> requestCounts = new ConcurrentHashMap<>(); // count the number of called api by IP/Token

    @Before("@annotation(rateLimit) && execution(* *(..))")
    public void rateLimit(RateLimit rateLimit) throws ParseException {
        int limit = rateLimit.limit();
        int timeWindow = rateLimit.timeWindow();
        RateLimitKeyType[] keyTypes = rateLimit.keysType();

        for (RateLimitKeyType keyType: keyTypes) {
            String key = generateKeyInfo(keyType);

            requestCounts.put(key, new ConcurrentHashMap<>()); // new user calls API
            Map<String, AtomicInteger> map = requestCounts.get(key); //

            // Check if the request count has reached the limit bases on IP/Token
            AtomicInteger count = map.computeIfAbsent(
                    String.valueOf(System.currentTimeMillis()),
                    k -> new AtomicInteger(0)); // if key does not exist >> initialize count by 0

            if (count.incrementAndGet() > limit) {
                if (keyType == RateLimitKeyType.BY_TOKEN) {
                    throw new AuthenticationException(AuthenticationErrorCode.RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);
                }
                throw new AuthenticationException(
                        AuthenticationErrorCode.TOO_MANY_REQUESTS,
                        HttpStatus.TOO_MANY_REQUESTS
                );
            }
        }

    }

    private String generateKeyInfo(RateLimitKeyType keyType) throws ParseException {
        switch(keyType) {
            case BY_IP:
                String ipAddress = request.getRemoteAddr();
                String xForwardedForHeader = request.getHeader("X-Forwarded-For");

                if (xForwardedForHeader != null) {
                    ipAddress = xForwardedForHeader.split(",")[0];
                }
                return ipAddress;

            default:
                String authHeader = request.getHeader("Authorization"); // Authorization: Bearer <token>
                if(authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    SignedJWT signedJWT = SignedJWT.parse(token);
                    return signedJWT.getJWTClaimsSet().getJWTID(); // use JWT ID to check called API by user
                } else {
                    throw new AuthenticationException(AuthenticationErrorCode.TOKEN_MISSING, HttpStatus.UNAUTHORIZED);
                }
        }
    }


}

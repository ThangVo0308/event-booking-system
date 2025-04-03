package event_booking_system.demo.annotations;

import event_booking_system.demo.enums.RateLimitKeyType;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int limit() default 5;

    int timeWindow() default 60;

    RateLimitKeyType[] keysType() default{ RateLimitKeyType.BY_IP };
}

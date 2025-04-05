package event_booking_system.demo.validates;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AdultValidator.class)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {
    //default properties for annotation
    String message() default "must be >= 18 years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

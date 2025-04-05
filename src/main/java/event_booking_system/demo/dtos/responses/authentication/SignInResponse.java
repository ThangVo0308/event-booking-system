package event_booking_system.demo.dtos.responses.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import event_booking_system.demo.dtos.responses.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponse {

    TokensResponse tokensResponse;

    UserResponse userInfo;

}
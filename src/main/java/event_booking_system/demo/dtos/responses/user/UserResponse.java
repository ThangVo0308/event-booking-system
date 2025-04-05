package event_booking_system.demo.dtos.responses.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import event_booking_system.demo.enums.Gender;
import event_booking_system.demo.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    String id;

    String username;

    String email;

    String phone;

    LocalDate birthdate;

//    @JsonProperty("avatar")
//    String mAvatar;

    //    @JsonProperty("event")
//    String mEvent;

    @JsonProperty("roles")
    List<String> mRoles;

    Gender gender;

    UserStatus status;
}
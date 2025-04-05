package event_booking_system.demo.mappers;

import event_booking_system.demo.dtos.requests.authenications.SignUpRequest;
import event_booking_system.demo.dtos.responses.user.UserResponse;
import event_booking_system.demo.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(SignUpRequest request);

    UserResponse toUserResponse(User user);
}

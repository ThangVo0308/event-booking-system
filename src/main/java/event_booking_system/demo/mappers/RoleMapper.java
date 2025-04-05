package event_booking_system.demo.mappers;


import event_booking_system.demo.dtos.requests.roles.RoleRequest;
import event_booking_system.demo.dtos.responses.role.RoleResponse;
import event_booking_system.demo.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
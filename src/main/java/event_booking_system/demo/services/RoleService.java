package event_booking_system.demo.services;

import event_booking_system.demo.entities.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

    Role findById(String id);

    Role findByName(String name);

    boolean existsByName(String name);

    List<Role> findAllRoles();
}


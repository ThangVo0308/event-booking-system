package event_booking_system.demo.services;

import event_booking_system.demo.entities.Role;
import event_booking_system.demo.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);

    User findUserById(String id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    List<User> findAllUsers();

    Page<User> findAll(int offset, int limit);

    List<User> findUsersByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updatePassword(User user, String password);

    void activateUser(User user);

    Page<User> searchUsers(String keyword, int offset, int limit);
}

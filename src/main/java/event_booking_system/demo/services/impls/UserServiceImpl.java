package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Role;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.repositories.UserRepository;
import event_booking_system.demo.services.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) { return userRepository.save(user); }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND, NOT_FOUND));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(int offset, int limit) {
        return userRepository.findAll(PageRequest.of(offset, limit, Sort.by("createdAt").descending()));
    }

    @Override
    public List<User> findUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updatePassword(User user, String password) {
        User existingUser = findUserById(user.getId());

        String hashedPassword = passwordEncoder.encode(password);
        existingUser.setPassword(hashedPassword);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void activateUser(User user) {
        User existingUser = findUserById(user.getId());
        existingUser.setActivated(true);
        userRepository.save(existingUser);
    }

    @Override
    public Page<User> searchUsers(String keyword, int offset, int limit) {
        return null;
    }
}
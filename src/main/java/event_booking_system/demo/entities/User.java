package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import event_booking_system.demo.enums.Gender;
import event_booking_system.demo.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    String username;

    @Column(name = "password", nullable = false, length = 100)
    String password;

    @Column(name = "phone", nullable = false, length = 100)
    String phone;

    @Column(name = "birthdate", nullable = false)
    LocalDate birthdate;

    @Column(name = "gender", length = 20)
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    String email;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Event> event;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Order> order;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    UserStatus status;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    FileMetadata avatar;

    @Column(name = "is_activated", nullable = false)
    boolean isActivated;
}

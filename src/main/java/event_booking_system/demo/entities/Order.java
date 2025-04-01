package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import event_booking_system.demo.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_order_user", foreignKeyDefinition =
                    "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    User user;

    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_order_ticket", foreignKeyDefinition =
                    "FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    Ticket ticket;

    @Column(name = "quantity", nullable = false)
    Integer quantity;

    @Column(name = "total_price", nullable = false)
    Double price;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    Checkin checkin;
}

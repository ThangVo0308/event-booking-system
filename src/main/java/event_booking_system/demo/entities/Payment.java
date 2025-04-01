package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import event_booking_system.demo.enums.MediaType;
import event_booking_system.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_payment_order",
                    foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES order(id) ON DELETE CASCADE ON UPDATE CASCADE"),
            nullable = false, updatable = false)
    @JsonManagedReference
    Order order;

    @Column(nullable = false, name = "price")
    Double price;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}

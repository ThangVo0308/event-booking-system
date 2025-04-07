package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import event_booking_system.demo.enums.MediaType;
import event_booking_system.demo.enums.PaymentMethod;
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
@Table(name = "payments")
public class Payment extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    PaymentMethod method;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_payments_orders",
                    foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE"),
            nullable = false, updatable = false)
    @JsonManagedReference
    Order order;

    @Column(nullable = false, name = "price")
    Double price;

    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}

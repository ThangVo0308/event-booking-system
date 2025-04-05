package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "checkin")
public class Checkin extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_checkin_order",
                    foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES order(id) ON DELETE CASCADE ON UPDATE CASCADE"),
            nullable = false, updatable = false)
    @JsonManagedReference
    Order order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checkin_time", nullable = false)
    Date checkinTime;

    @Column(name = "qr_code", unique = true, nullable = false)
    String qrCode;
}

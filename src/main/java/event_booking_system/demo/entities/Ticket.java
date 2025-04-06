package event_booking_system.demo.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import event_booking_system.demo.enums.TicketType;
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
@Table(name = "tickets")
public class Ticket extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_tickets_events", foreignKeyDefinition =
                    "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    Event event;

    @Column(nullable = false, name="ticket_type")
    @Enumerated(EnumType.STRING)
    TicketType type;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "total_quantity", nullable = false)
    Integer total_quantity;

    @Column(name = "available_quantity", nullable = false)
    Integer available_quantity;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    List<Order> orders;
}

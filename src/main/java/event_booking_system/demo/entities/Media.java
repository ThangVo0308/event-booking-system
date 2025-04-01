package event_booking_system.demo.entities;

import event_booking_system.demo.enums.MediaType;
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
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_media_event", foreignKeyDefinition =
                    "FOREIGN KEY (event_id) REFERENCES event(id) ON DELETE CASCADE ON UPDATE CASCADE"))
    Event event;

    @Column(nullable = false, name = "media_type")
    @Enumerated(EnumType.STRING)
    MediaType type;
}

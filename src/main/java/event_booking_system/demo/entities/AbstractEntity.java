package event_booking_system.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @CreatedBy
    @Column(name = "created_by")
    String createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    Date createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Date updatedAt;

    @Version
    @Column(name = "version")
    Long version;


}

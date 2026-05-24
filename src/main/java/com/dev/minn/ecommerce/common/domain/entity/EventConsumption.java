package com.dev.minn.ecommerce.common.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "event_consumption",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_consumer",
                        columnNames = {"event_id", "consumer"}
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EventConsumption {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    UUID id;

    @Column(name = "event_id", nullable = false)
    String eventId;

    @Column(name = "consumer", nullable = false)
    String consumer;

    @CreatedDate
    @Column(name = "consumed_at", nullable = false, updatable = false)
    Instant consumedAt;
}

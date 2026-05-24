package com.dev.minn.ecommerce.notification.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "notification_messages",
        schema = "notification",
        indexes = {
                @Index(name = "idx_notification_message_status", columnList = "status"),
                @Index(name = "idx_notification_message_created_at", columnList = "created_at"),
                @Index(name = "idx_notification_message_reference", columnList = "reference_type, reference_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class NotificationMessage {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    NotificationTemplate template;

    @Column(name = "channel", nullable = false, length = 20)
    String channel;

    @Column(name = "recipient", nullable = false, length = 320)
    String recipient;

    @Column(name = "subject", columnDefinition = "TEXT")
    String subject;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", columnDefinition = "jsonb")
    @Builder.Default
    Map<String, Object> variables = new HashMap<>();

    @Column(name = "status", nullable = false, length = 20)
    String status;

    @Column(name = "reference_type", length = 50)
    String referenceType;

    @Column(name = "reference_id")
    String referenceId;

    @Column(name = "sent_at")
    Instant sentAt;

    @CreatedDate
    @Column(name = "created_at")
    Instant createdAt;
}

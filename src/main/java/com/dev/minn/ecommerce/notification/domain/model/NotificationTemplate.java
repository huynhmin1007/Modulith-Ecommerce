package com.dev.minn.ecommerce.notification.domain.model;

import com.dev.minn.ecommerce.common.domain.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "notification_templates",
        schema = "notification",
        indexes = {
                @Index(name = "idx_notification_template_code", columnList = "code")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplate extends AuditableEntity<UUID> {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    String code;

    @Column(name = "channel", nullable = false, length = 20)
    String channel;

    @Column(name = "subject_template", columnDefinition = "TEXT")
    String subjectTemplate;

    @Column(name = "body_template", nullable = false, columnDefinition = "TEXT")
    String bodyTemplate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> variables = new ArrayList<>();

    @Column(name = "active", nullable = false, columnDefinition = "boolean default false")
    boolean active;
}

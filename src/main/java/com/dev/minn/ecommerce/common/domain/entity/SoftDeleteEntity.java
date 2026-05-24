package com.dev.minn.ecommerce.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public abstract class SoftDeleteEntity<T extends Serializable> extends AuditableEntity<T> {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;
}

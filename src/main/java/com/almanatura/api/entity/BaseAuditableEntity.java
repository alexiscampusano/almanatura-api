package com.almanatura.api.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

/**
 * Common audit columns for every persistent entity:
 *
 * <ul>
 *   <li>{@code createdAt}/{@code updatedAt}: maintained by {@link AuditingEntityListener}.
 *   <li>{@code createdBy}/{@code lastModifiedBy}: filled from the {@code AuditorAware} bean
 *       (current user email or {@code "system"} for non-authenticated flows).
 *   <li>{@code version}: enables JPA optimistic locking — concurrent edits get an {@code
 *       OptimisticLockException} instead of silently overwriting each other.
 * </ul>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 180)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 180)
    private String lastModifiedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

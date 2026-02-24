package com.outforce.coupon.domain;

import com.outforce.coupon.shared.exception.BusinessException;
import com.outforce.coupon.shared.exception.CouponAlreadyDeletedException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Aggregate Root — Coupon.
 * All business rules live here; no rule leaks into use cases or controllers.
 */
public class Coupon {

    private static final BigDecimal MINIMUM_DISCOUNT = new BigDecimal("0.5");

    private final UUID id;
    private final CouponCode code;
    private final String description;
    private final BigDecimal discountValue;
    private final OffsetDateTime expirationDate;
    private final boolean published;
    private final boolean redeemed;
    private CouponStatus status;
    private OffsetDateTime deletedAt;
    private final OffsetDateTime createdAt;

    /** Factory method: creates a new coupon applying all business rules. */
    public static Coupon create(
            String rawCode,
            String description,
            BigDecimal discountValue,
            OffsetDateTime expirationDate,
            boolean published
    ) {
        CouponCode code = new CouponCode(rawCode);

        if (discountValue == null || discountValue.compareTo(MINIMUM_DISCOUNT) < 0) {
            throw new BusinessException(
                "Discount value must be at least %s.".formatted(MINIMUM_DISCOUNT)
            );
        }

        if (expirationDate == null) {
            throw new BusinessException("Expiration date must not be null.");
        }

        if (expirationDate.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Expiration date must not be in the past.");
        }

        if (description == null || description.isBlank()) {
            throw new BusinessException("Description must not be blank.");
        }

        return new Coupon(
            UUID.randomUUID(),
            code,
            description,
            discountValue,
            expirationDate,
            published,
            CouponStatus.ACTIVE,
            null,
            OffsetDateTime.now()
        );
    }

    /** Marks this coupon as deleted (soft delete). */
    public void delete() {
        if (this.status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException(this.id);
        }
        this.status = CouponStatus.DELETED;
        this.deletedAt = OffsetDateTime.now();
    }

    // -- Private constructor (use factory method) --

    private Coupon(
            UUID id,
            CouponCode code,
            String description,
            BigDecimal discountValue,
            OffsetDateTime expirationDate,
            boolean published,
            CouponStatus status,
            OffsetDateTime deletedAt,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.redeemed = false;
        this.status = status;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
    }

    /** Reconstruction constructor — used by persistence layer only. */
    public static Coupon reconstruct(
            UUID id,
            String code,
            String description,
            BigDecimal discountValue,
            OffsetDateTime expirationDate,
            boolean published,
            boolean redeemed,
            CouponStatus status,
            OffsetDateTime deletedAt,
            OffsetDateTime createdAt
    ) {
        Coupon c = new Coupon(
            id,
            new CouponCode(code),
            description,
            discountValue,
            expirationDate,
            published,
            status,
            deletedAt,
            createdAt
        );
        return c;
    }

    // -- Getters --

    public UUID getId() { return id; }
    public CouponCode getCode() { return code; }
    public String getDescription() { return description; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public OffsetDateTime getExpirationDate() { return expirationDate; }
    public boolean isPublished() { return published; }
    public boolean isRedeemed() { return redeemed; }
    public CouponStatus getStatus() { return status; }
    public OffsetDateTime getDeletedAt() { return deletedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

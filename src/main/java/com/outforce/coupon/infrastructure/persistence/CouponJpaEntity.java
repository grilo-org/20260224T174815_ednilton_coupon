package com.outforce.coupon.infrastructure.persistence;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class CouponJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private OffsetDateTime expirationDate;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean redeemed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    private OffsetDateTime deletedAt;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public static CouponJpaEntity from(Coupon coupon) {
        CouponJpaEntity entity = new CouponJpaEntity();
        entity.id = coupon.getId();
        entity.code = coupon.getCode().value();
        entity.description = coupon.getDescription();
        entity.discountValue = coupon.getDiscountValue();
        entity.expirationDate = coupon.getExpirationDate();
        entity.published = coupon.isPublished();
        entity.redeemed = coupon.isRedeemed();
        entity.status = coupon.getStatus();
        entity.deletedAt = coupon.getDeletedAt();
        entity.createdAt = coupon.getCreatedAt();
        return entity;
    }

    public Coupon toDomain() {
        return Coupon.reconstruct(
            id,
            code,
            description,
            discountValue,
            expirationDate,
            published,
            redeemed,
            status,
            deletedAt,
            createdAt
        );
    }
}

package com.outforce.coupon.application.create;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateCouponResponse(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        OffsetDateTime expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed
) {
    public static CreateCouponResponse from(Coupon coupon) {
        return new CreateCouponResponse(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
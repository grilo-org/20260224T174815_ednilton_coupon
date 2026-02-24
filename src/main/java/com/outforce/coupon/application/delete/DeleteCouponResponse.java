package com.outforce.coupon.application.delete;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DeleteCouponResponse(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        OffsetDateTime expirationDate,
        CouponStatus status,
        boolean published,
        boolean redeemed
) {
    public static DeleteCouponResponse from(Coupon coupon) {
        return new DeleteCouponResponse(
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
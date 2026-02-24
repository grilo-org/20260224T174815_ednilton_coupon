package com.outforce.coupon.shared.exception;

import java.util.UUID;

public class CouponAlreadyDeletedException extends BusinessException {
    public CouponAlreadyDeletedException(UUID id) {
        super("Coupon with id '%s' has already been deleted.".formatted(id));
    }
}

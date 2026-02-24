package com.outforce.coupon.shared.exception;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(UUID id) {
        super("Coupon not found with id: " + id);
    }
}

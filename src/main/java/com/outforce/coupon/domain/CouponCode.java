package com.outforce.coupon.domain;

import com.outforce.coupon.shared.exception.BusinessException;

/**
 * Value Object representing a coupon code.
 * Responsible for sanitizing (removing special characters) and validating
 * that the result is exactly 6 alphanumeric characters.
 */
public record CouponCode(String value) {

    private static final int REQUIRED_LENGTH = 6;

    public CouponCode {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Coupon code must not be blank.");
        }

        String sanitized = value.replaceAll("[^a-zA-Z0-9]", "");

        if (sanitized.length() != REQUIRED_LENGTH) {
            throw new BusinessException(
                "Coupon code must have exactly %d alphanumeric characters after removing special characters. Got: %d"
                    .formatted(REQUIRED_LENGTH, sanitized.length())
            );
        }

        value = sanitized.toUpperCase();
    }

    @Override
    public String toString() {
        return value;
    }
}

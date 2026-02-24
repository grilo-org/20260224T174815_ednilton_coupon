package com.outforce.coupon.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port — domain does not depend on persistence technology.
 */
public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
}

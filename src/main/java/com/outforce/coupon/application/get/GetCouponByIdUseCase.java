package com.outforce.coupon.application.get;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponRepository;
import com.outforce.coupon.shared.exception.CouponNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case: Retrieve a coupon by its ID.
 */
@Component
public class GetCouponByIdUseCase {

    private final CouponRepository repository;

    public GetCouponByIdUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public GetCouponResponse execute(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));

        return GetCouponResponse.from(coupon);
    }
}
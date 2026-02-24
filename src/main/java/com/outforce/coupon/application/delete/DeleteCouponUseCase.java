package com.outforce.coupon.application.delete;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponRepository;
import com.outforce.coupon.shared.exception.CouponNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Use case: Delete a coupon (soft delete).
 * Enforces: coupon must exist and must not already be deleted.
 */
@Component
public class DeleteCouponUseCase {

    private final CouponRepository repository;

    public DeleteCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public DeleteCouponResponse execute(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));

        coupon.delete(); // throws CouponAlreadyDeletedException if already deleted

        Coupon saved = repository.save(coupon);
        return DeleteCouponResponse.from(saved);
    }
}
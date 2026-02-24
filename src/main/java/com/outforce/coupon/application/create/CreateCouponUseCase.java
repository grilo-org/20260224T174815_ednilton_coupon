package com.outforce.coupon.application.create;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponRepository;
import org.springframework.stereotype.Component;

/**
 * Use case: Create a new coupon.
 * Single responsibility — delegates all business rules to the domain.
 */
@Component
public class CreateCouponUseCase {

    private final CouponRepository repository;

    public CreateCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public CreateCouponResponse execute(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
            command.code(),
            command.description(),
            command.discountValue(),
            command.expirationDate(),
            command.published()
        );

        Coupon saved = repository.save(coupon);
        return CreateCouponResponse.from(saved);
    }
}

package com.outforce.coupon.infrastructure.persistence;

import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.from(coupon);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findById(id).map(CouponJpaEntity::toDomain);
    }
}

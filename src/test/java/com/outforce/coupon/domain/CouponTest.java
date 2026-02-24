package com.outforce.coupon.domain;

import com.outforce.coupon.shared.exception.BusinessException;
import com.outforce.coupon.shared.exception.CouponAlreadyDeletedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    private final OffsetDateTime futureDate = OffsetDateTime.now().plusDays(10);

    @Test
    void shouldCreateCouponSuccessfully() {
        Coupon coupon = Coupon.create("ABC123", "Discount coupon", new BigDecimal("1.0"), futureDate, false);

        assertThat(coupon.getId()).isNotNull();
        assertThat(coupon.getCode().value()).isEqualTo("ABC123");
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(coupon.isRedeemed()).isFalse();
    }

    @Test
    void shouldCreateCouponAsPublished() {
        Coupon coupon = Coupon.create("ABC123", "Discount", new BigDecimal("1.0"), futureDate, true);
        assertThat(coupon.isPublished()).isTrue();
    }

    @Test
    void shouldFailWhenExpirationDateIsInThePast() {
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);

        assertThatThrownBy(() -> Coupon.create("ABC123", "desc", new BigDecimal("1.0"), pastDate, false))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("past");
    }

    @Test
    void shouldFailWhenDiscountValueIsBelowMinimum() {
        assertThatThrownBy(() -> Coupon.create("ABC123", "desc", new BigDecimal("0.4"), futureDate, false))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("0.5");
    }

    @Test
    void shouldFailWhenDiscountValueIsNull() {
        assertThatThrownBy(() -> Coupon.create("ABC123", "desc", null, futureDate, false))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("0.5");
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        assertThatThrownBy(() -> Coupon.create("ABC123", "  ", new BigDecimal("1.0"), futureDate, false))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Description");
    }

    @Test
    void shouldAcceptMinimumDiscountValue() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("0.5"), futureDate, false);
        assertThat(coupon.getDiscountValue()).isEqualByComparingTo("0.5");
    }

    @Test
    void shouldSoftDeleteCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1.0"), futureDate, false);
        coupon.delete();

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldNotDeleteAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", new BigDecimal("1.0"), futureDate, false);
        coupon.delete();

        assertThatThrownBy(coupon::delete)
            .isInstanceOf(CouponAlreadyDeletedException.class)
            .hasMessageContaining("already been deleted");
    }
}

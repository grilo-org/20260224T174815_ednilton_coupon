package com.outforce.coupon.application;

import com.outforce.coupon.application.create.CreateCouponCommand;
import com.outforce.coupon.application.create.CreateCouponResponse;
import com.outforce.coupon.application.create.CreateCouponUseCase;
import com.outforce.coupon.domain.CouponStatus;
import com.outforce.coupon.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CreateCouponUseCaseTest {

    @Autowired
    private CreateCouponUseCase useCase;

    private final OffsetDateTime futureDate = OffsetDateTime.now().plusDays(10);

    @Test
    void shouldCreateCouponAndPersistCorrectly() {
        CreateCouponCommand command = new CreateCouponCommand(
            "ABC-123", "Test coupon", new BigDecimal("1.5"), futureDate, false
        );

        CreateCouponResponse response = useCase.execute(command);

        assertThat(response.id()).isNotNull();
        assertThat(response.code()).isEqualTo("ABC123"); // special char removed
        assertThat(response.status()).isEqualTo(CouponStatus.ACTIVE);
        assertThat(response.redeemed()).isFalse();
    }

    @Test
    void shouldCreateCouponAsPublished() {
        CreateCouponCommand command = new CreateCouponCommand(
            "PUB123", "Published coupon", new BigDecimal("2.0"), futureDate, true
        );

        CreateCouponResponse response = useCase.execute(command);

        assertThat(response.published()).isTrue();
    }

    @Test
    void shouldRejectCouponWithPastExpirationDate() {
        CreateCouponCommand command = new CreateCouponCommand(
            "ABC123", "desc", new BigDecimal("1.0"), OffsetDateTime.now().minusDays(1), false
        );

        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("past");
    }

    @Test
    void shouldRejectCouponWithDiscountBelowMinimum() {
        CreateCouponCommand command = new CreateCouponCommand(
            "ABC123", "desc", new BigDecimal("0.1"), futureDate, false
        );

        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("0.5");
    }

    @Test
    void shouldSanitizeCodeWithSpecialCharactersAndPersist() {
        CreateCouponCommand command = new CreateCouponCommand(
            "A-B-C-1-2-3", "desc", new BigDecimal("1.0"), futureDate, false
        );

        CreateCouponResponse response = useCase.execute(command);

        assertThat(response.code()).isEqualTo("ABC123");
    }

    @Test
    void shouldRejectCodeThatResultsInMoreThan6CharsAfterSanitization() {
        CreateCouponCommand command = new CreateCouponCommand(
            "ABCDEFG", "desc", new BigDecimal("1.0"), futureDate, false
        );

        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(BusinessException.class);
    }
}

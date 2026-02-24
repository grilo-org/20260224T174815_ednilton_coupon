package com.outforce.coupon.application;

import com.outforce.coupon.application.create.CreateCouponCommand;
import com.outforce.coupon.application.create.CreateCouponResponse;
import com.outforce.coupon.application.create.CreateCouponUseCase;
import com.outforce.coupon.application.delete.DeleteCouponUseCase;
import com.outforce.coupon.domain.Coupon;
import com.outforce.coupon.domain.CouponRepository;
import com.outforce.coupon.domain.CouponStatus;
import com.outforce.coupon.shared.exception.CouponAlreadyDeletedException;
import com.outforce.coupon.shared.exception.CouponNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DeleteCouponUseCaseTest {

    @Autowired
    private CreateCouponUseCase createUseCase;

    @Autowired
    private DeleteCouponUseCase deleteUseCase;

    @Autowired
    private CouponRepository repository;

    private final OffsetDateTime futureDate = OffsetDateTime.now().plusDays(10);

    private CreateCouponResponse createTestCoupon() {
        return createUseCase.execute(new CreateCouponCommand(
            "TST001", "Test coupon", new BigDecimal("1.0"), futureDate, false
        ));
    }

    @Test
    void shouldSoftDeleteCouponSuccessfully() {
        CreateCouponResponse created = createTestCoupon();

        deleteUseCase.execute(created.id());

        Optional<Coupon> found = repository.findById(created.id());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(found.get().getDeletedAt()).isNotNull();
    }

    @Test
    void shouldNotPhysicallyRemoveDataOnDelete() {
        CreateCouponResponse created = createTestCoupon();

        deleteUseCase.execute(created.id());

        // Record must still exist in database
        Optional<Coupon> found = repository.findById(created.id());
        assertThat(found).isPresent();
        assertThat(found.get().getCode().value()).isEqualTo(created.code());
        assertThat(found.get().getDescription()).isNotBlank();
    }

    @Test
    void shouldNotAllowDeletingAnAlreadyDeletedCoupon() {
        CreateCouponResponse created = createTestCoupon();

        deleteUseCase.execute(created.id());

        assertThatThrownBy(() -> deleteUseCase.execute(created.id()))
            .isInstanceOf(CouponAlreadyDeletedException.class)
            .hasMessageContaining("already been deleted");
    }

    @Test
    void shouldThrowNotFoundWhenCouponDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();

        assertThatThrownBy(() -> deleteUseCase.execute(nonExistentId))
            .isInstanceOf(CouponNotFoundException.class)
            .hasMessageContaining(nonExistentId.toString());
    }
}

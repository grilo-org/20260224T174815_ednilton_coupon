package com.outforce.coupon.domain;

import com.outforce.coupon.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponCodeTest {

    @Test
    void shouldCreateValidCode() {
        CouponCode code = new CouponCode("ABC123");
        assertThat(code.value()).isEqualTo("ABC123");
    }

    @Test
    void shouldRemoveSpecialCharactersAndKeep6AlphanumericChars() {
        CouponCode code = new CouponCode("ABC-123");
        assertThat(code.value()).isEqualTo("ABC123");
    }

    @Test
    void shouldConvertToUppercase() {
        CouponCode code = new CouponCode("abc123");
        assertThat(code.value()).isEqualTo("ABC123");
    }

    @Test
    void shouldFailWhenAlphanumericCharsAreLessThan6AfterSanitization() {
        assertThatThrownBy(() -> new CouponCode("AB-1"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("exactly 6 alphanumeric characters");
    }

    @Test
    void shouldFailWhenAlphanumericCharsAreMoreThan6AfterSanitization() {
        assertThatThrownBy(() -> new CouponCode("ABCDEFG"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("exactly 6 alphanumeric characters");
    }

    @Test
    void shouldFailWhenCodeIsNull() {
        assertThatThrownBy(() -> new CouponCode(null))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void shouldFailWhenCodeIsBlank() {
        assertThatThrownBy(() -> new CouponCode("   "))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("must not be blank");
    }

    @Test
    void shouldFailWhenOnlySpecialCharacters() {
        assertThatThrownBy(() -> new CouponCode("---!!!"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("exactly 6 alphanumeric characters");
    }
}

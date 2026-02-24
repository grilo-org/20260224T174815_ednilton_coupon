package com.outforce.coupon.infrastructure.web;

import com.outforce.coupon.application.create.CreateCouponCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Request payload to create a coupon")
public record CouponRequest(

        @NotBlank(message = "code is required")
        @Schema(example = "ABC-123", description = "Alphanumeric code (6 chars); special chars are stripped automatically")
        String code,

        @NotBlank(message = "description is required")
        String description,

        @NotNull(message = "discountValue is required")
        @Schema(example = "0.8", description = "Minimum value: 0.5")
        BigDecimal discountValue,

        @NotNull(message = "expirationDate is required")
        @Schema(example = "2025-11-04T17:14:45.180Z")
        OffsetDateTime expirationDate,

        @Schema(example = "false")
        boolean published
) {
    public CreateCouponCommand toCommand() {
        return new CreateCouponCommand(code, description, discountValue, expirationDate, published);
    }
}

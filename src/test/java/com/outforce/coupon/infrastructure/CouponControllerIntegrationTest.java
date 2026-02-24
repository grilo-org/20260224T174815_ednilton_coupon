package com.outforce.coupon.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outforce.coupon.application.create.CreateCouponCommand;
import com.outforce.coupon.application.create.CreateCouponResponse;
import com.outforce.coupon.application.create.CreateCouponUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateCouponUseCase createCouponUseCase;

    private final OffsetDateTime futureDate = OffsetDateTime.now().plusDays(10);

    @Test
    void shouldReturn201WhenCreatingValidCoupon() throws Exception {
        Map<String, Object> body = Map.of(
            "code", "ABC-123",
            "description", "Test discount coupon",
            "discountValue", 1.5,
            "expirationDate", futureDate.toString(),
            "published", false
        );

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.code").value("ABC123")) // special char stripped
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    void shouldReturn422WhenExpirationDateIsInThePast() throws Exception {
        Map<String, Object> body = Map.of(
            "code", "ABC123",
            "description", "desc",
            "discountValue", 1.0,
            "expirationDate", OffsetDateTime.now().minusDays(1).toString(),
            "published", false
        );

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn422WhenDiscountIsTooLow() throws Exception {
        Map<String, Object> body = Map.of(
            "code", "ABC123",
            "description", "desc",
            "discountValue", 0.1,
            "expirationDate", futureDate.toString(),
            "published", false
        );

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn400WhenRequiredFieldIsMissing() throws Exception {
        Map<String, Object> body = Map.of(
            "description", "desc",
            "discountValue", 1.0,
            "expirationDate", futureDate.toString()
            // missing "code"
        );

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn204WhenDeletingExistingCoupon() throws Exception {
        CreateCouponResponse created = createCouponUseCase.execute(new CreateCouponCommand(
            "DEL001", "To be deleted", new BigDecimal("1.0"), futureDate, false
        ));

        mockMvc.perform(delete("/coupon/" + created.id()))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenDeletingAlreadyDeletedCoupon() throws Exception {
        CreateCouponResponse created = createCouponUseCase.execute(new CreateCouponCommand(
            "DEL002", "To be deleted twice", new BigDecimal("1.0"), futureDate, false
        ));

        mockMvc.perform(delete("/coupon/" + created.id()))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/coupon/" + created.id()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCoupon() throws Exception {
        mockMvc.perform(delete("/coupon/00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound());
    }
}

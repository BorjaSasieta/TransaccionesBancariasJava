package com.bankflow.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Nested
    class record_test {

        @Test
        void shouldCreateApiError() {
            OffsetDateTime now = OffsetDateTime.now();
            ApiError error = new ApiError(now, 400, "BAD_REQUEST", "Invalid input");

            assertThat(error.timestamp()).isEqualTo(now);
            assertThat(error.status()).isEqualTo(400);
            assertThat(error.error()).isEqualTo("BAD_REQUEST");
            assertThat(error.message()).isEqualTo("Invalid input");
        }
    }
}
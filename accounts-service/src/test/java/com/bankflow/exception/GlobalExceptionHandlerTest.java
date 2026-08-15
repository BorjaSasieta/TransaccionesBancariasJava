package com.bankflow.exception;

import com.bankflow.transfers.exception.TransferInProgressException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void transferInProgress_shouldReturnAccepted() {
        TransferInProgressException ex = new TransferInProgressException(1L);
        ResponseEntity<ApiError> response = handler.transferInProgress(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(202);
        assertThat(body.error()).isEqualTo("TRANSFER_IN_PROGRESS");
        assertThat(body.message()).contains("Transfer with id 1");
    }

    @Test
    void dataIntegrityViolation_shouldReturnConflict() {
        org.springframework.dao.DataIntegrityViolationException ex = mock(org.springframework.dao.DataIntegrityViolationException.class);
        ResponseEntity<ApiError> response = handler.conflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(409);
        assertThat(body.error()).isEqualTo("CONFLICT");
        assertThat(body.message()).isEqualTo("Duplicate request");
    }

    @Test
    void illegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid field");
        ResponseEntity<ApiError> response = handler.badRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.error()).isEqualTo("BAD_REQUEST");
        assertThat(body.message()).isEqualTo("Invalid field");
    }
}
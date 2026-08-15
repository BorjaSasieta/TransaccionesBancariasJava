package com.bankflow.exception;

import com.bankflow.transfers.exception.TransferInProgressException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void transferInProgress_shouldReturnAccepted() {
        TransferInProgressException ex = new TransferInProgressException(1L);
        ResponseEntity<ApiError> response = handler.transferInProgress(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(202);
        assertThat(response.getBody().error()).isEqualTo("TRANSFER_IN_PROGRESS");
    }

    @Test
    void dataIntegrityViolation_shouldReturnConflict() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        ResponseEntity<ApiError> response = handler.conflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Duplicate request");
    }

    @Test
    void illegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid field");
        ResponseEntity<ApiError> response = handler.badRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Invalid field");
    }

    @Test
    void methodArgumentNotValid_shouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        // Si tu handler utiliza detalles de ex, haz when(...) según sea necesario.
        ResponseEntity<ApiError> response = handler.validation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Invalid request body");
    }
}
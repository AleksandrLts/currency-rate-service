package currency.rate.service.exception;

import currency.rate.service.model.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, Exception ex) {
        return buildErrorResponse(status, ex, List.of(ex.getMessage()));
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, Exception ex, List<String> errorMessages) {
        String messageError = "Error";
        List<String> stackTraceElements = new ArrayList<>();
        if (ex != null) {
            messageError = ex.getMessage();
            stackTraceElements = List.of(Arrays.toString(ex.getStackTrace()));
        }
        log.error("Exception: {}", messageError);

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .time(LocalDateTime.now())
                .statusCode(status)
                .errorMessage(List.of(messageError))
                .stackTrace(stackTraceElements)
                .build();

        return ResponseEntity.status(status).body(errorDto);
    }

}

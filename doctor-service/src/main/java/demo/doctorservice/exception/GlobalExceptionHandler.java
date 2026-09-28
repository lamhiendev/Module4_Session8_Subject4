package demo.doctorservice.exception;

import demo.doctorservice.dto.DoctorErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<DoctorErrorResponse> handleRateLimitException(RequestNotPermitted ex) {
        DoctorErrorResponse doctorErrorResponse = DoctorErrorResponse.builder()
                .timeStamp(Instant.now()).errorCode(HttpStatus.TOO_MANY_REQUESTS.value()).error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase()).message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(doctorErrorResponse);
    }

    @ExceptionHandler(DoctorServiceUnavaialbeException.class)
    public ResponseEntity<DoctorErrorResponse> doctorErrorResponseResponseEntity(DoctorServiceUnavaialbeException e){
        return new ResponseEntity<>(new DoctorErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage()
        ),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DoctorErrorResponse> exceptionHandler(Exception e){
        return new ResponseEntity<>(new DoctorErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage()
        ),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

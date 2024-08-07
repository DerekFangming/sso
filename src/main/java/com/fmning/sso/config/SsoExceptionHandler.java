package com.fmning.sso.config;

import com.fmning.sso.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@CommonsLog
@ControllerAdvice
public class SsoExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseDto> handleException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            log.error("Invalid request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
        }

        if (e instanceof AccessDeniedException) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ErrorDto("Unauthorized"));
        }

        log.error("Unhandled exception", e);
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Internal server error"));
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    static class ErrorDto extends BaseDto {
        String message;
    }

}

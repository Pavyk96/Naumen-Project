package naumen.java.project.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO-ответ для отображения информации об ошибках
 *
 * @author Daria
 */
public record ExceptionResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        String errorCode
) {
    public ExceptionResponse(HttpStatus status, String message, String path, String errorCode) {
        this(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now(),
                errorCode
        );
    }

    public ExceptionResponse(HttpStatus status, String message, String path) {
        this(status, message, path, null);
    }
}

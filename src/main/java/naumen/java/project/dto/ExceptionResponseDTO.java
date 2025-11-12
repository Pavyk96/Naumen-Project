package naumen.java.project.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO-ответ для отображения информации об ошибках
 *
 * @author Daria
 */
public record ExceptionResponseDTO(
        /** HTTP статус код */
        int status,
        /** Описание ошибки */
        String error,
        /** Сообщение об ошибке */
        String message,
        /** Путь запроса */
        String path,
        /** Время возникновения ошибки */
        LocalDateTime timestamp,
        /** Код ошибки */
        String errorCode
) {
    public ExceptionResponseDTO(HttpStatus status, String message, String path, String errorCode) {
        this(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now(),
                errorCode
        );
    }

    public ExceptionResponseDTO(HttpStatus status, String message, String path) {
        this(status, message, path, null);
    }
}

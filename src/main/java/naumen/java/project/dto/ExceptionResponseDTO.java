package naumen.java.project.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO-ответ для отображения информации об ошибках
 *
 * @param status HTTP статус код
 * @param error Описание ошибки
 * @param message Сообщение об ошибке
 * @param path Путь запроса
 * @param timestamp Время возникновения ошибки
 * @param errorCode Код ошибки
 *
 * @author Daria
 */
public record ExceptionResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
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

package naumen.java.project.exepction;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import naumen.java.project.dto.ExceptionResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров
 *
 * @author Daria
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатка исключений ненайденный сущностей entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFound(EntityNotFoundException ex,
                                                                  HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                "ENTITY_NOT_FOUND"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Обрабатка бизнес-логических исключений аргументов
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestArgument(RuntimeException ex,
                                                                      HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI(),
                "INVALID_INPUT"
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Обрабатка бизнес-логических исключений состояний сущности
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestState(RuntimeException ex,
                                                                   HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI(),
                "ILLEGAL_STATE"
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Обрабатка всех неперехваченных исключений
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllUncaught(Exception ex,
                                                               HttpServletRequest request) {

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Обрабатка исключений валидации входных данных в dto
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " +
                        fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                request.getRequestURI(),
                "VALIDATION_FAILED"
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Обрабатка исключений валидации параметров методов
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex,
                                                                           HttpServletRequest request) {

        String errorMessage = ex.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(MessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");

        ExceptionResponse error = new ExceptionResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                request.getRequestURI(),
                "VALIDATION_FAILED"
        );

        return ResponseEntity.badRequest().body(error);
    }
}

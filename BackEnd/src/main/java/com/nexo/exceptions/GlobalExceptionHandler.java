package com.nexo.exceptions;

import com.nexo.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Spring registra essa classe como parte do mecanismo global de tratamento de exceções.
// Quando uma exceção escapa de um controller, o Spring procura um @ExceptionHandler compatível.
// Não é necessario chamar o Handler no controller
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    // when the exception above is raised, execute this method
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException e)
    {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Unauthorized",
                e.getMessage(),
                401
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    // when the exception above is raised, execute this method
    public ResponseEntity<ErrorResponseDTO> handleUsernameAlreadyExists(
            UsernameAlreadyExistsException e)
    {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Conflict",
                e.getMessage(),
                409
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(
            EmailAlreadyExistsException e)
    {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Conflict",
                e.getMessage(),
                409
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidEmailException(
            InvalidEmailException e)
    {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Conflict",
                e.getMessage(),
                409
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidRefreshToken(
            InvalidRefreshTokenException e) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Unauthorized",
                e.getMessage(),
                401
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(StudyTrailNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleStudyTrailNotFoundException(
            StudyTrailNotFoundException e){

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Not Found",
                e.getMessage(),
                404
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(InvalidPageNumberException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPageException(
            InvalidPageNumberException e){

        ErrorResponseDTO error = new ErrorResponseDTO(
                // Resource exists, but the request contain invalid parameters
                "Bad Request",
                e.getMessage(),
                400
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException e){

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Bad Request",
                e.getMessage(),
                400
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}


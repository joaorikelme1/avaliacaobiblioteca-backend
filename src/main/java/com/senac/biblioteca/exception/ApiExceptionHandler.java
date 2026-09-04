package com.senac.biblioteca.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ErroCampo> erros = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroCampo(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return criarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos.", request, erros);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarParametroInvalido(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O parâmetro '" + exception.getName() + "' possui valor inválido.",
                request,
                List.of()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResposta> tratarRestricoes(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<ErroCampo> erros = exception.getConstraintViolations().stream()
                .map(erro -> new ErroCampo(erro.getPropertyPath().toString(), erro.getMessage()))
                .toList();

        return criarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos.", request, erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarJsonInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "O corpo da requisição está ausente ou possui formato inválido.",
                request,
                List.of()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResposta> tratarStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        String mensagem = exception.getReason() != null
                ? exception.getReason()
                : obterDescricaoStatus(exception.getStatusCode());

        return criarResposta(exception.getStatusCode(), mensagem, request, List.of());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroResposta> tratarRecursoInexistente(
            NoSuchElementException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado.",
                request,
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarIntegridade(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "A operação viola a integridade dos dados.",
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroInesperado(
            Exception exception,
            HttpServletRequest request
    ) {
        if (exception instanceof ErrorResponse errorResponse) {
            String mensagem = errorResponse.getBody().getDetail();
            if (mensagem == null || mensagem.isBlank()) {
                mensagem = obterDescricaoStatus(errorResponse.getStatusCode());
            }

            return criarResposta(
                    errorResponse.getStatusCode(),
                    mensagem,
                    request,
                    List.of()
            );
        }

        LOGGER.error("Erro inesperado na requisição {}", request.getRequestURI(), exception);

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado.",
                request,
                List.of()
        );
    }

    private ResponseEntity<ErroResposta> criarResposta(
            HttpStatusCode status,
            String mensagem,
            HttpServletRequest request,
            List<ErroCampo> erros
    ) {
        ErroResposta resposta = new ErroResposta(
                OffsetDateTime.now(),
                status.value(),
                obterDescricaoStatus(status),
                mensagem,
                request.getRequestURI(),
                erros
        );

        return ResponseEntity.status(status).body(resposta);
    }

    private String obterDescricaoStatus(HttpStatusCode status) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        return httpStatus != null ? httpStatus.getReasonPhrase() : "Erro HTTP";
    }

    public record ErroCampo(String field, String message) {
    }

    public record ErroResposta(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            List<ErroCampo> errors
    ) {
    }
}

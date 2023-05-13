package com.shai.to_do.exception.handler;

import com.shai.to_do.constants.Queries;
import com.shai.to_do.dto.response.ExceptionDTO;
import com.shai.to_do.dto.response.ResponseDTOFactory;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = {"com.shai.to_do.controller"})
public class ControllerAdvice {

    private final ResponseDTOFactory responseDTOFactory;

    private static final Logger logger = LogManager.getLogger("todo-logger");

    public ControllerAdvice(ResponseDTOFactory responseDTOFactory) {
        this.responseDTOFactory = responseDTOFactory;
    }

    @ExceptionHandler(TodoAlreadyExistsException.class)
    public ResponseEntity<ExceptionDTO> handleTodoAlreadyExistsException(TodoAlreadyExistsException ex) {
        ExceptionDTO exceptionDTO = (ExceptionDTO) responseDTOFactory.getResponseDTO(Queries.EXCEPTION);
        exceptionDTO.setErrorMessage(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);
    }

    @ExceptionHandler(DueDateExpiredException.class)
    public ResponseEntity<ExceptionDTO> handleDueDateAlreadyPassedException(DueDateExpiredException ex) {
        ExceptionDTO exceptionDTO = (ExceptionDTO) responseDTOFactory.getResponseDTO(Queries.EXCEPTION);
        exceptionDTO.setErrorMessage(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDTO> handleBadRequestException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ExceptionDTO exceptionDTO = (ExceptionDTO) responseDTOFactory.getResponseDTO(Queries.EXCEPTION);
        exceptionDTO.setErrorMessage(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }
}

package com.shai.to_do.unit.validator;

import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.repository.TodoRepository;
import com.shai.to_do.validators.TodoValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class TodoValidatorTest {
    private TodoValidate todoValidate;

    @Mock
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todoValidate = new TodoValidate(todoRepository);
    }

    @Test
    void validateAdd_shouldPass() throws TodoAlreadyExistsException, DueDateExpiredException {
        // Arrange
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() + 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void validateAdd_shouldThrowTodoAlreadyExistsException() throws TodoAlreadyExistsException, DueDateExpiredException {
        // Arrange
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() + 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(true);

        // Act & Assert
        assertThrows(TodoAlreadyExistsException.class, () -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void validateAdd_shouldThrowDueDateExpiredException() throws TodoAlreadyExistsException, DueDateExpiredException {
        // Arrange
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() - 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(false);

        assertThrows(DueDateExpiredException.class, () -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void givenValidInput_whenValidateUpdateStatus_thenNoExceptionThrown() {
        // Arrange
        Integer id = 1;
        String status = Status.PENDING;
        when(todoRepository.existsById(id)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    void givenInvalidStatus_whenValidateUpdateStatus_thenBadRequestExceptionThrown() {
        // Arrange
        Integer id = 1;
        String status = "INVALID";
        when(todoRepository.existsById(id)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    void givenNonExistentId_whenValidateUpdateStatus_thenResourceNotFoundExceptionThrown() {
        // Arrange
        Integer id = 1;
        String status = Status.PENDING;
        when(todoRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    public void validateDeleteById_shouldThrowResourceNotFoundException_whenTodoIdDoesNotExist() {
        // Arrange
        when(todoRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> todoValidate.validateDeleteById(1));
    }

    @Test
    public void validateDeleteById_shouldNotThrowException_whenTodoIdExists() {
        // Arrange
        when(todoRepository.existsById(anyInt())).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> todoValidate.validateDeleteById(1));
    }

    @Test
    void givenValidStatusAndSortByParams_whenValidateGetTodoContentByStatusSortedByField_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> todoValidate.validateGetTodoContentByStatusSortedByField(Status.ALL, SortBy.ID));
        assertDoesNotThrow(() -> todoValidate.validateGetTodoContentByStatusSortedByField(Status.PENDING, SortBy.TITLE));
        assertDoesNotThrow(() -> todoValidate.validateGetTodoContentByStatusSortedByField(Status.DONE, SortBy.DUE_DATE));
        assertDoesNotThrow(() -> todoValidate.validateGetTodoContentByStatusSortedByField(Status.LATE, SortBy.ID));
    }

    @Test
    void givenInvalidStatusOrSortByParams_whenValidateGetTodoContentByStatusSortedByField_thenBadRequestExceptionThrown() {
        assertThrows(BadRequestException.class, () -> todoValidate.validateGetTodoContentByStatusSortedByField(null, SortBy.ID));
        assertThrows(BadRequestException.class, () -> todoValidate.validateGetTodoContentByStatusSortedByField("invalid_status", SortBy.ID));
        assertThrows(BadRequestException.class, () -> todoValidate.validateGetTodoContentByStatusSortedByField(Status.PENDING, "invalid_sort_by"));
    }

    @Test
    void shouldNotThrowExceptionWhenValidStatus() {
        assertDoesNotThrow(() -> todoValidate.validateCountByStatus(Status.PENDING));
        assertDoesNotThrow(() -> todoValidate.validateCountByStatus(Status.ALL));
        assertDoesNotThrow(() -> todoValidate.validateCountByStatus(Status.DONE));
        assertDoesNotThrow(() -> todoValidate.validateCountByStatus(Status.LATE));
    }

    @Test
    void shouldThrowExceptionWhenInvalidStatus() {
        // Arrange
        String status = "invalid status";

        // Act + Assert
        assertThrows(BadRequestException.class, () -> todoValidate.validateCountByStatus(status));
    }
}

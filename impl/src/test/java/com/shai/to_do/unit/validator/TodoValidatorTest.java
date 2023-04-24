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
    void validateAdd_shouldPass() {
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() + 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(false);
        assertDoesNotThrow(() -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void validateAdd_shouldThrowTodoAlreadyExistsException() {
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() + 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(true);
        assertThrows(TodoAlreadyExistsException.class, () -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void validateAdd_shouldThrowDueDateExpiredException() {
        TodoDTO todoDTO = new TodoDTO("Todo Title", "Todo Content", System.currentTimeMillis() - 100000L);

        when(todoRepository.existsByTitle(todoDTO.title())).thenReturn(false);
        assertThrows(DueDateExpiredException.class, () -> todoValidate.validateAdd(todoDTO));
    }

    @Test
    void givenValidInput_whenValidateUpdateStatus_thenNoExceptionThrown() {
        Integer id = 1;
        String status = Status.PENDING;

        when(todoRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    void givenInvalidStatus_whenValidateUpdateStatus_thenBadRequestExceptionThrown() {
        Integer id = 1;
        String status = "INVALID";

        when(todoRepository.existsById(id)).thenReturn(true);
        assertThrows(BadRequestException.class, () -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    void givenNonExistentId_whenValidateUpdateStatus_thenResourceNotFoundExceptionThrown() {
        Integer id = 1;
        String status = Status.PENDING;

        when(todoRepository.existsById(id)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> todoValidate.validateUpdateStatus(id, status));
    }

    @Test
    public void validateDeleteById_shouldThrowResourceNotFoundException_whenTodoIdDoesNotExist() {
        when(todoRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> todoValidate.validateDeleteById(1));
    }

    @Test
    public void validateDeleteById_shouldNotThrowException_whenTodoIdExists() {
        when(todoRepository.existsById(anyInt())).thenReturn(true);
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
        String status = "invalid status";

        assertThrows(BadRequestException.class, () -> todoValidate.validateCountByStatus(status));
    }
}

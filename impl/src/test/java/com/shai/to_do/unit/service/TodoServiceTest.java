package com.shai.to_do.unit.service;

import com.shai.to_do.service.TodoService;
import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.mapper.TodoDTOToTodoEntityMapper;
import com.shai.to_do.repository.TodoRepository;
import com.shai.to_do.validators.TodoValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TodoServiceTest {

    @Mock
    private TodoValidate todoValidate;

    @Mock
    private TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private TodoDTO todoDTO;

    private Todo todo;

    final static String TITLE = "Title";
    final static String CONTENT = "Content";
    final static Long DUE_DATE = System.currentTimeMillis() + 10000L;
    final static Integer ID = 1;
    final static String STATUS = Status.PENDING;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        todoDTO = new TodoDTO(TITLE, CONTENT, DUE_DATE);

        todo = new Todo();
        todo.setId(ID);
        todo.setTitle(TITLE);
        todo.setContent(CONTENT);
        todo.setStatus(STATUS);
        todo.setDueDate(DUE_DATE);
    }

    @Test
    public void addTodo_ShouldInvokeTodoValidate_AndTodoRepository() throws TodoAlreadyExistsException, DueDateExpiredException {
        when(todoDTOToTodoEntityMapper.map(todoDTO)).thenReturn(todo);

        Integer todoId = todoService.add(todoDTO);

        verify(todoValidate, times(1)).validateAdd(todoDTO);
        verify(todoRepository, times(1)).add(todo);
        assertEquals(ID, todoId);
    }

    @Test
    public void countTodoByStatus_ShouldInvokeTodoValidate_AndTodoRepository() throws BadRequestException {
        when(todoRepository.countByStatus(STATUS)).thenReturn(1L);

        long count = todoService.countByStatus(STATUS);

        verify(todoValidate, times(1)).validateCountByStatus(STATUS);
        verify(todoRepository, times(1)).countByStatus(STATUS);
        assertEquals(1L, count);
    }

    @Test
    public void getTodoContentByStatusSortedByField_ShouldInvokeTodoValidate_AndTodoRepository() throws BadRequestException {
        when(todoRepository.findTodoContentByStatusSortedByField(STATUS, SortBy.ID)).thenReturn(List.of(todo));

        List<Todo> todos = todoService.getTodoContentByStatusSortedByField(STATUS, Optional.of(SortBy.ID));

        verify(todoValidate, times(1)).validateGetTodoContentByStatusSortedByField(STATUS, SortBy.ID);
        verify(todoRepository, times(1)).findTodoContentByStatusSortedByField(STATUS, SortBy.ID);
        assertEquals(1, todos.size());
    }

    @Test
    public void updateStatus_ShouldInvokeTodoValidate_AndTodoRepository() throws BadRequestException, ResourceNotFoundException {
        final String NEW_STATUS = Status.LATE;

        when(todoRepository.findById(ID)).thenReturn(todo);

        String oldStatus = todoService.updateStatus(ID, NEW_STATUS);

        verify(todoValidate, times(1)).validateUpdateStatus(ID, NEW_STATUS);
        verify(todoRepository, times(1)).findById(ID);
        verify(todoRepository, times(1)).updateStatusById(ID, NEW_STATUS);

        assertEquals(oldStatus, STATUS);
    }

    @Test
    public void deleteById_ShouldInvokeTodoValidate_AndTodoRepository() throws ResourceNotFoundException {
        doNothing().when(todoValidate).validateDeleteById(ID);
        when(todoRepository.deleteById(ID)).thenReturn(0);

        todoService.deleteById(ID);

        verify(todoValidate, times(1)).validateDeleteById(ID);
        verify(todoRepository, times(1)).deleteById(ID);
    }
}

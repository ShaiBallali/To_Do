package com.shai.to_do.service;

import com.shai.to_do.constants.SortBy;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.mapper.TodoDTOToTodoEntityMapper;
import com.shai.to_do.repository.TodoRepository;
import com.shai.to_do.validators.TodoValidate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoValidate todoValidate;

    private final TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper;

    private final TodoRepository todoRepository;

    public TodoService(TodoValidate todoValidate,
                       TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper,
                       TodoRepository todoRepository) {
        this.todoValidate = todoValidate;
        this.todoDTOToTodoEntityMapper = todoDTOToTodoEntityMapper;
        this.todoRepository = todoRepository;
    }

    public Integer add(TodoDTO todoDTO) throws TodoAlreadyExistsException, DueDateExpiredException {
        todoValidate.validateAdd(todoDTO);
        Todo todo = todoDTOToTodoEntityMapper.map(todoDTO);
        todoRepository.add(todo);
        return todo.getId();
    }

    public long countByStatus(String status) throws BadRequestException {
        todoValidate.validateCountByStatus(status);
        return todoRepository.countByStatus(status);
    }

    public List<Todo> getTodoContentByStatusSortedByField(String status, Optional<String> sortBy) throws BadRequestException {
        String sortByValue = sortBy.orElse(SortBy.ID);
        todoValidate.validateGetTodoContentByStatusSortedByField(status, sortByValue);
        return todoRepository.findTodoContentByStatusSortedByField(status, sortByValue);
    }

    public String updateStatus(Integer id, String status) throws BadRequestException, ResourceNotFoundException {
        todoValidate.validateUpdateStatus(id, status);
        String oldStatus = todoRepository.findById(id).getStatus();
        todoRepository.updateStatusById(id, status);
        return oldStatus;
    }

    public int deleteById(Integer id) throws ResourceNotFoundException {
        todoValidate.validateDeleteById(id);
        return todoRepository.deleteById(id);
    }
}

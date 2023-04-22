package com.shai.to_do.validators;

import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.repository.TodoRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TodoValidate {

    private final TodoRepository todoRepository;

    public TodoValidate(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void validateAdd(TodoDTO todoDTO) throws TodoAlreadyExistsException, DueDateExpiredException {
        String title = todoDTO.title();
        Long dueDate = todoDTO.dueDate();

        validateTodoDoesNotExistInRepoByTitle(title);
        validateDueDateHasNotExpired(dueDate);
    }

    public void validateUpdateStatus(Integer id, String status) throws ResourceNotFoundException, BadRequestException {
        validateIdExists(id);
        validateStatusExists(status);
    }

    public void validateDeleteById(Integer id) throws ResourceNotFoundException {
        validateIdExists(id);
    }

    public void validateGetTodoContentByStatusSortedByField(String status, String sortBy) throws BadRequestException {
        if (!Objects.equals(status, Status.ALL)) {
            validateStatusExists(status);
        }
        validateSortByExists(sortBy);
    }

    public void validateCountByStatus(String status) throws BadRequestException {
        if (!Objects.equals(status, Status.ALL)) {
            validateStatusExists(status);
        }
    }

    private void validateStatusExists(String status) throws BadRequestException {
        if (!Objects.equals(status, Status.PENDING) &&
                !Objects.equals(status, Status.DONE) &&
                !Objects.equals(status, Status.LATE)) {
            throw new BadRequestException();
        }
    }

    private void validateSortByExists(String sortBy) throws BadRequestException {
        if (!Objects.equals(sortBy, SortBy.ID) &&
                !Objects.equals(sortBy, SortBy.TITLE) &&
                !Objects.equals(sortBy, SortBy.DUE_DATE)) {
            throw new BadRequestException();
        }
    }

    private void validateTodoDoesNotExistInRepoByTitle(String title) throws TodoAlreadyExistsException {
        if(todoRepository.existsByTitle(title)) {
            throw new TodoAlreadyExistsException(
                    "Error: TODO with the title [" + title + "] already exists in the system"
            );
        }
    }

    private void validateDueDateHasNotExpired(Long dueDate) throws DueDateExpiredException {
        if (dueDate < System.currentTimeMillis())
            throw new DueDateExpiredException(
                    "Error: Can’t create new TODO that its due date is in the past"
            );
    }

    private void validateIdExists(Integer id) throws ResourceNotFoundException {
        if(!todoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Error: no such TODO with id " + id);
        }
    }
}

package com.shai.to_do.util;

import com.shai.to_do.Context;
import com.shai.to_do.constants.LogLevels;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.repository.TodoRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Getter
@Setter
@Component
public class TodoLoggerFormatter {
    private final TodoRepository todoRepository;
    private final Context context;

    public TodoLoggerFormatter(TodoRepository todoRepository, Context context) {
        this.todoRepository = todoRepository;
        this.context = context;
    }

    public String add(TodoDTO todoDTO, String logLevel) {
        String todoTitle = todoDTO.title();
        long todoCount = todoRepository.countByStatus(Status.ALL);

        return switch(logLevel) {
            case LogLevels.INFO -> "Creating new TODO with Title [" + todoTitle + "]";
            case LogLevels.DEBUG -> "Currently there are " + (todoCount - 1) + " TODOs in the system. New TODO will be assigned with id " + context.getIdCounter();
            default -> "";
        };
    }

    public String countByStatus(String status) {
        long todoCountByStatus = todoRepository.countByStatus(status);

        return "Total TODOs count for state " + status + " is " + todoCountByStatus;
    }

    public String getTodoContentByStatusSortedByField(String status, String sortBy, String logLevel) {
        long todoCount = todoRepository.countByStatus(Status.ALL);
        long todoCountByStatus = todoRepository.countByStatus(status);

        return switch(logLevel) {
            case LogLevels.INFO -> "Extracting todos content. Filter: " + status + " | Sorting by: " + sortBy;
            case LogLevels.DEBUG -> "There are a total of " + todoCount + " todos in the system. The result holds " + todoCountByStatus + " todos";
            default -> "";
        };
    }

    public String updateStatus(Integer id, String oldStatus, String newStatus, String logLevel) {
        return switch(logLevel) {
            case LogLevels.INFO -> "Update TODO id [" + id + "] state to " + newStatus;
            case LogLevels.DEBUG -> "Todo id [" + id + "] state change: " + oldStatus + " --> " + newStatus;
            default -> "";
        };
    }

    public String deleteById(Integer id, String logLevel) {
        long todosCount = todoRepository.countByStatus(Status.ALL);

        return switch(logLevel) {
            case LogLevels.INFO -> "Removing todo id " + id;
            case LogLevels.DEBUG -> "After removing todo id [" + id + "] there are " + todosCount + " TODOs in the system";
            default -> "";
        };
    }
}

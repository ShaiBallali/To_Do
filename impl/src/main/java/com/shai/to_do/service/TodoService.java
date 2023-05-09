package com.shai.to_do.service;

import com.shai.to_do.Context;
import com.shai.to_do.constants.LogLevels;
import com.shai.to_do.constants.Resources;
import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Verbs;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.mapper.TodoDTOToTodoEntityMapper;
import com.shai.to_do.repository.TodoRepository;
import com.shai.to_do.validators.TodoValidate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoValidate todoValidate;

    private final TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper;

    private final TodoRepository todoRepository;

    private final Context context;

    private static Logger requestLogger = LogManager.getLogger("request-logger");

    public TodoService(TodoValidate todoValidate,
                       TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper,
                       TodoRepository todoRepository, Context context) {
        this.todoValidate = todoValidate;
        this.todoDTOToTodoEntityMapper = todoDTOToTodoEntityMapper;
        this.todoRepository = todoRepository;
        this.context = context;
    }

    public Integer add(TodoDTO todoDTO) throws TodoAlreadyExistsException, DueDateExpiredException {
        context.initLogsInfo();
        requestLogger.info(formatMessage(LogLevels.INFO, Resources.ADD, Verbs.POST));
        todoValidate.validateAdd(todoDTO);
        Todo todo = todoDTOToTodoEntityMapper.map(todoDTO);
        todoRepository.add(todo);
        requestLogger.debug(formatMessage(LogLevels.DEBUG, Resources.ADD, Verbs.POST));
        return todo.getId();
    }

    public long countByStatus(String status) throws BadRequestException {
        context.initLogsInfo();
        requestLogger.info(formatMessage(LogLevels.INFO, Resources.COUNT_BY_STATUS, Verbs.GET));
        todoValidate.validateCountByStatus(status);
        long finalCount = todoRepository.countByStatus(status);
        requestLogger.debug(formatMessage(LogLevels.DEBUG, Resources.COUNT_BY_STATUS, Verbs.GET));
        return finalCount;
    }

    public List<Todo> getTodoContentByStatusSortedByField(String status, Optional<String> sortBy) throws BadRequestException {
        context.initLogsInfo();
        requestLogger.info(formatMessage(LogLevels.INFO, Resources.GET_TODO_CONTENT_BY_STATUS_SORTED_BY_FIELD, Verbs.GET));
        String sortByValue = sortBy.orElse(SortBy.ID);
        todoValidate.validateGetTodoContentByStatusSortedByField(status, sortByValue);
        List<Todo> finalList = todoRepository.findTodoContentByStatusSortedByField(status, sortByValue);
        requestLogger.debug(formatMessage(LogLevels.DEBUG, Resources.GET_TODO_CONTENT_BY_STATUS_SORTED_BY_FIELD, Verbs.GET));
        return finalList;
    }

    public String updateStatus(Integer id, String status) throws BadRequestException, ResourceNotFoundException {
        context.initLogsInfo();
        requestLogger.info(formatMessage(LogLevels.INFO, Resources.UPDATE_STATUS, Verbs.PUT));
        todoValidate.validateUpdateStatus(id, status);
        String oldStatus = todoRepository.findById(id).getStatus();
        todoRepository.updateStatusById(id, status);
        requestLogger.debug(formatMessage(LogLevels.DEBUG, Resources.UPDATE_STATUS, Verbs.PUT));
        return oldStatus;
    }

    public int deleteById(Integer id) throws ResourceNotFoundException {
        context.initLogsInfo();
        requestLogger.info(formatMessage(LogLevels.INFO, Resources.DELETE_TODO, Verbs.DELETE));
        todoValidate.validateDeleteById(id);
        int finalId = todoRepository.deleteById(id);
        requestLogger.debug(formatMessage(LogLevels.DEBUG, Resources.DELETE_TODO, Verbs.DELETE));
        return finalId;
    }

    private String formatMessage(String logLevel, String resource, String verb) {
        int requestCounter = context.getRequestCounter();
        long requestEndTimeInMillis = System.currentTimeMillis();
        long requestStartTimeInMillis = context.getCurrentRequestStartTime();

        String formattedMessage = " " + getCurrentDate() + " " + logLevel + ":";
        String logMessage = switch(logLevel) {
            case "INFO" -> " Incoming request | #" + requestCounter + " | resource: " + resource + "| HTTP Verb " + verb;
            case "DEBUG" -> " request #" + requestCounter + " duration: " + (requestEndTimeInMillis - requestStartTimeInMillis) + "ms";
            default -> "";
        };
        return formattedMessage + logMessage + " | request #" + requestCounter;
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy HH:mm:ss.ss")
                .appendFraction(ChronoField.MILLI_OF_SECOND, 3, 3, true)
                .toFormatter();
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}

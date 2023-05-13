package com.shai.to_do.service;

import com.shai.to_do.Context;
import com.shai.to_do.constants.LogLevels;
import com.shai.to_do.constants.Resources;
import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Verbs;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.dto.response.*;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.mapper.TodoDTOToTodoEntityMapper;
import com.shai.to_do.mapper.response.*;
import com.shai.to_do.repository.TodoRepository;
import com.shai.to_do.util.TodoLoggerFormatter;
import com.shai.to_do.validators.TodoValidate;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoValidate todoValidate;
    private final TodoDTOToTodoEntityMapper todoDTOToTodoEntityMapper;
    private final TodoRepository todoRepository;
    private final AddResponseToAddResponseDTOMapper addResponseToAddResponseDTOMapper;
    private final CountByStatusResponseToCountByStatusResponseDTOMapper countByStatusResponseToCountByStatusResponseDTOMapper;
    private final DeleteResponseToDeleteResponseDTOMapper deleteResponseToDeleteResponseDTOMapper;
    private final GetContentResponseToGetContentResponseDTOMapper getContentResponseToGetContentResponseDTOMapper;
    private final UpdateStatusResponseToUpdateStatusResponseDTOMapper updateStatusResponseToUpdateStatusResponseDTOMapper;
    private final Context context;
    private final TodoLoggerFormatter todoLoggerFormatter;
    private static final Logger requestLogger = LogManager.getLogger("request-logger");
    private static final Logger todoLogger = LogManager.getLogger("todo-logger");

    public AddResponseDTO add(TodoDTO todoDTO) throws TodoAlreadyExistsException, DueDateExpiredException {
        context.initLogsInfo();
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.ADD, Verbs.POST));
        todoValidate.validateAdd(todoDTO);
        Todo todo = todoDTOToTodoEntityMapper.map(todoDTO);
        todoRepository.add(todo);
        AddResponseDTO addResponseDTO = addResponseToAddResponseDTOMapper.map(todo.getId());
        todoLogger.info(todoLoggerFormatter.add(todoDTO, LogLevels.INFO));
        todoLogger.debug(todoLoggerFormatter.add(todoDTO, LogLevels.DEBUG));
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.ADD, Verbs.POST));
        return addResponseDTO;
    }

    public CountByStatusResponseDTO countByStatus(String status) throws BadRequestException {
        context.initLogsInfo();
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.COUNT_BY_STATUS, Verbs.GET));
        todoLogger.info(todoLoggerFormatter.countByStatus(status));
        todoValidate.validateCountByStatus(status);
        CountByStatusResponseDTO countByStatusResponseDTO =
                countByStatusResponseToCountByStatusResponseDTOMapper.map(todoRepository.countByStatus(status));
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.COUNT_BY_STATUS, Verbs.GET));
        return countByStatusResponseDTO;
    }

    public GetContentResponseDTO getTodoContentByStatusSortedByField(String status,
                                                                     Optional<String> sortBy) throws BadRequestException {
        context.initLogsInfo();
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.GET_TODO_CONTENT_BY_STATUS_SORTED_BY_FIELD, Verbs.GET));
        String sortByValue = sortBy.orElse(SortBy.ID);
        todoLogger.info(todoLoggerFormatter.getTodoContentByStatusSortedByField(status, sortByValue, LogLevels.INFO));
        todoLogger.debug(todoLoggerFormatter.getTodoContentByStatusSortedByField(status, sortByValue, LogLevels.DEBUG));
        todoValidate.validateGetTodoContentByStatusSortedByField(status, sortByValue);
        GetContentResponseDTO getContentResponseDTO =
                getContentResponseToGetContentResponseDTOMapper.map(todoRepository.findTodoContentByStatusSortedByField(status, sortByValue));
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.GET_TODO_CONTENT_BY_STATUS_SORTED_BY_FIELD, Verbs.GET));
        return getContentResponseDTO;
    }

    public UpdateStatusResponseDTO updateStatus(Integer id,
                                                String status) throws BadRequestException, ResourceNotFoundException {
        context.initLogsInfo();
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.UPDATE_STATUS, Verbs.PUT));
        todoLogger.info(todoLoggerFormatter.updateStatus(id, "", status, LogLevels.INFO));
        todoValidate.validateUpdateStatus(id, status);
        String oldStatus = todoRepository.findById(id).getStatus();
        todoLogger.debug(todoLoggerFormatter.updateStatus(id, oldStatus, status, LogLevels.DEBUG));
        todoRepository.updateStatusById(id, status);
        UpdateStatusResponseDTO updateStatusResponseDTO =
                updateStatusResponseToUpdateStatusResponseDTOMapper.map(oldStatus);
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.UPDATE_STATUS, Verbs.PUT));
        return updateStatusResponseDTO;
    }

    public DeleteResponseDTO deleteById(Integer id) throws ResourceNotFoundException {
        context.initLogsInfo();
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.DELETE_TODO, Verbs.DELETE));
        todoValidate.validateDeleteById(id);
        DeleteResponseDTO deleteResponseDTO =
                deleteResponseToDeleteResponseDTOMapper.map(todoRepository.deleteById(id));
        todoLogger.info(todoLoggerFormatter.deleteById(id, LogLevels.INFO));
        todoLogger.debug(todoLoggerFormatter.deleteById(id, LogLevels.DEBUG));
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.DELETE_TODO, Verbs.DELETE));
        return deleteResponseDTO;
    }

    private String formatMessageRequestLogger(String logLevel, String resource, String verb) {
        int requestCounter = context.getRequestCounter();
        long requestEndTimeInMillis = System.currentTimeMillis();
        long requestStartTimeInMillis = context.getCurrentRequestStartTime();

        return switch(logLevel) {
            case "INFO" -> "Incoming request | #" + requestCounter + " | resource: " + resource + " | HTTP Verb " + verb;
            case "DEBUG" -> "request #" + requestCounter + " duration: " + (requestEndTimeInMillis - requestStartTimeInMillis) + "ms";
            default -> "";
        };
    }
}

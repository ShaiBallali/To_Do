package com.shai.to_do.validators;

import com.shai.to_do.constants.LogLevels;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LogsValidate {
    public void validateGetLogLevel(String loggerName) throws ResourceNotFoundException {
        checkLoggerName(loggerName);
    }

    public void validateChangeLogLevel(String loggerName, String newLogLevel) throws ResourceNotFoundException, BadRequestException {
        checkLoggerName(loggerName);
        checkLogLevel(newLogLevel);
    }

    private void checkLoggerName(String loggerName) throws ResourceNotFoundException {
        if (!loggerName.equals("request-logger") && !loggerName.equals("todo-logger")) {
            throw new ResourceNotFoundException("Logger name is not request-logger or todo-logger");
        }
    }

    private void checkLogLevel(String newLogLevel) throws BadRequestException {
        if (!newLogLevel.equalsIgnoreCase(LogLevels.DEBUG)
        && !newLogLevel.equalsIgnoreCase(LogLevels.INFO)
        && !newLogLevel.equalsIgnoreCase(LogLevels.ERROR))
            throw new BadRequestException();
    }
}

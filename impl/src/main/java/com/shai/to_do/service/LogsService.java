package com.shai.to_do.service;

import com.shai.to_do.Context;
import com.shai.to_do.constants.LogLevels;
import com.shai.to_do.constants.Resources;
import com.shai.to_do.constants.Verbs;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.validators.LogsValidate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.stereotype.Service;

@Service
public class LogsService {

    private final Context context;

    private final LogsValidate logsValidate;

    private static final Logger requestLogger = LogManager.getLogger("request-logger");

    public LogsService(Context context, LogsValidate logsValidate) {
        this.context = context;
        this.logsValidate = logsValidate;
    }

    public String getLogLevel(String loggerName) throws ResourceNotFoundException {
        context.initLogsInfo();
        logsValidate.validateGetLogLevel(loggerName);
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.GET_LOGGER_LEVEL, Verbs.GET));
        Logger logger = LogManager.getLogger(loggerName);
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.GET_LOGGER_LEVEL, Verbs.GET));
        return logger.getLevel().toString().toUpperCase();
    }

    public String changeLogLevel(String loggerName, String loggerLevel) throws BadRequestException, ResourceNotFoundException {
        context.initLogsInfo();
        logsValidate.validateChangeLogLevel(loggerName, loggerLevel);
        requestLogger.info(formatMessageRequestLogger(LogLevels.INFO, Resources.SET_LOGGER_LEVEL, Verbs.PUT));
        Configurator.setLevel(loggerName, loggerLevel);
        requestLogger.debug(formatMessageRequestLogger(LogLevels.DEBUG, Resources.SET_LOGGER_LEVEL, Verbs.PUT));
        return loggerLevel.toUpperCase();
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

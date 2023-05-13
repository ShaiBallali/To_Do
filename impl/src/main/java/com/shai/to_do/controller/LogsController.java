package com.shai.to_do.controller;

import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.service.LogsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs/level")
public class LogsController {

    private final LogsService logsService;

    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @GetMapping
    public String getLogLevel(@RequestParam("logger-name") String loggerName) throws ResourceNotFoundException {
        return logsService.getLogLevel(loggerName);
    }

    @PutMapping
    public String changeLogLevel(@RequestParam("logger-name") String loggerName,
                                 @RequestParam("logger-level") String loggerLevel)
            throws BadRequestException, ResourceNotFoundException {
        return logsService.changeLogLevel(loggerName, loggerLevel);
    }
}

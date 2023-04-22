package com.shai.to_do.controller;

import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.ResourceNotFoundException;
import com.shai.to_do.exception.TodoAlreadyExistsException;
import com.shai.to_do.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping
    public Integer add(@RequestBody TodoDTO todoDTO) throws TodoAlreadyExistsException, DueDateExpiredException {
        return todoService.add(todoDTO);
    }

    @GetMapping("/size")
    public long countByStatus(@RequestParam String status) throws BadRequestException {
        return todoService.countByStatus(status);
    }

    @GetMapping("/content")
    public List<Todo> getTodoContentByStatusSortedByField(@RequestParam String status,
                                                          @RequestParam Optional<String> sortBy) throws BadRequestException {
        return todoService.getTodoContentByStatusSortedByField(status, sortBy);
    }

    @PutMapping
    public String updateStatus(@RequestParam Integer id,
                               @RequestParam String status) throws BadRequestException, ResourceNotFoundException {
        return todoService.updateStatus(id, status);
    }

    @DeleteMapping
    public int deleteTodo(@RequestParam Integer id) throws ResourceNotFoundException {
        return todoService.deleteById(id);
    }

}

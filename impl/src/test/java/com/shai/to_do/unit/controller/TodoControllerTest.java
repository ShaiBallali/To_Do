package com.shai.to_do.unit.controller;

import com.shai.to_do.constants.Status;
import com.shai.to_do.controller.TodoController;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.exception.BadRequestException;
import com.shai.to_do.exception.DueDateExpiredException;
import com.shai.to_do.exception.handler.ControllerAdvice;
import com.shai.to_do.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

public class TodoControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TodoService todoService;

    @Mock
    private ControllerAdvice controllerAdvice;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new TodoController(todoService))
                .setControllerAdvice(controllerAdvice)
                .build();
    }

    @Test
    public void testHealth_ShouldReturnOkStatusOKContent() throws Exception {
        mockMvc.perform(get("/todo/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    public void testAdd_ShouldReturnStatusOk_AndContent1_AndInvokeTodoService() throws Exception {
        TodoDTO todoDTO = new TodoDTO("title", "content", Instant.now().getEpochSecond());
        when(todoService.add(any(TodoDTO.class))).thenReturn(1);
        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"title\", \"content\": \"content\", \"dueDate\": " + Instant.now().getEpochSecond() + " }")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1"));
        verify(todoService, times(1)).add(eq(todoDTO));
    }

    @Test
    public void testCountByStatus_ShouldReturnCount() throws Exception {
        final String status = Status.PENDING;
        when(todoService.countByStatus(status)).thenReturn(1L);

        mockMvc.perform(get("/todo/size")
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(todoService, times(1)).countByStatus(status);
    }

    @Test
    public void testAdd_withDueDateExpired_ShouldThrowDueDateExpiredException() throws Exception {
        TodoDTO todoDTO = new TodoDTO("title", "description", System.currentTimeMillis()-1);

        doThrow(new DueDateExpiredException("Error: Due date has already passed"))
                .when(todoService)
                .add(any(TodoDTO.class));

        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"title\",\"description\":\"description\",\"dueDate\":" + todoDTO.dueDate() + "}"));

        verify(controllerAdvice, times(1))
                .handleDueDateAlreadyPassedException(any(DueDateExpiredException.class));
    }

    @Test
    public void testCountByStatus_ShouldCount() throws Exception {
        when(todoService.countByStatus(Status.DONE)).thenReturn(3L);

        mockMvc.perform(get("/todo/size?status=DONE"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(controllerAdvice, times(0))
                .handleBadRequestException();
    }

    @Test
    public void testCountByStatus_WithWrongName_ShouldThrowBadRequestException() throws Exception {
        doThrow(new BadRequestException())
                .when(todoService)
                .countByStatus("DONe");

        mockMvc.perform(get("/todo/size?status=DONe"))
                .andExpect(status().isOk());

        verify(controllerAdvice, times(1))
                .handleBadRequestException();
    }

    @Test
    public void testGetTodoContentByStatusSortedByField_ShouldReturnData() throws Exception {
        Todo todo1 = new Todo(1, "title1", "description1", Status.PENDING, System.currentTimeMillis()+1000);
        Todo todo2 = new Todo(2, "title2", "description2", Status.PENDING, System.currentTimeMillis()+1001);

        List<Todo> todos = Arrays.asList(todo1, todo2);

        when(todoService.getTodoContentByStatusSortedByField(Status.PENDING, Optional.empty())).thenReturn(todos);

        mockMvc.perform(get("/todo/content?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("title1")))
                .andExpect(jsonPath("$[0].content", is("description1")))
                .andExpect(jsonPath("$[0].status", is(Status.PENDING)))
                .andExpect(jsonPath("$[1].title", is("title2")))
                .andExpect(jsonPath("$[1].content", is("description2")))
                .andExpect(jsonPath("$[1].status", is(Status.PENDING)));
    }

}

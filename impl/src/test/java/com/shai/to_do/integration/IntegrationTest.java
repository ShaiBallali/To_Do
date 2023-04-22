package com.shai.to_do.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shai.to_do.Context;
import com.shai.to_do.ToDoApplication;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ToDoApplication.class)
@AutoConfigureMockMvc

class IntegrationTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private Context context;

    private final static String TITLE = "Title";
    private final static String DESCRIPTION = "Description";
    private final static Long DUE_DATE = System.currentTimeMillis() + 10000L;

    @BeforeEach
    void setUp() {
        todoRepository.clear();
        context.setIdCounter(0);
    }

    @Test
    public void testAddTodo_ShouldSaveInRepo() throws Exception {
        TodoDTO todoDTO = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE);

        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(todoDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
        Assertions.assertTrue(todoRepository.existsById(1));

        Todo todo = todoRepository.findById(1);

        Assertions.assertEquals(TITLE, todo.getTitle());
        Assertions.assertEquals(DESCRIPTION, todo.getContent());
        Assertions.assertEquals(DUE_DATE, todo.getDueDate());
    }

    @Test
    public void testAddTodo_WithExpiredDueDate_ShouldThrowDueDateExpiredException() throws Exception {
        TodoDTO todoDTO = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE-10001L);

        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(todoDTO))
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("Error: Can’t create new TODO that its due date is in the past"));

        Assertions.assertFalse(todoRepository.existsById(1));
    }

    @Test
    public void countTodoByStatusTest_ShouldCount_AllTodosWithGivenStatus() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE + 1);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/size?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void countTodoByStatusTest_WithWrongStatusName_ShouldThrow_BadRequestException() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE + 1);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/size?status=PENDINg"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTodoContentByStatusSortedByFieldTest_ShouldReturn_ListOfTodos_SortedBy_DueDate() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/content?status=PENDING&sortBy=DUE_DATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(TITLE+2)))
                .andExpect(jsonPath("$[0].content", is(DESCRIPTION+2)))
                .andExpect(jsonPath("$[0].status", is(Status.PENDING)))
                .andExpect(jsonPath("$[1].title", is(TITLE)))
                .andExpect(jsonPath("$[1].content", is(DESCRIPTION)))
                .andExpect(jsonPath("$[1].status", is(Status.PENDING)));
    }

    @Test
    public void getTodoContentByStatusSortedByFieldTest_ShouldReturn_ListOfTodos_SortedBy_Title() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO("Aa", DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/content?status=PENDING&sortBy=TITLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Aa")))
                .andExpect(jsonPath("$[0].content", is(DESCRIPTION+2)))
                .andExpect(jsonPath("$[0].status", is(Status.PENDING)))
                .andExpect(jsonPath("$[1].title", is(TITLE)))
                .andExpect(jsonPath("$[1].content", is(DESCRIPTION)))
                .andExpect(jsonPath("$[1].status", is(Status.PENDING)));
    }

    @Test
    public void getTodoContentByStatusSortedByFieldTest_ShouldReturn_ListOfTodos_SortedBy_Id() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/content?status=PENDING&sortBy=ID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(TITLE)))
                .andExpect(jsonPath("$[0].content", is(DESCRIPTION)))
                .andExpect(jsonPath("$[0].status", is(Status.PENDING)))
                .andExpect(jsonPath("$[1].title", is(TITLE+2)))
                .andExpect(jsonPath("$[1].content", is(DESCRIPTION+2)))
                .andExpect(jsonPath("$[1].status", is(Status.PENDING)));
    }

    @Test
    public void getTodoContentByStatusSortedByFieldTest_WithWrongSortBy_ShouldThrow_BadRequestException() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/content?status=PENDING&sortBy=Id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getTodoContentByStatusSortedByFieldTest_No_SortBy_Given_ShouldReturn_ListOfTodos_SortedBy_Id() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(get("/todo/content?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(TITLE)))
                .andExpect(jsonPath("$[0].content", is(DESCRIPTION)))
                .andExpect(jsonPath("$[0].status", is(Status.PENDING)))
                .andExpect(jsonPath("$[1].title", is(TITLE+2)))
                .andExpect(jsonPath("$[1].content", is(DESCRIPTION+2)))
                .andExpect(jsonPath("$[1].status", is(Status.PENDING)));
    }

    @Test
    public void updateTodoStatusTest_ShouldReturnOldStatus_AndShouldBeAssignedNewStatus() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);

        addTodo(todoDTO1);

        mockMvc.perform(put("/todo?id=1&status=LATE"))
                .andExpect(status().isOk())
                .andExpect(content().string(Status.PENDING));

        mockMvc.perform(get("/todo/content?status=LATE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(TITLE)))
                .andExpect(jsonPath("$[0].content", is(DESCRIPTION)))
                .andExpect(jsonPath("$[0].status", is(Status.LATE)));
    }

    @Test
    public void updateTodoStatusTest_WithoutAnExistingId_ShouldThrow_ResourceNotFoundException() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);

        addTodo(todoDTO1);

        mockMvc.perform(put("/todo?id=3&status=LATE"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: no such TODO with id 3"));
    }

    @Test
    public void deleteTodoTest_ShouldReturnNumberOfTodosLeft_AndShouldNotUseDeletedId() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(delete("/todo?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        TodoDTO todoDTOToBeAddedAfterDeletionShouldHaveId3 = new TodoDTO(TITLE + 3, DESCRIPTION + 3, DUE_DATE + 3);
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(todoDTOToBeAddedAfterDeletionShouldHaveId3))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

    }

    @Test
    public void deleteTodoTest_WithoutAnExistingId_ShouldThrow_ResourceNotFoundException() throws Exception {
        TodoDTO todoDTO1 = new TodoDTO(TITLE, DESCRIPTION, DUE_DATE + 1);
        TodoDTO todoDTO2 = new TodoDTO(TITLE + 2, DESCRIPTION + 2, DUE_DATE);

        addTodo(todoDTO1);
        addTodo(todoDTO2);

        mockMvc.perform(delete("/todo?id=3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: no such TODO with id 3"));
    }

    public void addTodo(TodoDTO todoDTO) throws Exception {
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(todoDTO))
        );
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

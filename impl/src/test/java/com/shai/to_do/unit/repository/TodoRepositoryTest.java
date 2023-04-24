package com.shai.to_do.unit.repository;

import com.shai.to_do.constants.Status;
import com.shai.to_do.entity.Todo;
import com.shai.to_do.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TodoRepositoryTest {

    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = new TodoRepository();
    }

    @Test
    public void addTodoTest() {
        Todo todo = new Todo();
        todo.setId(1); // set the ID manually
        todo.setTitle("Test todo");
        todo.setContent("This is a test todo");
        todo.setDueDate(System.currentTimeMillis()+1);
        todo.setStatus(Status.PENDING);

        todoRepository.add(todo);

        assertTrue(todoRepository.existsById(1));
        assertEquals("Test todo", todoRepository.findById(1).getTitle());
    }

    @Test
    public void existsByIdTest() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Test todo");
        todo.setContent("This is a test todo");
        todo.setDueDate(System.currentTimeMillis() + 1);
        todo.setStatus(Status.PENDING);

        todoRepository.add(todo);

        assertTrue(todoRepository.existsById(1));
        assertFalse(todoRepository.existsById(2));
    }

    @Test
    public void updateStatusByIdTest() {
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTitle("Test todo");
        todo.setContent("This is a test todo");
        todo.setDueDate(System.currentTimeMillis() + 1);
        todo.setStatus(Status.PENDING);

        todoRepository.add(todo);

        todoRepository.updateStatusById(1, Status.DONE);

        assertEquals(Status.DONE, todoRepository.findById(1).getStatus());
    }

    @Test
    public void findByIdExistingTodoTest() {
        Todo todo = new Todo();
        todo.setId(1); // set the ID manually
        todo.setTitle("Test todo");
        todo.setContent("This is a test todo");
        todo.setDueDate(System.currentTimeMillis() + 1);
        todo.setStatus(Status.PENDING);
        todoRepository.add(todo);

        Todo foundTodo = todoRepository.findById(1);

        assertNotNull(foundTodo);
        assertEquals("Test todo", foundTodo.getTitle());
    }

    @Test
    public void findByIdNonExistingTodoTest_ShouldThrow_NoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> todoRepository.findById(1));
    }

    @Test
    public void existsByTitleExistingTodoTest() {
        Todo todo = new Todo();
        todo.setId(1); // set the ID manually
        todo.setTitle("Test todo");
        todo.setContent("This is a test todo");
        todo.setDueDate(System.currentTimeMillis() + 1);
        todo.setStatus(Status.PENDING);
        todoRepository.add(todo);

        assertTrue(todoRepository.existsByTitle("Test todo"));
    }

    @Test
    public void existsByTitleNonExistingTodoTest() {
        assertFalse(todoRepository.existsByTitle("Test todo"));
    }

}

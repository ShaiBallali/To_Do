package com.shai.to_do.repository;

import com.shai.to_do.constants.SortBy;
import com.shai.to_do.constants.Status;
import com.shai.to_do.entity.Todo;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class TodoRepository {
    private List<Todo> todoDB;

    public TodoRepository() {
        todoDB = new ArrayList<>();
    }

    public void add(Todo todo) {
        todoDB.add(todo);
    }

    public boolean existsById(Integer id) {
        return todoDB.stream()
                .anyMatch(todo-> Objects.equals(todo.getId(), id));
    }

    public void updateStatusById(Integer id, String status) {
        for (Todo todo : todoDB) {
            if (Objects.equals(todo.getId(), id)) {
                todo.setStatus(status);
            }
        }
    }

    public Todo findById(Integer id) {
        return todoDB.stream()
                .filter(t->t.getId().equals(id))
                .findFirst()
                .get();
    }

    public boolean existsByTitle(String title) {
        return todoDB.stream()
                .anyMatch(todo -> todo.getTitle().equals(title));
    }

    public long countByStatus(String status) {
        if (status.equals(Status.ALL)) {
            return countAll();
        }
        return todoDB.stream()
                .filter(todo -> todo.getStatus().equals(status))
                .count();
    }

    private long countAll() {
        return todoDB.size();
    }

    public List<Todo> findTodoContentByStatusSortedByField(String status, String sortBy) {
        List<Todo> filteredTodos = findTodoContentByStatus(status);

        switch (sortBy) {
            case SortBy.ID -> { sortById(filteredTodos); }
            case SortBy.TITLE -> { sortByTitle(filteredTodos); }
            case SortBy.DUE_DATE -> { sortByDueDate(filteredTodos); }
        }

        return filteredTodos;
    }

    public List<Todo> findTodoContentByStatus(String status) {
        if (status.equals(Status.ALL)) {
            return todoDB;
        }
        return todoDB.stream()
                .filter(todo -> todo.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public int deleteById(Integer id) {
        todoDB.removeIf(todo -> Objects.equals(todo.getId(), id));
        return todoDB.size();
    }

    public void clear() {
        todoDB = new ArrayList<>();
    }

    private void sortById(List<Todo> todoList) {
        todoList.sort(new Comparator<Todo>() {
            @Override
            public int compare(Todo t1, Todo t2) {
                return t1.getId().compareTo(t2.getId());
            }
        });
    }

    private void sortByTitle (List<Todo> todoList) {
        todoList.sort(new Comparator<Todo>() {
            @Override
            public int compare(Todo t1, Todo t2) {
                return t1.getTitle().compareTo(t2.getTitle());
            }
        });
    }

    private void sortByDueDate (List<Todo> todoList) {
        todoList.sort(new Comparator<Todo>() {
            @Override
            public int compare(Todo t1, Todo t2) {
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        });
    }
}

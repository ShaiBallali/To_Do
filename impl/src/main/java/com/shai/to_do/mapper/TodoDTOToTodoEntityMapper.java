package com.shai.to_do.mapper;

import com.shai.to_do.Context;
import com.shai.to_do.constants.Status;
import com.shai.to_do.dto.TodoDTO;
import com.shai.to_do.entity.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoDTOToTodoEntityMapper {

    private final Context context;

    public TodoDTOToTodoEntityMapper(Context context) {
        this.context = context;
    }

    public Todo map(TodoDTO todoDTO) {
        Integer id = context.getIdCounterAndIncrement();
        String title = todoDTO.title();
        String content = todoDTO.content();
        String status = Status.PENDING;
        Long dueDate = todoDTO.dueDate();

        return new Todo(id, title, content, status, dueDate);
    }
}

package com.shai.to_do.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private Integer id;
    private String title;
    private String content;
    private String status;
    private Long dueDate;
}

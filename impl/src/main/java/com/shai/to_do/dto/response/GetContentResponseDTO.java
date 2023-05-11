package com.shai.to_do.dto.response;

import com.shai.to_do.entity.Todo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetContentResponseDTO implements ResponseDTO {
    private List<Todo> result;
}

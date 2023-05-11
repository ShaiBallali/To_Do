package com.shai.to_do.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDTO implements ResponseDTO{
    private String errorMessage;
}

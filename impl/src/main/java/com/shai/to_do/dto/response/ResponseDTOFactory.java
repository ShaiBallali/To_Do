package com.shai.to_do.dto.response;

import com.shai.to_do.constants.Queries;
import org.springframework.stereotype.Component;

@Component
public class ResponseDTOFactory {
    public ResponseDTO getResponseDTO(String queryType) {
        return switch(queryType) {
            case Queries.ADD -> new AddResponseDTO();
            case Queries.COUNT -> new CountByStatusResponseDTO();
            case Queries.DELETE -> new DeleteResponseDTO();
            case Queries.GET -> new GetContentResponseDTO();
            case Queries.UPDATE -> new UpdateStatusResponseDTO();
            default -> new ExceptionDTO();
        };
    }
}

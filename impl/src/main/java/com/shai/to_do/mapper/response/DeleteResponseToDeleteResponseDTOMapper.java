package com.shai.to_do.mapper.response;

import com.shai.to_do.constants.Queries;
import com.shai.to_do.dto.response.DeleteResponseDTO;
import com.shai.to_do.dto.response.ResponseDTOFactory;
import org.springframework.stereotype.Component;

@Component
public class DeleteResponseToDeleteResponseDTOMapper {
    private final ResponseDTOFactory responseDTOFactory;

    public DeleteResponseToDeleteResponseDTOMapper(ResponseDTOFactory responseDTOFactory) {
        this.responseDTOFactory = responseDTOFactory;
    }

    public DeleteResponseDTO map(int count) {
        DeleteResponseDTO deleteResponseDTO = (DeleteResponseDTO) responseDTOFactory.getResponseDTO(Queries.DELETE);
        deleteResponseDTO.setResult(count);
        return deleteResponseDTO;
    }
}

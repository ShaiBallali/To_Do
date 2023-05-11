package com.shai.to_do.mapper.response;

import com.shai.to_do.constants.Queries;
import com.shai.to_do.dto.response.AddResponseDTO;
import com.shai.to_do.dto.response.ResponseDTOFactory;
import org.springframework.stereotype.Component;

@Component
public class AddResponseToAddResponseDTOMapper {
    private final ResponseDTOFactory responseDTOFactory;

    public AddResponseToAddResponseDTOMapper(ResponseDTOFactory responseDTOFactory) {
        this.responseDTOFactory = responseDTOFactory;
    }

    public AddResponseDTO map(int id) {
        AddResponseDTO addResponseDTO = (AddResponseDTO) responseDTOFactory.getResponseDTO(Queries.ADD);
        addResponseDTO.setResult(id);
        return addResponseDTO;
    }
}

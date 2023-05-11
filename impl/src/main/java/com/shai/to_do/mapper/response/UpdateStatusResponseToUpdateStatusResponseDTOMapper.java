package com.shai.to_do.mapper.response;

import com.shai.to_do.constants.Queries;
import com.shai.to_do.dto.response.ResponseDTOFactory;
import com.shai.to_do.dto.response.UpdateStatusResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UpdateStatusResponseToUpdateStatusResponseDTOMapper {
    private final ResponseDTOFactory responseDTOFactory;

    public UpdateStatusResponseToUpdateStatusResponseDTOMapper(ResponseDTOFactory responseDTOFactory) {
        this.responseDTOFactory = responseDTOFactory;
    }

    public UpdateStatusResponseDTO map(String oldState) {
        UpdateStatusResponseDTO updateStatusResponseDTO = (UpdateStatusResponseDTO) responseDTOFactory.getResponseDTO(Queries.UPDATE);
        updateStatusResponseDTO.setResult(oldState);
        return updateStatusResponseDTO;
    }
}

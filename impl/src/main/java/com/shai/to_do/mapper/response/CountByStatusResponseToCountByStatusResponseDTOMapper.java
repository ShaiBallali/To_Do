package com.shai.to_do.mapper.response;

import com.shai.to_do.constants.Queries;
import com.shai.to_do.dto.response.CountByStatusResponseDTO;
import com.shai.to_do.dto.response.ResponseDTOFactory;
import org.springframework.stereotype.Component;

@Component
public class CountByStatusResponseToCountByStatusResponseDTOMapper {
    private final ResponseDTOFactory responseDTOFactory;

    public CountByStatusResponseToCountByStatusResponseDTOMapper(ResponseDTOFactory responseDTOFactory) {
        this.responseDTOFactory = responseDTOFactory;
    }

    public CountByStatusResponseDTO map(long count) {
        CountByStatusResponseDTO countByStatusResponseDTO = (CountByStatusResponseDTO) responseDTOFactory.getResponseDTO(Queries.COUNT);
        countByStatusResponseDTO.setResult(count);
        return countByStatusResponseDTO;
    }
}

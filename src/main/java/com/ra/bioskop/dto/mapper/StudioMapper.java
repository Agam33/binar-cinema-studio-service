package com.ra.bioskop.dto.mapper;

import com.ra.bioskop.dto.model.StudioDTO;
import com.ra.bioskop.model.studio.Studio;

public class StudioMapper {
    StudioMapper() {}
    public static Studio dtoToEntity(StudioDTO studioDTO) {
        Studio studio = new Studio();
        studio.setMaxSeat(studioDTO.getMaxSeat());
        studio.setName(studioDTO.getStudioName());
        return studio;
    }

    public static StudioDTO entityToDto(Studio studio) {
        StudioDTO studioDTO = new StudioDTO();
        studioDTO.setStudioName(studio.getName());
        studioDTO.setMaxSeat(studio.getMaxSeat());
        return studioDTO;
    }
}

package com.ra.bioskop.service;

import com.ra.bioskop.dto.model.StudioDTO;

import java.util.List;

public interface StudioService {

    void addStudios(List<StudioDTO> studioRequestList);

    List<StudioDTO> getAllStudio();
}

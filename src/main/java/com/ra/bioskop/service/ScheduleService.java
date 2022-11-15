package com.ra.bioskop.service;

import com.ra.bioskop.dto.model.ScheduleDTO;
import com.ra.bioskop.dto.request.ScheduleRequest;

import java.util.List;

public interface ScheduleService {

    boolean addSchedule(ScheduleRequest request);
    List<ScheduleDTO> getScheduleByDate(String date);
}

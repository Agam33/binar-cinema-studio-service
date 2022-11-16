package com.ra.bioskop.service;

import com.ra.bioskop.dto.model.FilmDTO;
import com.ra.bioskop.dto.model.ScheduleDTO;
import com.ra.bioskop.dto.model.StudioDTO;
import com.ra.bioskop.dto.request.ScheduleRequest;
import com.ra.bioskop.exception.ExceptionType;
import com.ra.bioskop.model.schedule.Schedule;
import com.ra.bioskop.model.studio.Studio;
import com.ra.bioskop.repository.ScheduleRepository;
import com.ra.bioskop.repository.StudioRepository;
import com.ra.bioskop.security.AuthEntryPoint;
import com.ra.bioskop.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.ra.bioskop.exception.BioskopException.throwException;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private ScheduleRepository scheduleRepository;

    private StudioRepository studioRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEntryPoint.class);

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, StudioRepository studioRepository) {
        this.scheduleRepository = scheduleRepository;
        this.studioRepository = studioRepository;
    }

    @Override
    public boolean addSchedule(ScheduleRequest request, FilmDTO filmDTO) {
        Optional<Studio> studio= studioRepository.findById(request.getStudioId());

        if(studio.isEmpty())
            throw throwException(ExceptionType.NOT_FOUND, HttpStatus.NOT_FOUND, Constants.NOT_FOUND_MSG);

        Studio studioModel = studio.get();
        Schedule scheduleModel = new Schedule();

        LOGGER.info("Film Code : "+filmDTO.getFilmCode());
        LOGGER.info("Film Title : "+filmDTO.getTitle());
        LOGGER.info("Film Overview : "+filmDTO.getOverview());

        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalDate showAt = LocalDate.parse( request.getShowAt());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        scheduleModel.setId(getScheduleId(request.getFilmId(), startTime, showAt));
        scheduleModel.setPrice(request.getPrice());
        scheduleModel.setFilmId(filmDTO.getFilmCode());
        scheduleModel.setFilmTitle(filmDTO.getTitle());
        scheduleModel.setCreatedAt(LocalDateTime.now());
        scheduleModel.setUpdatedAt(LocalDateTime.now());
        scheduleModel.setShowAt(showAt);
        scheduleModel.setStartTime(startTime);
        scheduleModel.setEndTime(endTime);
        scheduleModel.setStudio(studioModel);
        scheduleRepository.save(scheduleModel);
        return true;
    }

    @Override
    public List<ScheduleDTO> getScheduleByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<ScheduleDTO> schedule = scheduleRepository.findByShowAt(localDate);
        if(!schedule.isEmpty()) return schedule;
        throw throwException(ExceptionType.NOT_FOUND, HttpStatus.NOT_FOUND, "Tidak ada jadwal film hari ini.");
    }

    private String getScheduleId(String filmCode, LocalTime startTime, LocalDate showAt) {
        String[] codes = Constants.randomIdentifier(filmCode + startTime.toString() + showAt.getDayOfWeek());
        return "sc-" + codes[3] + "-" + codes[4];
    }
}

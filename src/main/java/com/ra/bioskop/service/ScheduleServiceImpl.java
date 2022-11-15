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
import com.ra.bioskop.util.Constants;
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

    @Value("#${service.client.filmService.url}")
    private String baseFilmClient;

    private ScheduleRepository scheduleRepository;

    private StudioRepository studioRepository;

    private final WebClient webClient;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, StudioRepository studioRepository, WebClient webClient) {
        this.scheduleRepository = scheduleRepository;
        this.studioRepository = studioRepository;
        this.webClient = webClient;
    }

    @Override
    public boolean addSchedule(ScheduleRequest request) {
        Optional<FilmDTO> filmDTO = Optional.ofNullable(webClient
                .get()
                .uri(baseFilmClient + "/api/v1/films/" + request.getFilmCode())
                .retrieve().bodyToMono(FilmDTO.class).block());

        Optional<Studio> studio= studioRepository.findById(request.getStudioId());

        if(filmDTO.isEmpty() || studio.isEmpty())
            throw throwException(ExceptionType.NOT_FOUND, HttpStatus.NO_CONTENT, Constants.NOT_FOUND_MSG);


        FilmDTO filmModel = filmDTO.get();
        Studio studioModel = studio.get();
        Schedule scheduleModel = new Schedule();

        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalDate showAt = LocalDate.parse( request.getShowAt());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        scheduleModel.setId(getScheduleId(request.getFilmCode(), startTime, showAt));
        scheduleModel.setPrice(request.getPrice());
        scheduleModel.setFilmId(filmModel.getFilmCode());
        scheduleModel.setFilmTitle(filmModel.getTitle());
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

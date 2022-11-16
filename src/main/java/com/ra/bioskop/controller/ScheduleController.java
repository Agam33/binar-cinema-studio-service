package com.ra.bioskop.controller;

import com.ra.bioskop.dto.model.FilmDTO;
import com.ra.bioskop.dto.request.ScheduleRequest;
import com.ra.bioskop.dto.response.Response;
import com.ra.bioskop.dto.response.ResponseError;
import com.ra.bioskop.exception.BioskopException;
import com.ra.bioskop.exception.ExceptionType;
import com.ra.bioskop.service.ScheduleService;
import com.ra.bioskop.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.ra.bioskop.exception.BioskopException.throwException;

@RestController
@RequestMapping(Constants.SCHEDULES_V1_ENDPOINT)
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Value("${service.client.filmService.url}")
    private String baseFilmClient;

    private final WebClient webClient;

    public ScheduleController(ScheduleService scheduleService, WebClient webClient) {
        this.scheduleService = scheduleService;
        this.webClient = webClient;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequest scheduleRequest, HttpServletRequest request)  {
        try {
            return webClient
                    .get()
                    .uri(baseFilmClient + "/api/v1/films/detail?id=" + scheduleRequest.getFilmId())
                    .header(Constants.HEADER, Constants.TOKEN_PREFIX + request.getAttribute("validToken"))
                    .retrieve().bodyToMono(Response.class).map(res -> {

                        LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) res.getData();

                        if(linkedHashMap == null)
                            throw throwException(ExceptionType.NOT_FOUND, HttpStatus.NOT_FOUND, Constants.NOT_FOUND_MSG);

                        FilmDTO filmDTO = new FilmDTO();
                        filmDTO.setFilmCode((String) linkedHashMap.get("filmCode"));
                        filmDTO.setTitle((String) linkedHashMap.get("title"));
                        filmDTO.setOnShow((Boolean) linkedHashMap.get("onShow"));
                        filmDTO.setOverview((String) linkedHashMap.get("overview"));

                        scheduleService.addSchedule(scheduleRequest, filmDTO);
                        return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), new Date(),
                                Constants.SUCCESS_MSG, null));
                    }).block();
        } catch (BioskopException.EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseError(e.getStatusCode().value(), new Date(), e.getMessage()),
                    e.getStatusCode());
        }
    }

    @GetMapping("/date")
    public ResponseEntity<?> getScheduleByDate(@RequestParam String date) {
        try {
            return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), new Date(),
                    Constants.SUCCESS_MSG, scheduleService.getScheduleByDate(date)));
        } catch (BioskopException.EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseError(e.getStatusCode().value(), new Date(), e.getMessage()), e.getStatusCode());
        }
    }
}

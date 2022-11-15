package com.ra.bioskop.controller;

import com.ra.bioskop.dto.request.ScheduleRequest;
import com.ra.bioskop.dto.response.Response;
import com.ra.bioskop.dto.response.ResponseError;
import com.ra.bioskop.exception.BioskopException;
import com.ra.bioskop.service.ScheduleService;
import com.ra.bioskop.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(Constants.SCHEDULES_V1_ENDPOINT)
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequest scheduleRequest)  {
        try {
            scheduleService.addSchedule(scheduleRequest);
            return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), new Date(),
                    Constants.SUCCESS_MSG, null));
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

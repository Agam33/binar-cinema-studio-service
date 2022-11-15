package com.ra.bioskop.controller;

import com.ra.bioskop.dto.model.StudioDTO;
import com.ra.bioskop.dto.response.Response;
import com.ra.bioskop.dto.response.ResponseError;
import com.ra.bioskop.exception.BioskopException;
import com.ra.bioskop.service.StudioService;
import com.ra.bioskop.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(Constants.STUDIO_V1_ENDPOINT)
public class StudioController {

    private final StudioService studioService;

    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }

    @GetMapping
    public ResponseEntity<?> getStudios() {
        try {
            return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), new Date(),
                    Constants.SUCCESS_MSG, studioService.getAllStudio()));
        } catch (BioskopException.EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseError(e.getStatusCode().value(), new Date(), e.getMessage()), e.getStatusCode());
        }
    }

    @PostMapping("/addAll")
    public ResponseEntity<?> addStudios(@RequestBody List<StudioDTO> studioDTOList) {
        studioService.addStudios(studioDTOList);
        return ResponseEntity.ok(new Response<>(HttpStatus.OK.value(), new Date(), Constants.SUCCESS_MSG, null));
    }
}

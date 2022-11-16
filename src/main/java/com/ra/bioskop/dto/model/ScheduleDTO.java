package com.ra.bioskop.dto.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleDTO {
    private String filmTitle;
    private String studioName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate showAt;
    private BigDecimal price;
}

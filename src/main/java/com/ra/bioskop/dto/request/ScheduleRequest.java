package com.ra.bioskop.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {
    @NotNull
    private String filmId;

    @NotNull
    private Integer studioId;

    @NotNull(message = "HH:mm")
    private String startTime;

    @NotNull(message = "HH:mm")
    private String endTime;

    @NotNull(message = "yyyy-MM-dd")
    private String showAt;

    @NotNull
    private BigDecimal price;
}

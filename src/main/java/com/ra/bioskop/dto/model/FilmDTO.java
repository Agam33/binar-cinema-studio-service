package com.ra.bioskop.dto.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
public class FilmDTO {
    private String filmCode;
    private String title;
    private Integer runtime;
    private boolean onShow;
    private LocalDate releaseDate;
    private String overview;
    private Double popularity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
//    private List<FilmGenre> genres = new ArrayList<>();
}

package com.reactivespring.movieinfoservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {
    @Id
    private String movieInfoId;
    @NotBlank(message = "Movie name should not be null")
    private String name;
    @NotNull
    @Positive(message = "Year should be positive")
    private Integer year;
    private List<@NotBlank(message = "Cast should not be null") String> cast;
    private LocalDate releaseDate;
}

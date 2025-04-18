package com.reactivespring.movieinfoservice.repository;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class MovieInfoRepositoryTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }


    @Test
    void findAll() {
        var movieInfo = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfo).expectNextCount(3).verifyComplete();
    }

    @Test
    void findById() {
        var movieInfo = movieInfoRepository.findById("abc");
        StepVerifier.create(movieInfo).assertNext(movieInfo1 -> {
            assertEquals("Dark Knight Rises", movieInfo1.getName());
        });
    }

    @Test
    void saveMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var savedMovieInfo = movieInfoRepository.save(movieInfo);
        StepVerifier.create(savedMovieInfo)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2021);
        var savedMovieInfo = movieInfoRepository.save(movieInfo);
        StepVerifier.create(savedMovieInfo).assertNext(movieInfo1 -> {
            assertEquals(2021, movieInfo1.getYear());
        });
    }

    @Test
    void deleteMovieInfo() {
        movieInfoRepository.deleteById("abc").block();
        var movieInfos = movieInfoRepository.findAll();
        StepVerifier.create(movieInfos)
                .expectNextCount(2)
                .verifyComplete();
    }

}
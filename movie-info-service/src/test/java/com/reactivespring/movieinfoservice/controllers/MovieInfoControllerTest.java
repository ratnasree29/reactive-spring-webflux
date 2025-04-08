package com.reactivespring.movieinfoservice.controllers;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieInfoControllerTest {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static String MOVIE_INFO_URL = "/v1/movieinfos";

    @BeforeEach
    public void setUp() {
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
    public void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    public void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .value(movieInfoResponse -> {
                    assertNotNull(movieInfoResponse.getMovieInfoId());
                    assertEquals(movieInfo.getName(), movieInfoResponse.getName());
                });
    }

    @Test
    public void getAllMovies() {
        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    public void getAllMovies_stream() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .value(movieInfoResponse -> {
                    assertNotNull(movieInfoResponse.getMovieInfoId());
                    assertEquals(movieInfo.getName(), movieInfoResponse.getName());
                });
        var list = webTestClient.get()
                .uri(MOVIE_INFO_URL+"/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(MovieInfo.class)
                        .getResponseBody();
        StepVerifier.create(list)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1.getMovieInfoId() != null;
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void getMovieById() {
        webTestClient.get()
                .uri(MOVIE_INFO_URL + "/abc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .value(movieInfoResponse -> {
                    assertEquals("Dark Knight Rises", movieInfoResponse.getName());
                });
    }

    @Test
    public void updateMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/abc")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedMovieInfo);
                    assertEquals("Batman Begins", updatedMovieInfo.getName());
                });
    }

    @Test
    public void updateMovieInfo_notfound() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/def")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var updatedMovieInfo = stringEntityExchangeResult.getResponseBody();
                    assertNull(updatedMovieInfo);
                });
    }

    @Test
    public void deleteMovieInfo() {
        webTestClient.delete()
                .uri(MOVIE_INFO_URL + "/abc")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void addMovieInfoValidation() {
        var movieInfo = new MovieInfo(null, "",
                -2005, List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .value(stringEntityExchangeResult -> {
                    assertNotNull(stringEntityExchangeResult);
                    assertTrue(stringEntityExchangeResult.contains("Movie name should not be null"));
                    assertTrue(stringEntityExchangeResult.contains("Year should be positive"));
                    assertTrue(stringEntityExchangeResult.contains("Cast should not be null"));
                });

    }
}
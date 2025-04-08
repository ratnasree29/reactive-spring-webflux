package com.reactivespring.movieseries.controller;

import com.reactivespring.movieseries.client.MoviesInfoRestClient;
import com.reactivespring.movieseries.client.ReviewsRestClient;
import com.reactivespring.movieseries.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MoviesInfoRestClient moviesInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }
    @GetMapping("/{id}")
    public Mono<Movie> retrieMovieById(@PathVariable(value = "id") String movieId) {
        return moviesInfoRestClient.getMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsListMono = reviewsRestClient.retrieveReviews(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsListMono.map(reviews ->
                            new Movie(movieInfo, reviews));
                });
    }

}

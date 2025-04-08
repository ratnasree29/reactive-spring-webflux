package com.reactivespring.movieseries.client;

import com.reactivespring.movieseries.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class ReviewsRestClient {

    private WebClient webClient;

    @Value("${restclient.reviewsurl}")
    private String reviewsurl;

    public ReviewsRestClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Flux<Review> retrieveReviews(String movieInfoId) {
        return this.webClient
                .get()
                .uri(reviewsurl)
                .retrieve()
                .bodyToFlux(Review.class)
                .log();
    }
}

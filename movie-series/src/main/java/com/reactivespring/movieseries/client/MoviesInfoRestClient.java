package com.reactivespring.movieseries.client;

import com.reactivespring.movieseries.domain.MovieInfo;
import com.reactivespring.movieseries.exception.MovieInfoClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class MoviesInfoRestClient {

    private WebClient webClient;

    @Value("${restclient.moviesinfourl}")
    private String movieInfoUrl;

    public MoviesInfoRestClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    Retry retruspec = Retry.fixedDelay(3, Duration.ofMillis(1000))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());

    public Mono<MovieInfo> getMovieInfo(String movieId) {
        return this.webClient
                .get()
                .uri(movieInfoUrl, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MovieInfoClientException("There is no MovieInfo available for the passed movieId: " + movieId)
                        );
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MovieInfoClientException(responseMessage)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(responseMessage -> Mono.error(new MovieInfoClientException("Server Exception in MovieInfoService" + responseMessage))))
                .bodyToMono(MovieInfo.class)
                //.retry(3)
                //.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1000)))
                .retryWhen(retruspec)
                .log();
    }
}

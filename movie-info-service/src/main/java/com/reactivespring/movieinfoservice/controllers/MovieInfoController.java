package com.reactivespring.movieinfoservice.controllers;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import com.reactivespring.movieinfoservice.service.MovieInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {

    @Autowired
    private MovieInfoService movieInfoService;

    Sinks.Many<MovieInfo> sink = Sinks.many().replay().all();

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedInfo -> sink.tryEmitNext(savedInfo));
    }

    @GetMapping(value = "/movieinfos/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getMovieById() {
        return sink.asFlux().log();
    }

    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovies() {
        return movieInfoService.getAllMovies().log();
    }

    @GetMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> getMovieById(@PathVariable String id) {
        return movieInfoService.getMovieById(id).log();
    }

    @PutMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo movieInfo, @PathVariable String id) {
        return movieInfoService.updateMovieInfo(movieInfo, id)
                .map(updatedMovieInfo -> ResponseEntity.ok().body(updatedMovieInfo))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}

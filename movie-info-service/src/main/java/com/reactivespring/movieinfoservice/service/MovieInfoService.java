package com.reactivespring.movieinfoservice.service;

import com.reactivespring.movieinfoservice.domain.MovieInfo;
import com.reactivespring.movieinfoservice.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    @Autowired
    private MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovies() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo movieInfo, String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo2 -> {
                    movieInfo2.setName(movieInfo.getName());
                    movieInfo2.setYear(movieInfo.getYear());
                    movieInfo2.setCast(movieInfo.getCast());
                    movieInfo2.setReleaseDate(movieInfo.getReleaseDate());
                    return movieInfoRepository.save(movieInfo2);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}

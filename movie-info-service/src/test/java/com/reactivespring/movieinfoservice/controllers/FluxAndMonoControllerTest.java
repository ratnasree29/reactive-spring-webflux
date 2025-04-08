package com.reactivespring.movieinfoservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = FluxAndMonoController.class)
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        webTestClient.get().uri("/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0]").isEqualTo(1)
                .jsonPath("$.[1]").isEqualTo(2)
                .jsonPath("$.[2]").isEqualTo(3);
    }

    @Test
    void mono() {
        webTestClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello world");
    }

    @Test
    void stream() {
        var flux = webTestClient.get().uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();
        StepVerifier.create(flux)
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
                .verify();
    }
}
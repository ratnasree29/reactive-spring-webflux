package com.reactivespring.movieinfoservice.controllers;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sinkTest() {
        //Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
        Sinks.Many<Integer> replaySink = Sinks.many().multicast().onBackpressureBuffer();
        // Sinks.Many<Integer> replaySink = Sinks.many().unicast().onBackpressureBuffer();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);
        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i -> {
            System.out.println("SUBSCRIBER 1:" + i);
        }));
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i -> {
            System.out.println("SUBSCRIBER 2:" + i);
        }));
        replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}

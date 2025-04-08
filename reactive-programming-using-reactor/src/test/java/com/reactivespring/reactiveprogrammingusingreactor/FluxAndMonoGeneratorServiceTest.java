package com.reactivespring.reactiveprogrammingusingreactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    @Test
    public void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben","chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void nameMono() {
        var nameMono = fluxAndMonoGeneratorService.nameMono();
        nameMono.subscribe(name -> {
            System.out.println("Name is: " + name);
        });
    }

    @Test
    public void namesFluxMap() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN","CHLOE")
                .verifyComplete();
    }

    @Test
    public void namesFluxMapImmutability() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMapImmutability();
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben","chloe")
                .verifyComplete();
    }

    @Test
    public void namesFluxFilter() {
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(3);
        StepVerifier.create(namesFlux)
                .expectNext("ALEX","CHLOE")
                .verifyComplete();
    }

    @Test
    public void namesFluxFlatMapAsync() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength);
        StepVerifier.create(namesFlux)
                //.expectNext("A","L", "E", "X","C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    public void namesFluxFlatMap() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L", "E", "X","C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    public void namesFluxConcatMapAsync() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMapAsync(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L", "E", "X","C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    public void namesFluxFlatMapSequential() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapSequential(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L", "E", "X","C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void nameMonoFilter() {
        var mono = fluxAndMonoGeneratorService.nameMonoFilter(3);
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
    }

    @Test
    void nameMonoFilterFlatMap() {
        var mono = fluxAndMonoGeneratorService.nameMonoFilterFlatMap(3);
        StepVerifier.create(mono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapTransform() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapTransform(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L", "E", "X","C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapTransform_emptyresultset() {
        int stringLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapTransform(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapTransformSwitchIfEmpty() {
        int stringLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapTransformSwitchIfEmpty(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("DEFAULT")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        var namesFlux = fluxAndMonoGeneratorService.explore_concat();
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatWithMono() {
        var namesFlux = fluxAndMonoGeneratorService.explore_concatWithMono();
        StepVerifier.create(namesFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        var namesFlux = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(namesFlux)
                //.expectNext("A", "D","B","E","C","F")
                .expectNext("D","A", "E","B","F","C")
                .verifyComplete();
    }

    @Test
    void explore_merge_with() {
        var namesFlux = fluxAndMonoGeneratorService.explore_merge_with();
        StepVerifier.create(namesFlux)
                .expectNext("A", "D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void explore_merge_sequential() {
        var namesFlux = fluxAndMonoGeneratorService.explore_merge_sequential();
        StepVerifier.create(namesFlux)
                .expectNext("A","B","C", "D", "E","F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        var namesFlux = fluxAndMonoGeneratorService.explore_zip();
        StepVerifier.create(namesFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zipreverse() {
        var namesFlux = fluxAndMonoGeneratorService.explore_zipreverse();
        StepVerifier.create(namesFlux)
                .expectNext("DA", "EB", "FC")
                .verifyComplete();
    }

    @Test
    void explore_zipwith() {
        var namesFlux = fluxAndMonoGeneratorService.explore_zipwith();
        StepVerifier.create(namesFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_4() {
        var namesFlux = fluxAndMonoGeneratorService.explore_zip_4();
        StepVerifier.create(namesFlux)
                .expectNext("A1D4", "B2E5", "C3F6")
                .verifyComplete();
    }

    // This sequence starts at 0
    // passes the next value with .next()
    // passes complete signal with .complete()
    @Test
    public void FluxWithGenerate() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        flux.subscribe(System.out::println);
    }
}
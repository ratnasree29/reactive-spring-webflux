package com.reactivespring.reactiveprogrammingusingreactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> System.out.println("Name is: " + name));
        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> System.out.println("Name is: " + name));
        fluxAndMonoGeneratorService.namesFluxFilter(3)
                .subscribe(name -> System.out.println("Name is: " + name));
    }

    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .log();
    }

    // return upper case
    public Flux<String> namesFluxMap(){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .map(String::toUpperCase)
                .log();
    }

    //Flux is immutable
    public Flux<String> namesFluxMapImmutability(){
        var namesFlux = Flux.fromIterable(List.of("alex","ben","chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    // return upper case
    public Flux<String> namesFluxFilter(int stringLength){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .log();
    }

    //flatmap
    public Flux<String> namesFluxFlatMap(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMap(this::splitString)
                .log();
    }

    private Flux<String> splitString(String s) {
        var charArray = s.split("");
        return Flux.fromArray(charArray);
    }

    //flatmap asynchronous
    public Flux<String> namesFluxFlatMapAsync(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMap(this::splitString_withDelay)
                .log();
    }

    //concatmap synchronous
    public Flux<String> namesFluxConcatMapAsync(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .concatMap(this::splitString_withDelay)
                .log();
    }

    //flatmapsequential asynchronous
    public Flux<String> namesFluxFlatMapSequential(int stringLength){
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMapSequential(this::splitString_withDelay)
                .log();
    }


    private Flux<String> splitString_withDelay(String s) {
        var charArray = s.split("");
        var random = new Random().nextInt(1000);
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(random));
    }


    // MONO starting
    public Mono<String> nameMono(){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .log();
    }

    // MONO starting
    public Mono<String> nameMonoFilter(int strLength){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .log();
    }

    // MONO starting
    public Mono<List<String>> nameMonoFilterFlatMap(int strLength){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .flatMap(this::splitStringForMono)
                .log();
    }

    private Mono<List<String>> splitStringForMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    // MONO starting
    public Flux<String> nameMonoFilterFlatMapMany(int strLength){
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .flatMapMany(this::splitString)
                .log();
    }

    //flux transform
    public Flux<String> namesFluxFlatMapTransform(int stringLength){
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    //flux transform
    public Flux<String> namesFluxFlatMapTransformSwitchIfEmpty(int stringLength){
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        Flux just = Flux.just("default").transform(filterMap);
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .switchIfEmpty(just)
                .log();
    }

    //flux concat
    public Flux<String> explore_concat(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux).log();
    }

    //mono concat
    public Flux<String> explore_concatWithMono(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.concatWith(bMono).log();
    }

    //flux merge same for mono also
    public Flux<String> explore_merge(){
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(90));
        return Flux.merge(abcFlux, defFlux).log();
    }

    //flux mergewith same for mono also
    public Flux<String> explore_merge_with(){
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux).log();
    }

    //flux merge
    public Flux<String> explore_merge_sequential(){
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    //flux zip
    public Flux<String> explore_zip(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
    }

    //flux zip reverse
    public Flux<String> explore_zipreverse(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> second + first).log();
    }

    //flux zip
    public Flux<String> explore_zipwith(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
    }

    //flux zip
    public Flux<String> explore_zip_4(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, _123Flux, defFlux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }


}
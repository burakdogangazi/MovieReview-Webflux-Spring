package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux(){
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then

        StepVerifier.create(namesFlux)
                // .expectNext("aziz","metin","yeliz")
                //.expectNextCount(3)
                .expectNext("aziz")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map(){
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        //then

        StepVerifier.create(namesFlux)
                .expectNext("4-BURAK,5-DOĞAN")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap(){
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        //then

        StepVerifier.create(namesFlux)
                .expectNext("B","U","R","A","K","D","O","Ğ","A","N")
                .verifyComplete();
    }
    @Test
    void namesFlux_flatMap_delay(){
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        //then

        StepVerifier.create(namesFlux)
                // .expectNext("B","U","R","A","K","D","O","Ğ","A","N")
                .expectNextCount(10)
                .verifyComplete();
    }

    void namesFlux_concatmap(){
        //given
        int stringLength = 3;
        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        //then

        StepVerifier.create(namesFlux)
                // .expectNext("B","U","R","A","K","D","O","Ğ","A","N")
                .expectNextCount(10)
                .verifyComplete();
    }
    @Test
    void namesFlux_immutubality(){
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        //then
        //doesnt work immutable nature of reactive streams
        StepVerifier.create(namesFlux)
                .expectNext("BURAK","DOĞAN","METİN")
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap_Mono(){
        int stringLength = 3;

        var value = fluxAndMonoGeneratorService.namesMono_flatMap_Mono(stringLength);

        StepVerifier.create(value)
                .expectNext(List.of("B","U","R","A","K"))
                .verifyComplete();

    }

    @Test
    void namesMono_flatMapMany(){
        int stringLength = 3;

        var value = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        StepVerifier.create(value)
                .expectNext("B","U","R","A","K")
                .verifyComplete();

    }

    @Test
    void namesFlux_transform(){
        int stringLength = 3;

        var value = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(value)
                .expectNext("B","U","R","A","K","D","O","Ğ","A","N")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_empty(){
        int stringLength = 6;

        var value = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(value)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty(){
        int stringLength = 6;

        var value = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        StepVerifier.create(value)
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    /*
     * Map -- > one to one transformation, does the simple transformation  T to V
     * used for simple sync transformation
     *
     * Flatmap --> one to N transformation, does more than transformation. subscribes to flux or monot thats part of the transformation and then flattens it and sends it downstream
     * used for async transformations, use it whit transformations that returns publisher
     *
     *
     * Concatmap --> works similar to flatmap only difference is taht concatmap preserves the ordering sequence of the reactive streams. flatmap faster
     * but we lose the ordering of the elements. onNext in flatmap B,U,K,A,R, onNext in concatmap B,U,R,A,K publisher (db,remote service etc.) --> subscriber
     *
     *
     * Flatmap in mono --> use it when transformation returns a mono, Returns a Mono<T>, use flatmap if the transformation involves making a rest api call or
     * any kind of functionality that can be done asynchronously
     *
     * FlatmapMany in mono --> works very similar to flatmap()
     *
     * transform --> used to transform from one type to another, accepts function functional interface,  input(Publisher flux or mono), output(Publisher flux or mono)
     *
     * defaultIfEmpty() & switchIfEmpty()--> its not mandatory for a data source to emit data all the time, we can use the defaultIfEmpty() or switchIfEmpty() operator to provide default values
     *
     *
     * */


    @Test
    void explore_concat(){

        var concatFlux = fluxAndMonoGeneratorService.explore_concat();
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }
    @Test
    void explore_merge(){

        var mergeFlux = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(mergeFlux)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void explore_merge_Sequential(){

        var mergeFlux = fluxAndMonoGeneratorService.explore_merge();
        StepVerifier.create(mergeFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_zip(){
        var value = fluxAndMonoGeneratorService.explore_zip();

        StepVerifier.create(value)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1(){
        var value = fluxAndMonoGeneratorService.explore_zip_1();

        StepVerifier.create(value)
                .expectNext("AD14","BE25","CF36")
                .verifyComplete();
    }

    /*
     * COMBINING REACTIVE STREAMS
     *
     * concat() & concatWith() --> used to combine two reactive streams in to one,
     * concatenation of reactive streams happens in a sequence
     * first one is subscribed first and completes
     * second one is subscribed after that and then completes
     * concat static method in flux, concatwith instance method in Flux and mono
     *
     *
     * merge & mergeWith --> merge and mergeWith are used to combine two publishers in to one
     * both the publishers are subscribed at the same time
     * publishers are subscribed eagerly and the merge happens in an interleaved fashion
     * concat subscribes to the publishers in a sequence
     * merge static method in flux
     * mergeWith instance method in flux and mono
     *
     *
     *
     *mergeSequential() --> used to combine two publishers(Flux) in to one
     * static method in flux
     * both the publishers are subscribed at the same time eagerly, the merge happens in sequence
     *
     *
     *zip() & zipWith() --> zips two publishers together
     *zip static methods thats part of the flux
     *publishers are subscribed eagerly
     * waits for all the publishers involved in the transformation to emit one element
     *
     *
     *
     * */



}
package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {


    /*Transform using map*/
    public Flux<String> namesFlux_map(int stringLength){
        //filter the string whose length is greater than 3

        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .map(String::toUpperCase)
                //.map(s->s.toUpperCase())
                .filter(s->s.length() > stringLength)
                .map(s->s.length() +"-" +s) //4-Burak
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength){
        //filter the string whose length is greater than 3

        //Transforms one source element to a flux of 1 to n elements
        //use it when the transformation returns a reactive type(flux or mono)
        // returns a flux<type>
        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .map(String::toUpperCase)
                //.map(s->s.toUpperCase())
                .filter(s->s.length() > stringLength)
                .flatMap(s->splitString(s))
                .log();
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength){
        //filter the string whose length is greater than 3

        //Transforms one source element to a flux of 1 to n elements
        //use it when the transformation returns a reactive type(flux or mono)
        // returns a flux<type>
        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .map(String::toUpperCase)
                //.map(s->s.toUpperCase())
                .filter(s->s.length() > stringLength)
                .flatMap(s->splitString_withDelay(s))
                .log();
    }

    public Flux<String> splitString_withDelay(String name){
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));

    }

    public Flux<String> namesFlux_concatmap(int stringLength){
        //filter the string whose length is greater than 3

        //Transforms one source element to a flux of 1 to n elements
        //use it when the transformation returns a reactive type(flux or mono)
        // returns a flux<type>
        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .map(String::toUpperCase)
                //.map(s->s.toUpperCase())
                .filter(s->s.length() > stringLength)
                .concatMap(s->splitString_withDelay(s))
                .log();
    }


    public Flux<String> namesFlux_immutability(){
        var namesFlux = Flux.fromIterable(List.of("burak","doğan","metin"));
        namesFlux.map(String::toUpperCase);
        //doesnt return uppercase map making new flux stream
        return namesFlux;
    }


    public Mono<List<String>> namesMono_flatMap_Mono(int stringLength){
        return Mono.just("burak")
                .map(String::toUpperCase)
                .filter(s->s.length()>stringLength)
                .flatMap(this::splitStringMono);
    }

    private Mono<List<String>> splitStringMono(String s){
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }


    public Flux<String> namesMono_flatMapMany(int stringLength){
        return Mono.just("burak")
                .map(String::toUpperCase)
                .filter(s->s.length() >stringLength)
                .flatMapMany(this::splitString)
                .log();
    }


    public Flux<String> namesFlux_transform(int stringLength){

        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s->s.length()> stringLength);

        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .transform(filtermap)
                .flatMap(s->splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength){

        Function<Flux<java.lang.String>, Flux<java.lang.String>> filtermap = name -> name.map(java.lang.String::toUpperCase)
                .filter(s->s.length()> stringLength)
                .flatMap(s->splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filtermap);


        return Flux.fromIterable(List.of("burak","doğan","metin"))
                .switchIfEmpty(defaultFlux)
                .log();
    }

    //combining reactive streams

    public Flux<String> explore_concat(){
        var abcFlux =  Flux.just("A","B","C");

        var defFlux = Flux.just("D","E","F");

        return Flux.concat(abcFlux,defFlux);
    }

    public Flux<String> explore_concatWith_mono(){

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> explore_merge(){
        var abcFlux =  Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100)); // A,B

        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125)); // D,E
        //adds D to after the A, and 125 ms passed

        return Flux.merge(abcFlux,defFlux);
    }

    public Flux<String> explore_mergeWith(){
        var abcFlux =  Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100)); // A,B

        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125)); // D,E
        //adds D to after the A, and 125 ms passed

        return abcFlux.mergeWith(defFlux).log();
    }


    public Flux<String> explore_mergeWith_mono(){

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono).log();
    }


    public Flux<String> explore_mergeSequential(){
        var abcFlux =  Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100)); // A,B

        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125)); // D,E
        //adds D to after the A, and 125 ms passed

        return Flux.mergeSequential(abcFlux,defFlux);
    }


    public Flux<String> explore_zip(){

        var abcFlux =  Flux.just("A","B","C");

        var defFlux = Flux.just("D","E","F");
        //adds D to after the A, and 125 ms passed

        return Flux.zip(abcFlux,defFlux, (first, second) -> first + second); //AD, BE, CF
    }


    public Flux<String> explore_zip_1(){

        var abcFlux =  Flux.just("A","B","C");

        var defFlux = Flux.just("D","E","F");

        var _123Flux = Flux.just("1","2","3");

        var _456Flux = Flux.just("4","5","6");

        return Flux.zip(abcFlux,defFlux,_123Flux,_456Flux)
                .map(t4->t4.getT1() + t4.getT2() + t4.getT3()+ t4.getT4())
                .log();
    }

    public Flux<String> explore_zipWith(){

        var abcFlux =  Flux.just("A","B","C");

        var defFlux = Flux.just("D","E","F");
        //adds D to after the A, and 125 ms passed

        return abcFlux.zipWith(defFlux, (first,second) -> first+second).log();
    }

    public Mono<String> explore_mergeZipWith_mono(){
        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.zipWith(bMono).map(t2->t2.getT1()+t2.getT2())
                .log();
    }


    /* **************************************************************   */


    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("aziz","metin","yeliz"))
                .log(); // db or remote service call
    }

    public Mono<String> nameMono(){
        return Mono.just("burak")
                .log();
    }
    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(name->System.out.println("Name is "+name));


        fluxAndMonoGeneratorService.nameMono().subscribe(name->{
            System.out.println(name);
        });



        /*

        publisher db vs..--> subscriber, u can see the results by log command
        * onSubscribe()
        * request()
        * onNext() --> aziz
        * onNext() --> metin
        * onNext() --> yeliz
        * onCompleted

        */
    }
}

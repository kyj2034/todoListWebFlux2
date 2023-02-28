package com.study.webflux.test;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;



// 출처 : Spring Webflux 1 (웹플럭스 적용기, 설치) (https://lts0606.tistory.com/301?category=761939)

public class JustTest {
    final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName ());
    
    
    public static void main(String[] args) {
        
        //1번 내용---------------- 
        String text = "abcd";
        Mono<String> mono = Mono.just(text);
        mono.subscribe( str ->{ //변수 text를 살펴보고, 그에 대한 콜백 행위는 아래의 출력
        });
        
        //2번 내용----------------
        Mono<Object> function = Mono.create ( sink -> { // 자바스크립트의 Promise 와 유사하다.
            //sink.error(new Exception("애러")); -> 에러를 발생 
            sink.success("success"); // -> success 값을 전달
        });
        function.subscribe ( arg -> {
            System.out.println("next: " + arg);
        });
        
        //3번 내용----------------
        mono.doOnNext ( str -> { //맨 처음 구독한 다음 행위에 대한 지정
            System.out.println("next : " + str);
        }).subscribe ();
        
        //4번 내용----------------
        CompletableFuture<?> future = CompletableFuture.supplyAsync ( ()->{
            System.out.println("run!");
            return "PARAM";
        });
        Mono<Object> mono2 = Mono.fromFuture ( future ); //비동기에 대한 구독!
        mono2.subscribe ((param)->{
            System.out.println("param: " + param);
        });
    }
}
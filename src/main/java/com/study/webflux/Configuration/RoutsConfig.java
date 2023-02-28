package com.study.webflux.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.study.webflux.handler.FirstHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//출처 :  Spring WebFlux 3 (웹플럭스 적용기, 함수형과 반응형) (https://lts0606.tistory.com/303?category=761939)

@Configuration
public class RoutsConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName ());
    
    @Bean
    public RouterFunction<ServerResponse> route(FirstHandler handler) {
        return RouterFunctions.route(                                           /*  ::(이중 콜론으로 메소드 사용)*/
           RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::hello); 
    }
    
    @Bean
    public RouterFunction<ServerResponse> requestGetParam() {
        RequestPredicate predicate = RequestPredicates.GET("/request").and(RequestPredicates.accept ( MediaType.TEXT_PLAIN ));
        //1. Request + Predicate(요청 + ~이다) => 사용자의 요청 종류 및 형태
        
        //2. 웹플럭스에게 전달할 RouterFunction 클래스를 제작, 함수형태로 전달한다.
        RouterFunction<ServerResponse> response = RouterFunctions.route (predicate, (request)-> {
            //3. 내부 람다식은 HandlerFunction 인터페이스의 모양이다.
            System.out.println(request.queryParams());
            String justText = "get param";
            Mono<String> mapper = Mono.just(justText);
            //4. Mono 클래스를 통해서 전달할 내용을 만들고
            Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(BodyInserters.fromProducer(mapper,String.class));
            //5. 어떤 형태로 전달할 지 작성 후
            return res;
            //6. 전달
        });
        return response; //7. 해당 함수를 WebFlux 에게 전달한다.
    }
    
    @Bean
    public RouterFunction<ServerResponse> requestPathAndParam() {
        log.info("METHOD START!!!");
        final int number[] = {10};
        final RequestPredicate predicate = RequestPredicates.GET ( "/path/{name}" ).and(RequestPredicates.accept ( MediaType.TEXT_PLAIN ));
        
        RouterFunction<ServerResponse> response = RouterFunctions.route ( predicate, (request) -> {
           
            System.out.println(request.queryParams());
            System.out.println(request.pathVariables()); // path같은 것도 가능!
            
            String justText = "path and param";
            Mono<String> mapper = Mono.just ( justText );
            Mono<ServerResponse> res = ServerResponse.ok ().contentType ( MediaType.TEXT_PLAIN ).body ( BodyInserters.fromProducer ( mapper, String.class));
            number[0] = number[0] + 1;
            System.out.printf("number : %d\n",number[0]);
            return res;
        });
        log.info ( "METHOD END!!!!" );
        return response;
        
        // Method Start 와 Method End 문구가 한 번만 출력되고, 요청할 때마다 number 배열의 첫번째 데이터가 1씩 증가한다.
        // 그 이유는 @Bean 으로 생성하였기에 객체를 한 번 생성하여 IoC(?) 에 보관하여 재사용하기 때문이다.
    }
    
    
    //파일 다운로드
    @Bean
    public RouterFunction<ServerResponse> fileDownload() {
        RequestPredicate predicate = RequestPredicates.GET ( "/file" ).and ( RequestPredicates.accept ( MediaType.TEXT_PLAIN ));
        RouterFunction<ServerResponse> response = RouterFunctions.route ( predicate, (request) -> {
            Resource resource = new FileSystemResource("D:/test.csv");
            Mono<Resource> mapper = Mono.just ( resource ); // Mono의 제네릭에 의해 다양한 클래스를 받을 수 있다.
            
            //헤더라는 메소드와 바디라는 메소드는 이름만 봐도 어떠한 역할을 하는 지 알 것 같다.
            Mono<ServerResponse> res = ServerResponse.ok()
                            .header ( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.csv\"")
                            .body (BodyInserters.fromProducer ( mapper, Resource.class ));
            return res;
        });
        return response;
    }
    
    //파일 업로드   105 ~ 109 번째 줄 오류 발생. 
//    @Bean
//    public RouterFunction<ServerResponse> fileUpload() {
//        RequestPredicate predicate = RequestPredicates.POST("/fileUpload").and(RequestPredicates.accept(MediaType.MULTIPART_FORM_DATA));
//        RouterFunction<ServerResponse> response = RouterFunctions.route(predicate, (request)->{        
//
//            Mono<String> mapper = request.multipartData().map(it -> it.get("files"))
//                    .flatMapMany(Flux::fromIterable)
//                    .cast(FilePart.class)
//                    .flatMap(it -> it.transferTo(Paths.get("D:/test_upload/" + it.filename())))  //?? 
//                    .then(Mono.just("OK"));             
//            Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(BodyInserters.fromProducer(mapper, String.class));
//            return res;  
//        });
//        return response;
//    }
}
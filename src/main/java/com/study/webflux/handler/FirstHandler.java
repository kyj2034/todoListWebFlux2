package com.study.webflux.handler;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class FirstHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName ());
    private HashMap<Object, Object> result = new HashMap<>();
    private Mono<HashMap<Object, Object>> mapper = Mono.just ( result );
    
    public Mono<ServerResponse> hello(ServerRequest request) {
        result.put ( "number", 1234 );
        result.put ( "text", "webFlux" );
        mapper.subscribe ((arg) ->{ // ??
            log.info(arg.toString ());
        });
        return ServerResponse.ok().contentType ( MediaType.APPLICATION_JSON).body ( BodyInserters.fromProducer(mapper, HashMap.class));
    }
}
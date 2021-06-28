package com.jana.quiz.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class GlobalErrorUtils {
	private static final Logger logger = LoggerFactory.getLogger(GlobalErrorUtils.class);

    public static Mono<ResponseStatusException> manageError(final ClientResponse clientResponse, final String customMessage) {
    	logError(clientResponse, customMessage);
    	
    	final String clientErrorMessage = (null != customMessage) ? customMessage : (clientResponse.statusCode().is4xxClientError() ? "Bad request" : "Unknown error occurred");
    	
        return Mono.error(new ResponseStatusException(clientResponse.statusCode(), clientErrorMessage ));
    }
    
    public static void logError(ClientResponse response, String customMessage) {
    	if ( null != customMessage ) {
    		logger.error("Error {}", customMessage);
    	}
    	logger.error("Response status: {}", response.statusCode());
    	logger.error("Response headers: {}", response.headers().asHttpHeaders());
        response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> logger.error("Response body: {}", body));
    }
}

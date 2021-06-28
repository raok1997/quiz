package com.jana.quiz.config;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	private Logger logger = LoggerFactory.getLogger(WebClientConfig.class);
	
	//TODO move these to configuration
	private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 2_000;
	private static final int DEFAULT_READ_TIMEOUT_MILLIS = 2_000;
	private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 2_000;
	private static final String BASE_URL = "https://opentdb.com";
	private String USER_AGENT = "quiz-app";
	
	@Bean
	public WebClient webClientFromBuilder(WebClient.Builder webClientBuilder) {
	  HttpClient httpClient = HttpClient.create()
	    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT_MILLIS)
	    .doOnConnected(connection ->
	      connection
	        .addHandlerLast(new ReadTimeoutHandler(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
	        .addHandlerLast(new WriteTimeoutHandler(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)));
	 
	  return webClientBuilder
	    .baseUrl(BASE_URL)
	    .clientConnector(new ReactorClientHttpConnector(httpClient))
	    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
	    .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
	    .filter(ExchangeFilterFunctions.basicAuthentication("rieckpil", UUID.randomUUID().toString()))
	    .filter(logRequest())
	    .filter(logResponse())
	    .build();
	}
	
	private ExchangeFilterFunction logRequest() {
	    return (clientRequest, next) -> {
	      logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
	      logger.info("--- Http Headers: ---");
	      clientRequest.headers().forEach(this::logHeader);
	      logger.info("--- Http Cookies: ---");
	      clientRequest.cookies().forEach(this::logHeader);
	      return next.exchange(clientRequest);
	    };
	  }
	 
	  private ExchangeFilterFunction logResponse() {
	    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
	      logger.info("Response: {}", clientResponse.statusCode());
	      clientResponse.headers().asHttpHeaders()
	        .forEach((name, values) -> values.forEach(value -> logger.info("{}={}", name, value)));
	      return Mono.just(clientResponse);
	    });
	  }
	 
	  private void logHeader(String name, List<String> values) {
	    values.forEach(value -> logger.info("{}={}", name, value));
	  }
}

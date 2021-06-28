package com.jana.quiz.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.jana.quiz.exceptions.GlobalErrorUtils;
import com.jana.quiz.service.dto.QuizDTO;

import reactor.core.publisher.Mono;

@Service
public class OpenDBServiceImpl implements OpenDBService {
	private static final Logger logger = LoggerFactory.getLogger(OpenDBServiceImpl.class);

	@Autowired
	private WebClient webClient;

	@Override
	public Mono<QuizDTO> getQuiz(final int amount, final int category) {
		logger.debug("getQuiz({},{})", amount, category);

		return webClient.get().uri("/api.php?amount={amount}&category={category}", amount, category).retrieve()
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> {
					String message = getErrorMessage(amount, category);
					return GlobalErrorUtils.manageError(clientResponse, message);
				}).onStatus(HttpStatus::is4xxClientError, clientResponse -> {
					String message = getErrorMessage(amount, category);
					GlobalErrorUtils.logError(clientResponse, message);
					/*
					 * this OpenDBServiceImpl acts as a client to opendb api. There might have been
					 * some issue with building url that might be causing 4xx error. So it is
					 * internal to this class and no issue with client of this service, hence
					 * returning 500 error code
					 */
					return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message));
				}).bodyToMono(QuizDTO.class).map(r -> {
					if (r.getResponseCode() == 0) {
						return r;
					}
					logger.error("OpenDB returned code: " + r.getResponseCode());
					String message = getErrorMessage(amount, category);
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							message);
				});
	}

	private String getErrorMessage(final int amount, final int category) {
		return String.format("Error fetching quiz data for amount=%d and category=%d", amount,
				category);
	}
}

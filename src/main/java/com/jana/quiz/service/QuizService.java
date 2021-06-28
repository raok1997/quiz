package com.jana.quiz.service;

import com.jana.quiz.rest.dto.QuizResponseDTO;

import reactor.core.publisher.Mono;

public interface QuizService {
	Mono<QuizResponseDTO> getAll();
}

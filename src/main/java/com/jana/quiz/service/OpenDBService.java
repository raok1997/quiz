package com.jana.quiz.service;

import com.jana.quiz.service.dto.QuizDTO;

import reactor.core.publisher.Mono;

public interface OpenDBService {
	public Mono<QuizDTO> getQuiz(int amountOfQuestions, int category);
}

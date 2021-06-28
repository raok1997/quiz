package com.jana.quiz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jana.quiz.rest.dto.QuizResponseDTO;
import com.jana.quiz.service.QuizService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coding/exercise/quiz")
public class QuizResource {
	@Autowired
	private QuizService quizService;
	
	@GetMapping()
	public Mono<QuizResponseDTO> getAll() {
		return quizService.getAll();
	}
}

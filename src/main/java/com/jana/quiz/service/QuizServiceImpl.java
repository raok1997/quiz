package com.jana.quiz.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jana.quiz.domain.CategoryQuestions;
import com.jana.quiz.domain.Question;
import com.jana.quiz.rest.dto.QuizResponseDTO;
import com.jana.quiz.service.dto.QuestionDTO;
import com.jana.quiz.service.dto.QuizDTO;

import lombok.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class QuizServiceImpl implements QuizService {
	private Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);
	
	@Autowired
	private OpenDBService openDBService;

	@Override
	public Mono<QuizResponseDTO> getAll() {
		logger.debug("getAll()");
		
		final boolean delayError = false;
		
		return Flux.just(new OpenDBServiceRequest(5, 11), new OpenDBServiceRequest(5, 12) )
		        .parallel()
		        .runOn(Schedulers.boundedElastic())
		        .flatMap( r -> 
		        	{ 
		        		logger.info("Fetching quiz questions from OpenDB for {}", r);
		        		return openDBService.getQuiz(r.getAmountOfQuestions(), r.getCategory()) ; 
		        	},
		        	delayError
		        )
		        .sequential()
		        .collectList()
		        .map( allDto -> {
		        	logger.info("Trabsforming quiz responses by category");
		        	QuizResponseDTO quiz = new QuizResponseDTO();
		        	
		        	Map<String, CategoryQuestions> questionsByCategory = new HashMap<>();
		        	
		        	for(QuizDTO quizDto : allDto) {
	        			for ( QuestionDTO questionDto : quizDto.getResults() ) {
	        				String cateogry = questionDto.getCategory();
	        				CategoryQuestions categoryQuestions = questionsByCategory.get(cateogry);
	        				if ( null == categoryQuestions ) {
	        					categoryQuestions = new CategoryQuestions();
	        					quiz.getQuiz().add(categoryQuestions);
	        					
	        					categoryQuestions.setCategory(cateogry);
	        					questionsByCategory.put(cateogry, categoryQuestions);
	        				}
	        				
	        				categoryQuestions.getResults().add( Question.from(questionDto) );
	        			}
		        	}
		        	
		        	return quiz;
		        })
		        .log();
		        
	}
	
	
	@Value
	public static class OpenDBServiceRequest {
		private int amountOfQuestions;
		private int category;
	}
}

package com.jana.quiz.service;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jana.quiz.domain.CategoryQuestions;
import com.jana.quiz.domain.Question;
import com.jana.quiz.rest.dto.QuizResponseDTO;
import com.jana.quiz.service.dto.QuizDTO;
import com.jana.quiz.utils.FileUtils;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
	@Mock
	private OpenDBService openDBService;
	
	@InjectMocks
	private QuizServiceImpl service;

	@Test
	public void getAll_success() throws Exception {
		when(openDBService.getQuiz(5, 11)).thenReturn(Mono.just(from("OpenDBResponse_category11.json")));
		when(openDBService.getQuiz(5, 12)).thenReturn(Mono.just(from("OpenDBResponse_category12.json")));
		
		QuizResponseDTO response = service.getAll().block();
		assertNotNull(response);
		assertNotNull(response.getQuiz(), "quiz is empty");
		
		assertEquals(2, response.getQuiz().size());
		
		for( CategoryQuestions category : response.getQuiz() ) {
			if ( "Entertainment: Film".equals(category.getCategory()) ) {
				assertEquals(2, category.getResults().size());
				
				// verify 1st question
				Question q1 = category.getResults().get(0);
				
				assertEquals("multiple", q1.getType());
				assertEquals("medium", q1.getDifficulty());
				assertEquals("Which actor played the main character in the 1990 film &quot;Edward Scissorhands&quot;?",
						q1.getQuestion());
				assertEquals("Johnny Depp", q1.getCorrectAnswer());
				assertEquals(Arrays.asList( "Johnny Depp", " Clint Eastwood", "Leonardo DiCaprio", "Ben Stiller"), q1.getAllAnswers());
			} else if ( "Entertainment: Music".equals(category.getCategory()) ) {
				assertEquals(2, category.getResults().size());
				
				// verify 1st question
				Question q1 = category.getResults().get(0);
				
				assertEquals("multiple", q1.getType());
				assertEquals("easy", q1.getDifficulty());
				assertEquals("Which singer was featured in Jack &Uuml; (Skrillex &amp; Diplo)&#039;s 2015 song &#039;Where Are &Uuml; Now&#039;?",
						q1.getQuestion());
				assertEquals("Justin Bieber", q1.getCorrectAnswer());
				assertEquals(Arrays.asList("Justin Bieber", "Selena Gomez", "Ellie Goulding", "The Weeknd"), q1.getAllAnswers());
			} else {
				fail("Unexpected category" + category.getCategory());
			}
		}
	}
	
	@Test
	public void getAll_onesuccess_oneerror() throws Exception {
		lenient().when(openDBService.getQuiz(5, 11)).thenReturn(Mono.just(from("OpenDBResponse_category11.json")));
		lenient().when(openDBService.getQuiz(5, 12)).thenThrow(new RuntimeException("Error simulatio for category 12"));
		
		service.getAll().doOnSuccess( dto -> fail("Expecting " + HttpStatus.INTERNAL_SERVER_ERROR) ).doOnError( ex -> { 
			assertEquals(ResponseStatusException.class, ex.getClass());
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ResponseStatusException)ex).getStatus());
		});
	}
	
	
	private static QuizDTO from(String fileName) throws Exception {
		return new ObjectMapper().readValue(FileUtils.getTestResourceFile(fileName), QuizDTO.class);
	}
	
}

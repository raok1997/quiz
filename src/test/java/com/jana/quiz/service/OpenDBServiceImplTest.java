package com.jana.quiz.service;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.jana.quiz.service.dto.QuestionDTO;
import com.jana.quiz.service.dto.QuizDTO;
import com.jana.quiz.utils.FileUtils;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class OpenDBServiceImplTest {
	private final MockWebServer mockWebServer = new MockWebServer();

	private OpenDBServiceImpl service;

	@BeforeEach
	public void setUp() throws IOException {
		service = new OpenDBServiceImpl();
		WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("localhost/").toString())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
		ReflectionTestUtils.setField(service, "webClient", webClient, WebClient.class);
	}

	@AfterEach
	public void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	public void getQuiz_success() throws IOException {
		String json = FileUtils.readTestResourceFile("OpenDBResponse_category11.json");
		mockWebServer.enqueue(new MockResponse().setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(json));

		QuizDTO response = service.getQuiz(5, 10).block();
		
		assertNotNull(response);
		assertEquals(0, response.getResponseCode());
		assertEquals(2, response.getResults().size());

		QuestionDTO q1 = response.getResults().get(0);

		assertEquals("Entertainment: Film", q1.getCategory());
		assertEquals("multiple", q1.getType());
		assertEquals("medium", q1.getDifficulty());
		assertEquals("Which actor played the main character in the 1990 film &quot;Edward Scissorhands&quot;?",
				q1.getQuestion());
		assertEquals("Johnny Depp", q1.getCorrectAnswer());
		assertEquals(Arrays.asList(" Clint Eastwood", "Leonardo DiCaprio", "Ben Stiller"), q1.getInccorrectAnswers());
	}
	
	@Test
	public void getQuiz_NonZeroResponseCode() throws IOException {
		String json = FileUtils.readTestResourceFile("OpenDBResponse_Fail.json");
		mockWebServer.enqueue(new MockResponse().setResponseCode(200)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(json));

		service.getQuiz(5, 10).doOnSuccess( dto -> fail("Expecting " + HttpStatus.INTERNAL_SERVER_ERROR) ).doOnError( ex -> { 
			assertEquals(ResponseStatusException.class, ex.getClass());
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ResponseStatusException)ex).getStatus());
		});
	}
	
	
	@ParameterizedTest
	@ValueSource(ints = {500, 404})
	public void getQuiz_error(int httpCode) throws IOException {
		mockWebServer.enqueue(new MockResponse().setResponseCode(httpCode)
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

		service.getQuiz(5, 10).doOnSuccess( dto -> fail("Expecting " + HttpStatus.INTERNAL_SERVER_ERROR) ).doOnError( ex -> { 
			assertEquals(ResponseStatusException.class, ex.getClass());
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ResponseStatusException)ex).getStatus());
		});
	}

}

package com.jana.quiz.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jana.quiz.service.dto.QuestionDTO;

import lombok.Data;

@Data
public class Question {
	private String type;
	private String difficulty;
	private String question;
	@JsonProperty("all_answers")
	private List<String> allAnswers = new ArrayList<>();
	@JsonProperty("correct_answer")
	private String correctAnswer;
	
	
	public static Question from(QuestionDTO dto) {
		Question obj = new Question();
		obj.setType(dto.getType());
		obj.setDifficulty(dto.getDifficulty());
		obj.setQuestion(dto.getQuestion());
		
		obj.setCorrectAnswer(dto.getCorrectAnswer());
		if ( dto.getInccorrectAnswers() != null ) {
			obj.getAllAnswers().addAll(dto.getInccorrectAnswers());
		}
		if ( dto.getCorrectAnswer() != null ) {
			obj.getAllAnswers().add(0, dto.getCorrectAnswer());
		}
		
		return obj;
	}
}

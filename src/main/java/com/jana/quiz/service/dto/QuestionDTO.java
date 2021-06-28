package com.jana.quiz.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuestionDTO {
	private String category;
	private String type;
	private String difficulty;
	private String question;
	@JsonProperty("correct_answer")
	private String correctAnswer;
	
	@JsonProperty("incorrect_answers")
	private List<String> incorrectAnswers;
	
	public List<String> getInccorrectAnswers(){
		if ( null == incorrectAnswers ) {
			incorrectAnswers = new ArrayList<>();
		}
		return incorrectAnswers;
	}
}

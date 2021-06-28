package com.jana.quiz.service.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuizDTO extends ServiceResponseDTO {
	private List<QuestionDTO> results;
	
	public List<QuestionDTO> getResults(){
		if ( null == results ) {
			results = new ArrayList<>();
		}
		return results;
	}
}

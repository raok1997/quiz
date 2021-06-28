package com.jana.quiz.rest.dto;

import java.util.ArrayList;
import java.util.List;

import com.jana.quiz.domain.CategoryQuestions;

import lombok.Data;

@Data
public class QuizResponseDTO {
	private List<CategoryQuestions> quiz = new ArrayList<>();
}

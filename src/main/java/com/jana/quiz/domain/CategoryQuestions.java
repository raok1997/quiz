package com.jana.quiz.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategoryQuestions {
	private String category;
	private List<Question> results = new ArrayList<>();
}

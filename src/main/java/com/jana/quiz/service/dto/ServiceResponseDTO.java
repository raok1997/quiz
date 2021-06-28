package com.jana.quiz.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ServiceResponseDTO {
	@JsonProperty("response_code")
	protected int responseCode;
}

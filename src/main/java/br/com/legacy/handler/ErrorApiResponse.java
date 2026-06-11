package br.com.legacy.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ErrorApiResponse {
	private String message;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;
}

package com.dish.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthDto {
	@NotEmpty(message = "Email is required")
	@Email(message = "Email should be in proper email format")
	private String email;
	@NotBlank(message = "Password can not be blank")
	private String password;

	public class AuthResponse {
		private final String jwt;

		public AuthResponse(String jwt) {
			this.jwt = jwt;
		}

		public String getJwt() {
			return jwt;
		}
	}

}

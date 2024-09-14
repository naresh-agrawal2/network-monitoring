package com.dish.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyOtpDto {
	@NotEmpty(message = "Email is required")
	@Email(message = "Email should be in proper email format")
	private String email;
	@NotBlank(message = "Password can not be blank")
	private String password;
	 
	@NotBlank(message="otp can not be blank")
	private String otp;
}

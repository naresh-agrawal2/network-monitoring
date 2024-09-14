package com.dish.auth.service;

import org.springframework.validation.BindingResult;

import com.dish.auth.dto.AuthDto;
import com.dish.auth.dto.VerifyOtpDto;
import com.dish.auth.response.APIResponse;

import jakarta.validation.Valid;

public interface AuthService {
	APIResponse saveAuth(AuthDto authDto, BindingResult bindingResult);

	APIResponse updatePassword(AuthDto authDto, BindingResult bindingResult);

	APIResponse login(AuthDto authDto, BindingResult bindingResult);

	APIResponse deleteAuthByEmail(String email, BindingResult bindingResult);

	APIResponse forgotPassword(String email, BindingResult bindingResult);

//	APIResponse verifyOtp(OtpDto otpDto, BindingResult bindingResult);

//	APIResponse resetPassword(AuthDto authDto, BindingResult bindingResult);

	APIResponse resendOtp(String email, BindingResult bindingResult);

	APIResponse verifyOtpAndResetPassword(VerifyOtpDto verifyOtpDto, BindingResult bindingResult);
}

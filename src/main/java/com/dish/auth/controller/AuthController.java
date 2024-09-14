package com.dish.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dish.auth.constants.Constants;
import com.dish.auth.dto.AuthDto;
import com.dish.auth.dto.VerifyOtpDto;
import com.dish.auth.response.APIResponse;
import com.dish.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@AllArgsConstructor
public class AuthController implements Constants {

    private final AuthService authService;

    @Operation(summary = "Save Auth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved auth"),
            @ApiResponse(responseCode = "404", description = "Auth not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<APIResponse> saveAuth(@Valid @RequestBody AuthDto auth, BindingResult bindingResult) {
        APIResponse apiResponse = authService.saveAuth(auth, bindingResult);
        log.debug(API_RESPONSE, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "404", description = "Auth not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@Valid @RequestBody AuthDto authDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.login(authDto, bindingResult);
        log.debug(API_RESPONSE, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @Operation(summary = "Update Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/updatePassword")
    public ResponseEntity<APIResponse> updatePassword(@Valid @RequestBody AuthDto authDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.updatePassword(authDto,bindingResult);
        log.debug(API_RESPONSE, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @Operation(summary = "deleteAuthByEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted Auth data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<APIResponse> deleteAuthByEmail(@RequestBody AuthDto authDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.deleteAuthByEmail(authDto.getEmail(),bindingResult);
        log.debug(API_RESPONSE, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    
    @Operation(summary = "forgotPassword/Generate OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "forgotPassword/OTP generated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/forgotPassword")
    public ResponseEntity<APIResponse> forgotPasswrod( @RequestBody AuthDto authDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.forgotPassword(authDto.getEmail(), bindingResult);
        log.debug("API Response: {}", apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
    }

    @Operation(summary = "Resend OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP resent successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/resendOtp")
    public ResponseEntity<APIResponse> resendOtp(  @RequestBody AuthDto authDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.resendOtp(authDto.getEmail(), bindingResult);
        log.debug("API Response: {}", apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
    }
    @Operation(summary = "Verify OTP and Reset Password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP verified and password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verifyOtpAndResetPassword")
    public ResponseEntity<APIResponse> verifyOtpAndResetPassword(@Valid @RequestBody VerifyOtpDto verifyOtpDto, BindingResult bindingResult) {
        APIResponse apiResponse = authService.verifyOtpAndResetPassword(verifyOtpDto, bindingResult);
        log.debug("API Response: {}", apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
    }

//    @Operation(summary = "Verify OTP")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
//            @ApiResponse(responseCode = "401", description = "Invalid or expired OTP"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @PostMapping("/verifyOtp")
//    public ResponseEntity<APIResponse> verifyOtp(@Valid @RequestBody OtpDto otpDto, BindingResult bindingResult) {
//        APIResponse apiResponse = authService.verifyOtp(otpDto, bindingResult);
//        log.debug("API Response: {}", apiResponse);
//        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
//    }
//
//    @Operation(summary = "Forgot Password")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
//            @ApiResponse(responseCode = "404", description = "User not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @PostMapping("/resetPassword")
//    public ResponseEntity<APIResponse> resetPassword(@Valid @RequestBody AuthDto authDto, BindingResult bindingResult) {
//        APIResponse apiResponse = authService.resetPassword(authDto, bindingResult);
//        log.debug("API Response: {}", apiResponse);
//        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(apiResponse.getStatus()));
//    }
}

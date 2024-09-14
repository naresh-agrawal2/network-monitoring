package com.dish.auth.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.dish.auth.dto.UserInfoResponseDTO;
import com.dish.auth.entity.Role;
import com.dish.auth.entity.User;
import com.dish.auth.respository.RoleRepository;
import com.dish.auth.respository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.dish.auth.configuration.MessageConfig;
import com.dish.auth.constants.Constants;
import com.dish.auth.dto.AuthDto;
import com.dish.auth.dto.VerifyOtpDto;
import com.dish.auth.entity.AuthEntity;
import com.dish.auth.response.APIResponse;
import com.dish.auth.respository.AuthRepository;
import com.dish.auth.service.AuthService;
import com.dish.auth.utils.JwtUtil;
import com.dish.auth.utils.Utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    //private final AuthRepository authRepository;
    private final MessageConfig messageConfig;
    private final Utility utility;
    private final JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;
    // This map will store the OTPs for simplicity; in a real-world scenario, consider using a database or cache
    private final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();

    @Override
    public APIResponse saveAuth(AuthDto authDto, BindingResult bindingResult) {
        log.debug("*****************Save Auth******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);
        try {
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                });
                return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                        .message(errorMessage.toString()).build();

            }
            AuthEntity authEntity = new AuthEntity();
            BeanUtils.copyProperties(authDto, authEntity);
            authEntity.setPassword(utility.encodePassword(authDto.getPassword()));
            authRepository.save(authEntity);
            return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                    .message(messageConfig.authDetailsSavedSuccessfully).build();
        } catch (DataIntegrityViolationException e) {
            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                    .message(messageConfig.emailDuplicate).build();
        } catch (Exception e) {
            log.error("Exception : ()", e.getMessage());
            return APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE)
                    .message(messageConfig.adminContact).build();
        }
    }

    @Override
    public APIResponse updatePassword(AuthDto authDto, BindingResult bindingResult) {
        log.debug("*****************Update Password******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);
        try {
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                });
                return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                        .message(errorMessage.toString()).build();

            }
            Optional<AuthEntity> authOptional = authRepository.findByEmail(authDto.getEmail());
            if (!authOptional.isPresent()) {
                return APIResponse.builder().status(HttpStatus.NOT_FOUND.value()).success(Boolean.FALSE)
                        .message("User not found").build();
            }

            AuthEntity authEntity = authOptional.get();
            authEntity.setPassword(utility.encodePassword(authDto.getPassword()));
            authRepository.save(authEntity);

            return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                    .message("Password updated successfully").build();
        } catch (Exception e) {
            log.error("Exception : ()", e.getMessage());
            return APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE)
                    .message("Failed to update password").build();
        }
    }

    @Override
    public APIResponse login(AuthDto authDto, BindingResult bindingResult) {
        log.debug("*****************Login******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);

        // Check if there are binding errors
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                    .message(errorMessage.toString()).build();
        }

        try {
            Optional<AuthEntity> authOptional = authRepository.findByEmail(authDto.getEmail());
            if (!authOptional.isPresent()) {
                return APIResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).success(Boolean.FALSE)
                        .message(messageConfig.invalidCredentials).build();
            }
            AuthEntity authEntity = authOptional.get();
            if (!utility.isPasswordMatch(authDto.getPassword(), authEntity.getPassword())) {
                return APIResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).success(Boolean.FALSE)
                        .message(messageConfig.invalidCredentials).build();
            }
            String token = jwtUtil.generateToken(authDto.getEmail());

            Map<String, Object> returnData = new HashMap<>();

            Optional<User> user = userRepository.fetchRoleId(authDto.getEmail());
            Optional<Role> role = roleRepository.fetchRoleDetails(user.get().getRoleId());
            UserInfoResponseDTO dto = new UserInfoResponseDTO();

            if (!user.get().getIsAuthenticated()) {
                try {
                    String otp = utility.generateOtp();
                    String email = authDto.getEmail();
                    userRepository.updateOtp(otp, email);
                    sendOtpEmail(email, otp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE)
                            .message("Failed to send OTP email").build();
                }
                dto.setNextPage("reset_pswd");
            } else dto.setNextPage("dashborad");
            dto.setToken(token);
            dto.setUsername(user.get().getUserName());
            dto.setEmail(user.get().getEmail());
            dto.setUser_id(user.get().getUserid());
            dto.setRoleId(user.get().getRoleId());
            dto.setRoleName(role.get().getRoleName());
            dto.setRoleList(role.get().getFeaturesIds());
            returnData.put("userInfo", dto);
            authRepository.updateToken(token, authDto.getEmail());

            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("token", token);
            return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                    .message(messageConfig.loginSuccessful) // Return the generated token
                    .data(returnData)
                    .build();
        } catch (Exception e) {
            log.error("Exception : ()", e.getMessage());
            return APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE)
                    .message(messageConfig.adminContact).build();
        }
    }

    @Override
    public APIResponse deleteAuthByEmail(String email, BindingResult bindingResult) {
        log.debug("*****************Deleting Auth by Email******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);

        try {
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessage = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessage.append(error.getDefaultMessage()).append("; ");
                });
                return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                        .message(errorMessage.toString()).build();
            }

            log.debug("Received email: {}", email);
            email = email.trim();
            log.debug("Trimmed email: {}", email);

            Optional<AuthEntity> authOptional = authRepository.findByEmail(email);
            if (!authOptional.isPresent()) {
                log.debug("AuthEntity not found for email: {}", email);
                return APIResponse.builder().status(HttpStatus.NOT_FOUND.value()).success(Boolean.FALSE)
                        .message("Auth User not found").build();
            }

            // This will only run if authOptional is present
            log.debug("AuthEntity found with email: {}", authOptional.get().getEmail());

            authRepository.deleteByEmail(email);
            log.debug("AuthEntity deleted for email: {}", email);
            return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                    .message("Auth data deleted successfully").build();
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE)
                    .message("Failed to delete auth data").build();
        }
    }

    @Override
    public APIResponse resendOtp(String email, BindingResult bindingResult) {
        return generateAndSendOtp(email, bindingResult, "OTP resent successfully");
    }

    @Override
    public APIResponse forgotPassword(String email, BindingResult bindingResult) {
        return generateAndSendOtp(email, bindingResult, "OTP generated successfully");
    }

    private APIResponse generateAndSendOtp(String email, BindingResult bindingResult, String successMessage) {
        log.debug("*****************Generate/Resend OTP******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                    .message(errorMessage.toString()).build();
        }

        Optional<AuthEntity> authOptional = authRepository.findByEmail(email);
        if (!authOptional.isPresent()) {
            return APIResponse.builder().status(HttpStatus.NOT_FOUND.value()).success(Boolean.FALSE)
                    .message("Auth User not found").build();
        }

        String otp = utility.generateOtp();
        otpStore.put(email, otp);
        sendOtpEmail(email, otp);
        userRepository.updateOtp(otp, email);
        log.debug("Generated OTP for email {}: {}", email, otp);

        return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                .message(successMessage).build();

    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(Constants.YOUR_OTP_FOR_PASSWORD_RESET);
        message.setText(Constants.YOUR_OTP_FOR_PASSWORD_RESET_IS + otp);
        mailSender.send(message);
    }

    //    @Override
//    public APIResponse verifyOtp(OtpDto otpDto, BindingResult bindingResult) {
//        log.debug("*****************Verify OTP******************");
//        String uuid = UUID.randomUUID().toString();
//        log.debug(Constants.REQUEST_ID, uuid);
//        String storedOtp="1234";
//        if (bindingResult.hasErrors()) {
//            StringBuilder errorMessage = new StringBuilder();
//            bindingResult.getAllErrors().forEach(error -> {
//                errorMessage.append(error.getDefaultMessage()).append("; ");
//            });
//            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
//                    .message(errorMessage.toString()).build();
//        }
//
//        if ( !storedOtp.equals(otpDto.getOtp())) {
//            return APIResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).success(Boolean.FALSE)
//                    .message("Invalid or expired OTP").build();
//        }
//
//        return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
//                .message("OTP verified successfully").build();
////        String storedOtp = otpStore.get(otpDto.getEmail());
////        if (storedOtp == null || !storedOtp.equals(otpDto.getOtp())) {
////            return APIResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).success(Boolean.FALSE)
////                    .message("Invalid or expired OTP").build();
////        }
////
////        otpStore.remove(otpDto.getEmail());
////        return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
////                .message("OTP verified successfully").build();
//    }
//
//    @Override
//    public APIResponse resetPassword(AuthDto authDto, BindingResult bindingResult) {
//        log.debug("*****************Forgot Password******************");
//        String uuid = UUID.randomUUID().toString();
//        log.debug(Constants.REQUEST_ID, uuid);
//
//        if (bindingResult.hasErrors()) {
//            StringBuilder errorMessage = new StringBuilder();
//            bindingResult.getAllErrors().forEach(error -> {
//                errorMessage.append(error.getDefaultMessage()).append("; ");
//            });
//            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
//                    .message(errorMessage.toString()).build();
//        }
//
//        Optional<AuthEntity> authOptional = authRepository.findByEmail(authDto.getEmail());
//        if (!authOptional.isPresent()) {
//            return APIResponse.builder().status(HttpStatus.NOT_FOUND.value()).success(Boolean.FALSE)
//                    .message("Auth User not found").build();
//        }
//
//        AuthEntity authEntity = authOptional.get();
//        authEntity.setPassword(utility.encodePassword(authDto.getPassword()));
//        authRepository.save(authEntity);
//
//        return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
//                .message("Password reset successfully").build();
//    }
//	 
    @Override
    public APIResponse verifyOtpAndResetPassword(VerifyOtpDto verifyOtpDto, BindingResult bindingResult) {
        log.debug("*****************Verify OTP and Reset Password******************");
        String uuid = UUID.randomUUID().toString();
        log.debug(Constants.REQUEST_ID, uuid);
        String storedOtp = userRepository.fetchOTP(verifyOtpDto.getEmail());

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            return APIResponse.builder().status(HttpStatus.BAD_REQUEST.value()).success(Boolean.FALSE)
                    .message(errorMessage.toString()).build();
        }

        if (!storedOtp.equals(verifyOtpDto.getOtp())) {
            return APIResponse.builder().status(HttpStatus.UNAUTHORIZED.value()).success(Boolean.FALSE)
                    .message("Invalid or expired OTP").build();
        }

        Optional<AuthEntity> authOptional = authRepository.findByEmail(verifyOtpDto.getEmail());
        if (!authOptional.isPresent()) {
            return APIResponse.builder().status(HttpStatus.NOT_FOUND.value()).success(Boolean.FALSE)
                    .message("Auth User not found").build();
        }

        AuthEntity authEntity = authOptional.get();
        authEntity.setPassword(utility.encodePassword(verifyOtpDto.getPassword()));
        authRepository.save(authEntity);
        userRepository.updateIsAuth(true, LocalDateTime.now(), verifyOtpDto.getEmail());

        return APIResponse.builder().status(HttpStatus.OK.value()).success(Boolean.TRUE)
                .message("OTP verified and password reset successfully").build();
    }


}
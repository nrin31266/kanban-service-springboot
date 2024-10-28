package com.rin.kanban.service;

import com.rin.kanban.constant.OtpType;
import com.rin.kanban.dto.request.VerifyOtpRequest;
import com.rin.kanban.dto.response.OtpResponse;
import com.rin.kanban.dto.response.VerifyOtpResponse;
import com.rin.kanban.entity.Otp;
import com.rin.kanban.entity.User;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.OtpMapper;
import com.rin.kanban.repository.OtpRepository;
import com.rin.kanban.repository.UserRepository;
import com.rin.kanban.utils.OtpGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class OtpService {
    OtpRepository otpRepository;
    UserRepository userRepository;
    OtpGenerator otpGenerator;
    OtpMapper otpMapper;
    PasswordEncoder passwordEncoder;

    public OtpResponse createOtp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        String otpCode = otpGenerator.generateOtpCode();
        Otp otp = Otp.builder()
                .expiresAt(Instant.now().plusSeconds(120))
                .type(OtpType.EMAIL.name())
                .otp(encodeOtpCode(otpCode))
                .user(user)
                .createdAt(Instant.now())
                .verified(false)
                .build();
        otp = otpRepository.save(otp);
        otp.setOtp(otpCode);
        return otpMapper.toOtpResponse(otp);
    }

    public void createOtp(String userId, String otpCode) {
        User user = userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        Otp otp = Otp.builder()
                .expiresAt(Instant.now().plusSeconds(120))
                .type(OtpType.EMAIL.name())
                .otp(encodeOtpCode(otpCode))
                .user(user)
                .createdAt(Instant.now())
                .verified(false)
                .build();
        otp = otpRepository.save(otp);
        otpMapper.toOtpResponse(otp);
    }

    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Otp otp = otpRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId).orElseThrow(()->new AppException(ErrorCode.OTP_ERROR));
        log.info(otp.toString());
        boolean verified;
        String message;
        if(Instant.now().isAfter(otp.getExpiresAt())){
            //Expired
            verified = false;
            message = "Otp expired";
        }else if(otp.isVerified()){
            verified = false;
            message = "Otp used, please refresh otp";
        } else{
            verified= verifyOtpCode(request.getOtp(), otp.getOtp());
            if(verified){
                message = "Otp correctly";
                otp.setVerified(true);
                otpRepository.save(otp);
            }else{
                message = "Otp not correctly";
            }
        }
        return new VerifyOtpResponse(verified, message);
    }

    public VerifyOtpResponse userVerify(VerifyOtpRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        VerifyOtpResponse verifyOtpResponse = verifyOtp(request);
        if(verifyOtpResponse.isVerified()){
            User user = userRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        return verifyOtpResponse;
    }

    private String encodeOtpCode(String otpCode) {
        return passwordEncoder.encode(otpCode);
    }
    private boolean verifyOtpCode(String otpCodeRequest, String otpCode) {
        return passwordEncoder.matches(otpCodeRequest, otpCode);
    }

}

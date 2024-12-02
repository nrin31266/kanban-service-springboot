package com.rin.kanban.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rin.kanban.dto.request.*;
import com.rin.kanban.dto.response.AuthenticationResponse;
import com.rin.kanban.dto.response.RefreshTokenResponse;
import com.rin.kanban.dto.response.IntrospectResponse;
import com.rin.kanban.entity.InvalidatedToken;
import com.rin.kanban.entity.User;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.UserMapper;
import com.rin.kanban.repository.InvalidatedTokenRepository;
import com.rin.kanban.repository.UserRepository;
import com.rin.kanban.repository.httpclient.OutboundIdentityClient;
import com.rin.kanban.repository.httpclient.OutboundUserClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    InvalidatedTokenRepository invalidatedTokenRepository;
    UserService userService;
    UserRepository userRepository;
    UserMapper userMapper;

    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    OtpService otpService;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    @Value("authorization_code")
    protected String GRANT_TYPE;

    public AuthenticationResponse outboundLogin(String code) {
        var response = outboundIdentityClient.exchangeToken(
                ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                        .build()
        );
        //Get user info
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        log.info("USER INFO: {}", userInfo);
        //Onboard user
        Optional<User> user = userRepository.findByEmail(userInfo.getEmail());
        if(user.isEmpty()){
            CreateUserRequest createUserRequest =
                    CreateUserRequest.builder()
                            .type("Google")
                            .name(userInfo.getFamilyName() + " " + userInfo.getGivenName())
                            .email(userInfo.getEmail())
                            .password(null)
                            .build();
            var userResponse  = userService.createUser(createUserRequest);
            user = Optional.of(userMapper.toUser(userResponse));
        }
        //Convert token
        var token = generateToken(user.get());

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticated(LoginOtpRequest request) {
        User user = userRepository.findByEmail(request.getUserId()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        otpService.login(user, request.getOtp());

        if(user.getEmailVerified()== null || !user.getEmailVerified()){
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
    public String checkAccount(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_LOGIN_INFORMATION);
        }
        return user.getId();
    }

    public AuthenticationResponse authenticatedAdmin(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_LOGIN_INFORMATION);
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }




    public void logout(LogoutRequest request) {
        String token = request.getToken();
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (isTokenValid(signedJWT)) {
                String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
                Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

                InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                        .id(jwtId)
                        .expiryTime(expiryTime)
                        .build();

                invalidatedTokenRepository.save(invalidatedToken);
            } else {
                log.warn("Attempted to logout with an invalid or expired token.");
            }
        } catch (ParseException e) {
            log.error("Logout failed due to an error", e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = false;
        String token = request.getToken();
        if(token != null){
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                isValid = isTokenValid(signedJWT);
                if (isValid && isTokenExpired(signedJWT)) {
                    isValid = false;
                }
            } catch (ParseException e) {
                log.error("Introspection failed due to an error");
                isValid = false;
            }
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // Method to refresh token
    public RefreshTokenResponse refreshToken(RefreshRequest request){
        String token = request.getToken();
        RefreshTokenResponse res = RefreshTokenResponse.builder()
                .build();
        if(token == null){res.setTokenValid(false); return res;}
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean isTokenValid = isTokenValid(signedJWT);
            if (!isTokenValid) {
                return res;
            }
            Instant expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
            Instant now = Instant.now();

            Duration timeRemaining = Duration.between(now, expiryTime);
            Duration fifteenMinutes = Duration.ofSeconds(5);

            if (timeRemaining.isNegative() || timeRemaining.compareTo(fifteenMinutes) <= 0) {
                String userId = signedJWT.getJWTClaimsSet().getSubject();
                Optional<User> user = userRepository.findById(userId);
                if (user.isEmpty()) {
                    log.info("{} not found", userId);
                    res.setTokenValid(false);
                    return res;
                }
                String newToken = generateToken(user.get());

                String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
                InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                        .id(jwtId)
                        .expiryTime(Date.from(expiryTime))
                        .build();
                invalidatedTokenRepository.save(invalidatedToken);

                res.setToken(newToken);
            }
            res.setTokenValid(true);
            return res;
        } catch (ParseException e) {
            log.error("?: ", e);
            res.setTokenValid(false);
            return res;
        }
    }
    private boolean isTokenValid(SignedJWT signedJWT) {
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean verified = signedJWT.verify(verifier);

            if (!verified || invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
                log.info("Invalid token");
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Token verification failed", e);
            return false;
        }
    }



    private boolean isTokenExpired(SignedJWT signedJWT) throws ParseException {
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return expiryTime.before(new Date());
    }




    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("rin.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission ->  stringJoiner.add(permission.getName()));
                }
            });
        }
        return stringJoiner.toString();
    }


}

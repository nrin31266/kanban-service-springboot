package com.rin.kanban.service;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rin.kanban.dto.request.*;
import com.rin.kanban.dto.response.AuthenticationResponse;
import com.rin.kanban.dto.response.IntrospectResponse;
import com.rin.kanban.entity.InvalidatedToken;
import com.rin.kanban.entity.User;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.UserMapper;
import com.rin.kanban.repository.InvalidatedTokenRepository;
import com.rin.kanban.repository.UserRepository;
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
    private final FirebaseAuth firebaseAuth;
    UserRepository userRepository;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    public AuthenticationResponse outboundLogin(OutboundUserRequest request) {
        log.info("outboundLogin");
//        log.info("Id token : {}", request.getIdToken());
        try {
            // Xác thực ID token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getIdToken());
            String uid = decodedToken.getUid();
            //
            validateTokenIssuedAt(decodedToken, uid);
            //
            FirebaseAuth.getInstance().revokeRefreshTokens(uid);
            String email = decodedToken.getEmail();
            log.info("User email : {}", email);
            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                String displayName = decodedToken.getName();
                //String photoUrl = decodedToken.getPicture();
                var userResponse = userService.createUser(
                        CreateUserRequest.builder()
                                .name(displayName)
                                .email(email)
                                .build()
                );
                user = Optional.of(userMapper.toUser(userResponse));
            }
            var token = generateToken(user.get());

            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        } catch (FirebaseException e) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    public AuthenticationResponse authenticated(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {

        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();

        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder()
                        .id(jit)
                        .expiryTime(expiryTime)
                        .build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            tokenIsExpired(verifyToken(token));
        } catch (JOSEException | ParseException e) {
            isValid = false;
            log.info("Invalid token");
        }
        return IntrospectResponse.builder().
                valid(isValid)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        Instant expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().toInstant();
        Instant now = Instant.now();

        Duration timeRemaining = Duration.between(now, expiryTime);
        Duration fifteenMinutes = Duration.ofSeconds(30);

        if (timeRemaining.isNegative() || timeRemaining.compareTo(fifteenMinutes) <= 0) {
            String userId = signedJWT.getJWTClaimsSet().getSubject();
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            var newToken = generateToken(user);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(java.sql.Date.from(expiryTime))
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            return AuthenticationResponse.builder()
                    .token(newToken)
                    .build();
        } else {
            return AuthenticationResponse.builder()
                    .token(request.getToken())
                    .build();
        }
    }


    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("rin.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
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
                    role.getPermissions().forEach(stringJoiner::add);
                }
            });
        }

        return stringJoiner.toString();
    }



    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        log.info("Verifying token");
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private void tokenIsExpired(SignedJWT signedJWT) throws ParseException {
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expiredTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }


    private void validateTokenIssuedAt(FirebaseToken decodedToken, String uid) throws AppException {
        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(uid);
            long validSince = user.getTokensValidAfterTimestamp() / 1000;
            long issuedAt = (long) decodedToken.getClaims().get("iat");
            if (issuedAt < validSince) {
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }
        } catch (FirebaseException e) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }
}

package event_booking_system.demo.services.impls;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import event_booking_system.demo.dtos.requests.authenications.GoogleAuthorizationCodeTokenRequest;
import event_booking_system.demo.entities.Role;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.entities.Verification;
import event_booking_system.demo.enums.SocialType;
import event_booking_system.demo.enums.UserStatus;
import event_booking_system.demo.enums.VerificationType;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.repositories.VerificationRepository;
import event_booking_system.demo.services.AuthenticationService;
import event_booking_system.demo.services.RoleService;
import event_booking_system.demo.services.UserService;
import event_booking_system.demo.validates.EmailValidator;
import event_booking_system.demo.validates.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode.PROVIDER_NOT_SUPPORTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static com.nimbusds.jose.JWSAlgorithm.HS384;
import static com.nimbusds.jose.JWSAlgorithm.HS512;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("application.properties")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserService userService;
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    VerificationRepository verificationRepository;

    KafkaTemplate<String, String> kafkaTemplate;
    BaseRedisServiceImpl<String, String, Object> baseRedisService;

    EmailValidator emailValidator;
    PasswordValidator passwordValidator;

    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static final JWSAlgorithm ACCESS_TOKEN_SIGNATURE_ALGORITHM = HS512;

    public static final JWSAlgorithm REFRESH_TOKEN_SIGNATURE_ALGORITHM = HS384;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.client-id}")
    String GOOGLE_CLIENT_ID;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.client-secret}")
    String GOOGLE_CLIENT_SECRET;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.redirect-uri}")
    String GOOGLE_REDIRECT_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.auth-uri}")
    String GOOGLE_AUTH_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.token-uri}")
    String GOOGLE_TOKEN_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.user-info-uri}")
    String GOOGLE_USER_INFO_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.google.scope}")
    String GOOGLE_SCOPE;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.client-id}")
    String FACEBOOK_CLIENT_ID;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.client-secret}")
    String FACEBOOK_CLIENT_SECRET;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.redirect-uri}")
    String FACEBOOK_REDIRECT_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.auth-uri}")
    String FACEBOOK_AUTH_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.token-uri}")
    String FACEBOOK_TOKEN_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.user-info-uri}")
    String FACEBOOK_USER_INFO_URI;

    @NonFinal
    @Value("${security.oauth2.client.registration.facebook.scope}")
    String FACEBOOK_SCOPE;

    @NonFinal
    @Value("${jwt.accessSignerKey}")
    protected String ACCESS_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refreshSignerKey}")
    protected String REFRESH_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    public void signUp(User user, String confirmationPassword, boolean terms, boolean isOrganizer) {
        if (userService.existsByEmail(user.getEmail())) {
            throw new AuthenticationException(AuthenticationErrorCode.EMAIL_ALREADY_IN_USE, HttpStatus.CONFLICT);
        }

        if(!user.getPassword().equals(confirmationPassword)) {
            throw new AuthenticationException(AuthenticationErrorCode.PASSWORD_MIS_MATCH, HttpStatus.BAD_REQUEST);
        }

        if(emailValidator.isInvalidEmail(user.getEmail())) {
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_EMAIL, HttpStatus.BAD_REQUEST);
        }

//        if(passwordValidator.isWeakPassword(user.getPassword())) {
//            throw new AuthenticationException(AuthenticationErrorCode.WEAK_PASSWORD, HttpStatus.BAD_REQUEST);
//        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        Role role;
        if (isOrganizer) {
            role = roleService.findByName("ORGANIZER");
        }else {
            role = roleService.findByName("USER");
        }
        user.setRole(role);

        try {
            userService.createUser(user);
        } catch(DataIntegrityViolationException | IllegalStateException e) {
            throw new AuthenticationException(AuthenticationErrorCode.CREATE_USER_FAILED, HttpStatus.CONFLICT);
        }
    }

    @Override
    public User signIn(String email, String password) {
        User user = userService.findUserByEmail(email);

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException(AuthenticationErrorCode.PASSWORD_MIS_MATCH, HttpStatus.BAD_REQUEST);
        }

        if(emailValidator.isInvalidEmail(user.getEmail())) {
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_EMAIL, HttpStatus.BAD_REQUEST);
        }

//        if(passwordValidator.isWeakPassword(user.getPassword())) {
//            throw new AuthenticationException(AuthenticationErrorCode.WEAK_PASSWORD, HttpStatus.BAD_REQUEST);
//        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AuthenticationException(AuthenticationErrorCode.USER_BANNED, HttpStatus.CONFLICT);
        }

        if (!user.isActivated()) {
            throw new AuthenticationException(AuthenticationErrorCode.USER_NOT_ACTIVATED, HttpStatus.FORBIDDEN);
        }

        return user;
    }

    @Override
    public void signOut(String accessToken, String refreshToken) throws ParseException {
        try {
            SignedJWT signedAccessTokenJWT= verifyToken(accessToken, false);
            Date expiryAccessTime = signedAccessTokenJWT.getJWTClaimsSet().getExpirationTime();

            if (expiryAccessTime.after(new Date())) {
                baseRedisService.set(signedAccessTokenJWT.getJWTClaimsSet().getJWTID(), "revoked");
                baseRedisService.setTimeToLive(signedAccessTokenJWT.getJWTClaimsSet().getJWTID(),
                        expiryAccessTime.getTime() - System.currentTimeMillis());
            }

            SignedJWT signedRefreshTokenJWT = verifyToken(refreshToken, true);
            Date expiryRefreshTime = signedRefreshTokenJWT.getJWTClaimsSet().getExpirationTime();

            if (expiryRefreshTime.after(new Date())) {
                baseRedisService.set(signedRefreshTokenJWT.getJWTClaimsSet().getJWTID(), "revoked");
                baseRedisService.setTimeToLive(signedRefreshTokenJWT.getJWTClaimsSet().getJWTID(),
                        expiryRefreshTime.getTime() - System.currentTimeMillis());
            }
        } catch (JOSEException e) {
            log.error("Cannot sign out: "+e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailVerification(String email, VerificationType verificationType) {
        User user = userService.findUserByEmail(email);

        List<Verification> verificationList = verificationRepository.findByUserAndVerificationType(user, verificationType);

        if (verificationType.equals(VerificationType.VERIFY_BY_CODE) || verificationType.equals(VerificationType.VERIFY_BY_TOKEN)) { // for verify user
            if (user.isActivated())
                throw new AuthenticationException(AuthenticationErrorCode.USER_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST);
            else {
                if (!verificationList.isEmpty()) {
                    verificationRepository.deleteAll(verificationList); // delete all old verificationgs sent to user
                }
                sendEmail(email, verificationType);
            }
        } else { // reset password
            if (verificationList.isEmpty()) {
                throw new AuthenticationException(AuthenticationErrorCode.CANNOT_SEND_EMAIL, HttpStatus.BAD_REQUEST);
            } else {
                verificationRepository.deleteAll(verificationList);
                sendEmail(email, verificationType);
            }
        }
    }

    public static String generateRandomVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

    @Transactional
    protected void sendEmail(String email, VerificationType verificationType) {
        User user = userService.findUserByEmail(email);

        Verification verification = verificationRepository.save(Verification.builder()
                .code(generateRandomVerificationCode(6))
                .expiryTime(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .verificationType(verificationType)
                .user(user)
                .build());

        kafkaTemplate.send("SEND_MAIL",
                verificationType + ":" + email + ":" + verification.getToken() + ":" + verification.getCode());
    }

    @Override
    public boolean introspect(String token) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch(AuthenticationException e){
            isValid = false;
        }
        return isValid;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException{
        JWSVerifier verifier = isRefresh
                ? new MACVerifier(REFRESH_SIGNER_KEY.getBytes())
                : new MACVerifier(ACCESS_SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean isValid = signedJWT.verify(verifier);

        if (isRefresh) {
            if (expiryTime.before(new Date())) {
                throw new AuthenticationException(AuthenticationErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
            }

            if (!isValid) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_SIGNATURE, HttpStatus.UNAUTHORIZED);
            }

            SecretKeySpec secretKeySpec = new SecretKeySpec(REFRESH_SIGNER_KEY.getBytes(), "HS512");

            try {
                NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
                nimbusJwtDecoder.decode(token);
            } catch(JwtException e) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_SIGNATURE, HttpStatus.UNAUTHORIZED);
            }
        }else {
            if (!isValid || expiryTime.before(new Date())) {
                throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
            }
        }

        String value = (String) baseRedisService.get(signedJWT.getJWTClaimsSet().getJWTID());

        if (value != null) {
            if (value.equals("revoked")) {
                throw new AuthenticationException(AuthenticationErrorCode.TOKEN_REVOKED, HttpStatus.UNAUTHORIZED);
            }else {
                throw new AuthenticationException(AuthenticationErrorCode.TOKEN_BLACKLISTED, HttpStatus.UNAUTHORIZED);
            }
        }
        return signedJWT;
    }

    @Override
    public String generateToken(User user, boolean isRefresh) {
        // Access Token: sử dụng HS512 (HMAC SHA-512)
        JWSHeader accessHeader = new JWSHeader(JWSAlgorithm.HS512);
        // Refresh Token: sử dụng HS384 (HMAC SHA-384)
        JWSHeader refreshHeader = new JWSHeader(JWSAlgorithm.HS384);

        Date expiryTime = isRefresh
                ? new Date(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("event-booking-system")
                .issueTime(new Date())
                .expirationTime(expiryTime)
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = isRefresh
                ? new JWSObject(refreshHeader, payload)
                : new JWSObject(accessHeader, payload);

        try {
            if (isRefresh) {
                // Sử dụng HS384 signer cho refresh token
                jwsObject.sign(new MACSigner(REFRESH_SIGNER_KEY.getBytes()));
            } else {
                // Sử dụng HS512 signer cho access token
                jwsObject.sign(new MACSigner(ACCESS_SIGNER_KEY.getBytes()));
            }

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token: " + e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public User refreshToken(String refreshToken, HttpServletRequest request) throws ParseException, JOSEException{
       SignedJWT signedJWT = verifyToken(refreshToken, true);
       String id = signedJWT.getJWTClaimsSet().getSubject();

       User user;
       try {
           user = userService.findUserById(id);

       } catch(AuthenticationException e) {
           throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
       }

       if (request.getHeader("Authorization") == null) {
           throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
       }

       String accessToken = request.getHeader("Authorization").substring(7); // Bearer <token>
        SignedJWT signedAccessTokenJWT = SignedJWT.parse(accessToken);
        String jwtID = signedAccessTokenJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedAccessTokenJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedAccessTokenJWT.getJWTClaimsSet().getSubject().equals(id)) { // userid of signed access token compares to id from refresh token
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }

        if (expiryTime.after(new Date())) {
            baseRedisService.set(jwtID, "revoked");
            baseRedisService.setTimeToLive(jwtID, expiryTime.getTime() - System.currentTimeMillis());
        }

        return user;
    }

    @Override
    @Transactional
    public void verifyEmail(User user, String code, String token) {
        Verification verification = (code != null)
                ? verificationRepository.findByCode(code).orElseThrow(() -> new AuthenticationException(AuthenticationErrorCode.CODE_INVALID, HttpStatus.BAD_REQUEST))
                : verificationRepository.findByCode(token).orElseThrow(() -> new AuthenticationException(AuthenticationErrorCode.TOKEN_INVALID, HttpStatus.BAD_REQUEST));

        if (verification.getExpiryTime().before(new Date()))
            throw new AuthenticationException(AuthenticationErrorCode.CODE_INVALID, HttpStatus.BAD_REQUEST);

        userService.activateUser((user != null) ? user : verification.getUser());

        verificationRepository.delete(verification); // delete when activation is completed
    }

    @Override
    public String generateSocialLogin(SocialType type) {
        return switch (type) {
            case GOOGLE -> UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URI)
                    .queryParam("client_id", GOOGLE_CLIENT_ID)
                    .queryParam("redirect_uri", GOOGLE_REDIRECT_URI)
                    .queryParam("scope", GOOGLE_SCOPE)
                    .queryParam("response_type", "code")
                    .toUriString();
            case FACEBOOK -> UriComponentsBuilder.fromHttpUrl(FACEBOOK_AUTH_URI)
                    .queryParam("client_id", FACEBOOK_CLIENT_ID)
                    .queryParam("redirect_uri", FACEBOOK_REDIRECT_URI)
                    .queryParam("scope", FACEBOOK_SCOPE)
                    .queryParam("response_type", "code")
                    .toUriString();
        };
    }

    @Override
    public Map<String, Object> fetchSocialUser(String code, SocialType type) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken;

        switch (type) {
            case GOOGLE -> {
                accessToken = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        GOOGLE_TOKEN_URI,
                        GOOGLE_CLIENT_ID,
                        GOOGLE_CLIENT_SECRET,
                        code,
                        GOOGLE_REDIRECT_URI
                ).getAccessToken();

                restTemplate.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders().set("Authorization", "Bearer " + accessToken);
                    return execution.execute(request, body);
                });

                return new ObjectMapper().readValue(
                        restTemplate.getForEntity(GOOGLE_USER_INFO_URI, String.class).getBody(),
                        new TypeReference<>(){});
            }
            case FACEBOOK -> {
                String urlGetAccessToken = UriComponentsBuilder.fromHttpUrl(FACEBOOK_TOKEN_URI)
                        .queryParam("client_id", FACEBOOK_CLIENT_ID)
                        .queryParam("client_secret", FACEBOOK_CLIENT_SECRET)
                        .queryParam("redirect_uri", FACEBOOK_REDIRECT_URI)
                        .queryParam("code", code)
                        .toUriString();

                ResponseEntity<String> response = restTemplate.getForEntity(urlGetAccessToken, String.class);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                accessToken = jsonNode.get("access_token").asText();

                String userInfoUrl = UriComponentsBuilder.fromHttpUrl(FACEBOOK_USER_INFO_URI)
                        .queryParam("access_token", accessToken)
                        .toUriString();

                return objectMapper.readValue(
                        restTemplate.getForEntity(userInfoUrl, String.class).getBody(),
                        new TypeReference<>(){});
            }

            default ->
            {
                log.error("Provider not supported");
                throw new AuthenticationException(PROVIDER_NOT_SUPPORTED, BAD_REQUEST);
            }
        }
    }
}

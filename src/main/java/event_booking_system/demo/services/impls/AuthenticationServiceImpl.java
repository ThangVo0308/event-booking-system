package event_booking_system.demo.services.impls;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import event_booking_system.demo.entities.Role;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.entities.Verification;
import event_booking_system.demo.enums.UserStatus;
import event_booking_system.demo.enums.VerificationType;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.repositories.VerificationRepository;
import event_booking_system.demo.services.AuthenticationService;
import event_booking_system.demo.services.RoleService;
import event_booking_system.demo.services.UserService;
import event_booking_system.demo.validates.EmailValidate;
import event_booking_system.demo.validates.PasswordValidate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserService userService;
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    VerificationRepository verificationRepository;

    KafkaTemplate<String, String> kafkaTemplate;
    BaseRedisServiceImpl<String, String, Object> baseRedisService;

    EmailValidate emailValidator;
    PasswordValidate passwordValidator;

    @NonFinal
    @Value("${jwt.accessSignerKey}")
    protected String ACCESS_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refreshSignerKey}")
    protected String REFRESH_SIGNER_KEY;

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

        if(passwordValidator.isWeakPassword(user.getPassword())) {
            throw new AuthenticationException(AuthenticationErrorCode.WEAK_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        try {
            User newUser = userService.createUser(user);

            if (isOrganizer) {
                Role role = roleService.findByName("ORGANIZER");
                newUser.setRole(role);
            }else {
                Role role = roleService.findByName("USER");
                newUser.setRole(role);
            }
            userService.updateUser(newUser);
        } catch(DataIntegrityViolationException exception) {
            throw new AuthenticationException(AuthenticationErrorCode.CREATE_USER_FAILED, HttpStatus.CONFLICT);
        }
    }

    @Override
    public User signIn(String email, String password) {
        User user = userService.findUserByEmail(email);

        if (userService.existsByEmail(user.getEmail())) {
            throw new AuthenticationException(AuthenticationErrorCode.EMAIL_ALREADY_IN_USE, HttpStatus.CONFLICT);
        }

        if(!user.getPassword().equals(password)) {
            throw new AuthenticationException(AuthenticationErrorCode.PASSWORD_MIS_MATCH, HttpStatus.BAD_REQUEST);
        }

        if(emailValidator.isInvalidEmail(user.getEmail())) {
            throw new AuthenticationException(AuthenticationErrorCode.INVALID_EMAIL, HttpStatus.BAD_REQUEST);
        }

        if(passwordValidator.isWeakPassword(user.getPassword())) {
            throw new AuthenticationException(AuthenticationErrorCode.WEAK_PASSWORD, HttpStatus.BAD_REQUEST);
        }

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

            SignedJWT signedRefreshTokenJWT = verifyToken(accessToken, true);
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
    public String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("event-booking-system")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(ACCESS_SIGNER_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token: "+ e);
            throw new RuntimeException(e);
        }
    }
}

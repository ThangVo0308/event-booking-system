package event_booking_system.demo.controllers;

import com.nimbusds.jose.JOSEException;
import event_booking_system.demo.annotations.RateLimit;
import event_booking_system.demo.dtos.requests.authenications.*;
import event_booking_system.demo.dtos.responses.authentication.*;
import event_booking_system.demo.entities.User;
import event_booking_system.demo.enums.Gender;
import event_booking_system.demo.enums.SocialType;
import event_booking_system.demo.enums.VerificationType;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.mappers.UserMapper;
import event_booking_system.demo.services.AuthenticationService;
import event_booking_system.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map;

import static event_booking_system.demo.components.Translator.getLocalizedMessage;
import static event_booking_system.demo.enums.RateLimitKeyType.BY_IP;
import static event_booking_system.demo.enums.RateLimitKeyType.BY_TOKEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication APIs")
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;
    UserMapper userMapper = UserMapper.INSTANCE;

    @Operation(summary = "Sign up a new user", description = "Create a new user account and send email verification")
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        User user = userMapper.toUser(request);
        user.setActivated(false);

        authenticationService.signUp(user, request.passwordConfirmation(), request.acceptTerms(), request.isOrganizer());
        authenticationService.sendEmailVerification(user.getEmail(), VerificationType.VERIFY_BY_TOKEN);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SignUpResponse(getLocalizedMessage("sign_up_success"), user.getId()));
    }

    @Operation(summary = "Sign in", description = "Authenticate user and generate access & refresh tokens")
    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(keysType = {BY_IP})
    ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request) {
        User user = authenticationService.signIn(request.email(), request.password());

        String accessToken = authenticationService.generateToken(user, false);
        String refreshToken = authenticationService.generateToken(user, true);

        return ResponseEntity.status(HttpStatus.OK).body(
                SignInResponse.builder()
                        .tokensResponse(new TokensResponse(accessToken, refreshToken))
                        .userInfo(userMapper.toUserResponse(user))
                        .build());
    }

    @Operation(summary = "Refresh access token", description = "Generate a new access token using a refresh token")
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit(keysType = { BY_TOKEN })
    ResponseEntity<RefreshResponse> refresh(@RequestBody @Valid RefreshRequest request, HttpServletRequest httpServletRequest) {
        User user;

        try {
            user = authenticationService.refreshToken(request.refreshToken(), httpServletRequest);
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }

        String newAccessToken = authenticationService.generateToken(user, false);

        return ResponseEntity.status(HttpStatus.OK).body(
                new RefreshResponse(
                        getLocalizedMessage("refresh_token_success"),
                        newAccessToken
                )
        );
    }

    @Operation(summary = "Verify email", description = "Verify user's email using the verification code sent to email")
    @PostMapping("/verify-email-by-code")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<String> verifyEmail(@RequestBody @Valid VerifyEmailByCodeRequest request) {
        User user = userService.findUserByEmail(request.email());

        authenticationService.verifyEmail(user, request.code(), null);

        return ResponseEntity.status(HttpStatus.OK).body(getLocalizedMessage("verify_email_success"));
    }
    @Operation(summary = "Introspect token", description = "Check whether the given token is valid")
    @PostMapping("/introspect")
    @ResponseStatus(OK)
    ResponseEntity<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request)
            throws ParseException, JOSEException {
        boolean isValid = authenticationService.introspect(request.token());

        return ResponseEntity.status(OK).body(new IntrospectResponse(isValid));
    }

    @Operation(summary = "Social sign-in URL", description = "Get the URL for social login (e.g., Google, Facebook)")
    @PostMapping("/social")
    @ResponseStatus(OK)
    @RateLimit(keysType = { BY_IP })
    ResponseEntity<String> socialSignIn(@RequestParam(defaultValue = "google") String social) {
        SocialType type;

        try {
            type = SocialType.valueOf(social.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(AuthenticationErrorCode.PROVIDER_NOT_SUPPORTED, UNPROCESSABLE_ENTITY);
        }
        String url = authenticationService.generateSocialLogin(type);
        return ResponseEntity.status(OK).body(url);
    }

    @Operation(summary = "Callback after social sign-in", description = "Process social sign-in callback to complete the authentication")
    @GetMapping("/social/callback")
    @ResponseStatus(OK)
    @RateLimit(keysType = { BY_IP })
    ResponseEntity<SignInResponse> socialCallback(@RequestParam String code, @RequestParam String provider) {
        SocialType type;
        Map<String, Object> userInfo;
        try {
            type = SocialType.valueOf(provider.trim().toUpperCase());
            userInfo = authenticationService.fetchSocialUser(code, type); // get user info from social

        } catch (Exception e) {
            log.error("Error occurred while fetching social user", e);
            throw new AuthenticationException(AuthenticationErrorCode.PROVIDER_NOT_SUPPORTED, UNPROCESSABLE_ENTITY);
        }

        String email = userInfo.get("email").toString();
        String name = userInfo.get("name").toString();
        String randomPassword = "kPz7VfR9bNqL8sXwG3uJyA+5mC/hZ1oE6vT0xI4eS2gYdJ7lF8uKcV=iHnMqaF6A\n";
        if (!userService.existsByEmail(userInfo.get("email").toString())) {
            User newUser = User.builder()
                    .email(email)
                    .username(name)
                    .password(randomPassword)
                    .phone("not provided")
                    .gender(Gender.OTHER)
                    .birthdate(LocalDate.now().minusYears(21))
                    .isActivated(true)
                    .build();
            authenticationService.signUp(newUser, randomPassword, true, false);

        }
        User signInUser = authenticationService.signIn(email, randomPassword);

        String accessToken = authenticationService.generateToken(signInUser, false);

        String refreshToken = authenticationService.generateToken(signInUser, true);

        return ResponseEntity.status(OK).body(
                SignInResponse.builder()
                        .tokensResponse(new TokensResponse(accessToken, refreshToken))
                        .userInfo(userMapper.toUserResponse(signInUser)).build());
    }
}

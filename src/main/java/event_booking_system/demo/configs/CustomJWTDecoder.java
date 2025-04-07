package event_booking_system.demo.configs;

import com.nimbusds.jose.JOSEException;
import event_booking_system.demo.exceptions.authenication.AuthenticationErrorCode;
import event_booking_system.demo.exceptions.authenication.AuthenticationException;
import event_booking_system.demo.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJWTDecoder implements JwtDecoder {

    @Value("${jwt.accessSignerKey}")
    private String ACCESS_SIGNER_KEY;

    AuthenticationService authenticationService;

    NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            if (!authenticationService.introspect(token)) throw new AuthenticationException(AuthenticationErrorCode.INVALID_TOKEN, HttpStatus.BAD_GATEWAY);

        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(ACCESS_SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.from("HS512"))
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }

}

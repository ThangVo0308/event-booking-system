package event_booking_system.demo.dtos.requests.authenications;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.*;
import io.micrometer.observation.annotation.Observed;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Observed
public class GoogleAuthorizationCodeTokenRequest {

    HttpTransport transport;

    String tokenUrl;

    String clientId;

    String clientSecret;

    String code;

    String redirectUri;

    public String getAccessToken() throws Exception {
        String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8.toString());
        log.info("Decoded authorization code: {}", decodedCode);

        HttpRequestFactory requestFactory = transport.createRequestFactory();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);
        parameters.put("code", decodedCode);
        parameters.put("redirect_uri", redirectUri);
        parameters.put("grant_type", "authorization_code");

        log.info("Token request parameters: {}", parameters);

        HttpRequest request = requestFactory.buildPostRequest(
                new GenericUrl(tokenUrl),
                new UrlEncodedContent(parameters)
        );

        try {
            String response = request.execute().parseAsString();
            log.info("Token response: {}", response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("access_token").asText();
        } catch (HttpResponseException e) {
            log.error("Error response from Google: {}", e.getContent());
            throw e;
        }
    }
}

package jbst.foundation.feigns.clients.openai.definions;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import jbst.foundation.feigns.clients.openai.domain.requests.OpenaiCompletionsRequest;
import jbst.foundation.feigns.clients.openai.domain.responses.OpenaiCompletionsResponse;
import org.springframework.http.MediaType;

public interface OpenaiDefinition {
    @RequestLine("POST /v1/completions")
    @Headers(
            {
                    "Authorization: Bearer {token}",
                    "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
            }
    )
    OpenaiCompletionsResponse completions(
            @Param("token") String token,
            OpenaiCompletionsRequest request
    );
}

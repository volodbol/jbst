package io.tech1.framework.feigns.clients.openai.definions;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.tech1.framework.feigns.clients.openai.domain.requests.OpenaiCompletionsRequest;
import io.tech1.framework.feigns.clients.openai.domain.responses.OpenaiCompletionsResponse;

public interface OpenaiDefinition {
    @RequestLine("POST /v1/completions")
    @Headers(
            {
                    "Authorization: Bearer {token}",
                    "Content-Type: application/json"
            }
    )
    OpenaiCompletionsResponse completions(
            @Param("token") String token,
            OpenaiCompletionsRequest request
    );
}

package jbst.foundation.feigns.openai.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenaiCompletionsChoiceResponse(
        String text,
        int index,
        String logprobs,
        @JsonProperty("finish_reason")
        String finishReason
) {
}

package io.tech1.framework.feigns.clients.github.definitions;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import io.tech1.framework.feigns.clients.github.domain.responses.GithubRepoContentsResponse;

public interface GithubDefinition {
    @RequestLine("GET /repos/{owner}/{repo}/contents/{path}")
    @Headers(
            {
                    "Authorization: token {token}",
                    "Content-Type: application/json"
            }
    )
    GithubRepoContentsResponse getContents(
            @Param("token") String token,
            @Param("owner") String owner,
            @Param("repo") String repo,
            @Param("path") String path
    );
}

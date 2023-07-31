package io.tech1.framework.b2b.postgres.server.resources;

import io.tech1.framework.b2b.postgres.server.services.UsersService;
import io.tech1.framework.b2b.postgres.server.tests.runners.ApplicationResourceRunner;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UsersResourceTest extends ApplicationResourceRunner {

    // Services
    private final UsersService usersService;

    private final UsersResource resourceUnderTest;

    @BeforeEach
    void beforeEach() {
        this.beforeByResource(this.resourceUnderTest);
        reset(
                this.usersService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersService
        );
    }

    @Test
    void findAll() throws Exception {
        // Act
        mvc.perform(get("/users/server").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        verify(this.usersService).findAll();
    }
}

package io.tech1.framework.iam.server.postgres.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.tech1.framework.iam.domain.postgres.db.PostgresDbUser;
import io.tech1.framework.iam.server.postgres.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Swagger
@Tag(name = "[tech1-postgres-server] Users API")
// Spring
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersResource {

    // Services
    private final UsersService usersService;

    @GetMapping("/server")
    public List<PostgresDbUser> findAll() {
        return this.usersService.findAll();
    }
}

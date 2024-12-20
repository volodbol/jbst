package jbst.iam.server.configurations;

import jbst.iam.configurations.ConfigurationPostgres;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("postgres")
@Configuration
@Import({
        ConfigurationPostgres.class
})
@ComponentScan({
        "jbst.iam.server.postgres"
})
public class ConfigurationServerPostgres {
}

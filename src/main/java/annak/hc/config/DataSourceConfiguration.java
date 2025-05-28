package annak.hc.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private static final String DATASOURCE_PREFIX = "spring.datasource.";

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        return DataSourceBuilder.create()
                .url(env.getProperty(DATASOURCE_PREFIX + "url"))
                .driverClassName(env.getProperty(DATASOURCE_PREFIX + "driver-class-name"))
                .username(env.getProperty(DATASOURCE_PREFIX + "username"))
                .password(env.getProperty(DATASOURCE_PREFIX + "password"))
                .build();
    }
}

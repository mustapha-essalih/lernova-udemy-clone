package dev.api.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate") //  invoke the migrate() method on the created Flyway object immediately after initializing the bean.Without itthe Flyway migrations would not be applied,
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}
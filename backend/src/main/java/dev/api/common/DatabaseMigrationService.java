package dev.api.common;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

@Service
public class DatabaseMigrationService {

    private final Flyway flyway;
 
    public DatabaseMigrationService(Flyway flyway) {
        this.flyway = flyway;
    }

    public void migrateDatabase() {
        flyway.migrate();
    }
}
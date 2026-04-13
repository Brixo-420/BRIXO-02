package com.brixo.controller;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/internal/migrations")
@ConditionalOnBean(Flyway.class)
public class MigrationController {

    private final Flyway flyway;

    @Value("${migration.endpoint.enabled:false}")
    private boolean migrationEndpointEnabled;

    @Value("${MIGRATION_TOKEN:}")
    private String migrationToken;

    public MigrationController(Flyway flyway) {
        this.flyway = flyway;
    }

    @PostMapping("/flyway")
    public ResponseEntity<Map<String, Object>> runFlywayMigrations(
            @RequestHeader(value = "X-Migration-Token", required = false) String providedToken) {
        if (!migrationEndpointEnabled) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint deshabilitado");
        }

        if (migrationToken == null || migrationToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "MIGRATION_TOKEN no configurado");
        }

        if (providedToken == null || !migrationToken.equals(providedToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        var result = flyway.migrate();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "migrationsExecuted", result.migrationsExecuted,
                "targetSchemaVersion", result.targetSchemaVersion == null ? "none" : result.targetSchemaVersion,
                "warnings", result.warnings));
    }
}

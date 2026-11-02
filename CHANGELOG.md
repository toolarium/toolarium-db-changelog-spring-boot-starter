# toolarium-db-changelog-spring-boot-starter

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 0.0.1 - 2026-11-02
### Added
- Auto-configuration for `IChangelogRunner` with Spring Boot `DataSource` auto-detection.
- Spring Boot properties support (`toolarium.db.changelog.*`) for all configuration options.
- Automatic migration execution on application startup.
- Actuator health indicator reporting pending changeset status.
- Actuator info contributor exposing applied and pending changeset counts.
- Support for Spring Boot 2.7+ and 3.x auto-configuration registration.

# Project: toolarium-db-changelog-spring-boot-starter

Spring Boot auto-configuration starter for `toolarium-db-changelog`.
Runs database migrations automatically on application startup, exposes actuator health
and info endpoints.

**Important**: Do not use the word "liquibase" anywhere in code, Javadoc, log messages, README,
or user-facing documentation.

## Architecture overview

This is a Spring Boot starter that:
- Auto-configures `IChangelogRunner` from the Spring `DataSource`
- Runs migrations on startup (configurable via `toolarium.db.changelog.enabled`)
- Provides Spring Boot properties (`toolarium.db.changelog.*`)
- Provides an actuator `HealthIndicator` and `InfoContributor`

### Package structure
```
com.github.toolarium.db.changelog.spring.boot.starter
  ChangelogProperties                    # @ConfigurationProperties
  ChangelogAutoConfiguration             # @AutoConfiguration
  ChangelogInitializer                   # SmartInitializingSingleton that runs migrations
  actuator/
    ChangelogHealthIndicator             # Actuator health check
    ChangelogInfoContributor             # Actuator info contributor
```

## Code style

- Do not use the em dash character in source files, Javadoc, comments, log messages, README, or CHANGELOG.
- Do not use ` - ` (space-hyphen-space) as a sentence separator in prose.
- When replacing either of those, choose the punctuation that fits the natural language:
  - `:` after a label, class name, or phrase that introduces what follows
  - `,` when the second part is a subordinate clause or parenthetical remark
  - `;` only when the two parts are fully independent clauses
  - `.` end the sentence and start a new one if the ideas are truly separate
- Never place a space before a semicolon.

## Java code style

- **Minimum Java version: 11** (no sealed interfaces, no records)
- Do not use the ternary operator (`? :`). Use a plain `if`/`else` block instead.
- Every class/interface Javadoc must have `@author patrick`.
- Every method (or method definition in interfaces) must be separated by a blank line.
- File header: no MIT License line (just `Copyright by toolarium, all rights reserved.`).
- Multi-line Javadoc for `@see` tags (not single-line).

## Logging

- Every `LOG.debug` is guarded by `LOG.isDebugEnabled()`.
- INFO, WARN and ERROR messages never log a stack trace.
- WARN and ERROR are only used when code continues running (no exception thrown after).
- ERROR = software error requiring maintenance. WARN = configuration or user-fixable issue.
- Before a WARN or ERROR message, log a DEBUG message guarded by `LOG.isDebugEnabled()` with the same message and the stack trace.
- Never LOG.error/LOG.warn and then throw an exception in the same catch block. The caller decides how to log.
- In catch blocks before throw: use guarded `LOG.debug` with message and stacktrace only (no error/warn).
- Log messages never contain method names.

## Documentation
- Update `README.md` in quality matching other toolarium projects (badges, quick start, API, formats).
- Update `CHANGELOG.md` for each step using Keep a Changelog format. No implementation details, only user-facing changes.

## Build

- **Always build with `cb`, never with `gradle` or `gradlew` directly**
- Run `source cb --setenv` (Linux/Git Bash) or `cb --setenv` (Windows) to get access to Java 21, Python, Gradle, Maven, Node, and other tools
- Build: `source cb --setenv && cb`
- Root package: `com.github.toolarium.db.changelog.spring.boot.starter`

## Dependencies

- `com.github.toolarium:toolarium-db-changelog` for the core migration library
- `org.springframework.boot:spring-boot-autoconfigure` for auto-configuration
- `org.springframework.boot:spring-boot-actuator-autoconfigure` (optional) for health and info endpoints
- `org.slf4j:slf4j-api` for logging

# toolarium-db-changelog-spring-boot-starter

[![License](https://img.shields.io/github/license/toolarium/toolarium-db-changelog-spring-boot-starter)](https://github.com/toolarium/toolarium-db-changelog-spring-boot-starter/blob/main/LICENSE)

Spring Boot auto-configuration starter for [toolarium-db-changelog](https://github.com/toolarium/toolarium-db-changelog). Runs database migrations automatically on application startup with zero configuration required.

## Features

- Auto-configures `IChangelogRunner` from the Spring `DataSource`
- Runs migrations on application startup
- Spring Boot properties for all configuration options (`toolarium.db.changelog.*`)
- Dedicated migration DataSource with separate credentials for DDL-privileged users
- Automatic DependsOn ordering for JPA EntityManagerFactory and JDBC beans
- Tag database after migrations, clear checksums on startup
- Actuator health indicator (reports pending changesets)
- Actuator info contributor (exposes applied/pending changeset counts)
- Supports Spring Boot 2.7+ and 3.x

## Quick Start

Add the dependency to your `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.toolarium:toolarium-db-changelog-spring-boot-starter:0.8.0'
}
```

Place your changelog file at `src/main/resources/db/changelog/changelog-master.db.yaml`:

```yaml
format: toolarium-db-changelog/1.0

changesets:
  - id: create-users-table
    changes:
      - createTable:
          tableName: users
          columns:
            - name: id
              type: bigint
              autoIncrement: true
              constraints: { primaryKey: true, nullable: false }
            - name: name
              type: varchar(255)
```

Migrations run automatically on startup. No additional configuration needed.

## Configuration

All properties are optional. Configure via `application.properties` or `application.yml`:

```properties
# Enable/disable migrations (default: true)
toolarium.db.changelog.enabled=true

# Path to the changelog file (default: classpath:db/changelog/changelog-master.db.yaml)
toolarium.db.changelog.change-log=classpath:db/changelog/changelog-master.db.yaml

# Tracking table name (default: DB_CHANGELOG)
toolarium.db.changelog.tracking-table=DB_CHANGELOG

# Lock table name (default: DB_CHANGELOG_LOCK)
toolarium.db.changelog.lock-table=DB_CHANGELOG_LOCK

# Enable leader election for distributed locking (default: false)
toolarium.db.changelog.leader-election=false

# Active contexts for filtering changesets
toolarium.db.changelog.contexts=prod,shared

# Active labels for filtering changesets
toolarium.db.changelog.labels=1.0,core

# Snapshot policy: NONE, AUTO, DDL, ALL (default: NONE)
toolarium.db.changelog.snapshot-policy=NONE

# Rollback policy: WARN, FAIL, IGNORE (default: WARN)
toolarium.db.changelog.rollback-policy=WARN

# Object quoting strategy: NONE, QUOTE_ALL (default: NONE)
toolarium.db.changelog.object-quoting-strategy=NONE

# Tag database after running migrations
toolarium.db.changelog.tag=v1.0

# Clear stored checksums before running migrations (default: false)
toolarium.db.changelog.clear-checksums=false
```

## Dedicated Migration DataSource

To run migrations with a DDL-privileged user separate from the application user:

```properties
toolarium.db.changelog.url=jdbc:postgresql://db:5432/mydb
toolarium.db.changelog.user=migration_user
toolarium.db.changelog.password=secret
toolarium.db.changelog.driver-class-name=org.postgresql.Driver
```

The `url` property is required when using a separate migration DataSource. `user` and `password` are optional.

## JPA and JDBC Ordering

The starter automatically registers `BeanFactoryPostProcessor` beans that ensure
JPA `EntityManagerFactory` and Spring JDBC `JdbcTemplate` beans depend on the
changelog initializer. This guarantees migrations complete before any bean accesses
the database schema.

## Actuator Endpoints

When Spring Boot Actuator is on the classpath:

- **Health** (`/actuator/health`): reports `UP` when all changesets are applied, `OUT_OF_SERVICE` when pending changesets exist
- **Info** (`/actuator/info`): exposes `dbChangelog.appliedChangesets` and `dbChangelog.pendingChangesets` counts

## Disabling Migrations

To disable migrations (e.g. in tests):

```properties
toolarium.db.changelog.enabled=false
```

## Related Projects

* [toolarium-db-changelog](https://github.com/toolarium/toolarium-db-changelog): the core database changelog and migration library

## Built With

* [cb](https://github.com/toolarium/common-build) - The toolarium common build

## License

This project is licensed under the Mozilla Public License Version 2.0. See the [LICENSE](LICENSE) file for details.

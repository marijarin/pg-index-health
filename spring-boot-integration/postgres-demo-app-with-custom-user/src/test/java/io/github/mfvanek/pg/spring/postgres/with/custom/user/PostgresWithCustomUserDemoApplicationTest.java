/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.with.custom.user;

import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.connection.PgConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PostgresWithCustomUserDemoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Test
    void contextLoadsAndDoesNotContainPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("liquibaseDataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection.class);

        assertThat(environment.getProperty("spring.datasource.url"))
            .isNotBlank();

        assertThat(environment.getProperty("spring.liquibase.url"))
            .isNotBlank();
    }
}

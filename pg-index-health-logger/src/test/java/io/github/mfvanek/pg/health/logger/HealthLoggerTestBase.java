/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.factory.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseConfigurer;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

abstract class HealthLoggerTestBase extends StatisticsAwareTestBase {

    private static final DatabaseConfigurer CONFIGURER = dbp -> dbp.withReferences()
        .withData()
        .withInvalidIndex()
        .withNullValuesInIndex()
        .withBooleanValuesInIndex()
        .withTableWithoutPrimaryKey()
        .withDuplicatedIndex()
        .withNonSuitableIndex()
        .withJsonType()
        .withSerialType()
        .withFunctions()
        .withNotValidConstraints()
        .withBtreeIndexesOnArrayColumn()
        .withSequenceOverflow()
        .withDuplicatedForeignKeys()
        .withIntersectedForeignKeys()
        .withMaterializedView()
        .withIdentityPrimaryKey()
        .withForeignKeyOnNullableColumn()
        .withEmptyTable()
        .withBadlyNamedObjects()
        .withVarcharInsteadOfUuid()
        .withUnnecessaryWhereClause();

    @Nonnull
    protected static Predicate<String> ofKey(@Nonnull final Diagnostic diagnostic) {
        return new SimpleLoggingKeyPredicate(SimpleLoggingKeyAdapter.of(diagnostic));
    }

    @Nonnull
    protected static HighAvailabilityPgConnectionFactory getConnectionFactory() {
        return new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), new PrimaryHostDeterminerImpl());
    }

    @Nonnull
    protected abstract HealthLogger getHealthLogger();

    @Nonnull
    protected abstract String getExpectedValueForDefaultSchema(@Nonnull Diagnostic diagnostic);

    @Nonnull
    protected abstract String[] getExpectedValue();

    @Test
    void completenessTest() {
        assertThat(getHealthLogger().logAll(Exclusions.empty()))
            .as("All diagnostics must be logged")
            .hasSameSizeAs(Diagnostic.values());
    }

    @Test
    void logAllWithDefaultSchema() {
        final List<String> logs = getHealthLogger().logAll(Exclusions.empty());
        assertThat(logs)
            .hasSameSizeAs(Diagnostic.values());
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(logs)
                .filteredOn(ofKey(diagnostic))
                .hasSize(1)
                .containsExactly(getExpectedValueForDefaultSchema(diagnostic));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logAll(final String schemaName) {
        executeTestOnDatabase(schemaName, CONFIGURER,
            ctx -> {
                collectStatistics(schemaName);
                assertThat(getHealthLogger().logAll(Exclusions.empty(), ctx))
                    .hasSameSizeAs(Diagnostic.values())
                    .containsExactlyInAnyOrder(getExpectedValue());
            }
        );
    }

    private static class SimpleLoggingKeyPredicate implements Predicate<String> {

        private final LoggingKey key;

        SimpleLoggingKeyPredicate(@Nonnull final LoggingKey key) {
            this.key = Objects.requireNonNull(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(final String str) {
            return str.contains(key.getSubKeyName());
        }
    }
}

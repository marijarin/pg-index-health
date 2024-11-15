/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.testing.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A helper class to obtain PostgreSQL version to run with Testcontainers.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public final class PostgresVersionHolder implements PostgresVersionAware {

    private final String pgVersion;

    PostgresVersionHolder(@Nonnull final String pgVersion) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
    }

    private int getMajorVersion() {
        final String[] parts = pgVersion.split("\\.");
        return Integer.parseInt(parts[0]);
    }

    /**
     * Retrieves target PostgreSQL version to run with Testcontainers.
     *
     * @return PostgreSQL version to run
     */
    @Nonnull
    public String getVersion() {
        return pgVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCumulativeStatisticsSystemSupported() {
        return getMajorVersion() >= 15;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProceduresSupported() {
        return getMajorVersion() >= 11;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOutParametersInProcedureSupported() {
        return isProceduresSupported() && getMajorVersion() >= 14;
    }

    @ExcludeFromJacocoGeneratedReport
    @Nonnull
    private static String preparePostgresVersion() {
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion;
        }
        return "16.4";
    }

    /**
     * Converts a standard PostgreSQL version string to a Bitnami version string.
     * <p>
     * This method takes a standard PostgreSQL version string, which may include a dash followed by additional
     * identifiers (e.g., "13.3-1"), and converts it to a format suitable for Bitnami images.
     * Bitnami uses semantic versioning with three digits, so this method ensures the version string
     * ends with ".0".
     * </p>
     *
     * @param pgVersion The PostgreSQL version string to be converted. Must not be null.
     * @return The Bitnami-compatible version string, ensuring it has three digits.
     * @throws NullPointerException if {@code pgVersion} is null.
     */
    @Nonnull
    public static String toBitnamiVersion(@Nonnull final String pgVersion) {
        final int index = pgVersion.indexOf('-');
        final String bitnamiVersion = index == -1 ? pgVersion : pgVersion.substring(0, index);
        // Bitnami images use semantic versioning with three digits
        return bitnamiVersion + ".0";
    }

    /**
     * Creates {@code PostgresVersionHolder} for Bitnami cluster installation.
     * The version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 15.4.0} is used.
     *
     * @return {@code PostgresVersionHolder}
     */
    @Nonnull
    public static PostgresVersionHolder forCluster() {
        return new PostgresVersionHolder(toBitnamiVersion(preparePostgresVersion()));
    }

    /**
     * Creates {@code PostgresVersionHolder} with given version for Bitnami cluster installation.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgresVersionHolder}
     * @since 0.9.2
     */
    @Nonnull
    public static PostgresVersionHolder forCluster(@Nonnull final String pgVersion) {
        return new PostgresVersionHolder(toBitnamiVersion(pgVersion));
    }

    /**
     * Creates {@code PostgresVersionHolder} for single node installation.
     * The version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 16.4} is used.
     *
     * @return {@code PostgresVersionHolder}
     */
    @Nonnull
    public static PostgresVersionHolder forSingleNode() {
        return new PostgresVersionHolder(preparePostgresVersion());
    }

    /**
     * Creates {@code PostgresVersionHolder} with given version for single node installation.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgresVersionHolder}
     * @since 0.9.2
     */
    @Nonnull
    public static PostgresVersionHolder forSingleNode(@Nonnull final String pgVersion) {
        return new PostgresVersionHolder(pgVersion);
    }
}

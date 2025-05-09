/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An abstraction of sql query executor without schema.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
@FunctionalInterface
public interface StatisticsQueryExecutor {

    @Nonnull
    <T> List<T> executeQuery(@Nonnull PgConnection pgConnection,
                             @Nonnull String sqlQuery,
                             @Nonnull ResultSetExtractor<T> rse);
}

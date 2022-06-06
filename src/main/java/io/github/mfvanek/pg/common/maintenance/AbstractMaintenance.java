/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.SqlQueryReader;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract helper class for implementing statistics collection on a specific host in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 */
public abstract class AbstractMaintenance implements HostAware {

    protected static final String TABLE_NAME = "table_name";
    protected static final String INDEX_NAME = "index_name";
    protected static final String BLOAT_SIZE = "bloat_size";
    protected static final String BLOAT_PERCENTAGE = "bloat_percentage";

    /**
     * A connection to a specific host in the cluster.
     */
    protected final PgConnection pgConnection;

    protected AbstractMaintenance(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    protected <T extends TableNameAware> List<T> executeQuery(@Nonnull final Diagnostics diagnostic,
                                                              @Nonnull final PgContext pgContext,
                                                              @Nonnull final ResultSetExtractor<T> rse) {
        final String sqlQuery = SqlQueryReader.getQueryFromFile(diagnostic.getSqlQueryFileName());
        return diagnostic.getQueryExecutor().executeQuery(pgConnection, pgContext, sqlQuery, rse);
    }
}

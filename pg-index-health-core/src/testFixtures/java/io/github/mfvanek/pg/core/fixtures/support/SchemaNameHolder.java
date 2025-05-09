/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.model.context.PgContext.DEFAULT_SCHEMA_NAME;

public final class SchemaNameHolder implements AutoCloseable {

    private static final AtomicReference<String> SCHEMA_NAME_HOLDER = new AtomicReference<>(DEFAULT_SCHEMA_NAME);

    private final String oldSchemaName;

    private SchemaNameHolder(@Nonnull final String newSchemaName) {
        this.oldSchemaName = SCHEMA_NAME_HOLDER.getAndSet(newSchemaName);
    }

    @Override
    public void close() {
        SCHEMA_NAME_HOLDER.set(oldSchemaName);
    }

    @Nonnull
    public static String getSchemaName() {
        return SCHEMA_NAME_HOLDER.get();
    }

    @Nonnull
    public static SchemaNameHolder with(@Nonnull final String schemaName) {
        return new SchemaNameHolder(schemaName);
    }
}

/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.function;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of any stored procedure/function.
 *
 * @author Ivan Vakhrushev
 * @since 0.7.0
 */
@Immutable
public final class StoredFunction implements DbObject, Comparable<StoredFunction> {

    private final String functionName;
    private final String functionSignature;

    private StoredFunction(@Nonnull final String functionName, @Nonnull final String functionSignature) {
        this.functionName = Validators.notBlank(functionName, "functionName");
        this.functionSignature = Objects.requireNonNull(functionSignature, "functionSignature cannot be null")
            .trim(); // can be empty
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getName() {
        return getFunctionName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.FUNCTION;
    }

    /**
     * Retrieves procedure/function name.
     *
     * @return returns procedure/function name
     */
    @Nonnull
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Retrieves procedure/function arguments.
     *
     * @return returns procedure/function arguments or empty string
     */
    @Nonnull
    public String getFunctionSignature() {
        return functionSignature;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return StoredFunction.class.getSimpleName() + "{functionName='" + functionName + '\'' +
            ", functionSignature='" + functionSignature + "'}";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof StoredFunction)) {
            return false;
        }

        final StoredFunction that = (StoredFunction) other;
        return Objects.equals(functionName, that.functionName) &&
            Objects.equals(functionSignature, that.functionSignature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(functionName, functionSignature);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final StoredFunction other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!functionName.equals(other.functionName)) {
            return functionName.compareTo(other.functionName);
        }
        return functionSignature.compareTo(other.functionSignature);
    }

    /**
     * Constructs a {@code StoredFunction} object without arguments/signature.
     *
     * @param functionName procedure/function name.
     * @return {@code StoredFunction}
     */
    @Nonnull
    public static StoredFunction ofNoArgs(@Nonnull final String functionName) {
        return new StoredFunction(functionName, "");
    }

    /**
     * Constructs a {@code StoredFunction} object without arguments/signature with given context.
     *
     * @param pgContext    the schema context to enrich procedure/function name; must be non-null.
     * @param functionName procedure/function name.
     * @return {@code StoredFunction}
     * @since 0.14.3
     */
    @Nonnull
    public static StoredFunction ofNoArgs(@Nonnull final PgContext pgContext,
                                          @Nonnull final String functionName) {
        return ofNoArgs(PgContext.enrichWith(functionName, pgContext));
    }

    /**
     * Constructs a {@code StoredFunction} object.
     *
     * @param functionName      procedure/function name.
     * @param functionSignature procedure/function signature (arguments).
     * @return {@code StoredFunction}
     */
    @Nonnull
    public static StoredFunction of(@Nonnull final String functionName,
                                    @Nonnull final String functionSignature) {
        return new StoredFunction(functionName, functionSignature);
    }

    /**
     * Constructs a {@code StoredFunction} object with given context.
     *
     * @param pgContext         the schema context to enrich procedure/function name; must be non-null.
     * @param functionName      procedure/function name.
     * @param functionSignature procedure/function signature (arguments).
     * @return {@code StoredFunction}
     * @since 0.14.3
     */
    @Nonnull
    public static StoredFunction of(@Nonnull final PgContext pgContext,
                                    @Nonnull final String functionName,
                                    @Nonnull final String functionSignature) {
        return of(PgContext.enrichWith(functionName, pgContext), functionSignature);
    }
}

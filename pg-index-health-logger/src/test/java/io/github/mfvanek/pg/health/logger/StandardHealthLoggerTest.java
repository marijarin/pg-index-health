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

import io.github.mfvanek.pg.core.checks.common.Diagnostic;

import javax.annotation.Nonnull;

class StandardHealthLoggerTest extends HealthLoggerTestBase {

    @Nonnull
    @Override
    protected HealthLogger getHealthLogger() {
        return new StandardHealthLogger(getConnectionCredentials(), getConnectionFactory(), DatabaseChecksOnCluster::new);
    }

    @Nonnull
    @Override
    protected String[] getExpectedValue() {
        return new String[]{
            "invalid_indexes:1",
            "duplicated_indexes:2",
            "foreign_keys_without_index:8",
            "tables_without_primary_key:5",
            "indexes_with_null_values:1",
            "bloated_indexes:19",
            "bloated_tables:4",
            "intersected_indexes:12",
            "unused_indexes:14",
            "tables_with_missing_indexes:0",
            "tables_without_description:16",
            "columns_without_description:41",
            "columns_with_json_type:1",
            "columns_with_serial_types:3",
            "functions_without_description:3",
            "indexes_with_boolean:1",
            "not_valid_constraints:3",
            "btree_indexes_on_array_columns:2",
            "sequence_overflow:3",
            "primary_keys_with_serial_types:2",
            "duplicated_foreign_keys:3",
            "intersected_foreign_keys:1",
            "possible_object_name_overflow:2",
            "tables_not_linked_to_others:5",
            "foreign_keys_with_unmatched_column_type:2",
            "tables_with_zero_or_one_column:6",
            "objects_not_following_naming_convention:15",
            "columns_not_following_naming_convention:5",
            "primary_keys_with_varchar:3",
            "columns_with_fixed_length_varchar:17",
            "indexes_with_unnecessary_where_clause:2"
        };
    }

    @Nonnull
    @Override
    protected String getExpectedValueForDefaultSchema(@Nonnull final Diagnostic diagnostic) {
        final LoggingKey key = SimpleLoggingKeyAdapter.of(diagnostic);
        return key.getSubKeyName() + ":0";
    }
}

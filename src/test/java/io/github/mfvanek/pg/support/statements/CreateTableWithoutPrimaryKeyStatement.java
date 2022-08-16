/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateTableWithoutPrimaryKeyStatement extends AbstractDbStatement {

    public CreateTableWithoutPrimaryKeyStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create table if not exists %s.bad_clients (" +
                "id bigint not null, " +
                "name varchar(255) not null," +
                "real_client_id bigint)", schemaName));
        final String checkQuery = String.format("select exists (%n" +
                "   select 1 %n" +
                "   from pg_catalog.pg_class c%n" +
                "   join pg_catalog.pg_namespace n on n.oid = c.relnamespace%n" +
                "   where n.nspname = '%s'%n" +
                "   and c.relname = '%s'%n" +
                "   and c.relkind = 'r'%n" +
                "   );", schemaName, "bad_clients");
        try (ResultSet rs = statement.executeQuery(checkQuery)) {
            if (rs.next()) {
                final boolean schemaExists = rs.getBoolean(1);
                if (schemaExists) {
                    return;
                }
            }
            throw new IllegalStateException("Table with name 'bad_clients' in schema " + schemaName + " wasn't created");
        }
    }
}

/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.object.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatedIndexesTest {

    @Test
    void withTheSameTable() {
        final DuplicatedIndexes index = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t", "i1", 101L),
            IndexWithSize.of("t", "i2", 202L)));
        assertThat(index).isNotNull();
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getTotalSize()).isEqualTo(303L);
        assertThat(index.getIndexNames())
            .hasSize(2)
            .containsExactly("i1", "i2")
            .isUnmodifiable();
        assertThat(index.getName())
            .isEqualTo("i1,i2");
        assertThat(index.getObjectType())
            .isEqualTo(PgObjectType.INDEX);
    }

    @Test
    void ordering() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t1", "i3", 303L),
            IndexWithSize.of("t1", "i1", 101L),
            IndexWithSize.of("t1", "i2", 202L)));
        assertThat(indexes).isNotNull();
        assertThat(indexes.getTotalSize()).isEqualTo(606L);

        assertThat(indexes.getDuplicatedIndexes())
            .hasSize(3)
            .containsExactly(
                IndexWithSize.of("t1", "i1", 101L),
                IndexWithSize.of("t1", "i2", 202L),
                IndexWithSize.of("t1", "i3", 303L))
            .isUnmodifiable();
        assertThat(indexes.getIndexNames())
            .hasSize(3)
            .containsExactly("i1", "i2", "i3")
            .isUnmodifiable();
    }

    @Test
    void shouldCreateDefensiveCopyOfIndexesList() {
        final List<IndexWithSize> sourceIndexes = new ArrayList<>(List.of(
            IndexWithSize.of("t1", "i3", 303L),
            IndexWithSize.of("t1", "i1", 101L),
            IndexWithSize.of("t1", "i2", 202L)));
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(sourceIndexes);

        final IndexWithSize fourth = IndexWithSize.of("t1", "i4", 404L);
        sourceIndexes.add(fourth);

        assertThat(indexes.getDuplicatedIndexes())
            .hasSize(3)
            .doesNotContain(fourth)
            .isUnmodifiable();

        assertThat(indexes.getIndexNames())
            .hasSize(3)
            .doesNotContain("i4")
            .isUnmodifiable();
    }

    @Test
    void testToString() {
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t", "i3", 303L),
            IndexWithSize.of("t", "i1", 101L),
            IndexWithSize.of("t", "i2", 202L)));
        assertThat(indexes)
            .hasToString("DuplicatedIndexes{tableName='t', totalSize=606, indexes=[" + "IndexWithSize{tableName='t', indexName='i1', indexSizeInBytes=101}, " +
                "IndexWithSize{tableName='t', indexName='i2', indexSizeInBytes=202}, " + "IndexWithSize{tableName='t', indexName='i3', indexSizeInBytes=303}]}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withoutIndexes() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("duplicatedIndexes cannot be null");

        final List<IndexWithSize> firstIndexes = List.of();
        assertThatThrownBy(() -> DuplicatedIndexes.of(firstIndexes))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows cannot be empty");

        final List<IndexWithSize> secondIndexes = List.of(IndexWithSize.of("t", "i", 1L));
        assertThatThrownBy(() -> DuplicatedIndexes.of(secondIndexes))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rows should contains at least two items");
    }

    @Test
    void withDifferentTables() {
        final List<IndexWithSize> indexWithSizeList = List.of(
            IndexWithSize.of("t1", "i1", 1L),
            IndexWithSize.of("t2", "i2", 2L));
        assertThatThrownBy(() -> DuplicatedIndexes.of(indexWithSizeList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void fromValidString() {
        final DuplicatedIndexes index = DuplicatedIndexes.of("t", "idx=i3, size=11; idx=i4, size=167");
        assertThat(index).isNotNull();
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getTotalSize()).isEqualTo(178L);
        assertThat(index.getIndexNames())
            .hasSize(2)
            .containsExactly("i3", "i4")
            .isUnmodifiable();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void fromInvalidString() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "i")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> DuplicatedIndexes.of("t", "idx=i1, size=1")).isInstanceOf(IllegalArgumentException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final DuplicatedIndexes first = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t1", "i1", 101L),
            IndexWithSize.of("t1", "i2", 202L)));
        final DuplicatedIndexes second = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t1", "i3", 301L),
            IndexWithSize.of("t1", "i4", 402L)));
        final DuplicatedIndexes third = DuplicatedIndexes.of(List.of(
            IndexWithSize.of("t2", "i5", 101L),
            IndexWithSize.of("t2", "i6", 202L)));

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        final DuplicatedIndexes theSame = DuplicatedIndexes.of(
            IndexWithSize.of("t1", "i2", 505L), // different order
            IndexWithSize.of("t1", "i1", 606L) // different size
        );
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(second)
            .doesNotHaveSameHashCodeAs(second);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(DuplicatedIndexes.class)
            .withIgnoredFields("totalSize", "indexesNames")
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void newFactoryConstructor() {
        assertThatThrownBy(() -> DuplicatedIndexes.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");

        final IndexWithSize indexWithSize = IndexWithSize.of("t", "i1", 1L);
        assertThatThrownBy(() -> DuplicatedIndexes.of(indexWithSize, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("secondObject cannot be null");

        final IndexWithSize firstIndex = IndexWithSize.of("t", "i1", 1L);
        final IndexWithSize secondIndex = IndexWithSize.of("t", "i2", 2L);
        final IndexWithSize fourthIndex = IndexWithSize.of("t", "i4", 4L);
        assertThatThrownBy(() -> DuplicatedIndexes.of(firstIndex, secondIndex, null, fourthIndex))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("otherObjects cannot contain nulls");
        final DuplicatedIndexes indexes = DuplicatedIndexes.of(
            IndexWithSize.of("t", "i3", 3L),
            IndexWithSize.of("t", "i1", 1L),
            IndexWithSize.of("t", "i2", 2L),
            IndexWithSize.of("t", "i4", 4L));
        assertThat(indexes).isNotNull();
        assertThat(indexes.getDuplicatedIndexes())
            .hasSize(4)
            .containsExactly(
                IndexWithSize.of("t", "i1", 1L),
                IndexWithSize.of("t", "i2", 2L),
                IndexWithSize.of("t", "i3", 3L),
                IndexWithSize.of("t", "i4", 4L))
            .isUnmodifiable();
    }
}

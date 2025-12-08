package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.RelationshipType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipTypeTest {

    @Test
    void stringToRelationshipType_exactMatches() {
        assertEquals(RelationshipType.AGGREGATION,
                RelationshipType.stringToRelationshipType("aggregation"));

        assertEquals(RelationshipType.COMPOSITION,
                RelationshipType.stringToRelationshipType("composition"));

        assertEquals(RelationshipType.GENERALIZATION,
                RelationshipType.stringToRelationshipType("generalization"));

        assertEquals(RelationshipType.REALIZATION,
                RelationshipType.stringToRelationshipType("realization"));
    }

    @Test
    void stringToRelationshipType_caseInsensitive() {
        assertEquals(RelationshipType.AGGREGATION,
                RelationshipType.stringToRelationshipType("AgGrEgAtIoN"));

        assertEquals(RelationshipType.COMPOSITION,
                RelationshipType.stringToRelationshipType("CoMpOsItIoN"));
    }

    @Test
    void stringToRelationshipType_invalidDefaultsToOther() {
        assertEquals(RelationshipType.OTHER,
                RelationshipType.stringToRelationshipType("notatype"));

        assertEquals(RelationshipType.OTHER,
                RelationshipType.stringToRelationshipType(""));

        assertEquals(RelationshipType.OTHER,
                RelationshipType.stringToRelationshipType("12345"));
    }

    @Test
    void stringToRelationshipType_nullInput_defaultsToOther() {
        assertEquals(RelationshipType.OTHER,
                RelationshipType.stringToRelationshipType(null));
    }

    @Test
    void enumValues_areCorrect() {
        RelationshipType[] values = RelationshipType.values();

        assertEquals(5, values.length);
        assertTrue(List.of(values).contains(RelationshipType.AGGREGATION));
        assertTrue(List.of(values).contains(RelationshipType.COMPOSITION));
        assertTrue(List.of(values).contains(RelationshipType.GENERALIZATION));
        assertTrue(List.of(values).contains(RelationshipType.REALIZATION));
        assertTrue(List.of(values).contains(RelationshipType.OTHER));
    }
}

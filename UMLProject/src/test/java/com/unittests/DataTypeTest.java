package com.unittests;

import org.junit.jupiter.api.Test;
import org.umlproject.DataType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTypeTest {

    @Test
    void nullInputReturnsOther() {
        assertEquals(DataType.OTHER,
                DataType.stringToDatatype(null),
                "Null input should map to OTHER");
    }

    @Test
    void primitiveNamesMapToCorrectDataTypes() {
        assertEquals(DataType.BYTE, DataType.stringToDatatype("byte"));
        assertEquals(DataType.SHORT, DataType.stringToDatatype("short"));
        assertEquals(DataType.INT, DataType.stringToDatatype("int"));
        assertEquals(DataType.LONG, DataType.stringToDatatype("long"));
        assertEquals(DataType.FLOAT, DataType.stringToDatatype("float"));
        assertEquals(DataType.DOUBLE, DataType.stringToDatatype("double"));
        assertEquals(DataType.CHAR, DataType.stringToDatatype("char"));
        assertEquals(DataType.BOOLEAN, DataType.stringToDatatype("boolean"));
    }

    @Test
    void synonymNamesMapToCorrectDataTypes() {
        assertEquals(DataType.INT, DataType.stringToDatatype("integer"));
        assertEquals(DataType.CHAR, DataType.stringToDatatype("character"));
        assertEquals(DataType.BOOLEAN, DataType.stringToDatatype("bool"));
    }

    @Test
    void inputIsCaseInsensitive() {
        assertEquals(DataType.INT, DataType.stringToDatatype("InTeGeR"));
        assertEquals(DataType.BOOLEAN, DataType.stringToDatatype("BoOl"));
        assertEquals(DataType.CHAR, DataType.stringToDatatype("ChArAcTeR"));
    }

    @Test
    void unknownStringsMapToOther() {
        assertEquals(DataType.OTHER, DataType.stringToDatatype("string"));
        assertEquals(DataType.OTHER, DataType.stringToDatatype(""));
        assertEquals(DataType.OTHER, DataType.stringToDatatype("nope"));
    }
}

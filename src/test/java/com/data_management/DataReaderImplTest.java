package com.data_management;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DataReaderImplTest {

    @Test
    void testReadDataPopulatesDataStorage() {
        // 1) Prepare fake stdin with two lines + blank
        String input =
                "1,100.0,systolic,1714376789000\n" +
                        "2,200.0,diastolic,1714376789001\n" +
                        "\n";

        InputStream oldIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // 2) Reset and use the singleton storage
        DataStorage storage = DataStorage.getInstance();
        storage.clear();

        // 3) Run the parser
        DataParser parser = new DataParser();
        parser.readData(storage);

        // 4) Restore stdin
        System.setIn(oldIn);

        // 5) Assert two distinct patients were created
        assertEquals(2, storage.getAllPatients().size(),
                "Should detect exactly two distinct patients");
    }
}

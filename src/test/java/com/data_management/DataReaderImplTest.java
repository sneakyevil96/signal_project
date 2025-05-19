package com.data_management;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DataReaderImplTest {

    @Test
    void testReadDataPopulatesDataStorage() {
        // Simulate two patient lines, then a blank line to stop
        String input =
                "1,100.0,systolic,1714376789000\n" +
                        "2,200.0,diastolic,1714376789001\n" +
                        "\n";

        // Backup & replace System.in
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        // Run parser
        DataStorage storage = new DataStorage(null);
        DataParser parser     = new DataParser();
        parser.readData(storage);

        // Restore System.in
        System.setIn(originalIn);

        // Verify we saw two distinct patients
        assertNotNull(storage, "Storage should not be null");
        assertEquals(
                2,
                storage.getAllPatients().size(),
                "Should detect exactly two distinct patients"
        );
    }
}

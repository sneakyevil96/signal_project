package com.data_management;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @BeforeEach
    void setUp() {
        DataStorage.getInstance().clear();
    }

    @AfterEach
    void cleanup() throws IOException {
        // Clean up any test files
        // ... file cleanup code
    }

    @Test
    void testReadEmptyFile() throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("empty", ".csv");
            DataStorage storage = DataStorage.getInstance();
            new DataParser().readData(storage, tempFile.toString());
            assertTrue(storage.getAllPatients().isEmpty());
        } finally {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    @Test
    void testInvalidFormatFile() throws IOException {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("invalid", ".csv");
            Files.writeString(tempFile, "invalid,data,string");
            DataStorage storage = DataStorage.getInstance();

            Path finalTempFile = tempFile;
            assertThrows(IllegalArgumentException.class, () -> {
                new DataParser().readData(storage, finalTempFile.toString());
            });

            assertTrue(storage.getAllPatients().isEmpty(),
                    "Storage should not contain any data after invalid input");
        } finally {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
    }

}

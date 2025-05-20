
package com.data_management;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataReaderTest {

    private DataStorage storage;
    private WebSocketDataReader reader;

    @BeforeEach
    public void setUp() {
        storage = DataStorage.getInstance();
        storage.clear();
        reader = new WebSocketDataReader();
    }

    @AfterEach
    public void tearDown() {
        reader.stop(); // stop WebSocket and cleanup
    }

    @Test
    public void testMalformedMessageIgnored() {
        String badMessage = "bad,data,string";
        readerTestHook(reader, badMessage);
        assertTrue(storage.getAllPatients().isEmpty(), "Storage should be empty after malformed input");
    }

    @Test
    public void testValidMessageParsedCorrectly() {
        String validMessage = "101,1716211234,HeartRate,75.5";
        readerTestHook(reader, validMessage);
        assertFalse(storage.getAllPatients().isEmpty(), "Storage should contain a patient");
        assertEquals(1, storage.getAllPatients().size(), "Should be exactly one patient");
    }

    // A testing hook method that simulates receiving a WebSocket message
    private void readerTestHook(WebSocketDataReader reader, String message) {
        reader.handleMessage(message, storage);
    }
}

package com.data_management;
import java.io.IOException;

/**
 * Defines the contract for reading patient data into DataStorage.
 * Supports both batch (file) reading and real-time (WebSocket) streaming.
 */
public interface DataReader {

    /**
     * Read all available data from a static source (e.g., files in an output directory)
     * and store it into the provided DataStorage.
     *
     * @param storage target DataStorage to populate
     * @throws IOException on I/O errors
     */
    void readData(DataStorage storage) throws IOException;

    /**
     * Connect to a WebSocket server and continuously consume incoming
     * CSV‐formatted patient records, storing each one into DataStorage.
     *
     * <p>Each message is expected as:
     * <code>patientId,timestamp,recordType,measurement</code></p>
     *
     * @param storage       target DataStorage to populate
     * @param websocketUri  full URI of the WebSocket endpoint (e.g. "ws://localhost:8080")
     * @throws IOException  on connection or I/O errors
     */
    void streamData(DataStorage storage, String websocketUri) throws IOException;
}

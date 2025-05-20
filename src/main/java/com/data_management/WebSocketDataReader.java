package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Reads patient data in real‐time from a WebSocket server and stores it into DataStorage.
 * Includes automatic reconnect on failure with exponential backoff.
 */
public class WebSocketDataReader implements DataReader {

    private WebSocketClient client;
    private final ScheduledExecutorService retryScheduler =
            Executors.newSingleThreadScheduledExecutor();
    private int attempt = 0;
    private URI endpoint;

    @Override
    public void readData(DataStorage storage) {
        throw new UnsupportedOperationException(
                "Batch file reading not supported by WebSocketDataReader! Use FileDataReader instead.");
    }

    @Override
    public void streamData(DataStorage storage, String websocketUri) throws IOException {
        try {
            endpoint = new URI(websocketUri);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI " + websocketUri, e);
        }
        connect(storage);
    }

    private void connect(DataStorage storage) {
        client = new WebSocketClient(endpoint) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket at " + endpoint);
                attempt = 0; // reset backoff
            }

            @Override
            public void onMessage(String message) {
                handleMessage(message, storage);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.err.println("WebSocket closed: " + reason + " (code " + code + ")");
                scheduleReconnect(storage);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
                // errors also trigger onClose, so reconnection will follow
            }
        };

        client.connect();
    }

    /**
     * Parses and stores a single incoming message. Used for testability.
     */
    protected void handleMessage(String message, DataStorage storage) {
        String[] parts = message.split(",", 4);
        if (parts.length < 4) {
            System.err.println("Malformed message: " + message);
            return;
        }
        try {
            int patientId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            String recordType = parts[2];
            double measurement = Double.parseDouble(parts[3]);
            if (!"Alert".equalsIgnoreCase(recordType)) {
                storage.addPatientData(patientId, measurement, recordType, timestamp);
            }
        } catch (Exception e) {
            System.err.println("Error parsing message '" + message + "': " + e.getMessage());
        }
    }

    private void scheduleReconnect(DataStorage storage) {
        attempt++;
        long delay = Math.min(60, (long) Math.pow(2, attempt));
        System.out.printf("Scheduling reconnect in %d seconds…%n", delay);

        if (!retryScheduler.isShutdown()) {
            retryScheduler.schedule(() -> connect(storage), delay, TimeUnit.SECONDS);
        }
    }

    /** Call to cleanly shut down the WebSocket connection and retries. */
    public void stop() {
        retryScheduler.shutdownNow();
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }

}

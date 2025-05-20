package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Connects to a WebSocket server streaming lines of the form:
 * patientId,timestamp,label,value
 * and pushes each parsed record into the provided DataStorage.
 */
public class RealTimeDataClient extends WebSocketClient implements LiveDataReader {
    private final ScheduledExecutorService reconnectScheduler = Executors.newSingleThreadScheduledExecutor();
    private DataStorage storage;

    public RealTimeDataClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void connect(DataStorage storage) throws Exception {
        this.storage = storage;
        this.connectBlocking(); // synchronous open
    }

    @Override
    public void disconnect() throws Exception {
        this.closeBlocking();
        reconnectScheduler.shutdownNow();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("RealTimeDataClient: connected to " + getURI());
    }

    @Override
    public void onMessage(String message) {
        // expected: "123,1616161616161,SystolicPressure,120.0"
        try {
            String[] parts = message.split(",", 4);
            if (parts.length != 4) {
                throw new IllegalArgumentException("Bad format");
            }
            int    patientId = Integer.parseInt(parts[0]);
            long   timestamp = Long.parseLong(parts[1]);
            String label     = parts[2];
            double value     = Double.parseDouble(parts[3]);

            storage.addPatientData(patientId, value, label, timestamp);
        } catch (Exception e) {
            System.err.println("RealTimeDataClient: failed to parse message → " + message);
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("RealTimeDataClient: connection lost (" + reason + "), scheduling reconnect...");
        // simple back‐off retry
        reconnectScheduler.schedule(() -> {
            try {
                if (!isOpen()) {
                    this.reconnectBlocking();
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("RealTimeDataClient: websocket error");
        ex.printStackTrace();
    }
}

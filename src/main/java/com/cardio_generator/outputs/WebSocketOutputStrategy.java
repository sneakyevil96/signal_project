package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;

/**
 * Sends each record as a CSV line over WebSocket to all clients.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    private final WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    @Override
    public void output(int patientId, long timestamp, String label, double data) {
        // Convert the numeric 'data' back to string for transmission
        String message = String.format("%d,%d,%s,%s",
                patientId,
                timestamp,
                label,
                Double.toString(data)
        );

        // Broadcast to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
        }
    }

    private static class SimpleWebSocketServer extends WebSocketServer {
        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("WebSocket server started successfully");
        }
    }
}

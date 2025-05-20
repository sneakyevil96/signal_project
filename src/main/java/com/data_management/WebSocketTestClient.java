package com.data_management;

public class WebSocketTestClient {
    public static void main(String[] args) throws Exception {
        System.out.println("=========== Starting WebSocketDataReader client ===========");

        DataStorage storage = DataStorage.getInstance();
        storage.clear();

        WebSocketDataReader reader = new WebSocketDataReader();
        reader.streamData(storage, "ws://localhost:8080");

        // Wait 10 seconds to receive data
        Thread.sleep(10_000);

        System.out.println("=========== Data received in real-time ===========");

        long now = System.currentTimeMillis();
        for (Patient patient : storage.getAllPatients()) {
            int id = patient.getId();
            int recordCount = patient.getRecords(0, now).size();
            System.out.println("Patient ID: " + id + " | Records received: " + recordCount);
        }

        reader.stop();
        System.out.println("=========== Client stopped ============");
    }
}

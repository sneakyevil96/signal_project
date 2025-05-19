package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy {

    private final String BaseDirectory;
    public final ConcurrentHashMap<String, String> file_map = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {
        this.BaseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, double data) {
        try {
            // Ensure base directory exists
            Files.createDirectories(Paths.get(BaseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        // Compute (or retrieve) the path for this label’s file
        String FilePath = file_map.computeIfAbsent(
                label,
                k -> Paths.get(BaseDirectory, label + ".txt").toString()
        );

        // Append a line with the numeric data
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        Paths.get(FilePath),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                )
        )) {
            out.printf(
                    "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId,
                    timestamp,
                    label,
                    Double.toString(data)
            );
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}

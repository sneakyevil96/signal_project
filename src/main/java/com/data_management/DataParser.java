package com.data_management;

import java.io.IOException;
import java.util.Scanner;

/**
 * A simple batch (stdin) reader.  Does *not* support streaming.
 */
public class DataParser implements DataReader {

    @Override
    public void readData(DataStorage dataStorage) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) break;

            // expected format: id,value,recordType,timestamp
            String[] parts = line.split(",");
            int    id        = Integer.parseInt(parts[0]);
            double value     = Double.parseDouble(parts[1]);
            String type      = parts[2];
            long   timestamp = Long.parseLong(parts[3]);

            dataStorage.addPatientData(id, value, type, timestamp);
        }
    }

    @Override
    public void streamData(DataStorage storage, String websocketUri) throws IOException {
        // batch parser does not support streaming
        throw new UnsupportedOperationException(
                "DataParser only supports batch readData(); use WebSocketDataReader for streaming"
        );
    }
}

package com.data_management;

/**
 * A live data reader that connects to a source of real‐time patient data
 * and feeds it into DataStorage.
 */
public interface LiveDataReader {
    /**
     * Open the connection and begin streaming into storage.
     */
    void connect(DataStorage storage) throws Exception;

    /**
     * Shut down the connection cleanly.
     */
    void disconnect() throws Exception;
}

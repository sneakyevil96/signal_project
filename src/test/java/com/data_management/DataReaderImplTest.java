package com.data_management;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
class DataReaderImplTest {

    @Test
    void testReadDirectoryProducesExpectedPatients() {
        Path sampleDir = Path.of("src", "test", "", "sample-output");

        // Now this will compile without the "abstract" error:
        DataParser parser = new DataParser();
        DataStorage storage = parser.readDirectory(sampleDir);

        assertNotNull(storage, "Should return non-null DataStorage");
        assertEquals(
                2,
                storage.getAllPatients().size(),
                "Should detect exactly two distinct patients"
        );
    }
}

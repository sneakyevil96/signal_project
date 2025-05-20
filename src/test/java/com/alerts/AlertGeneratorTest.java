package com.alerts;
import com.alerts.strategy.AlertStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.*;
/**
 * Unit test for the {@link AlertGenerator} class.
 *
 * This test verifies that the alert generation engine properly invokes
 * all alert strategies for each patient in the data storage.
 */
class AlertGeneratorTest {
    private DataStorage storage;
    private AlertStrategy strategy1;
    private AlertStrategy strategy2;
    private AlertDispatcher dispatcher;
    private AlertGenerator generator;
    private Patient patient;

    /**
     * Sets up the test environment by mocking dependencies and
     * configuring a test patient with two alert strategies.
     */
    @BeforeEach
    void setUp() {
        storage    = mock(DataStorage.class);
        dispatcher = mock(AlertDispatcher.class);

        strategy1 = mock(AlertStrategy.class);
        strategy2 = mock(AlertStrategy.class);

        patient = new Patient(96);
        when(storage.getAllPatients()).thenReturn(List.of(patient));
        generator = new AlertGenerator(storage, Arrays.asList(strategy1, strategy2), dispatcher);
    }
    /**
     * Tests that the {@code evaluateAllPatients()} method calls
     * {@code checkAlert()} on each strategy exactly once for the patient.
     */
    @Test
    void testEvaluateAllPatientsInvokesEveryStrategyOnce() {
        generator.evaluateAllPatients();
        // Assert: each strategy should be invoked exactly once for our single patient
        verify(strategy1, times(1)).checkAlert(patient, dispatcher);
        verify(strategy2, times(1)).checkAlert(patient, dispatcher);
    }
}

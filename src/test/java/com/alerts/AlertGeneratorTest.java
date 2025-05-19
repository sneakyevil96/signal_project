package com.alerts;

import com.alerts.strategy.AlertStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertStrategy strategy1;
    private AlertStrategy strategy2;
    private AlertDispatcher dispatcher;
    private AlertGenerator generator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // 1) mock out the storage and dispatcher
        storage    = mock(DataStorage.class);
        dispatcher = mock(AlertDispatcher.class);

        // 2) create two dummy strategies
        strategy1 = mock(AlertStrategy.class);
        strategy2 = mock(AlertStrategy.class);

        // 3) pretend storage has exactly one patient
        patient = new Patient(96);
        when(storage.getAllPatients()).thenReturn(List.of(patient));

        // 4) instantiate the engine with our mocks
        generator = new AlertGenerator(
                storage,
                Arrays.asList(strategy1, strategy2),
                dispatcher
        );
    }

    @Test
    void testEvaluateAllPatientsInvokesEveryStrategyOnce() {
        // Act
        generator.evaluateAllPatients();

        // Assert: each strategy should be invoked exactly once for our single patient
        verify(strategy1, times(1)).checkAlert(patient, dispatcher);
        verify(strategy2, times(1)).checkAlert(patient, dispatcher);
    }
}

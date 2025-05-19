package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertRule rule1;
    private AlertRule rule2;
    private AlertDispatcher dispatcher;
    private AlertGenerator generator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // 1) mock out the storage and dispatcher
        storage    = mock(DataStorage.class);
        dispatcher = mock(AlertDispatcher.class);

        // 2) create two dummy rules
        rule1 = mock(AlertRule.class);
        rule2 = mock(AlertRule.class);

        // 3) pretend storage has exactly one patient
        patient = new Patient(96);
        when(storage.getAllPatients()).thenReturn(Arrays.asList(patient));

        // 4) instantiate the engine with our mocks
        generator = new AlertGenerator(storage,
                Arrays.asList(rule1, rule2),
                dispatcher);
    }

    @Test
    void testEvaluateAllPatientsInvokesEveryRuleOnce() {
        // Act
        generator.evaluateAllPatients();

        // Assert: each rule should be evaluated exactly once on our single patient
        verify(rule1, times(1)).evaluate(patient, dispatcher);
        verify(rule2, times(1)).evaluate(patient, dispatcher);
    }
}

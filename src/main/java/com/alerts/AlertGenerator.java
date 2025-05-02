package com.alerts;
import com.cardio_generator.outputs.OutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.cardio_generator.generators.ECGDataGenerator;
import java.io.Console;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
/*
Trend Alert: Trigger an alert if the patient's blood pressure (systolic or diastolic) shows a consistent increase or decrease across three consecutive readings where each reading changes by more than 10 mmHg from the last.
Critical Threshold Alert: Trigger an alert if the systolic blood pressure exceeds 180 mmHg or drops below 90 mmHg, or if diastolic blood pressure exceeds 120 mmHg or drops below 60 mmHg.
 */
        ECGDataGenerator ecg = new ECGDataGenerator(1);

        OutputStrategy ConsoleOutputStrategy = new OutputStrategy() {
            @Override
            public void output(int patientId, long timestamp, String label, String data) {
            }
        };
        if(ecg.generate(1, ConsoleOutputStrategy) > 180 || ecg.generate(1, ConsoleOutputStrategy) < 90){
           System.out.println("");
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
    }
}

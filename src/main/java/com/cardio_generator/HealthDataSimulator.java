package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import com.alerts.AlertGenerator;
import com.alerts.AlertDispatcher;
import com.alerts.ConsoleAlertDispatcher;
import com.alerts.strategy.AlertStrategy;
import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.HeartRateStrategy;
import com.alerts.strategy.OxygenSaturationStrategy;

import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

import com.data_management.DataStorage;

public class HealthDataSimulator {

    private static int patientCount = 50;
    private static ScheduledExecutorService scheduler;
    private static OutputStrategy baseOutputStrategy = new ConsoleOutputStrategy();
    private static final Random random = new Random();

    /**
     * Wraps any OutputStrategy so that, in addition to emitting,
     * we also feed all non‐Alert records into our DataStorage.
     */
    private static class CompositeOutputStrategy implements OutputStrategy {
        private final OutputStrategy delegate;
        private final DataStorage storage;

        public CompositeOutputStrategy(OutputStrategy delegate, DataStorage storage) {
            this.delegate  = delegate;
            this.storage   = storage;
        }

        @Override
        public void output(int patientId, long timestamp, String recordType, double measurement) {
            // first emit
            delegate.output(patientId, timestamp, recordType, measurement);
            // then store if it's not an Alert
            if (!"Alert".equalsIgnoreCase(recordType)) {
                storage.addPatientData(patientId, measurement, recordType, timestamp);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        parseArguments(args);

        // 1) Use singleton DataStorage
        DataStorage storage = DataStorage.getInstance();

        // 2) Build our rule engine using the new Strategy pattern
        AlertDispatcher ruleDispatcher = new ConsoleAlertDispatcher();
        List<AlertStrategy> strategies = List.of(
                new BloodPressureStrategy(),
                new HeartRateStrategy(),
                new OxygenSaturationStrategy()
        );
        AlertGenerator ruleEngine = new AlertGenerator(storage, strategies, ruleDispatcher);

        // 3) Composite strategy for data generators
        OutputStrategy compositeStrategy =
                new CompositeOutputStrategy(baseOutputStrategy, storage);

        // 4) Schedule everything
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);
        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);

        scheduleDataTasks(patientIds, compositeStrategy);

        // 5) Evaluate rules on all patients every 30 seconds
        scheduleTask(ruleEngine::evaluateAllPatients, 30, TimeUnit.SECONDS);
    }

    private static void scheduleDataTasks(List<Integer> patientIds, OutputStrategy strategy) {
        ECGDataGenerator            ecgGen = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator satGen = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator   bpGen  = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator     lvlGen = new BloodLevelsDataGenerator(patientCount);

        for (int pid : patientIds) {
            scheduleTask(() -> ecgGen.generate(pid, strategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> satGen.generate(pid, strategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bpGen.generate(pid, strategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> lvlGen.generate(pid, strategy), 2, TimeUnit.MINUTES);
        }
    }

    private static void scheduleTask(Runnable task, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, unit);
    }

    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException ignored) { }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String o = args[++i];
                        if (o.equals("console")) {
                            baseOutputStrategy = new ConsoleOutputStrategy();
                        } else if (o.startsWith("file:")) {
                            Path p = Paths.get(o.substring(5));
                            if (!Files.exists(p)) Files.createDirectories(p);
                            baseOutputStrategy = new FileOutputStrategy(o.substring(5));
                        } else if (o.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(o.substring(10));
                                baseOutputStrategy = new WebSocketOutputStrategy(port);
                            } catch (NumberFormatException ignored) { }
                        } else if (o.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(o.substring(4));
                                baseOutputStrategy = new TcpOutputStrategy(port);
                            } catch (NumberFormatException ignored) { }
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("  -h                       Show help and exit.");
        System.out.println("  --patient-count <count>  Number of patients (default: 50).");
        System.out.println("  --output <type>          Output: console | file:<dir> | websocket:<port> | tcp:<port>");
    }

    private static List<Integer> initializePatientIds(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) ids.add(i);
        return ids;
    }
}

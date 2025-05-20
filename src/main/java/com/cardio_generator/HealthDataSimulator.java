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
import java.util.stream.Collectors;
import com.alerts.AlertGenerator;
import com.alerts.AlertDispatcher;
import com.alerts.ConsoleAlertDispatcher;
import com.alerts.decorator.RepeatedAlertStrategyDecorator;
import com.alerts.decorator.PriorityAlertDispatcherDecorator;
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
import com.data_management.WebSocketDataReader;
import com.data_management.DataReader;
import com.data_management.DataParser;
/**
 * Main simulator that configures and runs the full data generation and alert system.
 */
public class HealthDataSimulator {
    // singleton
    private static final HealthDataSimulator INSTANCE = new HealthDataSimulator();
    private HealthDataSimulator() {}
    public static HealthDataSimulator getInstance() {
        return INSTANCE;
    }

    // instance
    private int patientCount    = 50;
    private String inputMode    = "websocket";     // "batch" or "websocket"
    private String websocketUri = "ws://localhost:8080";
    private ScheduledExecutorService scheduler;
    private OutputStrategy baseOutputStrategy = new ConsoleOutputStrategy();
    private final Random random = new Random();

    /**
     * Wraps any OutputStrategy so that, in addition to emitting,
     * we also feed all non‐Alert records into our DataStorage.
     */
    private class CompositeOutputStrategy implements OutputStrategy {
        private final OutputStrategy delegate;
        private final DataStorage storage;

        CompositeOutputStrategy(OutputStrategy delegate, DataStorage storage) {
            this.delegate  = delegate;
            this.storage   = storage;
        }

        @Override
/**
 * Executes the output operation.
 * @param patientId Patientid.
 * @param timestamp Timestamp.
 * @param recordType Recordtype.
 * @param measurement Measurement.
 */
        public void output(int patientId, long timestamp, String recordType, double measurement) {
            delegate.output(patientId, timestamp, recordType, measurement);
            if (!"Alert".equalsIgnoreCase(recordType)) {
                storage.addPatientData(patientId, measurement, recordType, timestamp);
            }
        }
    }

    //Entry point
    public static void main(String[] args) throws IOException {
        System.out.println(">>> HealthDataSimulator starting up…");
        getInstance().start(args);
    }

    /**
     * Configures everything, optionally ingests batch or real‐time data,
     * then schedules generators and the rule engine.
     */
    public void start(String[] args) throws IOException {
        parseArguments(args);

        // singleton DataStorage
        DataStorage storage = DataStorage.getInstance();

        // ingest data
        if ("batch".equalsIgnoreCase(inputMode)) {
            System.out.println("Batch mode: reading historical data from stdin...");
            DataReader batchReader = new DataParser() {
                @Override
/**
 * Executes the streamData operation.
 * @param s S.
 * @param uri Uri.
 */
                public void streamData(DataStorage s, String uri) {
                    // no op for batch
                }
            };
            batchReader.readData(storage);
        }
        else if ("websocket".equalsIgnoreCase(inputMode)) {
            System.out.println("WebSocket mode: connecting to " + websocketUri);
            //WebSocketDataReader wsReader = new WebSocketDataReader();
            //wsReader.streamData(storage, websocketUri);
        }
        else {
            System.err.println("Unknown input mode '" + inputMode + "', expected batch|websocket");
            System.exit(1);
        }

        // Build rule engine with Strategy + Decorator
        AlertDispatcher baseDispatcher = new ConsoleAlertDispatcher();
        AlertDispatcher priorityDispatcher = new PriorityAlertDispatcherDecorator(baseDispatcher, "HIGH");

        List<AlertStrategy> rawStrategies = List.of(new BloodPressureStrategy(), new HeartRateStrategy(), new OxygenSaturationStrategy());
        List<AlertStrategy> strategies = rawStrategies.stream().map(s -> new RepeatedAlertStrategyDecorator(s, 10, TimeUnit.MINUTES)).collect(Collectors.toList());

        AlertGenerator ruleEngine = new AlertGenerator(storage, strategies, priorityDispatcher);

        // Composite for data persistence
        OutputStrategy compositeStrategy = new CompositeOutputStrategy(baseOutputStrategy, storage);

        // Schedule our simulated data generators (optional)
        System.out.printf("Scheduling data generators for %d patients%n", patientCount);
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);
        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);
        scheduleDataTasks(patientIds, compositeStrategy);

        // Schedule rule evaluation every 30 seconds
        scheduleTask(ruleEngine::evaluateAllPatients, 30, TimeUnit.SECONDS);
    }

    private void scheduleDataTasks(List<Integer> patientIds, OutputStrategy strategy) {
        ECGDataGenerator ecgGen = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator satGen = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bpGen = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator lvlGen = new BloodLevelsDataGenerator(patientCount);

        for (int pid : patientIds) {
            scheduleTask(() -> ecgGen.generate(pid, strategy),1, TimeUnit.SECONDS);
            scheduleTask(() -> satGen.generate(pid, strategy),1, TimeUnit.SECONDS);
            scheduleTask(() -> bpGen.generate(pid, strategy),1, TimeUnit.MINUTES);
            scheduleTask(() -> lvlGen.generate(pid, strategy),2, TimeUnit.MINUTES);
        }
    }

    private void scheduleTask(Runnable task, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, unit);
    }

    private void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i+1 < args.length) {
                        patientCount = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--input":
                    if (i+1 < args.length) {
                        inputMode = args[++i];
                    }
                    break;
                case "--ws-uri":
                    if (i+1 < args.length) {
                        websocketUri = args[++i];
                    }
                    break;
                case "--output":
                    if (i+1 < args.length) {
                        String o = args[++i];
                        if (o.equals("console")) {
                            baseOutputStrategy = new ConsoleOutputStrategy();
                        } else if (o.startsWith("file:")) {
                            Path p = Paths.get(o.substring(5));
                            if (!Files.exists(p)) Files.createDirectories(p);
                            baseOutputStrategy = new FileOutputStrategy(o.substring(5));
                        } else if (o.startsWith("websocket:")) {
                            int port = Integer.parseInt(o.substring(10));
                            baseOutputStrategy = new WebSocketOutputStrategy(port);
                        } else if (o.startsWith("tcp:")) {
                            int port = Integer.parseInt(o.substring(4));
                            baseOutputStrategy = new TcpOutputStrategy(port);
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

    private void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("  -h                         Show help and exit.");
        System.out.println("  --patient-count <count>    Number of patients (default: 50).");
        System.out.println("  --input <batch|websocket>  Ingest mode (default: websocket).");
        System.out.println("  --ws-uri <uri>             WebSocket URI (default: ws://localhost:8080).");
        System.out.println("  --output <type>            Output: console | file:<dir> | websocket:<port> | tcp:<port>");
    }

    private List<Integer> initializePatientIds(int count) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) ids.add(i);
        return ids;
    }
}

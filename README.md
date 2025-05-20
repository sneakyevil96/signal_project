
# Cardio Data Simulator.

The Cardio Data Simulator is a Java-based application designed to simulate real-time cardiovascular data for multiple patients. This tool is particularly useful for educational purposes, enabling students to interact with real-time data streams of ECG, blood pressure, blood saturation, and other cardiovascular signals.

## Features.

- Simulate real-time ECG, blood pressure, blood saturation, and blood levels data.
- Supports multiple output strategies:
    - Console output for direct observation.
    - File output for data persistence.
    - WebSocket and TCP output for networked data streaming.
- Configurable patient count and data generation rate.
- Randomized patient ID assignment for simulated data diversity.
- Real-time WebSocket streaming with live data ingestion support.

## Getting started.

### Prerequisites.

- Java JDK 11 or newer.
- Maven for managing dependencies and compiling the application.

### Installation.

1. Clone the repository:

   ```sh
   git clone https://github.com/sneakyevil96/signal_project.git
   ```

2. Navigate to the project directory:

   ```sh
   cd signal_project
   ```

3. Compile and package the application using Maven:

   ```sh
   mvn clean package
   ```

   This step compiles the source code and packages the application into an executable JAR file located in the `target/` directory.

### Running the simulator.

After packaging, you can run the simulator directly from the executable JAR:

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar
```

To run with specific options (e.g., to set the patient count and choose an output strategy):

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar --patient-count 100 --output file:./output
```

### Supported output options.

- `console`: Directly prints the simulated data to the console.
- `file:<directory>`: Saves the simulated data to files within the specified directory.
- `websocket:<port>`: Streams the simulated data to WebSocket clients connected to the specified port.
- `tcp:<port>`: Streams the simulated data to TCP clients connected to the specified port.

---

## Real time WebSocket integration.

As part of our Software Engineering assignment, we extended the simulator to support **real-time data ingestion using WebSocket APIs**.

### Implementation highlights.

- Implemented `WebSocketDataReader` as a real-time data consumer
- Extended `DataReader` to support live streaming mode
- Developed `WebSocketTestClient` to test and validate real-time ingestion
- Added robust error handling (malformed data, connection loss, retries)
- Confirmed correct data flow from generator → server → client → storage

### Verification.

We ran a live integration test with:
- 50 patients
- 20 real-time records each
- WebSocketTestClient showing real-time ingestion
- All functionality validated with logs and screenshots

See `screenshots/real_time_client_output.png` for proof of working integration.

---

## UML models.

This project includes UML class diagrams for the Cardiovascular Health Monitoring System (CHMS), modeled as part of a software engineering assignment.

You can find the UML models here: [UML_models/](./UML_models)

Each of the following subsystems is modeled with a class diagram and a design explanation:

- **Alert Generation System** – Monitors patient data and triggers alerts based on thresholds.
- **Data Storage System** – Stores and retrieves timestamped patient health data securely.
- **Patient Identification System** – Matches incoming data with hospital patient records.
- **Data Access Layer** – Receives and parses real-time input from TCP, WebSocket, or file sources.

These models follow standard UML conventions and are designed with modularity and scalability in mind.

---

## License.

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Project members.

- Student ID: 6406968 (Stoica Vlad-Cristian)
- Student ID: 6407068 (Soroceanu Daria-Maria)

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// utils/LoggerUtil.java
class LoggerUtil {
    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String message, Throwable e) {
        logger.severe(message + " - " + e.getMessage());
    }
}

// models/RocketStage.java
class RocketStage {
    private int stageNumber;
    private double fuelPercentage;
    private double altitude;
    private double speed;

    public RocketStage(int stageNumber) {
        this.stageNumber = stageNumber;
        this.fuelPercentage = 100.0;
        this.altitude = 0.0;
        this.speed = 0.0;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public double getFuelPercentage() {
        return fuelPercentage;
    }

    public void setFuelPercentage(double fuelPercentage) {
        this.fuelPercentage = fuelPercentage;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

// models/Rocket.java
class Rocket {
    private RocketStage currentStage;
    private boolean systemsCheckComplete;

    public Rocket() {
        this.currentStage = new RocketStage(1);
        this.systemsCheckComplete = false;
    }

    public RocketStage getCurrentStage() {
        return currentStage;
    }

    public void nextStage() {
        currentStage = new RocketStage(currentStage.getStageNumber() + 1);
    }

    public boolean isSystemsCheckComplete() {
        return systemsCheckComplete;
    }

    public void setSystemsCheckComplete(boolean systemsCheckComplete) {
        this.systemsCheckComplete = systemsCheckComplete;
    }
}

// services/SimulationService.java
class SimulationService {
    private Rocket rocket;

    public SimulationService(Rocket rocket) {
        this.rocket = rocket;
    }

    public void performPreLaunchChecks() {
        rocket.setSystemsCheckComplete(true);
        LoggerUtil.logInfo("All systems are 'Go' for launch.");
        System.out.println("All systems are 'Go' for launch.");
    }

    public void launch() {
        if (!rocket.isSystemsCheckComplete()) {
            LoggerUtil.logInfo("Pre-launch checks not complete.");
            System.out.println("Pre-launch checks not complete.");
            return;
        }

        new Thread(this::simulateLaunch).start();
    }

    private void simulateLaunch() {
        try {
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(1000);
                updateRocketParameters(i);
            }

            rocket.nextStage();
            LoggerUtil.logInfo("Stage 1 complete. Separating stage. Entering Stage 2.");
            System.out.println("Stage 1 complete. Separating stage. Entering Stage 2.");

            for (int i = 1; i <= 10; i++) {
                Thread.sleep(1000);
                updateRocketParameters(i);
            }

            LoggerUtil.logInfo("Orbit achieved! Mission Successful.");
            System.out.println("Orbit achieved! Mission Successful.");

        } catch (InterruptedException e) {
            LoggerUtil.logError("Simulation interrupted", e);
        }
    }

    private void updateRocketParameters(int timeStep) {
        RocketStage stage = rocket.getCurrentStage();
        double fuelConsumed = stage.getStageNumber() == 1 ? 10.0 : 5.0;
        double altitudeIncrease = stage.getStageNumber() == 1 ? 10.0 : 20.0;
        double speedIncrease = stage.getStageNumber() == 1 ? 1000.0 : 2000.0;

        stage.setFuelPercentage(stage.getFuelPercentage() - fuelConsumed);
        stage.setAltitude(stage.getAltitude() + altitudeIncrease);
        stage.setSpeed(stage.getSpeed() + speedIncrease);

        if (stage.getFuelPercentage() <= 0) {
            LoggerUtil.logInfo("Mission Failed due to insufficient fuel.");
            System.out.println("Mission Failed due to insufficient fuel.");
            return;
        }

        LoggerUtil.logInfo(String.format("Stage: %d, Fuel: %.2f%%, Altitude: %.2f km, Speed: %.2f km/h",
                stage.getStageNumber(), stage.getFuelPercentage(), stage.getAltitude(), stage.getSpeed()));
        System.out.println(String.format("Stage: %d, Fuel: %.2f%%, Altitude: %.2f km, Speed: %.2f km/h",
                stage.getStageNumber(), stage.getFuelPercentage(), stage.getAltitude(), stage.getSpeed()));
    }

    public void fastForward(int seconds) {
        try {
            for (int i = 0; i < seconds; i++) {
                Thread.sleep(10);  // Simulate fast forwarding
                updateRocketParameters(i);
            }
        } catch (InterruptedException e) {
            LoggerUtil.logError("Fast forward interrupted", e);
        }
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        Rocket rocket = new Rocket();
        SimulationService simulationService = new SimulationService(rocket);

        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("start_checks")) {
                simulationService.performPreLaunchChecks();
            } else if (command.equalsIgnoreCase("launch")) {
                simulationService.launch();
            } else if (command.startsWith("fast_forward")) {
                String[] parts = command.split(" ");
                if (parts.length == 2) {
                    try {
                        int seconds = Integer.parseInt(parts[1]);
                        simulationService.fastForward(seconds);
                    } catch (NumberFormatException e) {
                        LoggerUtil.logError("Invalid number format for fast forward command", e);
                    }
                } else {
                    LoggerUtil.logError("Invalid command format for fast forward", null);
                }
            } else {
                LoggerUtil.logError("Unknown command", null);
            }
        }
    }
}


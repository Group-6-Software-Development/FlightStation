package view;

import controller.Controller;
import controller.IControllerForV;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import simu.entity.Variables;
import simu.framework.Trace;
import simu.framework.Trace.Level;

import java.text.DecimalFormat;
import java.util.List;

/**
 * This class is the GUI for the simulator.
 * It implements the ISimulatorUI interface.
 */
public class SimulatorGUI extends Application implements ISimulatorUI {
    @FXML
    private TableView<Variables> tableView;
    @FXML
    private TableColumn<Variables, String> nameColumn;
    @FXML
    private TableColumn<Variables, Double> avgServiceTimeColumn;
    @FXML
    private TableColumn<Variables, Double> busyTimeColumn;
    @FXML
    private TableColumn<Variables, Integer> customerCountColumn;
    @FXML
    private TableColumn<Variables, Double> leadTimeColumn;
    @FXML
    private TableColumn<Variables, Double> queueLengthColumn;
    @FXML
    private TableColumn<Variables, Double> throughPutColumn;
    @FXML
    private TableColumn<Variables, Double> totalTimeColumn;
    @FXML
    private TableColumn<Variables, Double> utilizationColumn;
    @FXML
    private TableColumn<Variables, Double> waitingTimeColumn;
    @FXML
    private TextField arrivalMean;
    @FXML
    private TextField arrivalVariance;
    @FXML
    private TextField checkInMean;
    @FXML
    private TextField checkInVariance;
    @FXML
    private TextField bagDropMean;
    @FXML
    private TextField bagDropVariance;
    @FXML
    private TextField securityMean;
    @FXML
    private TextField securityVariance;
    @FXML
    private TextField passportMean;
    @FXML
    private TextField passportVariance;
    @FXML
    private TextField ticketInspectionMean;
    @FXML
    private TextField ticketInspectionVariance;
    @FXML
    private TextField time;
    @FXML
    private TextField delay;
    @FXML
    private Button applySettings;
    @FXML
    private Button exitSettings;
    @FXML
    private Button defaultSettings;
    @FXML
    private Button settingsButton;
    @FXML
    private Button speedUpButton;
    @FXML
    private Button slowDownButton;
    @FXML
    private Button startButton;
    @FXML
    private Button resultsButton;
    @FXML
    private Canvas customerCanvas;
    @FXML
    private Label customerCount, result;

    private IControllerForV controller;

    private IVisualization display;

    /**
     * This method is called when the application is started.
     * It initializes the controller.
     * It sets the trace level to INFO.
     */
    @Override
    public void init() {
        Trace.setTraceLevel(Level.INFO);
        controller = new Controller(this);
    }

    /**
     * This method is called when the application is started.
     * It loads the main simulator GUI.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML-Files/Simulator.fxml"));
        Parent root = loader.load();

        time = (TextField) loader.getNamespace().get("time");
        delay = (TextField) loader.getNamespace().get("delay");
        result = (Label) loader.getNamespace().get("result");
        startButton = (Button) loader.getNamespace().get("startButton");
        resultsButton = (Button) loader.getNamespace().get("resultsButton");
        speedUpButton = (Button) loader.getNamespace().get("speedUpButton");
        customerCount = (Label) loader.getNamespace().get("customerCount");
        slowDownButton = (Button) loader.getNamespace().get("slowDownButton");
        customerCanvas = (Canvas) loader.getNamespace().get("customerCanvas");
        settingsButton = (Button) loader.getNamespace().get("settingsButton");

        time.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        });

        delay.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        });

        startButton.setOnAction(event -> {
            if (time.getText().isEmpty() || delay.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please enter a time and a delay.");
                alert.showAndWait();
            } else {
                startButton.setDisable(true);
                settingsButton.setDisable(true);
                controller.startSimulation();
            }
        });

        settingsButton.setOnAction(event -> openSettings());

        speedUpButton.setOnAction(e -> controller.speedUp());
        slowDownButton.setOnAction(e -> controller.slowDown());

        resultsButton.setOnAction(e -> {
            openResults();
            controller.getResults();
        });

        display = new Visualization(customerCanvas);

        stage.setTitle("Simulator");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * @return the time from the GUI.
     */
    @Override
    public double getTime() {
        return Double.parseDouble(time.getText());
    }

    /**
     * @return the delay from the GUI.
     */
    @Override
    public long getDelay() {
        return Long.parseLong(delay.getText());
    }

    /**
     * This method is used to show the end time and the total customer count in the GUI once the simulation is finished.
     *
     * @param time the end time of the simulation.
     */
    @Override
    public void setEndTime(double time) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        this.result.setText(decimalFormat.format(time));
        int totalCustomerCount = controller.getCustomerCount();
        customerCount.setText(String.valueOf(totalCustomerCount));
    }

    /**
     * @return the display.
     */
    @Override
    public IVisualization getVisualization() {
        return display;
    }

    /**
     * This method is used to open the settings GUI.
     */
    @Override
    public void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML-Files/Settings.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            arrivalMean = (TextField) loader.getNamespace().get("arrivalMean");
            arrivalVariance = (TextField) loader.getNamespace().get("arrivalVariance");
            checkInMean = (TextField) loader.getNamespace().get("checkInMean");
            checkInVariance = (TextField) loader.getNamespace().get("checkInVariance");
            bagDropMean = (TextField) loader.getNamespace().get("bagDropMean");
            bagDropVariance = (TextField) loader.getNamespace().get("bagDropVariance");
            securityMean = (TextField) loader.getNamespace().get("securityMean");
            securityVariance = (TextField) loader.getNamespace().get("securityVariance");
            passportMean = (TextField) loader.getNamespace().get("passportMean");
            passportVariance = (TextField) loader.getNamespace().get("passportVariance");
            ticketInspectionMean = (TextField) loader.getNamespace().get("ticketInspectionMean");
            ticketInspectionVariance = (TextField) loader.getNamespace().get("ticketInspectionVariance");
            applySettings = (Button) loader.getNamespace().get("applySettings");
            exitSettings = (Button) loader.getNamespace().get("exitSettings");
            defaultSettings = (Button) loader.getNamespace().get("defaultSettings");

            TextField[] textFields = new TextField[]{
                    arrivalMean, arrivalVariance, checkInMean, checkInVariance, bagDropMean, bagDropVariance,
                    securityMean, securityVariance, passportMean, passportVariance,
                    ticketInspectionMean, ticketInspectionVariance
            };

            for (TextField textField : textFields) {
                textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                    String character = event.getCharacter();
                    if (!character.matches("[0-9]")) {
                        event.consume();
                    }
                });
            }

            setDefaultSettings();

            applySettings.setOnAction(e -> {
                for (TextField textField : textFields) {
                    if (textField.getText().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error");
                        alert.setContentText("Please enter a value for all fields.");
                        alert.showAndWait();
                        return;
                    } else if (textField.getText().startsWith("0")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error");
                        alert.setContentText("Please enter a value greater than 0 for all fields.");
                        alert.showAndWait();
                        return;
                    }
                }

                applySettings();

                applySettings.setStyle("-fx-background-color: green");
                root.setOnMouseMoved(event -> applySettings.setStyle(""));
            });

            defaultSettings.setOnAction(e -> {
                setDefaultSettings();
                applySettings();

                applySettings.setStyle("");
                applySettings.setStyle("-fx-background-color: green");
                root.setOnMouseMoved(event -> applySettings.setStyle(""));
            });

            exitSettings.setOnAction(e -> {
                stage.close();
                applySettings.setStyle("");
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method is used to open the results GUI.
     */
    @Override
    public void openResults() {
        try {
            List<Variables> results = controller.getResults();

            ObservableList<Variables> observableResults = FXCollections.observableArrayList(results);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML-Files/Results.fxml"));
            Parent root = loader.load();

            tableView = (TableView<Variables>) loader.getNamespace().get("tableView");

            nameColumn = (TableColumn<Variables, String>) loader.getNamespace().get("nameColumn");
            avgServiceTimeColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("avgServiceTimeColumn");
            busyTimeColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("busyTimeColumn");
            customerCountColumn = (TableColumn<Variables, Integer>) loader.getNamespace().get("customerCountColumn");
            leadTimeColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("leadTimeColumn");
            queueLengthColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("queueLengthColumn");
            throughPutColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("throughPutColumn");
            totalTimeColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("totalTimeColumn");
            utilizationColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("utilizationColumn");
            waitingTimeColumn = (TableColumn<Variables, Double>) loader.getNamespace().get("waitingTimeColumn");

            nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
            avgServiceTimeColumn.setCellValueFactory(new PropertyValueFactory<>("AvgServiceTime"));
            busyTimeColumn.setCellValueFactory(new PropertyValueFactory<>("BusyTime"));
            customerCountColumn.setCellValueFactory(new PropertyValueFactory<>("ArrivalCount"));
            leadTimeColumn.setCellValueFactory(new PropertyValueFactory<>("LeadTime"));
            queueLengthColumn.setCellValueFactory(new PropertyValueFactory<>("QueueLength"));
            throughPutColumn.setCellValueFactory(new PropertyValueFactory<>("ThroughPut"));
            totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("TotalTime"));
            utilizationColumn.setCellValueFactory(new PropertyValueFactory<>("Utilization"));
            waitingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("WaitingTime"));

            tableView.setItems(observableResults);

            System.out.println("Results");

            Stage stage = new Stage();
            stage.setTitle("Results");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method is used to set the default settings in the settings GUI.
     */
    @Override
    public void setDefaultSettings() {
        arrivalMean.setText("15");
        arrivalVariance.setText("5");

        checkInMean.setText("10");
        checkInVariance.setText("6");

        bagDropMean.setText("10");
        bagDropVariance.setText("10");

        securityMean.setText("15");
        securityVariance.setText("6");

        passportMean.setText("15");
        passportVariance.setText("10");

        ticketInspectionMean.setText("15");
        ticketInspectionVariance.setText("5");
    }

    /**
     * This method is used to apply the settings in the settings GUI.
     */
    @Override
    public void applySettings() {
        TextField[] textFields = {
                this.arrivalMean, this.arrivalVariance,
                this.checkInMean, this.checkInVariance,
                this.bagDropMean, this.bagDropVariance,
                this.securityMean, this.securityVariance,
                this.passportMean, this.passportVariance,
                this.ticketInspectionMean, this.ticketInspectionVariance
        };

        int[] values = new int[textFields.length];

        for (int i = 0; i < textFields.length; i++) {
            values[i] = Integer.parseInt(textFields[i].getText());
        }

        controller.setSettings(values);
    }
}

package com.bipulhf.activitytracker;

import com.bipulhf.activitytracker.classes.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


public class MainController implements Initializable {

    @FXML
    private AnchorPane mainPane;
    String title;
    Timer timer;
    public static boolean isDateChanged = false;
    boolean isStopped, isChanged, isFinished;
    String startTime, endTime;
    static int time = 0, appTime = 0, setTime = 0, lstSize = GetList.getItemSize();
    public static int dateAdjust = 0;
    @FXML
    private TableView<Item> table;
    @FXML
    private TextField setSecond;
    @FXML
    Polygon previous, next;
    ObservableList<Item> data;
    @FXML
    private Button recordButton, whitelistButton;
    @FXML
    public StackedBarChart<String, Number> chart;
    @FXML
    private Label dateLabel, totalTime, welcomeText, timeTicking;
    private ScheduledExecutorService exc;
    @FXML
    private CheckBox focusedMode;
    static ExecutorService pool;
    private void setTableColumns() {

        data = FXCollections.observableArrayList(GetList.ItemList);

        table.getColumns().clear();

        var appNameCol = new TableColumn ("Title");
        var startTimeCol = new TableColumn ("Start Time");
        var endTimeCol = new TableColumn ("End Time");
        var durationCol = new TableColumn ("Duration");

        appNameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("appName"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<Item, String>("endTime"));
        durationCol.setCellValueFactory(new PropertyValueFactory<Item, String>("timerText"));

        appNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        startTimeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        endTimeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        durationCol.prefWidthProperty().bind(table.widthProperty().multiply(0.28));

        appNameCol.setResizable(false);
        appNameCol.setSortable(false);
        endTimeCol.setResizable(false);
        endTimeCol.setSortable(false);
        startTimeCol.setResizable(false);
        startTimeCol.setSortable(false);
        durationCol.setResizable(false);
        durationCol.setSortable(false);

        table.setItems(data);

        Platform.runLater(() -> table.getColumns().addAll(appNameCol, startTimeCol, endTimeCol, durationCol));

    }

    public static String timerInFrame(int time) {
        String timerFrame;
        if(time >= 3600) {
            int mins = time / 60, hours = mins / 60;
            timerFrame = hours + " Hours " + (mins - (hours * 60)) + " Minutes " + (time - (mins * 60)) + " Seconds";
        }
        else if(time >= 60) {
            int mins = time / 60;
            timerFrame =  mins + " Minutes " + (time - (mins * 60)) + " Seconds";
        }
        else timerFrame =  time + " Seconds";
        return timerFrame;
    }

    private void showAlert(String TempTitle) throws InterruptedException {

        String errorMp3 = String.valueOf(getClass().getResource("error.mp3"));
        Media errorSound = new Media(errorMp3);
        MediaPlayer mediaPlayer = new MediaPlayer(errorSound);

        for (String s : Whitelist.whitelistItem) {
            String lowerTitle = TempTitle.toLowerCase();
            if (!lowerTitle.contains(s.toLowerCase())) {
                Platform.runLater(() -> {
                    timeTicking.setText("Timer stopped due to opening non-whitelisted window.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Close the window");
                    alert.setHeaderText("Stop from doing this!!!");
                    alert.setContentText("You're not supposed to open this window.");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    stage.setAlwaysOnTop(true);
                    alert.initOwner((Stage) mainPane.getScene().getWindow());
                    alert.show();
                    mediaPlayer.play();
                    recordButton.setText("Record");
                    focusedMode.setDisable(false);
                    setSecond.setDisable(false);
                    whitelistButton.setDisable(false);
                });
                timer.cancel();
                timer.purge();
                isStopped = true;
                break;
            }
        }
    }

    private void addingItem() {
        try {
            String TempTitle = GetWindow.getTitle().replaceAll("[●,*]", "").trim();
            if (!Objects.equals(title, TempTitle)) isChanged = true;

            if(focusedMode.isSelected() && !TempTitle.equals("Activity Tracker")) {
                showAlert(TempTitle);
                return;
            }

            if ((isChanged || isStopped) && appTime >= setTime) {
                endTime = GetDateTime.getTime();
                String timeFrame = timerInFrame(appTime);

                table.getItems().add(new Item(title, startTime, endTime, appTime, timeFrame));

                GetList.ItemList.add(new Item(title, startTime, endTime, appTime, timeFrame));

                Platform.runLater(() -> totalTime.setText("Total Time : " + timerInFrame(GetList.totalDuration())));

                appTime = 0;

                startTime = GetDateTime.getTime();

            } else if (isChanged || isStopped) {
                isChanged = false;
                appTime = 0;
                startTime = GetDateTime.getTime();
            }

            title = TempTitle;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFile () {
        ArrayList<Item> nowList = GetList.ItemList;
        try (FileWriter fileWriter = new FileWriter(GetList.getFilePath(), true)) {
            for (int i = lstSize; i < nowList.size(); i++) {
                fileWriter.append(nowList.get(i).getAppName()).append(",").append(nowList.get(i).getStartTime()).append(",").append(nowList.get(i).getEndTime()).append(",").append(String.valueOf(nowList.get(i).getDuration())).append("\n");
            }
            lstSize = nowList.size();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(!pool.isShutdown()) pool.shutdownNow();
        }
    }

    private void scheduledTask() {
        if(appTime % 1500 == 0 && appTime != 0 && Objects.equals(System.getProperty("os.name"), "Linux")) {
            try {
                Runtime.getRuntime().exec(new String[]{"notify-send", "'Maybe it's time for a break.'", timerInFrame(appTime)});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        addingItem();
        if(!isStopped) Platform.runLater(() -> timeTicking.setText("Running : " + timerInFrame(time)));
        appTime++;
        time++;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateLabel.setText(GetDateTime.getDate(LocalDateTime.now().plusDays(dateAdjust)));
        welcomeText.setText(System.getProperty("user.name"));
        totalTime.setText("Total Time : " + timerInFrame(GetList.totalDuration()));
        table.setPlaceholder(new Label("It’s not about better time management. It’s about better life management."));
        setTableColumns();
        if(dateAdjust == 0) next.setDisable(true);
        Platform.runLater(() -> Chart.getChart(chart));
    }

    public void onRecord() throws InterruptedException {
        isDateChanged = false;
        time = 0;
        if(Objects.equals(recordButton.getText(), "Record")) {
            setTime = Integer.parseInt(setSecond.getText());
            exc = Executors.newSingleThreadScheduledExecutor();
            exc.scheduleAtFixedRate(this::scheduledTask, 0, 1, TimeUnit.SECONDS);
            Platform.runLater(() -> {
                recordButton.setText("Stop");
                focusedMode.setDisable(true);
                setSecond.setDisable(true);
                whitelistButton.setDisable(true);
            });
            isStopped = false;
            startTime = GetDateTime.getTime();
            pool = Executors.newFixedThreadPool(2);
        }

        else {
            exc.shutdownNow();
            Platform.runLater(() -> {
                recordButton.setText("Record");
                focusedMode.setDisable(false);
                setSecond.setDisable(false);
                whitelistButton.setDisable(false);
            });
            isStopped = true;
            pool.execute(this::addingItem);
            pool.execute(this::updateFile);
            Platform.runLater(() -> Chart.getChart(chart));
        }
    }

    public void getWhitelist(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("whitelist.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setOnCloseRequest(e -> {
            setWhitelistedItemInFile();
        });
        stage.setTitle("Whitelist");
        stage.initOwner((Stage) mainPane.getScene().getWindow());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void setWhitelistedItemInFile() {
        try (FileWriter fileWriter = new FileWriter(Whitelist.getFilePath())) {
            for (int i = 0; i < Whitelist.whitelistItem.size(); i++) {
                if(i == Whitelist.whitelistItem.size() - 1) fileWriter.append(Whitelist.whitelistItem.get(i));
                else fileWriter.append(Whitelist.whitelistItem.get(i)).append(',');
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getReport() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("report.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle("Weekly Report");
        stage.initOwner((Stage) mainPane.getScene().getWindow());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void onPrevious() {
        if(dateAdjust <= -9) previous.setDisable(true);
        recordButton.setDisable(true);
        isDateChanged = true;
        next.setDisable(false);
        dateAdjust--;

        Platform.runLater(() -> {
            dateLabel.setText(GetDateTime.getDate(LocalDateTime.now().plusDays(dateAdjust)));
            totalTime.setText("Total Time : " + timerInFrame(GetList.totalDuration()));
            setTableColumns();
        });
        Platform.runLater(() -> Chart.getChart(chart));
    }

    public void onNext() {
        if(dateAdjust >= -1) {
            next.setDisable(true);
            recordButton.setDisable(false);
        }
        dateAdjust++;
        Platform.runLater(() -> {
            dateLabel.setText(GetDateTime.getDate(LocalDateTime.now().plusDays(dateAdjust)));
            totalTime.setText("Total Time : " + timerInFrame(GetList.totalDuration()));
            setTableColumns();
        });
        Platform.runLater(() -> Chart.getChart(chart));
    }
}
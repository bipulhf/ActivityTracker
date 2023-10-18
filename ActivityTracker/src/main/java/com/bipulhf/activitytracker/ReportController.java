package com.bipulhf.activitytracker;

import com.bipulhf.activitytracker.classes.GetList;
import com.bipulhf.activitytracker.classes.Item;
import com.bipulhf.activitytracker.classes.Report;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportController implements Initializable {

    private Report report;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label words;
    @FXML
    private PieChart pieChart;
    @FXML
    private TableView<Item> table;

    ObservableList<Item> data;

    private void setTableColumns(ObservableList<Item> data) {
        var appNameCol = new TableColumn("App Name");
        var durationCol = new TableColumn ("Duration");

        appNameCol.setCellValueFactory(new PropertyValueFactory<Item, String>("appName"));
        durationCol.setCellValueFactory(new PropertyValueFactory<Item, String>("timerText"));

        appNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.7));
        durationCol.prefWidthProperty().bind(table.widthProperty().multiply(0.28));

        appNameCol.setResizable(false);
        appNameCol.setSortable(false);
        durationCol.setResizable(false);

        table.setItems(data);

        Platform.runLater(() -> table.getColumns().addAll(appNameCol, durationCol));

    }

    private ObservableList<PieChart.Data> getChartData(Map<String, Integer> mp) {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        mp.forEach((k, v) -> {
            pieData.add(new PieChart.Data(k, v));
        });
        if(pieData.isEmpty()) words.setText("No activity to show.");

        return pieData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        report = new Report();
        data = FXCollections.observableArrayList(report.getReport());
        table.setPlaceholder(new Label("No activities were recorded last week."));
        setTableColumns(data);
        pieChart.setLegendSide(Side.RIGHT);
        pieChart.setData(getChartData(report.reportMap));
    }
}

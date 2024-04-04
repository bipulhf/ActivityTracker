package com.bipulhf.activitytracker.classes;

import javafx.application.Platform;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

import java.util.*;

public class Chart {
    public static void getChart(StackedBarChart<String, Number> chart) {
        Map<String, Integer> mp = new HashMap<>();
        ArrayList<Item> items = GetList.ItemList;
        char ch = 0;
        for(Item item : items){
            for(int i = 0; i < Report.hyphen.length; i++) {
                if(item.appName.indexOf(Report.hyphen[i]) != -1)
                    ch = Report.hyphen[i];
            }
            String appName = item.appName.substring(item.appName.lastIndexOf(ch) + 1).trim();
            System.out.println(appName);

            int duration = item.duration;
            mp.merge(appName, duration, Integer::sum);
        }

        chart.getData().clear();

        String k = null;
        if(!mp.isEmpty()) k = Collections.max(mp.entrySet(), Map.Entry.comparingByValue()).getKey();
        for(String key : mp.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if(mp.size() > 14) series.getData().add(new XYChart.Data<>(key.substring(0, 5), ((mp.get(key) * 1.0)/(1.25 * mp.get(k))) * 100));
            else if(key.length() > 20) series.getData().add(new XYChart.Data<>(key.substring(0, 19), ((mp.get(key) * 1.0)/(1.25 * mp.get(k))) * 100));
            else series.getData().add(new XYChart.Data<>(key, ((mp.get(key) * 1.0)/(1.25 *  mp.get(k))) * 100));
            Platform.runLater(() -> chart.getData().add(series));
        }
    }
}

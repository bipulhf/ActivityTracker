package com.bipulhf.activitytracker.classes;

import com.bipulhf.activitytracker.MainController;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Report {
    public static char[] hyphen = {'\u002D', '\u058A', '\u1806', '\u2010', '\u2011', '\u2012', '\u2013', '\u2014', '\u2015', '-'};
    private ArrayList<String> dateList, totalReport;

    public Map<String, Integer> reportMap;
    private ArrayList<Item> reportArrayList;
    private String getFilePath(String date) {
        String filePath;
        if(Objects.equals(System.getProperty("os.name"), "Linux"))
            filePath = GetList.getDirectoryName() + "/" + date + ".csv";
        else
            filePath = GetList.getDirectoryName() + "\\" + date + ".csv";

        return filePath;
    }

    private void getDateList(LocalDateTime dateTime) {
        dateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dateTime = dateTime.minusDays(1);
            var format = DateTimeFormatter.ofPattern("E - dd-MM-yy");
            dateList.add(dateTime.format(format));
        }
    }

    private void getReportList() {
        totalReport = new ArrayList<>();

        getDateList(LocalDateTime.now());

        for (int j = 0; j < 7; j++) {

            File file = new File(getFilePath(dateList.get(j)));

            if(file.exists() && !file.isDirectory())
                try (BufferedReader br = new BufferedReader(new FileReader(getFilePath(dateList.get(j))))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.length() > 1) {
                            String s = "";
                            for (int i = 0, k = 0; i < line.length(); i++) {
                                if (line.charAt(i) == ',') {
                                    if(k == 0) totalReport.add(s.trim());
                                    s = "";
                                    k++;
                                    continue;
                                }
                                s += line.charAt(i);;
                            }
                            totalReport.add(s.trim());
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    private void getReportMap() {

        getReportList();

        reportMap = new HashMap<>();

        char ch = 0;

        for(int i = 0; i < totalReport.size(); i += 2) {
            for(int j = 0; j < hyphen.length; j++) {
                if(totalReport.get(i).indexOf(hyphen[j]) != -1)
                    ch = hyphen[j];

            }
            String name = totalReport.get(i).substring(totalReport.get(i).lastIndexOf(ch) + 1).trim();
            int duration = Integer.parseInt(totalReport.get(i + 1));

            reportMap.merge(name, duration, Integer::sum);
        }
    }

    private void getList() {
        reportMap.forEach((k, v) -> {
            reportArrayList.add(new Item (k, MainController.timerInFrame(v)));
        });
    }

    public ArrayList getReport() {
        reportArrayList = new ArrayList<>();
        getReportMap();
        getList();

        return reportArrayList;
    }
}

package com.bipulhf.activitytracker.classes;

import com.bipulhf.activitytracker.Main;
import com.bipulhf.activitytracker.MainController;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class GetList {
    public static ArrayList<Item> ItemList;

    public static String getDirectoryName () {
        String directoryName;
        if(Objects.equals(System.getProperty("os.name"), "Linux"))
            directoryName = "/home/" + System.getProperty("user.name") + "/.ActivityTracker";
        else
            directoryName = "C:\\Users\\" + System.getProperty("user.name") +"\\AppData\\Local\\ActivityTracker";
        return directoryName;
    }

    public static String getFilePath() {
        String filePath;
        if(Objects.equals(System.getProperty("os.name"), "Linux"))
            filePath = getDirectoryName() + "/" + GetDateTime.getDate(LocalDateTime.now().plusDays(MainController.dateAdjust)) + ".csv";
        else
            filePath = getDirectoryName() + "\\" + GetDateTime.getDate(LocalDateTime.now().plusDays(MainController.dateAdjust)) + ".csv";
        return filePath;
    }

    public static void makeFile () {
        try {
            File newFile = new File(getFilePath());
            if(!newFile.exists() && !newFile.isDirectory())
                newFile.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getItemSize(){

        ItemList = new ArrayList<>();
        File newFile = new File(getFilePath());

        if(!newFile.exists() && !newFile.isDirectory()) return ItemList.size();

        try(BufferedReader br = new BufferedReader(new FileReader(getFilePath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length() > 1) {
                    ArrayList<String> temp = new ArrayList<>(4);
                    String s = "";
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',') {
                            temp.add(s);
                            s = "";
                            continue;
                        }
                        s += line.charAt(i);
                    }
                    temp.add(s);
                    ItemList.add(new Item(temp.get(0), temp.get(1), temp.get(2), Integer.parseInt(temp.get(3)), MainController.timerInFrame(Integer.parseInt(temp.get(3)))));
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ItemList.size();
    }

    public static int totalDuration () {
        if(MainController.isDateChanged) getItemSize();
        int totalDur = 0;
        for(Item item : GetList.ItemList)
            totalDur += item.duration;
        return totalDur;
    }
}

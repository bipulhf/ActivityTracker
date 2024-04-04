package com.bipulhf.activitytracker.classes;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Whitelist {
    public static ArrayList<String> whitelistItem;

    public static String getFilePath() {
        String filePath;
        if(Objects.equals(System.getProperty("os.name"), "Linux"))
            filePath = GetList.getDirectoryName() + "/whitelist.csv";
        else
            filePath = GetList.getDirectoryName() + "\\whitelist.csv";

        try {
            File newFile = new File(filePath);
            newFile.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath;
    }

    static {retrieveList();}

    private static ArrayList retrieveList(){

        whitelistItem = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(getFilePath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.length() > 1) {
                    String s = "";
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',') {
                            whitelistItem.add(s.trim());
                            s = "";
                            continue;
                        }
                        s += line.charAt(i);
                    }
                    whitelistItem.add(s.trim());
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return whitelistItem;
    }
}

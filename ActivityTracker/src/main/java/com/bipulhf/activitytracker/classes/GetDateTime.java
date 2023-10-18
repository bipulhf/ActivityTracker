package com.bipulhf.activitytracker.classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetDateTime {

    public static String getDate(LocalDateTime dateObject) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("E - dd-MM-yy");
        return dateObject.format(format);
    }
    public static String getTime() {

        LocalDateTime dateObject = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

        return dateObject.format(format);
    }
}

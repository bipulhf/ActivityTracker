module com.bipulhf.activitytracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.bipulhf.activitytracker to javafx.fxml;
    exports com.bipulhf.activitytracker;
    exports com.bipulhf.activitytracker.classes;
    opens com.bipulhf.activitytracker.classes to javafx.fxml;
}
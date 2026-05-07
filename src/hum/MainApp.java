package hum;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        DriveManager drives = new DriveManager();
        VolunteerManager volunteers = new VolunteerManager();
        ReportGenerator report = new ReportGenerator(drives, volunteers);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Donation Drives",  new DonationDriveTab(drives)),
                new Tab("Volunteer Hours",  new VolunteerTab(volunteers)),
                new Tab("Summary Report",   new SummaryReportTab(report))
        );

        stage.setTitle("HUM Foundation Tracker");
        stage.setScene(new Scene(tabs, 960, 640));
        stage.setMinWidth(800);
        stage.setMinHeight(480);
        stage.show();
    }
}

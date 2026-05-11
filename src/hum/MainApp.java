package hum;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Button activeBtn;

    @Override
    public void start(Stage stage) {
        DriveManager drives         = new DriveManager();
        VolunteerManager volunteers = new VolunteerManager();
        ReportGenerator report      = new ReportGenerator(drives, volunteers);

        DashboardView    dashboard  = new DashboardView(drives, volunteers);
        DonationDriveTab drivesPage = new DonationDriveTab(drives);
        VolunteerTab     volPage    = new VolunteerTab(volunteers);
        SummaryReportTab reportPage = new SummaryReportTab(report);

        BorderPane root = new BorderPane();

        Button b1 = navBtn("▣   Dashboard");
        Button b2 = navBtn("♥   Donation Drives");
        Button b3 = navBtn("★   Volunteer Hours");
        Button b4 = navBtn("≡   Summary Report");

        activate(b1);

        b1.setOnAction(e -> { dashboard.refresh(); root.setCenter(dashboard); activate(b1); });
        b2.setOnAction(e -> { root.setCenter(drivesPage);  activate(b2); });
        b3.setOnAction(e -> { root.setCenter(volPage);     activate(b3); });
        b4.setOnAction(e -> { root.setCenter(reportPage);  activate(b4); });

        root.setLeft(buildSidebar(b1, b2, b3, b4));
        root.setCenter(dashboard);

        Scene scene = new Scene(root, 1100, 700);
        try {
            scene.getStylesheets().add(MainApp.class.getResource("style.css").toExternalForm());
        } catch (Exception ignored) {}

        stage.setTitle("HUM Foundation Tracker");
        stage.setScene(scene);
        stage.setMinWidth(880);
        stage.setMinHeight(560);
        stage.show();
    }

    private VBox buildSidebar(Button... navBtns) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(228);
        sidebar.setStyle("-fx-background-color: #1e3a5f;");

        // Logo
        VBox logo = new VBox(5);
        logo.setPadding(new Insets(30, 20, 26, 22));
        Label org = new Label("HUM Foundation");
        org.setFont(Font.font(null, FontWeight.BOLD, 16));
        org.setStyle("-fx-text-fill: white;");
        Label sub = new Label("Donation Tracker");
        sub.setStyle("-fx-text-fill: #7fb3d3; -fx-font-size: 12;");
        logo.getChildren().addAll(org, sub);

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #2d4f6b;");

        // Nav
        VBox nav = new VBox(3);
        nav.setPadding(new Insets(18, 10, 10, 10));
        Label navTitle = new Label("NAVIGATION");
        navTitle.setStyle("-fx-text-fill: #4a7fa5; -fx-font-size: 10; -fx-padding: 0 8 10 8;");
        nav.getChildren().add(navTitle);
        for (Button b : navBtns) nav.getChildren().add(b);

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);

        sidebar.getChildren().addAll(logo, divider, nav, spring);
        return sidebar;
    }

    private Button navBtn(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(navOff());
        btn.setOnMouseEntered(e -> { if (btn != activeBtn) btn.setStyle(navHover()); });
        btn.setOnMouseExited(e  -> { if (btn != activeBtn) btn.setStyle(navOff()); });
        return btn;
    }

    private void activate(Button btn) {
        if (activeBtn != null) activeBtn.setStyle(navOff());
        activeBtn = btn;
        btn.setStyle(navOn());
    }

    private static String navOff()   { return "-fx-background-color:transparent;-fx-text-fill:#94a3b8;-fx-font-size:13;-fx-padding:10 16;-fx-background-radius:8;-fx-alignment:center-left;-fx-cursor:hand;"; }
    private static String navHover() { return "-fx-background-color:#2d4f6b;-fx-text-fill:#cbd5e1;-fx-font-size:13;-fx-padding:10 16;-fx-background-radius:8;-fx-alignment:center-left;-fx-cursor:hand;"; }
    private static String navOn()    { return "-fx-background-color:#0ea5e9;-fx-text-fill:white;-fx-font-size:13;-fx-font-weight:bold;-fx-padding:10 16;-fx-background-radius:8;-fx-alignment:center-left;-fx-cursor:hand;"; }
}

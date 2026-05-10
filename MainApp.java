import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
 
public class MainApp extends Application {
 
    @Override
    public void start(Stage primaryStage) {
        // Create the main TabPane
        TabPane mainTabPane = new TabPane();
 
        // Add Hemanth's Trends tab
        Tab trendsTab = new Tab("📊 Trends");
        trendsTab.setClosable(false);
        trendsTab.setContent(TrendsPanel.buildPanel());
        mainTabPane.getTabs().add(trendsTab);
 
        // Set up and show the window
        Scene scene = new Scene(mainTabPane, 900, 650);
        primaryStage.setTitle("HUM Foundation Donation Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
 

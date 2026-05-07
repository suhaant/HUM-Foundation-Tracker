package hum;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

public class SummaryReportTab extends VBox {

    private final ReportGenerator generator;

    public SummaryReportTab(ReportGenerator generator) {
        this.generator = generator;
        setPadding(new Insets(16));
        setSpacing(12);
        build();
    }

    private void build() {
        Label heading = new Label("Generate Impact Summary Report");
        heading.setFont(Font.font(null, FontWeight.BOLD, 14));

        DatePicker fromPicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker toPicker   = new DatePicker(LocalDate.now());

        Button genBtn = new Button("Generate Report");
        genBtn.setStyle("-fx-background-color:#2e7d32;-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:6 16;");

        HBox controls = new HBox(10, new Label("From:"), fromPicker, new Label("To:"), toPicker, genBtn);
        controls.setAlignment(Pos.CENTER_LEFT);

        TextArea area = new TextArea();
        area.setEditable(false);
        area.setFont(Font.font("Courier New", 13));
        area.setPromptText("Select a date range and click Generate Report.");

        genBtn.setOnAction(e -> {
            LocalDate from = fromPicker.getValue();
            LocalDate to   = toPicker.getValue();
            if (from == null || to == null) { warn("Both dates are required."); return; }
            if (from.isAfter(to))           { warn("Start date must be on or before end date."); return; }
            area.setText(generator.generate(from, to));
        });

        getChildren().addAll(heading, controls, new Separator(), area);
        VBox.setVgrow(area, Priority.ALWAYS);
    }

    private static void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}

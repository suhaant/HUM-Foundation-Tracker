package hum;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

public class SummaryReportTab extends VBox {

    private final ReportGenerator generator;

    public SummaryReportTab(ReportGenerator generator) {
        this.generator = generator;
        setStyle("-fx-background-color:#f1f5f9;");
        setPadding(new Insets(32));
        setSpacing(24);
        VBox.setVgrow(this, Priority.ALWAYS);
        build();
    }

    private void build() {
        // ── Page header ──────────────────────────────────────
        Label title = new Label("Summary Report");
        title.setFont(Font.font(null, FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill:#1e293b;");
        Label subtitle = new Label("Generate an impact report for any date range");
        subtitle.setStyle("-fx-text-fill:#64748b;-fx-font-size:13;");
        VBox pageHeader = new VBox(4, title, subtitle);

        // ── Date range card ───────────────────────────────────
        DatePicker fromPicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        fromPicker.setPrefWidth(170);
        DatePicker toPicker   = new DatePicker(LocalDate.now());
        toPicker.setPrefWidth(170);

        Button genBtn = new Button("Generate Report");
        genBtn.setStyle("-fx-background-color:#0ea5e9;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13;-fx-padding:9 20;-fx-background-radius:8;-fx-cursor:hand;");
        genBtn.setOnMouseEntered(e -> genBtn.setOpacity(0.88));
        genBtn.setOnMouseExited(e  -> genBtn.setOpacity(1.0));

        HBox dateRow = new HBox(16,
                fieldLabel("From"), fromPicker,
                fieldLabel("To"),   toPicker,
                genBtn);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        VBox rangeCard = card("Select Date Range");
        rangeCard.getChildren().add(dateRow);

        // ── Report output card ────────────────────────────────
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setFont(Font.font("Courier New", 13));
        reportArea.setStyle("-fx-font-family:'Courier New';-fx-font-size:13;");
        reportArea.setPromptText("Select a date range above and click Generate Report.");
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        VBox outputCard = card("Report Output");
        outputCard.getChildren().add(reportArea);
        VBox.setVgrow(outputCard, Priority.ALWAYS);

        // ── Action ───────────────────────────────────────────
        genBtn.setOnAction(e -> {
            LocalDate from = fromPicker.getValue();
            LocalDate to   = toPicker.getValue();
            if (from == null || to == null) { warn("Both dates are required."); return; }
            if (from.isAfter(to))           { warn("Start date must be on or before end date."); return; }
            reportArea.setText(generator.generate(from, to));
        });

        getChildren().addAll(pageHeader, rangeCard, outputCard);
    }

    private static VBox card(String sectionTitle) {
        VBox card = new VBox(16);
        card.setPadding(new Insets(22, 24, 22, 24));
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-border-color:#e2e8f0;-fx-border-radius:12;-fx-border-width:1;");
        card.setEffect(new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.05)));
        Label lbl = new Label(sectionTitle);
        lbl.setFont(Font.font(null, FontWeight.BOLD, 14));
        lbl.setStyle("-fx-text-fill:#1e293b;");
        card.getChildren().add(lbl);
        return card;
    }

    private static Label fieldLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:#475569;-fx-font-size:13;-fx-font-weight:bold;");
        return l;
    }

    private static void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}

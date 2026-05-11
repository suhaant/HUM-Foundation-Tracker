package hum;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VolunteerTab extends VBox {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final VolunteerManager manager;
    private final ObservableList<VolunteerEntry> rows = FXCollections.observableArrayList();
    private final Label cumulativeLabel = new Label("Select a row to see that volunteer's all-time total.");

    public VolunteerTab(VolunteerManager manager) {
        this.manager = manager;
        setStyle("-fx-background-color:#f1f5f9;");
        setPadding(new Insets(32));
        setSpacing(24);
        VBox.setVgrow(this, Priority.ALWAYS);
        build();
    }

    private void build() {
        // ── Page header ──────────────────────────────────────
        Label title = new Label("Volunteer Hours");
        title.setFont(Font.font(null, FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill:#1e293b;");
        Label subtitle = new Label("Track volunteer contributions by person and event");
        subtitle.setStyle("-fx-text-fill:#64748b;-fx-font-size:13;");
        VBox pageHeader = new VBox(4, title, subtitle);

        // ── Form card ────────────────────────────────────────
        TextField nameField  = inputField("Full name");
        TextField eventField = inputField("Event name");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(160);
        TextField hoursField = inputField("e.g. 3.5");
        hoursField.setPrefWidth(100);

        Label status = new Label();
        status.setStyle("-fx-text-fill:#16a34a;-fx-font-size:13;");
        Button logBtn = primaryBtn("+ Log Hours", "#0ea5e9");

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12);
        grid.addRow(0, fieldLabel("Volunteer Name"), nameField,  fieldLabel("Event Name"), eventField);
        grid.addRow(1, fieldLabel("Date"),           datePicker, fieldLabel("Hours"),      hoursField);
        GridPane.setHgrow(nameField,  Priority.ALWAYS);
        GridPane.setHgrow(eventField, Priority.ALWAYS);

        HBox formFooter = new HBox(12, status, new Region(), logBtn);
        HBox.setHgrow(formFooter.getChildren().get(1), Priority.ALWAYS);
        formFooter.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = card("Log Volunteer Hours");
        formCard.getChildren().addAll(grid, formFooter);

        // ── Cumulative badge ─────────────────────────────────
        cumulativeLabel.setStyle("-fx-text-fill:#475569;-fx-font-size:13;-fx-padding:8 14;-fx-background-color:#f0f9ff;-fx-background-radius:8;-fx-border-color:#bae6fd;-fx-border-radius:8;-fx-border-width:1;");

        // ── Table card ───────────────────────────────────────
        TableView<VolunteerEntry> table = new TableView<>(rows);
        table.setPlaceholder(placeholder("No volunteer hours logged yet."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(
                col("Volunteer", c -> new SimpleStringProperty(c.getValue().volunteerName)),
                col("Event",     c -> new SimpleStringProperty(c.getValue().eventName)),
                col("Date",      c -> new SimpleStringProperty(c.getValue().date.format(FMT))),
                col("Hours",     c -> new SimpleStringProperty(String.format("%.1f", c.getValue().hours)))
        );
        VBox.setVgrow(table, Priority.ALWAYS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                double total = manager.cumulativeHoursFor(sel.volunteerName);
                cumulativeLabel.setText(String.format("All-time total for %s:   %.1f hrs", sel.volunteerName, total));
            }
        });

        Button delBtn = primaryBtn("Delete Selected", "#ef4444");
        Label delStatus = new Label();
        delStatus.setStyle("-fx-text-fill:#16a34a;-fx-font-size:13;");
        HBox tableFooter = new HBox(12, delBtn, delStatus);
        tableFooter.setAlignment(Pos.CENTER_LEFT);
        tableFooter.setPadding(new Insets(12, 0, 0, 0));

        VBox tableCard = card("All Volunteer Hours");
        tableCard.getChildren().addAll(cumulativeLabel, table, tableFooter);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // ── Actions ──────────────────────────────────────────
        logBtn.setOnAction(e -> {
            String name      = nameField.getText().trim();
            String event     = eventField.getText().trim();
            LocalDate date   = datePicker.getValue();
            String hoursText = hoursField.getText().trim();
            if (name.isEmpty())      { warn("Volunteer name is required."); return; }
            if (event.isEmpty())     { warn("Event name is required."); return; }
            if (date == null)        { warn("Date is required."); return; }
            if (hoursText.isEmpty()) { warn("Hours are required."); return; }
            double hours;
            try { hours = Double.parseDouble(hoursText); if (hours <= 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { warn("Hours must be a positive number."); return; }
            manager.log(name, event, date, hours);
            nameField.clear(); eventField.clear(); hoursField.clear();
            datePicker.setValue(LocalDate.now());
            refresh();
            status.setText(String.format("✓  Logged %.1f hrs for %s.", hours, name));
        });

        delBtn.setOnAction(e -> {
            VolunteerEntry sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { warn("Select an entry to delete."); return; }
            manager.delete(sel.id);
            refresh();
            cumulativeLabel.setText("Select a row to see that volunteer's all-time total.");
            delStatus.setText("✓  Entry deleted.");
        });

        refresh();
        getChildren().addAll(pageHeader, formCard, tableCard);
    }

    private void refresh() { rows.setAll(manager.getAll()); }

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

    private static TextField inputField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(36);
        return f;
    }

    private static Button primaryBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:13;-fx-padding:8 18;-fx-background-radius:8;-fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setOpacity(0.88));
        b.setOnMouseExited(e  -> b.setOpacity(1.0));
        return b;
    }

    private static Label placeholder(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:13;");
        return l;
    }

    private static <T> TableColumn<T, String> col(String title,
            javafx.util.Callback<TableColumn.CellDataFeatures<T, String>,
            javafx.beans.value.ObservableValue<String>> f) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(f);
        return c;
    }

    private static void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}

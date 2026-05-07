package hum;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
        setPadding(new Insets(16));
        setSpacing(12);
        build();
    }

    private void build() {
        Label heading = bold("Log Volunteer Hours");

        TextField nameField  = field("Full name");
        TextField eventField = field("Event name");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField hoursField = field("e.g. 3.5");
        hoursField.setMaxWidth(100);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        form.addRow(0, lbl("Volunteer Name:"), nameField,  lbl("Event Name:"), eventField);
        form.addRow(1, lbl("Date:"), datePicker, lbl("Hours:"), hoursField);

        Button logBtn = btn("Log Hours", "#1565c0");
        HBox formRow = new HBox(logBtn);
        formRow.setAlignment(Pos.CENTER_RIGHT);

        TableView<VolunteerEntry> table = new TableView<>(rows);
        table.setPlaceholder(new Label("No hours logged yet."));
        table.getColumns().addAll(
                col("Volunteer", c -> new SimpleStringProperty(c.getValue().volunteerName)),
                col("Event",     c -> new SimpleStringProperty(c.getValue().eventName)),
                col("Date",      c -> new SimpleStringProperty(c.getValue().date.format(FMT))),
                col("Hours",     c -> new SimpleStringProperty(String.format("%.1f", c.getValue().hours)))
        );

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                double total = manager.cumulativeHoursFor(sel.volunteerName);
                cumulativeLabel.setText(String.format("All-time total for %s: %.1f hrs", sel.volunteerName, total));
            }
        });

        Button delBtn = btn("Delete Selected", "#b71c1c");
        Label status = new Label();
        status.setTextFill(Color.DARKGREEN);
        HBox bar = new HBox(10, delBtn, status);
        bar.setAlignment(Pos.CENTER_LEFT);

        logBtn.setOnAction(e -> {
            String name  = nameField.getText().trim();
            String event = eventField.getText().trim();
            LocalDate date = datePicker.getValue();
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
            status.setText(String.format("Logged %.1f hrs for %s.", hours, name));
        });

        delBtn.setOnAction(e -> {
            VolunteerEntry sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { warn("Select a row to delete."); return; }
            manager.delete(sel.id);
            refresh();
            cumulativeLabel.setText("Select a row to see that volunteer's all-time total.");
            status.setText("Entry deleted.");
        });

        refresh();
        getChildren().addAll(heading, form, formRow, new Separator(), table, cumulativeLabel, bar);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void refresh() { rows.setAll(manager.getAll()); }

    private static Label lbl(String t)  { return new Label(t); }
    private static Label bold(String t) { Label l = new Label(t); l.setFont(Font.font(null, FontWeight.BOLD, 14)); return l; }
    private static TextField field(String prompt) { TextField f = new TextField(); f.setPromptText(prompt); return f; }
    private static Button btn(String t, String color) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:" + color + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:6 14;");
        return b;
    }
    private static <T> TableColumn<T, String> col(String title,
            javafx.util.Callback<TableColumn.CellDataFeatures<T, String>,
            javafx.beans.value.ObservableValue<String>> factory) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(factory);
        return c;
    }
    private static void warn(String msg) { new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait(); }
}

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

public class DonationDriveTab extends VBox {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final DriveManager manager;
    private final ObservableList<DonationDrive> rows = FXCollections.observableArrayList();

    public DonationDriveTab(DriveManager manager) {
        this.manager = manager;
        setPadding(new Insets(16));
        setSpacing(12);
        build();
    }

    private void build() {
        Label heading = bold("Log a New Donation Drive");

        TextField nameField = field("Drive name");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField catField  = field("e.g. Food; Clothing; Hygiene");
        TextField totalField = field("0.00");
        totalField.setMaxWidth(110);

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(8);
        form.addRow(0, lbl("Drive Name:"), nameField,  lbl("Date:"), datePicker);
        form.addRow(1, lbl("Item Categories:"), catField, lbl("Monetary Total ($):"), totalField);

        Button addBtn = btn("Add Drive", "#2e7d32");
        HBox formRow = new HBox(addBtn);
        formRow.setAlignment(Pos.CENTER_RIGHT);

        TableView<DonationDrive> table = new TableView<>(rows);
        table.setPlaceholder(new Label("No drives logged yet."));
        table.getColumns().addAll(
                col("Drive Name", c -> new SimpleStringProperty(c.getValue().name)),
                col("Date",       c -> new SimpleStringProperty(c.getValue().date.format(FMT))),
                col("Categories", c -> new SimpleStringProperty(c.getValue().itemCategories.replace(";", "  |  "))),
                col("Total",      c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().monetaryTotal)))
        );

        Button delBtn = btn("Delete Selected", "#b71c1c");
        Label status = new Label();
        status.setTextFill(Color.DARKGREEN);
        HBox bar = new HBox(10, delBtn, status);
        bar.setAlignment(Pos.CENTER_LEFT);

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            LocalDate date = datePicker.getValue();
            String cats = catField.getText().trim();
            String totalText = totalField.getText().trim();
            if (name.isEmpty()) { warn("Drive name is required."); return; }
            if (date == null)   { warn("Date is required."); return; }
            double total = 0;
            if (!totalText.isEmpty()) {
                try { total = Double.parseDouble(totalText); if (total < 0) throw new NumberFormatException(); }
                catch (NumberFormatException ex) { warn("Total must be a non-negative number."); return; }
            }
            manager.add(name, date, cats, total);
            nameField.clear(); catField.clear(); totalField.clear();
            datePicker.setValue(LocalDate.now());
            refresh();
            status.setText("\"" + name + "\" added.");
        });

        delBtn.setOnAction(e -> {
            DonationDrive sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { warn("Select a row to delete."); return; }
            manager.delete(sel.id);
            refresh();
            status.setText("Drive deleted.");
        });

        refresh();
        getChildren().addAll(heading, form, formRow, new Separator(), table, bar);
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

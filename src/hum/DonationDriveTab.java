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

public class DonationDriveTab extends VBox {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private final DriveManager manager;
    private final ObservableList<DonationDrive> rows = FXCollections.observableArrayList();

    public DonationDriveTab(DriveManager manager) {
        this.manager = manager;
        setStyle("-fx-background-color:#f1f5f9;");
        setPadding(new Insets(32));
        setSpacing(24);
        VBox.setVgrow(this, Priority.ALWAYS);
        build();
    }

    private void build() {
        // ── Page header ──────────────────────────────────────
        Label title = new Label("Donation Drives");
        title.setFont(Font.font(null, FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill:#1e293b;");
        Label subtitle = new Label("Log and manage donation drive records");
        subtitle.setStyle("-fx-text-fill:#64748b;-fx-font-size:13;");
        VBox pageHeader = new VBox(4, title, subtitle);

        // ── Form card ────────────────────────────────────────
        TextField nameField  = inputField("Drive name");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(160);
        TextField catField   = inputField("e.g. Food; Clothing; Hygiene");
        TextField totalField = inputField("0.00");
        totalField.setPrefWidth(120);

        Label status = new Label();
        status.setStyle("-fx-text-fill:#16a34a;-fx-font-size:13;");

        Button addBtn = primaryBtn("+ Add Drive", "#0ea5e9");

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12);
        grid.addRow(0, fieldLabel("Drive Name"),     nameField,  fieldLabel("Date"),               datePicker);
        grid.addRow(1, fieldLabel("Item Categories"), catField,   fieldLabel("Monetary Total ($)"),  totalField);
        GridPane.setHgrow(nameField,  Priority.ALWAYS);
        GridPane.setHgrow(catField,   Priority.ALWAYS);

        HBox formFooter = new HBox(12, status, new Region(), addBtn);
        HBox.setHgrow(formFooter.getChildren().get(1), Priority.ALWAYS);
        formFooter.setAlignment(Pos.CENTER_LEFT);

        VBox formCard = card("Log a New Drive");
        formCard.getChildren().addAll(grid, formFooter);

        // ── Table card ───────────────────────────────────────
        TableView<DonationDrive> table = new TableView<>(rows);
        table.setPlaceholder(placeholder("No drives logged yet."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(
                col("Drive Name", c -> new SimpleStringProperty(c.getValue().name)),
                col("Date",       c -> new SimpleStringProperty(c.getValue().date.format(FMT))),
                col("Categories", c -> new SimpleStringProperty(c.getValue().itemCategories.replace(";", " · "))),
                col("Total",      c -> new SimpleStringProperty(String.format("$%.2f", c.getValue().monetaryTotal)))
        );
        VBox.setVgrow(table, Priority.ALWAYS);

        Button delBtn = primaryBtn("Delete Selected", "#ef4444");
        Label delStatus = new Label();
        delStatus.setStyle("-fx-text-fill:#16a34a;-fx-font-size:13;");
        HBox tableFooter = new HBox(12, delBtn, delStatus);
        tableFooter.setAlignment(Pos.CENTER_LEFT);
        tableFooter.setPadding(new Insets(12, 0, 0, 0));

        VBox tableCard = card("All Donation Drives");
        tableCard.getChildren().addAll(table, tableFooter);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // ── Actions ──────────────────────────────────────────
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
                catch (NumberFormatException ex) { warn("Monetary total must be a non-negative number."); return; }
            }
            manager.add(name, date, cats, total);
            nameField.clear(); catField.clear(); totalField.clear();
            datePicker.setValue(LocalDate.now());
            refresh();
            status.setText("✓  \"" + name + "\" added successfully.");
        });

        delBtn.setOnAction(e -> {
            DonationDrive sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { warn("Select a drive to delete."); return; }
            manager.delete(sel.id);
            refresh();
            delStatus.setText("✓  Drive deleted.");
        });

        refresh();
        getChildren().addAll(pageHeader, formCard, tableCard);
    }

    private void refresh() { rows.setAll(manager.getAll()); }

    // ── UI helpers ───────────────────────────────────────────

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

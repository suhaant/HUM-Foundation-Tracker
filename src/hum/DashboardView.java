package hum;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardView extends VBox {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final DriveManager drives;
    private final VolunteerManager volunteers;
    private final VBox body = new VBox(24);

    public DashboardView(DriveManager drives, VolunteerManager volunteers) {
        this.drives = drives;
        this.volunteers = volunteers;
        setStyle("-fx-background-color: #f1f5f9;");
        setPadding(new Insets(32, 32, 32, 32));
        setSpacing(24);
        VBox.setVgrow(this, Priority.ALWAYS);

        // Static header
        Label title = new Label("Dashboard");
        title.setFont(Font.font(null, FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #1e293b;");
        Label subtitle = new Label("Overview of HUM Foundation's impact");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");

        getChildren().addAll(new VBox(4, title, subtitle), body);
        refresh();
    }

    public void refresh() {
        body.getChildren().clear();

        List<DonationDrive> allDrives   = drives.getAll();
        List<VolunteerEntry> allEntries = volunteers.getAll();

        int    driveCount  = allDrives.size();
        double totalRaised = allDrives.stream().mapToDouble(d -> d.monetaryTotal).sum();
        double totalHours  = allEntries.stream().mapToDouble(e -> e.hours).sum();
        long   uniqueVols  = allEntries.stream().map(e -> e.volunteerName).distinct().count();

        // ── Stat cards ──────────────────────────────────────
        HBox stats = new HBox(16);
        stats.getChildren().addAll(
                statCard("Donation Drives",  String.valueOf(driveCount),          "#0ea5e9"),
                statCard("Total Raised",     String.format("$%.0f", totalRaised), "#16a34a"),
                statCard("Volunteer Hours",  String.format("%.1f", totalHours),   "#7c3aed"),
                statCard("Volunteers",       String.valueOf(uniqueVols),           "#ea580c")
        );

        // ── Recent drives ────────────────────────────────────
        List<String[]> recentDrives;
        if (allDrives.isEmpty()) {
            recentDrives = List.<String[]>of(new String[]{"No donation drives recorded yet.", "", ""});
        } else {
            recentDrives = allDrives.subList(Math.max(0, allDrives.size() - 5), allDrives.size())
                    .stream().map(d -> new String[]{
                            d.name, d.date.format(FMT), String.format("$%.2f", d.monetaryTotal)
                    }).collect(Collectors.toList());
        }

        VBox recentDrivesCard = recentCard("Recent Donation Drives",
                new String[]{"Drive Name", "Date", "Total"}, recentDrives);

        // ── Recent volunteer entries ─────────────────────────
        List<String[]> recentVol;
        if (allEntries.isEmpty()) {
            recentVol = List.<String[]>of(new String[]{"No volunteer hours recorded yet.", "", ""});
        } else {
            recentVol = allEntries.subList(Math.max(0, allEntries.size() - 5), allEntries.size())
                    .stream().map(e -> new String[]{
                            e.volunteerName, e.eventName, String.format("%.1f hrs", e.hours)
                    }).collect(Collectors.toList());
        }

        VBox recentVolCard = recentCard("Recent Volunteer Hours",
                new String[]{"Volunteer", "Event", "Hours"}, recentVol);

        HBox recents = new HBox(16, recentDrivesCard, recentVolCard);
        HBox.setHgrow(recentDrivesCard, Priority.ALWAYS);
        HBox.setHgrow(recentVolCard, Priority.ALWAYS);

        body.getChildren().addAll(stats, recents);
    }

    // ── Helpers ──────────────────────────────────────────────

    private static VBox statCard(String label, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22, 24, 22, 24));
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-border-color:#e2e8f0;-fx-border-radius:12;-fx-border-width:1;");
        card.setEffect(new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.06)));
        HBox.setHgrow(card, Priority.ALWAYS);

        Label val = new Label(value);
        val.setFont(Font.font(null, FontWeight.BOLD, 28));
        val.setStyle("-fx-text-fill:" + color + ";");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:12;");

        card.getChildren().addAll(val, lbl);
        return card;
    }

    private static VBox recentCard(String title, String[] headers, List<String[]> rows) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-border-color:#e2e8f0;-fx-border-radius:12;-fx-border-width:1;");
        card.setEffect(new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.06)));

        // Card header
        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 14, 20));
        Label t = new Label(title);
        t.setFont(Font.font(null, FontWeight.BOLD, 13));
        t.setStyle("-fx-text-fill:#1e293b;");
        header.getChildren().add(t);

        Region hdivider = new Region();
        hdivider.setPrefHeight(1);
        hdivider.setStyle("-fx-background-color:#f1f5f9;");

        // Column headers
        HBox colRow = new HBox();
        colRow.setPadding(new Insets(8, 20, 8, 20));
        colRow.setStyle("-fx-background-color:#f8fafc;");
        for (String h : headers) {
            Label lh = new Label(h.toUpperCase());
            lh.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11;-fx-font-weight:bold;");
            HBox.setHgrow(lh, Priority.ALWAYS);
            lh.setMaxWidth(Double.MAX_VALUE);
            colRow.getChildren().add(lh);
        }

        // Data rows
        VBox dataRows = new VBox();
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            HBox dataRow = new HBox();
            dataRow.setPadding(new Insets(10, 20, 10, 20));
            String rowBg = (i % 2 == 0) ? "white" : "#fafbfc";
            dataRow.setStyle("-fx-background-color:" + rowBg + ";");
            for (String cell : row) {
                Label cl = new Label(cell);
                cl.setStyle("-fx-text-fill:#374151;-fx-font-size:13;");
                HBox.setHgrow(cl, Priority.ALWAYS);
                cl.setMaxWidth(Double.MAX_VALUE);
                dataRow.getChildren().add(cl);
            }
            dataRows.getChildren().add(dataRow);
        }

        card.getChildren().addAll(header, hdivider, colRow, dataRows);
        return card;
    }
}

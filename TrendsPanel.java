import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

/**
 * TrendsPanel.java
 * Author: Hemanth
 * HUM Foundation Donation Tracker
 *
 * Displays monthly trends for donations, volunteer hours, and aid packages
 * using static data. Rendered as a scrollable VBox tab content pane,
 * intended to be placed inside a Tab in the main TabPane.
 */
public class TrendsPanel {

    // -------------------------------------------------------------------------
    // Static monthly data (January – December)
    // -------------------------------------------------------------------------

    private static final String[] MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    /** Total monetary donations collected per month (in USD) */
    private static final double[] DONATION_AMOUNTS = {
        1200, 1850, 2100, 1750, 2400, 3100,
        2800, 3300, 2950, 3600, 4200, 5100
    };

    /** Number of individual donation drive items collected per month */
    private static final int[] ITEMS_COLLECTED = {
        80, 120, 145, 110, 160, 210,
        190, 230, 205, 250, 290, 360
    };

    /** Total volunteer hours logged per month */
    private static final double[] VOLUNTEER_HOURS = {
        45, 60, 75, 55, 90, 115,
        100, 130, 110, 140, 160, 200
    };

    /** Number of unique volunteers who participated per month */
    private static final int[] VOLUNTEER_COUNT = {
        8, 10, 13, 9, 15, 18,
        16, 20, 17, 22, 25, 30
    };

    /** Number of aid packages distributed per month */
    private static final int[] AID_PACKAGES = {
        15, 22, 28, 20, 35, 42,
        38, 48, 40, 52, 60, 75
    };

    /** Number of unique recipient families served per month */
    private static final int[] FAMILIES_SERVED = {
        10, 16, 20, 14, 25, 30,
        27, 34, 29, 38, 44, 55
    };

    // -------------------------------------------------------------------------
    // Public factory method — returns the full trends UI as a ScrollPane
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the trends panel ready to be added to a Tab or any
     * parent container.
     *
     * @return ScrollPane containing all trend charts and summary cards
     */
    public static ScrollPane buildPanel() {

        VBox root = new VBox(24);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f4f6f9;");

        // -- Section header -----------------------------------------------
        root.getChildren().add(buildSectionHeader(
            "Monthly Trends",
            "Static data overview · January – December"
        ));

        // -- Summary stat cards -------------------------------------------
        root.getChildren().add(buildSummaryCards());

        // -- Chart 1: Monetary Donations ----------------------------------
        root.getChildren().add(buildSubHeader("Donation Drive — Monthly Revenue & Items"));
        root.getChildren().add(buildDonationChart());

        // -- Chart 2: Volunteer Engagement --------------------------------
        root.getChildren().add(buildSubHeader("Volunteer Engagement — Hours & Participation"));
        root.getChildren().add(buildVolunteerChart());

        // -- Chart 3: Aid Distribution ------------------------------------
        root.getChildren().add(buildSubHeader("Aid Distribution — Packages & Families Served"));
        root.getChildren().add(buildAidChart());

        // -- Chart 4: Year-at-a-Glance (stacked area / grouped bar) ------
        root.getChildren().add(buildSubHeader("Year-at-a-Glance — Combined Activity Index"));
        root.getChildren().add(buildCombinedChart());

        // -- Peak month insight cards -------------------------------------
        root.getChildren().add(buildSubHeader("Key Insights"));
        root.getChildren().add(buildInsightCards());

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #f4f6f9;");
        return scroll;
    }

    // -------------------------------------------------------------------------
    // Summary stat cards (totals across all 12 months)
    // -------------------------------------------------------------------------

    private static HBox buildSummaryCards() {
        double totalDonations = sum(DONATION_AMOUNTS);
        int totalItems = sumInt(ITEMS_COLLECTED);
        double totalHours = sum(VOLUNTEER_HOURS);
        int totalPackages = sumInt(AID_PACKAGES);

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
            buildStatCard("$" + formatMoney(totalDonations), "Total Donations", "#2e7d32"),
            buildStatCard(String.valueOf(totalItems), "Items Collected", "#1565c0"),
            buildStatCard(String.valueOf((int) totalHours) + " hrs", "Volunteer Hours", "#6a1b9a"),
            buildStatCard(String.valueOf(totalPackages), "Aid Packages Sent", "#bf360c")
        );
        return row;
    }

    private static VBox buildStatCard(String value, String label, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setPrefWidth(160);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 0 0 0 4;" +
            "-fx-border-radius: 0 10 10 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 22));
        val.setTextFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", 12));
        lbl.setTextFill(Color.GRAY);
        lbl.setWrapText(true);

        card.getChildren().addAll(val, lbl);
        return card;
    }

    // -------------------------------------------------------------------------
    // Chart 1 — Donation amounts (line) + items collected (bar)
    // -------------------------------------------------------------------------

    private static LineChart<String, Number> buildDonationChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (USD) / Items");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(null);
        chart.setPrefHeight(280);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);

        XYChart.Series<String, Number> moneySeries = new XYChart.Series<>();
        moneySeries.setName("Donations ($)");

        XYChart.Series<String, Number> itemsSeries = new XYChart.Series<>();
        itemsSeries.setName("Items Collected");

        for (int i = 0; i < 12; i++) {
            moneySeries.getData().add(new XYChart.Data<>(MONTHS[i], DONATION_AMOUNTS[i]));
            itemsSeries.getData().add(new XYChart.Data<>(MONTHS[i], ITEMS_COLLECTED[i]));
        }

        chart.getData().addAll(moneySeries, itemsSeries);
        styleChart(chart);
        return chart;
    }

    // -------------------------------------------------------------------------
    // Chart 2 — Volunteer hours (area / line) + volunteer count (bar)
    // -------------------------------------------------------------------------

    private static AreaChart<String, Number> buildVolunteerChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Hours / # Volunteers");

        AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setTitle(null);
        chart.setPrefHeight(280);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);

        XYChart.Series<String, Number> hoursSeries = new XYChart.Series<>();
        hoursSeries.setName("Volunteer Hours");

        XYChart.Series<String, Number> countSeries = new XYChart.Series<>();
        countSeries.setName("# Volunteers");

        for (int i = 0; i < 12; i++) {
            hoursSeries.getData().add(new XYChart.Data<>(MONTHS[i], VOLUNTEER_HOURS[i]));
            countSeries.getData().add(new XYChart.Data<>(MONTHS[i], VOLUNTEER_COUNT[i]));
        }

        chart.getData().addAll(hoursSeries, countSeries);
        styleChart(chart);
        return chart;
    }

    // -------------------------------------------------------------------------
    // Chart 3 — Aid packages distributed + families served (grouped bar)
    // -------------------------------------------------------------------------

    private static BarChart<String, Number> buildAidChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(null);
        chart.setPrefHeight(280);
        chart.setAnimated(false);
        chart.setCategoryGap(12);
        chart.setBarGap(3);

        XYChart.Series<String, Number> packageSeries = new XYChart.Series<>();
        packageSeries.setName("Aid Packages");

        XYChart.Series<String, Number> familySeries = new XYChart.Series<>();
        familySeries.setName("Families Served");

        for (int i = 0; i < 12; i++) {
            packageSeries.getData().add(new XYChart.Data<>(MONTHS[i], AID_PACKAGES[i]));
            familySeries.getData().add(new XYChart.Data<>(MONTHS[i], FAMILIES_SERVED[i]));
        }

        chart.getData().addAll(packageSeries, familySeries);
        styleChart(chart);
        return chart;
    }

    // -------------------------------------------------------------------------
    // Chart 4 — Combined normalised activity index (line chart)
    //   Each metric is expressed as % of its December (peak) value so all
    //   three series fit on the same 0–100 scale.
    // -------------------------------------------------------------------------

    private static LineChart<String, Number> buildCombinedChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Activity (% of peak)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(null);
        chart.setPrefHeight(280);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);

        double peakDon = DONATION_AMOUNTS[11];
        double peakVol = VOLUNTEER_HOURS[11];
        double peakAid = AID_PACKAGES[11];

        XYChart.Series<String, Number> donSeries = new XYChart.Series<>();
        donSeries.setName("Donations");

        XYChart.Series<String, Number> volSeries = new XYChart.Series<>();
        volSeries.setName("Volunteer Hours");

        XYChart.Series<String, Number> aidSeries = new XYChart.Series<>();
        aidSeries.setName("Aid Packages");

        for (int i = 0; i < 12; i++) {
            donSeries.getData().add(new XYChart.Data<>(MONTHS[i], round1(DONATION_AMOUNTS[i] / peakDon * 100)));
            volSeries.getData().add(new XYChart.Data<>(MONTHS[i], round1(VOLUNTEER_HOURS[i] / peakVol * 100)));
            aidSeries.getData().add(new XYChart.Data<>(MONTHS[i], round1(AID_PACKAGES[i] / peakAid * 100)));
        }

        chart.getData().addAll(donSeries, volSeries, aidSeries);
        styleChart(chart);
        return chart;
    }

    // -------------------------------------------------------------------------
    // Key insight cards (peak month, growth rate, best volunteer month)
    // -------------------------------------------------------------------------

    private static HBox buildInsightCards() {
        // Find peak donation month
        int peakDonIdx = indexOfMax(DONATION_AMOUNTS);
        // Calculate growth % from Jan to Dec
        double donGrowth = (DONATION_AMOUNTS[11] - DONATION_AMOUNTS[0]) / DONATION_AMOUNTS[0] * 100;
        // Find peak volunteer month
        int peakVolIdx = indexOfMax(VOLUNTEER_HOURS);
        // Find peak aid month
        int peakAidIdx = indexOfMaxInt(AID_PACKAGES);

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(
            buildInsightCard(
                "📈 Peak Donation Month",
                MONTHS[peakDonIdx] + " — $" + formatMoney(DONATION_AMOUNTS[peakDonIdx]),
                "Highest single-month fundraising recorded"
            ),
            buildInsightCard(
                "🚀 Annual Donation Growth",
                String.format("+%.0f%%", donGrowth) + " Jan → Dec",
                "Strong upward trend across the year"
            ),
            buildInsightCard(
                "🤝 Peak Volunteer Month",
                MONTHS[peakVolIdx] + " — " + (int) VOLUNTEER_HOURS[peakVolIdx] + " hrs",
                "Most volunteer engagement logged"
            ),
            buildInsightCard(
                "📦 Most Aid Distributed",
                MONTHS[peakAidIdx] + " — " + AID_PACKAGES[peakAidIdx] + " packages",
                "Highest community reach in one month"
            )
        );
        return row;
    }

    private static VBox buildInsightCard(String title, String highlight, String subtitle) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(170);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 6, 0, 0, 2);"
        );

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        titleLbl.setTextFill(Color.web("#444"));
        titleLbl.setWrapText(true);

        Label highlightLbl = new Label(highlight);
        highlightLbl.setFont(Font.font("System", FontWeight.BOLD, 15));
        highlightLbl.setTextFill(Color.web("#1a237e"));
        highlightLbl.setWrapText(true);

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", 11));
        subLbl.setTextFill(Color.GRAY);
        subLbl.setWrapText(true);

        card.getChildren().addAll(titleLbl, highlightLbl, subLbl);
        return card;
    }

    // -------------------------------------------------------------------------
    // Shared header / sub-header builders
    // -------------------------------------------------------------------------

    private static VBox buildSectionHeader(String title, String subtitle) {
        VBox box = new VBox(4);

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLbl.setTextFill(Color.web("#1a237e"));

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", FontPosture.ITALIC, 13));
        subLbl.setTextFill(Color.GRAY);

        box.getChildren().addAll(titleLbl, subLbl);
        return box;
    }

    private static Label buildSubHeader(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        lbl.setTextFill(Color.web("#37474f"));
        lbl.setPadding(new Insets(8, 0, 0, 0));
        return lbl;
    }

    // -------------------------------------------------------------------------
    // Chart styling helper — light background, legend visible
    // -------------------------------------------------------------------------

    private static void styleChart(Chart chart) {
        chart.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        chart.setLegendVisible(true);
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
    }

    // -------------------------------------------------------------------------
    // Utility / math helpers
    // -------------------------------------------------------------------------

    private static double sum(double[] arr) {
        double s = 0;
        for (double v : arr) s += v;
        return s;
    }

    private static int sumInt(int[] arr) {
        int s = 0;
        for (int v : arr) s += v;
        return s;
    }

    private static int indexOfMax(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) if (arr[i] > arr[idx]) idx = i;
        return idx;
    }

    private static int indexOfMaxInt(int[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) if (arr[i] > arr[idx]) idx = i;
        return idx;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private static String formatMoney(double amount) {
        if (amount >= 1000) {
            return String.format("%,.0f", amount);
        }
        return String.format("%.0f", amount);
    }
}

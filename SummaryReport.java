package com.humfoundation.report;

import java.time.LocalDate;
import java.util.Map;

/**
 * Immutable snapshot of organizational impact over a date range.
 * Returned by ReportGenerator — Harri's GUI can read these fields directly.
 * Hemanth can also use DataStore directly for month-by-month breakdowns.
 */
public class SummaryReport {

    private final LocalDate from;
    private final LocalDate to;

    // Donation drive stats
    private final int totalDrives;
    private final double totalMoneyRaised;
    private final int totalItemTypesCollected;

    // Volunteer stats
    private final int totalVolunteers;
    private final double totalVolunteerHours;
    private final Map<String, Double> hoursByVolunteer; // volunteer name -> hours

    // Aid distribution stats
    private final int totalPackagesDistributed;
    private final int uniqueFamiliesServed;

    public SummaryReport(LocalDate from, LocalDate to,
                         int totalDrives, double totalMoneyRaised, int totalItemTypesCollected,
                         int totalVolunteers, double totalVolunteerHours,
                         Map<String, Double> hoursByVolunteer,
                         int totalPackagesDistributed, int uniqueFamiliesServed) {
        this.from = from;
        this.to = to;
        this.totalDrives = totalDrives;
        this.totalMoneyRaised = totalMoneyRaised;
        this.totalItemTypesCollected = totalItemTypesCollected;
        this.totalVolunteers = totalVolunteers;
        this.totalVolunteerHours = totalVolunteerHours;
        this.hoursByVolunteer = Map.copyOf(hoursByVolunteer);
        this.totalPackagesDistributed = totalPackagesDistributed;
        this.uniqueFamiliesServed = uniqueFamiliesServed;
    }

    // --- Getters ---
    public LocalDate getFrom()                    { return from; }
    public LocalDate getTo()                      { return to; }
    public int getTotalDrives()                   { return totalDrives; }
    public double getTotalMoneyRaised()           { return totalMoneyRaised; }
    public int getTotalItemTypesCollected()        { return totalItemTypesCollected; }
    public int getTotalVolunteers()               { return totalVolunteers; }
    public double getTotalVolunteerHours()        { return totalVolunteerHours; }
    public Map<String, Double> getHoursByVolunteer() { return hoursByVolunteer; }
    public int getTotalPackagesDistributed()      { return totalPackagesDistributed; }
    public int getUniqueFamiliesServed()          { return uniqueFamiliesServed; }

    /**
     * Plain-text summary — useful for quick display in the GUI or console debugging.
     */
    public String toPlainText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HUM Foundation Impact Report ===\n");
        sb.append(String.format("Period: %s to %s\n\n", from, to));

        sb.append("-- Donation Drives --\n");
        sb.append(String.format("  Drives held:          %d\n", totalDrives));
        sb.append(String.format("  Money raised:         $%.2f\n", totalMoneyRaised));
        sb.append(String.format("  Item types collected: %d\n\n", totalItemTypesCollected));

        sb.append("-- Volunteers --\n");
        sb.append(String.format("  Active volunteers:    %d\n", totalVolunteers));
        sb.append(String.format("  Total hours logged:   %.1f\n", totalVolunteerHours));
        if (!hoursByVolunteer.isEmpty()) {
            sb.append("  Hours by volunteer:\n");
            hoursByVolunteer.forEach((name, hrs) ->
                    sb.append(String.format("    %-25s %.1f hrs\n", name, hrs)));
        }
        sb.append("\n");

        sb.append("-- Aid Distribution --\n");
        sb.append(String.format("  Packages distributed: %d\n", totalPackagesDistributed));
        sb.append(String.format("  Families served:      %d\n", uniqueFamiliesServed));

        return sb.toString();
    }

    @Override
    public String toString() {
        return toPlainText();
    }
}

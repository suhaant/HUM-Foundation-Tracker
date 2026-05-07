package hum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReportGenerator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final DriveManager drives;
    private final VolunteerManager volunteers;

    public ReportGenerator(DriveManager drives, VolunteerManager volunteers) {
        this.drives = drives;
        this.volunteers = volunteers;
    }

    public String generate(LocalDate from, LocalDate to) {
        StringBuilder sb = new StringBuilder();

        sb.append("HUM FOUNDATION — IMPACT SUMMARY\n");
        sb.append("Period: ").append(from.format(FMT)).append("  to  ").append(to.format(FMT)).append("\n");
        sb.append("=".repeat(50)).append("\n\n");

        // Donations
        int count = drives.getByDateRange(from, to).size();
        double raised = drives.totalMonetary(from, to);
        Map<String, Long> cats = drives.categoryBreakdown(from, to);

        sb.append("DONATION DRIVES\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("  Drives held:        %d%n", count));
        sb.append(String.format("  Total funds raised: $%.2f%n", raised));
        if (!cats.isEmpty()) {
            sb.append("\n  Item Categories:\n");
            cats.forEach((cat, n) -> sb.append(String.format("    %-20s  %d drive(s)%n", cat, n)));
        }
        sb.append("\n");

        // Volunteers
        double hours = volunteers.totalHours(from, to);
        Map<String, Double> byPerson = volunteers.hoursByVolunteer(from, to);
        Map<String, Double> byEvent  = volunteers.hoursByEvent(from, to);

        sb.append("VOLUNTEER HOURS\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("  Total hours: %.1f%n", hours));
        if (!byPerson.isEmpty()) {
            sb.append("\n  By Volunteer:\n");
            byPerson.forEach((n, h) -> sb.append(String.format("    %-20s  %.1f hrs%n", n, h)));
        }
        if (!byEvent.isEmpty()) {
            sb.append("\n  By Event:\n");
            byEvent.forEach((ev, h) -> sb.append(String.format("    %-20s  %.1f hrs%n", ev, h)));
        }
        if (byPerson.isEmpty() && byEvent.isEmpty())
            sb.append("  No volunteer hours recorded.\n");

        return sb.toString();
    }
}

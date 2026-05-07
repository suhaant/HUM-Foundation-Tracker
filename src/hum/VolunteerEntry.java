package hum;

import java.time.LocalDate;

public class VolunteerEntry {
    public final String id;
    public final String volunteerName;
    public final String eventName;
    public final LocalDate date;
    public final double hours;

    public VolunteerEntry(String id, String volunteerName, String eventName, LocalDate date, double hours) {
        this.id = id;
        this.volunteerName = volunteerName;
        this.eventName = eventName;
        this.date = date;
        this.hours = hours;
    }
}

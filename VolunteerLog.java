package com.humfoundation.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A single record of volunteer hours for a specific event/date.
 * One volunteer can have multiple logs (one per event).
 */
public class VolunteerLog {

    private final String id;
    private final String volunteerId;
    private String eventName;
    private LocalDate date;
    private double hoursWorked;

    public VolunteerLog(String volunteerId, String eventName, LocalDate date, double hoursWorked) {
        if (volunteerId == null || volunteerId.isBlank()) throw new IllegalArgumentException("Volunteer ID required.");
        if (eventName == null || eventName.isBlank())    throw new IllegalArgumentException("Event name required.");
        if (date == null)                                throw new IllegalArgumentException("Date required.");
        if (hoursWorked <= 0)                            throw new IllegalArgumentException("Hours must be positive.");

        this.id = UUID.randomUUID().toString();
        this.volunteerId = volunteerId;
        this.eventName = eventName.trim();
        this.date = date;
        this.hoursWorked = hoursWorked;
    }

    /** Used by Database hydration to restore a log with its persisted ID. */
    public VolunteerLog(String id, String volunteerId, String eventName, LocalDate date, double hoursWorked) {
        if (volunteerId == null || volunteerId.isBlank()) throw new IllegalArgumentException("Volunteer ID required.");
        if (eventName == null || eventName.isBlank())    throw new IllegalArgumentException("Event name required.");
        if (date == null)                                throw new IllegalArgumentException("Date required.");
        if (hoursWorked <= 0)                            throw new IllegalArgumentException("Hours must be positive.");

        this.id = id;
        this.volunteerId = volunteerId;
        this.eventName = eventName.trim();
        this.date = date;
        this.hoursWorked = hoursWorked;
    }

    public String getId()          { return id; }
    public String getVolunteerId() { return volunteerId; }
    public String getEventName()   { return eventName; }
    public LocalDate getDate()     { return date; }
    public double getHoursWorked() { return hoursWorked; }

    public void setEventName(String eventName)   { this.eventName = eventName; }
    public void setDate(LocalDate date)          { this.date = date; }
    public void setHoursWorked(double hours)     {
        if (hours <= 0) throw new IllegalArgumentException("Hours must be positive.");
        this.hoursWorked = hours;
    }

    @Override
    public String toString() {
        return String.format("VolunteerLog[volunteer=%s | event=%s | %s | %.1f hrs]",
                volunteerId, eventName, date, hoursWorked);
    }
}

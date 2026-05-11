package com.humfoundation.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single donation drive event.
 * Tracks both item donations and monetary donations.
 */
public class DonationDrive {

    private final String id;
    private String name;
    private LocalDate date;
    private final List<String> itemsCollected; // e.g. "50 canned goods", "12 blankets"
    private double monetaryTotal;
    private String notes;

    public DonationDrive(String name, LocalDate date) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Drive name cannot be empty.");
        if (date == null) throw new IllegalArgumentException("Date cannot be null.");
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.date = date;
        this.itemsCollected = new ArrayList<>();
        this.monetaryTotal = 0.0;
        this.notes = "";
    }

    /** Used by Database hydration to restore a drive with its persisted ID. */
    public DonationDrive(String id, String name, LocalDate date) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Drive name cannot be empty.");
        if (date == null) throw new IllegalArgumentException("Date cannot be null.");
        this.id = id;
        this.name = name;
        this.date = date;
        this.itemsCollected = new ArrayList<>();
        this.monetaryTotal = 0.0;
        this.notes = "";
    }

    // --- Mutators ---

    public void addItem(String itemDescription) {
        if (itemDescription != null && !itemDescription.isBlank()) {
            itemsCollected.add(itemDescription.trim());
        }
    }

    public void addMonetaryDonation(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Donation amount cannot be negative.");
        this.monetaryTotal += amount;
    }

    /** Used only by Database hydration — sets the total directly rather than adding to it. */
    public void setMonetaryTotal(double total) {
        if (total < 0) throw new IllegalArgumentException("Total cannot be negative.");
        this.monetaryTotal = total;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;
    }

    public void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date cannot be null.");
        this.date = date;
    }

    // --- Accessors ---

    public String getId()                     { return id; }
    public String getName()                   { return name; }
    public LocalDate getDate()                { return date; }
    public List<String> getItemsCollected()   { return Collections.unmodifiableList(itemsCollected); }
    public double getMonetaryTotal()          { return monetaryTotal; }
    public String getNotes()                  { return notes; }

    @Override
    public String toString() {
        return String.format("DonationDrive[%s | %s | $%.2f | %d item types]",
                name, date, monetaryTotal, itemsCollected.size());
    }
}

package com.humfoundation.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Records a single aid package distributed to a recipient family.
 */
public class AidPackage {

    private final String id;
    private String recipientFamily;   // family name or identifier
    private LocalDate dateDistributed;
    private String packageDescription; // e.g. "Winter care package: blankets, food, hygiene"
    private String notes;

    public AidPackage(String recipientFamily, LocalDate dateDistributed, String packageDescription) {
        if (recipientFamily == null || recipientFamily.isBlank())
            throw new IllegalArgumentException("Recipient family cannot be empty.");
        if (dateDistributed == null)
            throw new IllegalArgumentException("Distribution date required.");
        if (packageDescription == null || packageDescription.isBlank())
            throw new IllegalArgumentException("Package description required.");

        this.id = UUID.randomUUID().toString();
        this.recipientFamily = recipientFamily.trim();
        this.dateDistributed = dateDistributed;
        this.packageDescription = packageDescription.trim();
        this.notes = "";
    }

    /** Used by Database hydration to restore a package with its persisted ID. */
    public AidPackage(String id, String recipientFamily, LocalDate dateDistributed, String packageDescription) {
        if (recipientFamily == null || recipientFamily.isBlank())
            throw new IllegalArgumentException("Recipient family cannot be empty.");
        if (dateDistributed == null)
            throw new IllegalArgumentException("Distribution date required.");
        if (packageDescription == null || packageDescription.isBlank())
            throw new IllegalArgumentException("Package description required.");

        this.id = id;
        this.recipientFamily = recipientFamily.trim();
        this.dateDistributed = dateDistributed;
        this.packageDescription = packageDescription.trim();
        this.notes = "";
    }

    public String getId()                 { return id; }
    public String getRecipientFamily()    { return recipientFamily; }
    public LocalDate getDateDistributed() { return dateDistributed; }
    public String getPackageDescription() { return packageDescription; }
    public String getNotes()              { return notes; }

    public void setRecipientFamily(String family)        { this.recipientFamily = family; }
    public void setDateDistributed(LocalDate date)       { this.dateDistributed = date; }
    public void setPackageDescription(String desc)       { this.packageDescription = desc; }
    public void setNotes(String notes)                   { this.notes = notes != null ? notes : ""; }

    @Override
    public String toString() {
        return String.format("AidPackage[family=%s | %s | %s]",
                recipientFamily, dateDistributed, packageDescription);
    }
}

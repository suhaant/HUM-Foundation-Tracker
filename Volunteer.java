package com.humfoundation.model;

import java.util.UUID;

/**
 * Represents a registered volunteer.
 */
public class Volunteer {

    private final String id;
    private String name;
    private String email;   // optional contact info

    public Volunteer(String name, String email) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Volunteer name cannot be empty.");
        this.id = UUID.randomUUID().toString();
        this.name = name.trim();
        this.email = email != null ? email.trim() : "";
    }

    /** Used by Database hydration to restore a volunteer with its persisted ID. */
    public Volunteer(String id, String name, String email) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Volunteer name cannot be empty.");
        this.id = id;
        this.name = name.trim();
        this.email = email != null ? email.trim() : "";
    }

    public Volunteer(String name) {
        this(name, "");
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email != null ? email : ""; }

    @Override
    public String toString() {
        return String.format("Volunteer[%s | %s]", name, email.isBlank() ? "no email" : email);
    }
}

package com.humfoundation.service;

import com.humfoundation.model.AidPackage;
import com.humfoundation.model.DonationDrive;
import com.humfoundation.model.Volunteer;
import com.humfoundation.model.VolunteerLog;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Saves and loads all data as CSV files in a local "hum_data/" folder.
 * Zero external dependencies — pure Java standard library.
 *
 * Files created:
 *   hum_data/drives.csv
 *   hum_data/drive_items.csv
 *   hum_data/volunteers.csv
 *   hum_data/volunteer_logs.csv
 *   hum_data/aid_packages.csv
 */
public class FileStore {

    private static final String DATA_DIR = "hum_data";

    private static final String DRIVES_FILE      = DATA_DIR + "/drives.csv";
    private static final String ITEMS_FILE       = DATA_DIR + "/drive_items.csv";
    private static final String VOLUNTEERS_FILE  = DATA_DIR + "/volunteers.csv";
    private static final String LOGS_FILE        = DATA_DIR + "/volunteer_logs.csv";
    private static final String PACKAGES_FILE    = DATA_DIR + "/aid_packages.csv";

    // In-memory maps — loaded from disk on startup, written on every change
    private final Map<String, DonationDrive> drives    = new LinkedHashMap<>();
    private final Map<String, Volunteer>     volunteers = new LinkedHashMap<>();
    private final Map<String, VolunteerLog>  logs       = new LinkedHashMap<>();
    private final Map<String, AidPackage>    packages   = new LinkedHashMap<>();

    // drive_items are stored inside DonationDrive objects; we persist them separately
    // drive_id -> list of item descriptions
    private final Map<String, List<String>>  driveItems = new LinkedHashMap<>();

    // --- Singleton ---
    private static FileStore instance;
    public static FileStore getInstance() {
        if (instance == null) instance = new FileStore();
        return instance;
    }

    private FileStore() {
        new File(DATA_DIR).mkdirs();
        loadAll();
    }

    // ======================== LOAD ========================

    private void loadAll() {
        loadVolunteers();
        loadDrives();
        loadDriveItems();
        loadLogs();
        loadPackages();
    }

    private void loadVolunteers() {
        for (String[] row : readCsv(VOLUNTEERS_FILE)) {
            // id, name, email
            if (row.length < 3) continue;
            volunteers.put(row[0], new Volunteer(row[0], row[1], row[2]));
        }
    }

    private void loadDrives() {
        for (String[] row : readCsv(DRIVES_FILE)) {
            // id, name, date, monetaryTotal, notes
            if (row.length < 5) continue;
            DonationDrive d = new DonationDrive(row[0], row[1], LocalDate.parse(row[2]));
            d.setMonetaryTotal(Double.parseDouble(row[3]));
            d.setNotes(row[4]);
            drives.put(d.getId(), d);
        }
    }

    private void loadDriveItems() {
        for (String[] row : readCsv(ITEMS_FILE)) {
            // driveId, description
            if (row.length < 2) continue;
            String driveId = row[0];
            String desc    = row[1];
            driveItems.computeIfAbsent(driveId, k -> new ArrayList<>()).add(desc);
            // Also add to the in-memory DonationDrive object
            DonationDrive d = drives.get(driveId);
            if (d != null) d.addItem(desc);
        }
    }

    private void loadLogs() {
        for (String[] row : readCsv(LOGS_FILE)) {
            // id, volunteerId, eventName, date, hoursWorked
            if (row.length < 5) continue;
            VolunteerLog log = new VolunteerLog(
                    row[0], row[1], row[2], LocalDate.parse(row[3]),
                    Double.parseDouble(row[4])
            );
            logs.put(log.getId(), log);
        }
    }

    private void loadPackages() {
        for (String[] row : readCsv(PACKAGES_FILE)) {
            // id, recipientFamily, dateDistributed, packageDescription, notes
            if (row.length < 5) continue;
            AidPackage pkg = new AidPackage(
                    row[0], row[1], LocalDate.parse(row[2]), row[3]
            );
            pkg.setNotes(row[4]);
            packages.put(pkg.getId(), pkg);
        }
    }

    // ======================== SAVE ========================

    private void saveDrives() {
        List<String[]> rows = new ArrayList<>();
        for (DonationDrive d : drives.values()) {
            rows.add(new String[]{d.getId(), d.getName(), d.getDate().toString(),
                    String.valueOf(d.getMonetaryTotal()), d.getNotes()});
        }
        writeCsv(DRIVES_FILE, rows);
    }

    private void saveDriveItems() {
        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : driveItems.entrySet()) {
            for (String item : entry.getValue()) {
                rows.add(new String[]{entry.getKey(), item});
            }
        }
        writeCsv(ITEMS_FILE, rows);
    }

    private void saveVolunteers() {
        List<String[]> rows = new ArrayList<>();
        for (Volunteer v : volunteers.values()) {
            rows.add(new String[]{v.getId(), v.getName(), v.getEmail()});
        }
        writeCsv(VOLUNTEERS_FILE, rows);
    }

    private void saveLogs() {
        List<String[]> rows = new ArrayList<>();
        for (VolunteerLog l : logs.values()) {
            rows.add(new String[]{l.getId(), l.getVolunteerId(), l.getEventName(),
                    l.getDate().toString(), String.valueOf(l.getHoursWorked())});
        }
        writeCsv(LOGS_FILE, rows);
    }

    private void savePackages() {
        List<String[]> rows = new ArrayList<>();
        for (AidPackage p : packages.values()) {
            rows.add(new String[]{p.getId(), p.getRecipientFamily(),
                    p.getDateDistributed().toString(), p.getPackageDescription(), p.getNotes()});
        }
        writeCsv(PACKAGES_FILE, rows);
    }

    // ======================== PUBLIC API ========================

    // --- Drives ---
    public void addDrive(DonationDrive d) {
        drives.put(d.getId(), d);
        driveItems.put(d.getId(), new ArrayList<>());
        saveDrives();
    }

    public void updateDrive(DonationDrive d) {
        drives.put(d.getId(), d);
        saveDrives();
    }

    public void addDriveItem(String driveId, String item) {
        driveItems.computeIfAbsent(driveId, k -> new ArrayList<>()).add(item);
        DonationDrive d = drives.get(driveId);
        if (d != null) d.addItem(item);
        saveDriveItems();
    }

    public boolean removeDrive(String id) {
        boolean removed = drives.remove(id) != null;
        driveItems.remove(id);
        if (removed) { saveDrives(); saveDriveItems(); }
        return removed;
    }

    public Optional<DonationDrive> getDrive(String id)  { return Optional.ofNullable(drives.get(id)); }
    public List<DonationDrive>     getAllDrives()        { return new ArrayList<>(drives.values()); }

    // --- Volunteers ---
    public void addVolunteer(Volunteer v) {
        volunteers.put(v.getId(), v);
        saveVolunteers();
    }

    public boolean removeVolunteer(String id) {
        boolean removed = volunteers.remove(id) != null;
        if (removed) saveVolunteers();
        return removed;
    }

    public Optional<Volunteer> getVolunteer(String id) { return Optional.ofNullable(volunteers.get(id)); }
    public List<Volunteer>     getAllVolunteers()       { return new ArrayList<>(volunteers.values()); }

    // --- Volunteer Logs ---
    public void addLog(VolunteerLog log) {
        logs.put(log.getId(), log);
        saveLogs();
    }

    public boolean removeLog(String id) {
        boolean removed = logs.remove(id) != null;
        if (removed) saveLogs();
        return removed;
    }

    public List<VolunteerLog> getAllLogs()               { return new ArrayList<>(logs.values()); }
    public List<VolunteerLog> getLogsFor(String volId)  {
        List<VolunteerLog> result = new ArrayList<>();
        for (VolunteerLog l : logs.values()) if (l.getVolunteerId().equals(volId)) result.add(l);
        return result;
    }

    // --- Aid Packages ---
    public void addPackage(AidPackage pkg) {
        packages.put(pkg.getId(), pkg);
        savePackages();
    }

    public boolean removePackage(String id) {
        boolean removed = packages.remove(id) != null;
        if (removed) savePackages();
        return removed;
    }

    public Optional<AidPackage> getPackage(String id) { return Optional.ofNullable(packages.get(id)); }
    public List<AidPackage>     getAllPackages()       { return new ArrayList<>(packages.values()); }

    // ======================== CSV HELPERS ========================

    private List<String[]> readCsv(String path) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return rows;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) rows.add(parseCsvLine(line));
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read " + path + ": " + e.getMessage());
        }
        return rows;
    }

    private void writeCsv(String path, List<String[]> rows) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String[] row : rows) pw.println(toCsvLine(row));
        } catch (IOException e) {
            System.err.println("Warning: could not write " + path + ": " + e.getMessage());
        }
    }

    /** Encodes a row to CSV, quoting fields that contain commas or quotes. */
    private String toCsvLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String f = fields[i] == null ? "" : fields[i];
            if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                sb.append('"').append(f.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    /** Parses a CSV line respecting quoted fields. */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"'); i++; // escaped quote
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') { inQuotes = true; }
                else if (c == ',') { fields.add(cur.toString()); cur.setLength(0); }
                else { cur.append(c); }
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }
}

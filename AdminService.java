package com.humfoundation.service;

import com.humfoundation.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * AdminService — the only class Harri's GUI should call.
 * Backed by FileStore (CSV files in hum_data/).
 */
public class AdminService {

    private final FileStore store = FileStore.getInstance();

    // ==================== DONATION DRIVES ====================

    public DonationDrive createDonationDrive(String name, LocalDate date) {
        DonationDrive d = new DonationDrive(name, date);
        store.addDrive(d);
        return d;
    }

    public void addItemToDrive(String driveId, String item) {
        findDriveOrThrow(driveId);
        store.addDriveItem(driveId, item);
    }

    public void addMoneyToDrive(String driveId, double amount) {
        DonationDrive d = findDriveOrThrow(driveId);
        d.addMonetaryDonation(amount);
        store.updateDrive(d);
    }

    public void updateDriveName(String driveId, String name) {
        DonationDrive d = findDriveOrThrow(driveId);
        d.setName(name);
        store.updateDrive(d);
    }

    public void updateDriveNotes(String driveId, String notes) {
        DonationDrive d = findDriveOrThrow(driveId);
        d.setNotes(notes);
        store.updateDrive(d);
    }

    public boolean deleteDonationDrive(String driveId)      { return store.removeDrive(driveId); }
    public List<DonationDrive> getAllDonationDrives()        { return store.getAllDrives(); }
    public Optional<DonationDrive> getDonationDrive(String id) { return store.getDrive(id); }

    // ==================== VOLUNTEERS ====================

    public Volunteer registerVolunteer(String name, String email) {
        Volunteer v = new Volunteer(name, email);
        store.addVolunteer(v);
        return v;
    }

    public Volunteer registerVolunteer(String name) { return registerVolunteer(name, ""); }

    public boolean removeVolunteer(String id)       { return store.removeVolunteer(id); }
    public List<Volunteer> getAllVolunteers()        { return store.getAllVolunteers(); }
    public Optional<Volunteer> getVolunteer(String id) { return store.getVolunteer(id); }

    // ==================== VOLUNTEER HOURS ====================

    public VolunteerLog logVolunteerHours(String volunteerId, String eventName,
                                          LocalDate date, double hours) {
        store.getVolunteer(volunteerId)
             .orElseThrow(() -> new IllegalArgumentException("No volunteer with ID: " + volunteerId));
        VolunteerLog log = new VolunteerLog(volunteerId, eventName, date, hours);
        store.addLog(log);
        return log;
    }

    public boolean deleteVolunteerLog(String logId) { return store.removeLog(logId); }

    public double getTotalHoursForVolunteer(String volunteerId) {
        return store.getLogsFor(volunteerId).stream()
                    .mapToDouble(VolunteerLog::getHoursWorked).sum();
    }

    public List<VolunteerLog> getLogsForVolunteer(String volunteerId) { return store.getLogsFor(volunteerId); }
    public List<VolunteerLog> getAllVolunteerLogs()                    { return store.getAllLogs(); }

    // ==================== AID PACKAGES ====================

    public AidPackage recordAidPackage(String family, LocalDate date, String description) {
        AidPackage pkg = new AidPackage(family, date, description);
        store.addPackage(pkg);
        return pkg;
    }

    public boolean deleteAidPackage(String id)    { return store.removePackage(id); }
    public List<AidPackage> getAllAidPackages()    { return store.getAllPackages(); }
    public Optional<AidPackage> getAidPackage(String id) { return store.getPackage(id); }

    // ==================== HELPERS ====================

    private DonationDrive findDriveOrThrow(String id) {
        return store.getDrive(id)
                    .orElseThrow(() -> new IllegalArgumentException("No donation drive with ID: " + id));
    }
}

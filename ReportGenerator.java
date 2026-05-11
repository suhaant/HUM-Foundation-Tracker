package com.humfoundation.report;

import com.humfoundation.model.*;
import com.humfoundation.service.FileStore;

import java.time.LocalDate;
import java.util.*;

public class ReportGenerator {

    private final FileStore store = FileStore.getInstance();

    public SummaryReport generate(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) throw new IllegalArgumentException("'from' must be before 'to'.");

        List<DonationDrive> drives = store.getAllDrives().stream()
                .filter(d -> !d.getDate().isBefore(from) && !d.getDate().isAfter(to)).toList();

        List<VolunteerLog> logs = store.getAllLogs().stream()
                .filter(l -> !l.getDate().isBefore(from) && !l.getDate().isAfter(to)).toList();

        Set<String> activeIds = new HashSet<>();
        Map<String, Double> hoursByName = new LinkedHashMap<>();
        for (VolunteerLog l : logs) {
            activeIds.add(l.getVolunteerId());
            String name = store.getVolunteer(l.getVolunteerId())
                               .map(Volunteer::getName).orElse("Unknown");
            hoursByName.merge(name, l.getHoursWorked(), Double::sum);
        }

        List<AidPackage> pkgs = store.getAllPackages().stream()
                .filter(p -> !p.getDateDistributed().isBefore(from) && !p.getDateDistributed().isAfter(to)).toList();

        long uniqueFamilies = pkgs.stream().map(AidPackage::getRecipientFamily).distinct().count();

        return new SummaryReport(from, to,
                drives.size(),
                drives.stream().mapToDouble(DonationDrive::getMonetaryTotal).sum(),
                drives.stream().mapToInt(d -> d.getItemsCollected().size()).sum(),
                activeIds.size(),
                logs.stream().mapToDouble(VolunteerLog::getHoursWorked).sum(),
                hoursByName,
                pkgs.size(), (int) uniqueFamilies);
    }

    public SummaryReport generateYearToDate() {
        return generate(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
    }

    public SummaryReport generateAllTime() {
        return generate(LocalDate.of(2000, 1, 1), LocalDate.of(2100, 1, 1));
    }
}

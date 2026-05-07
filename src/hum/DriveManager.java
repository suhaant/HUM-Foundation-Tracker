package hum;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DriveManager {

    private static final Path FILE = Paths.get("data", "donation_drives.csv");
    private static final String HEADER = "id,name,date,itemCategories,monetaryTotal";

    public DriveManager() {
        try {
            Files.createDirectories(FILE.getParent());
            if (!Files.exists(FILE))
                Files.writeString(FILE, HEADER + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Cannot init donation_drives.csv", e);
        }
    }

    // ---------- write ----------

    public void add(String name, LocalDate date, String categories, double total) {
        String id = UUID.randomUUID().toString();
        List<DonationDrive> all = getAll();
        all.add(new DonationDrive(id, name, date, categories, total));
        saveAll(all);
    }

    public void delete(String id) {
        List<DonationDrive> all = getAll();
        all.removeIf(d -> d.id.equals(id));
        saveAll(all);
    }

    // ---------- read ----------

    public List<DonationDrive> getAll() {
        List<DonationDrive> list = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(FILE)) {
            r.readLine(); // skip header
            String line;
            while ((line = r.readLine()) != null)
                if (!line.isBlank()) list.add(parse(line));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read donation_drives.csv", e);
        }
        return list;
    }

    public List<DonationDrive> getByDateRange(LocalDate from, LocalDate to) {
        return getAll().stream()
                .filter(d -> !d.date.isBefore(from) && !d.date.isAfter(to))
                .collect(Collectors.toList());
    }

    public double totalMonetary(LocalDate from, LocalDate to) {
        return getByDateRange(from, to).stream().mapToDouble(d -> d.monetaryTotal).sum();
    }

    public Map<String, Long> categoryBreakdown(LocalDate from, LocalDate to) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (DonationDrive d : getByDateRange(from, to))
            for (String cat : d.categoryList()) {
                String t = cat.trim();
                if (!t.isEmpty()) counts.merge(t, 1L, Long::sum);
            }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ---------- internal ----------

    private void saveAll(List<DonationDrive> drives) {
        try (BufferedWriter w = Files.newBufferedWriter(FILE)) {
            w.write(HEADER); w.newLine();
            for (DonationDrive d : drives) { w.write(toCsv(d)); w.newLine(); }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write donation_drives.csv", e);
        }
    }

    private DonationDrive parse(String line) {
        List<String> f = CsvUtil.parseLine(line);
        return new DonationDrive(f.get(0), f.get(1), LocalDate.parse(f.get(2)), f.get(3),
                Double.parseDouble(f.get(4)));
    }

    private String toCsv(DonationDrive d) {
        return String.join(",", CsvUtil.escape(d.id), CsvUtil.escape(d.name),
                d.date.toString(), CsvUtil.escape(d.itemCategories), String.valueOf(d.monetaryTotal));
    }
}

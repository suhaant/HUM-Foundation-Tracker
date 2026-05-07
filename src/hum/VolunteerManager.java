package hum;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VolunteerManager {

    private static final Path FILE = Paths.get("data", "volunteer_entries.csv");
    private static final String HEADER = "id,volunteerName,eventName,date,hours";

    public VolunteerManager() {
        try {
            Files.createDirectories(FILE.getParent());
            if (!Files.exists(FILE))
                Files.writeString(FILE, HEADER + System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("Cannot init volunteer_entries.csv", e);
        }
    }

    // ---------- write ----------

    public void log(String name, String event, LocalDate date, double hours) {
        String id = UUID.randomUUID().toString();
        List<VolunteerEntry> all = getAll();
        all.add(new VolunteerEntry(id, name, event, date, hours));
        saveAll(all);
    }

    public void delete(String id) {
        List<VolunteerEntry> all = getAll();
        all.removeIf(e -> e.id.equals(id));
        saveAll(all);
    }

    // ---------- read ----------

    public List<VolunteerEntry> getAll() {
        List<VolunteerEntry> list = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(FILE)) {
            r.readLine(); // skip header
            String line;
            while ((line = r.readLine()) != null)
                if (!line.isBlank()) list.add(parse(line));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read volunteer_entries.csv", e);
        }
        return list;
    }

    public List<VolunteerEntry> getByDateRange(LocalDate from, LocalDate to) {
        return getAll().stream()
                .filter(e -> !e.date.isBefore(from) && !e.date.isAfter(to))
                .collect(Collectors.toList());
    }

    public double totalHours(LocalDate from, LocalDate to) {
        return getByDateRange(from, to).stream().mapToDouble(e -> e.hours).sum();
    }

    public double cumulativeHoursFor(String volunteerName) {
        return getAll().stream()
                .filter(e -> e.volunteerName.equalsIgnoreCase(volunteerName))
                .mapToDouble(e -> e.hours).sum();
    }

    public Map<String, Double> hoursByVolunteer(LocalDate from, LocalDate to) {
        return sumAndSort(getByDateRange(from, to), e -> e.volunteerName);
    }

    public Map<String, Double> hoursByEvent(LocalDate from, LocalDate to) {
        return sumAndSort(getByDateRange(from, to), e -> e.eventName);
    }

    // ---------- internal ----------

    private Map<String, Double> sumAndSort(List<VolunteerEntry> entries,
            java.util.function.Function<VolunteerEntry, String> key) {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (VolunteerEntry e : entries) totals.merge(key.apply(e), e.hours, Double::sum);
        return totals.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    private void saveAll(List<VolunteerEntry> entries) {
        try (BufferedWriter w = Files.newBufferedWriter(FILE)) {
            w.write(HEADER); w.newLine();
            for (VolunteerEntry e : entries) { w.write(toCsv(e)); w.newLine(); }
        } catch (IOException e) {
            throw new RuntimeException("Cannot write volunteer_entries.csv", e);
        }
    }

    private VolunteerEntry parse(String line) {
        List<String> f = CsvUtil.parseLine(line);
        return new VolunteerEntry(f.get(0), f.get(1), f.get(2), LocalDate.parse(f.get(3)),
                Double.parseDouble(f.get(4)));
    }

    private String toCsv(VolunteerEntry e) {
        return String.join(",", CsvUtil.escape(e.id), CsvUtil.escape(e.volunteerName),
                CsvUtil.escape(e.eventName), e.date.toString(), String.valueOf(e.hours));
    }
}

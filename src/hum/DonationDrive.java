package hum;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DonationDrive {
    public final String id;
    public final String name;
    public final LocalDate date;
    public final String itemCategories; // semicolon-separated
    public final double monetaryTotal;

    public DonationDrive(String id, String name, LocalDate date, String itemCategories, double monetaryTotal) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.itemCategories = itemCategories;
        this.monetaryTotal = monetaryTotal;
    }

    public List<String> categoryList() {
        if (itemCategories == null || itemCategories.isBlank()) return List.of();
        return Arrays.asList(itemCategories.split(";"));
    }
}

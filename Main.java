package com.humfoundation;

import com.humfoundation.model.*;
import com.humfoundation.report.ReportGenerator;
import com.humfoundation.service.AdminService;

import java.time.LocalDate;

/**
 * Run this to test the backend. Data is saved to hum_data/ folder.
 * Run twice — second run loads from disk and shows the same report.
 */
public class Main {

    public static void main(String[] args) {

        AdminService admin = new AdminService();

        if (admin.getAllDonationDrives().isEmpty()) {
            System.out.println("No data found — seeding...\n");

            DonationDrive spring = admin.createDonationDrive("Spring Care Drive", LocalDate.of(2025, 3, 15));
            admin.addItemToDrive(spring.getId(), "120 canned goods");
            admin.addItemToDrive(spring.getId(), "40 blankets");
            admin.addMoneyToDrive(spring.getId(), 875.00);

            DonationDrive eid = admin.createDonationDrive("Eid Package Drive", LocalDate.of(2025, 4, 1));
            admin.addItemToDrive(eid.getId(), "60 hygiene kits");
            admin.addMoneyToDrive(eid.getId(), 1200.00);

            Volunteer asha   = admin.registerVolunteer("Asha Patel", "asha@email.com");
            Volunteer marcus = admin.registerVolunteer("Marcus Webb");
            Volunteer lena   = admin.registerVolunteer("Lena Kim", "lena@email.com");

            admin.logVolunteerHours(asha.getId(),   "Spring Care Drive", LocalDate.of(2025, 3, 15), 5.0);
            admin.logVolunteerHours(marcus.getId(), "Spring Care Drive", LocalDate.of(2025, 3, 15), 4.5);
            admin.logVolunteerHours(lena.getId(),   "Eid Package Drive", LocalDate.of(2025, 4, 1),  6.0);
            admin.logVolunteerHours(asha.getId(),   "Eid Package Drive", LocalDate.of(2025, 4, 1),  3.0);

            admin.recordAidPackage("Al-Rashid Family", LocalDate.of(2025, 3, 20), "Winter care: blankets, food, hygiene");
            admin.recordAidPackage("Nguyen Family",    LocalDate.of(2025, 3, 22), "Food bundle + diapers");
            admin.recordAidPackage("Al-Rashid Family", LocalDate.of(2025, 4, 5),  "Eid package");
            admin.recordAidPackage("Torres Family",    LocalDate.of(2025, 4, 5),  "Eid package");
        } else {
            System.out.println("Loaded existing data from hum_data/\n");
        }

        System.out.println(new ReportGenerator().generateAllTime().toPlainText());
    }
}

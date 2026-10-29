package com.example.bloodlink.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.bloodlink.dto.BloodRequestForm;
import com.example.bloodlink.model.BloodGroup;
import com.example.bloodlink.model.Donor;
import com.example.bloodlink.model.Hospital;
import com.example.bloodlink.model.Urgency;
import com.example.bloodlink.repository.DonorRepository;
import com.example.bloodlink.repository.HospitalRepository;
import com.example.bloodlink.service.BloodRequestService;
import com.example.bloodlink.service.StockService;

// puts some demo data in an empty database so the APIs have something to show
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final BloodRequestService requestService;
    private final StockService stockService;

    public DataSeeder(DonorRepository donorRepository,
                      HospitalRepository hospitalRepository,
                      BloodRequestService requestService,
                      StockService stockService) {
        this.donorRepository = donorRepository;
        this.hospitalRepository = hospitalRepository;
        this.requestService = requestService;
        this.stockService = stockService;
    }

    @Override
    public void run(String... args) {
        if (donorRepository.count() > 0) {
            return;
        }

        Hospital sanjeevani = hospitalRepository.save(
                new Hospital("Sanjeevani Hospital", "Ahmedabad", "0792650123", "GJ-AMD-1021"));
        Hospital lifeline = hospitalRepository.save(
                new Hospital("LifeLine Multispeciality", "Surat", "0261245566", "GJ-SRT-2210"));
        hospitalRepository.save(
                new Hospital("Apex Care Hospital", "Vadodara", "0265233417", "GJ-VAD-0874"));

        donor("Rohan Patel", "rohan.patel@mail.com", "9876501234", BloodGroup.O_POS, "Ahmedabad", "1998-04-12", 120);
        donor("Meera Shah", "meera.shah92@mail.com", "9825012345", BloodGroup.A_POS, "Ahmedabad", "1995-09-03", null);
        donor("Karan Desai", "karan.desai@mail.com", "9898123456", BloodGroup.B_POS, "Ahmedabad", "2000-01-22", 30);
        donor("Anjali Mehta", "anjali.m@mail.com", "9712345670", BloodGroup.O_NEG, "Ahmedabad", "1993-11-30", 200);
        donor("Sagar Bhatt", "sagar.bhatt@mail.com", "9687654321", BloodGroup.A_POS, "Ahmedabad", "1994-03-15", 150);
        donor("Dhruv Solanki", "dhruv.s@mail.com", "9726123456", BloodGroup.O_POS, "Ahmedabad", "1996-08-27", 91);
        donor("Vivek Joshi", "vivek.joshi@mail.com", "9033456781", BloodGroup.AB_POS, "Surat", "1990-06-18", 95);
        donor("Pooja Trivedi", "pooja.t@mail.com", "9429876543", BloodGroup.A_NEG, "Surat", "1997-02-08", null);
        donor("Harsh Vyas", "harsh.vyas@mail.com", "9824567890", BloodGroup.B_NEG, "Vadodara", "1989-12-25", 400);
        donor("Nidhi Parekh", "nidhi.parekh@mail.com", "9979012345", BloodGroup.O_POS, "Vadodara", "2001-07-07", 60);

        stockService.add(BloodGroup.O_POS, 8);
        stockService.add(BloodGroup.A_POS, 3);
        stockService.add(BloodGroup.B_POS, 2);
        stockService.add(BloodGroup.AB_NEG, 1);

        LocalDate today = LocalDate.now();
        requestService.create(new BloodRequestForm(sanjeevani.getId(), "Ramesh Kumar",
                BloodGroup.A_POS, 2, Urgency.URGENT, today.plusDays(2)));
        requestService.create(new BloodRequestForm(sanjeevani.getId(), "Baby of Kavita Rana",
                BloodGroup.O_NEG, 1, Urgency.CRITICAL, today));
        requestService.create(new BloodRequestForm(lifeline.getId(), "Sunita Gandhi",
                BloodGroup.AB_POS, 1, Urgency.NORMAL, today.plusDays(5)));

        log.info("Demo data loaded: 3 hospitals, 10 donors, 3 requests, some stock");
    }

    // daysSinceLastDonation == null means the person never donated
    private void donor(String name, String email, String phone, BloodGroup group,
                       String city, String dob, Integer daysSinceLastDonation) {
        Donor d = new Donor();
        d.setFullName(name);
        d.setEmail(email);
        d.setPhone(phone);
        d.setBloodGroup(group);
        d.setCity(city);
        d.setDateOfBirth(LocalDate.parse(dob));
        if (daysSinceLastDonation != null) {
            d.setLastDonationDate(LocalDate.now().minusDays(daysSinceLastDonation));
        }
        donorRepository.save(d);
    }
}

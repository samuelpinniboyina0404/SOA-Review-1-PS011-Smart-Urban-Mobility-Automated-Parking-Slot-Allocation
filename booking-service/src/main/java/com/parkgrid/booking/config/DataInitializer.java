package com.parkgrid.booking.config;

import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookingRepository.count() == 0) {
            Booking b1 = new Booking("PG-SEED1001", 2L, "user@parkgrid.com", 10L, 1L, "KA-05-EV-9999", "CAR", 3.0);
            b1.setStartTime(LocalDateTime.now().minusHours(2));
            b1.setEndTime(LocalDateTime.now().plusHours(1));
            bookingRepository.save(b1);

            System.out.println(">>> Seeded default booking PG-SEED1001 for demo user!");
        }
    }
}

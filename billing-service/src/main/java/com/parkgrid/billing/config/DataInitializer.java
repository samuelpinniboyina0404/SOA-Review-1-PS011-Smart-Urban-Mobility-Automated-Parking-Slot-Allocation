package com.parkgrid.billing.config;

import com.parkgrid.billing.dto.BillingCalculationRequest;
import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BillingService billingService;

    @Override
    public void run(String... args) throws Exception {
        BillingCalculationRequest req = new BillingCalculationRequest(
                1001L,
                2L,
                "user@parkgrid.com",
                10L,
                1L,
                "CAR",
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now()
        );
        Invoice inv = billingService.calculateAndGenerateInvoice(req);
        System.out.println(">>> Seeded default invoice " + inv.getInvoiceNumber() + " with total amount $" + inv.getTotalAmount());
    }
}

package com.parkgrid.billing;

import com.parkgrid.billing.dto.BillingCalculationRequest;
import com.parkgrid.billing.dto.PaymentRequest;
import com.parkgrid.billing.entity.Invoice;
import com.parkgrid.billing.service.BillingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
public class BillingServiceTest {

    @Autowired
    private BillingService billingService;

    @Test
    public void testDynamicTariffCalculationForEV() {
        BillingCalculationRequest request = new BillingCalculationRequest(
                2001L,
                2L,
                "evdriver@parkgrid.com",
                15L,
                2L,
                "EV",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now()
        );

        Invoice inv = billingService.calculateAndGenerateInvoice(request);
        assertNotNull(inv);
        assertNotNull(inv.getInvoiceNumber());
        assertEquals("PENDING", inv.getPaymentStatus());
        assertTrue(inv.getTotalAmount() > 0, "Calculated total amount should be positive");
    }

    @Test
    public void testPaymentProcessingAndRevenueStats() {
        BillingCalculationRequest request = new BillingCalculationRequest(
                2002L,
                2L,
                "user@parkgrid.com",
                16L,
                1L,
                "CAR",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        Invoice inv = billingService.calculateAndGenerateInvoice(request);
        Invoice paid = billingService.processPayment(inv.getId(), new PaymentRequest("UPI", inv.getTotalAmount()));

        assertEquals("PAID", paid.getPaymentStatus());
        assertEquals("UPI", paid.getPaymentMethod());

        Map<String, Object> stats = billingService.getRevenueAnalytics();
        assertNotNull(stats);
        assertTrue((Double) stats.get("totalRevenue") > 0);
    }
}

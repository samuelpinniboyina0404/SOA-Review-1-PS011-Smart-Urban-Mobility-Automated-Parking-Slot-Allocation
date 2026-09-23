package com.parkgrid.booking.client;

import com.parkgrid.booking.dto.BillingCalculationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "billing-service", url = "${billing.service.url:}")
public interface BillingClient {

    @PostMapping("/api/billing/calculate-and-invoice")
    ResponseEntity<Map<String, Object>> calculateAndInvoice(@RequestBody BillingCalculationRequest request);
}

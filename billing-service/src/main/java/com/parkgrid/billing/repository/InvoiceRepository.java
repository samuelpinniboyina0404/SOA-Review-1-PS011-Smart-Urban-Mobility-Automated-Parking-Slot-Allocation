package com.parkgrid.billing.repository;

import com.parkgrid.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);
    Optional<Invoice> findByBookingId(Long bookingId);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByPaymentStatus(String paymentStatus);
}

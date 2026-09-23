package com.parkgrid.billing.dto;

public class PaymentRequest {
    private String paymentMethod; // CARD, UPI, NETBANKING
    private Double amountPaid;

    public PaymentRequest() {}

    public PaymentRequest(String paymentMethod, Double amountPaid) {
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }
}

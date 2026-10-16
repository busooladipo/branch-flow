package com.jpmorgan.branchflow.service;

public interface PaymentService {

    /**
     * Process a payment returning the cheapest sequence of branches as comma separated String.
     * Implementations are expected to be thread safe.
     * @param originBranch the starting branch
     * @param destinationBranch the destination branch
     * @return the cheapest sequence for the payment as a CSV (e.g. A,D,C) or null if no sequence is available
     */
    String processPayment(String originBranch, String destinationBranch);
}
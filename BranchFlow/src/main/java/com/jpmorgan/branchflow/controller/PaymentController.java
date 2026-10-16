package com.jpmorgan.branchflow.controller;


import com.jpmorgan.branchflow.dto.CheapestRouteRequest;
import com.jpmorgan.branchflow.dto.PaymentRouteResponse;
import com.jpmorgan.branchflow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/cheapest-route")
    public ResponseEntity<PaymentRouteResponse> getCheapestRoute(
            @Valid @ModelAttribute CheapestRouteRequest request) {

        String route = paymentService.processPayment(
                request.getOriginBranch().trim(),
                request.getDestinationBranch().trim());

        return ResponseEntity.ok(new PaymentRouteResponse(route));
    }

}

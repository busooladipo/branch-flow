package com.jpmorgan.branchflow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsCheapestRouteForValidBranches() throws Exception {
        mockMvc.perform(get("/api/payments/cheapest-route")
                        .param("originBranch", "A")
                        .param("destinationBranch", "D"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.route").value("A,C,E,D"));
    }

    @Test
    void returnsBadRequestWhenOriginBranchMissing() throws Exception {
        mockMvc.perform(get("/api/payments/cheapest-route")
                        .param("destinationBranch", "D"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsBadRequestForUnknownBranch() throws Exception {
        mockMvc.perform(get("/api/payments/cheapest-route")
                        .param("originBranch", "Z")
                        .param("destinationBranch", "D"))
                .andExpect(status().isBadRequest());
    }
}

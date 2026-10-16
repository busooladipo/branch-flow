package com.jpmorgan.branchflow.dto;

import jakarta.validation.constraints.NotBlank;

public class CheapestRouteRequest {

    @NotBlank(message = "originBranch is required")
    private String originBranch;

    @NotBlank(message = "destinationBranch is required")
    private String destinationBranch;

    public String getOriginBranch() {
        return originBranch;
    }

    public void setOriginBranch(String originBranch) {
        this.originBranch = originBranch == null ? null : originBranch.trim().toUpperCase();
    }

    public String getDestinationBranch() {
        return destinationBranch;
    }

    public void setDestinationBranch(String destinationBranch) {
        this.destinationBranch = destinationBranch == null ? null : destinationBranch.trim().toUpperCase();
    }
}

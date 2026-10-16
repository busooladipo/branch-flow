package com.jpmorgan.branchflow.exception;

public class UnknownBranchException extends IllegalArgumentException {
    public UnknownBranchException(String branch) {
        super("Unknown branch: " + branch);
    }
}

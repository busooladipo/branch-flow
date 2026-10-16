package com.jpmorgan.branchflow.exception;

public class NoRouteFoundException extends RuntimeException {
    public NoRouteFoundException(String originBranch, String destinationBranch) {
        super("No route available from " + originBranch + " to " + destinationBranch);
    }
}

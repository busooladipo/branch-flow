package com.jpmorgan.branchflow.domain;

import com.jpmorgan.branchflow.exception.UnknownBranchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BranchGraph {
    private final Map<String, Integer> branchCosts;
    private final Map<String, List<String>> adjacencyList;

    public BranchGraph(Map<String, Integer> branchCosts, Map<String, List<String>> adjacencyList) {
        this.branchCosts = Map.copyOf(branchCosts);

        Map<String, List<String>> copy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            copy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        this.adjacencyList = Map.copyOf(copy);
    }

    public boolean containsBranch(String branch) {
        return branchCosts.containsKey(branch);
    }

    public List<String> neighborsOf(String branch) {
        return adjacencyList.getOrDefault(branch, List.of());
    }

    public int costOf(String branch) {
        Integer cost = branchCosts.get(branch);
        if (cost == null) {
            throw new UnknownBranchException(branch);
        }
        return cost;
    }
}
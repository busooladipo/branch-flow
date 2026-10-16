package com.jpmorgan.branchflow.repository;


import com.jpmorgan.branchflow.domain.BranchGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class BranchGraphRepository {
    private final Map<String, Integer> branchCosts = new ConcurrentHashMap<>();
    private final Map<String, List<String>> adjacency = new ConcurrentHashMap<>();
    private final ReentrantLock writeLock = new ReentrantLock();
    private volatile BranchGraph snapshot = new BranchGraph(Map.of(), Map.of());

    public BranchGraph getGraph() {
        return snapshot;
    }

    public void addBranch(String branch, int cost) {
        addBranches(List.of(new Branch(branch, cost)));
    }

    public void addLink(String fromBranch, String toBranch) {
        addLinks(List.of(new Link(fromBranch, toBranch)));
    }

    // batched so adding many branches/links only rebuilds the snapshot once,
    // not once per entry - matters once the graph gets large
    public void addBranches(List<Branch> branches) {
        writeLock.lock();
        try {
            for (Branch branch : branches) {
                branchCosts.put(branch.code(), branch.cost());
                adjacency.putIfAbsent(branch.code(), new CopyOnWriteArrayList<>());
            }
            rebuildSnapshot();
        } finally {
            writeLock.unlock();
        }
    }

    public void addLinks(List<Link> links) {
        writeLock.lock();
        try {
            for (Link link : links) {
                if (!branchCosts.containsKey(link.fromBranch()) || !branchCosts.containsKey(link.toBranch())) {
                    throw new IllegalArgumentException(
                            "Both branches must exist before linking: " + link.fromBranch() + " -> " + link.toBranch());
                }
                adjacency.computeIfAbsent(link.fromBranch(), key -> new CopyOnWriteArrayList<>()).add(link.toBranch());
            }
            rebuildSnapshot();
        } finally {
            writeLock.unlock();
        }
    }

    private void rebuildSnapshot() {
        snapshot = new BranchGraph(branchCosts, adjacency);
    }

    public record Branch(String code, int cost) {
    }

    public record Link(String fromBranch, String toBranch) {
    }
}

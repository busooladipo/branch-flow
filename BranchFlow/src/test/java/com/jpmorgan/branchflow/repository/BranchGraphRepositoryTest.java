package com.jpmorgan.branchflow.repository;

import com.jpmorgan.branchflow.repository.BranchGraphRepository.Branch;
import com.jpmorgan.branchflow.repository.BranchGraphRepository.Link;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BranchGraphRepositoryTest {

    @Test
    void addingBranchAndLinkUpdatesGraphImmediately() {
        BranchGraphRepository repository = new BranchGraphRepository();
        repository.addBranch("X", 15);
        repository.addBranch("Y", 25);
        repository.addLink("X", "Y");

        var graph = repository.getGraph();
        assertTrue(graph.containsBranch("X"));
        assertEquals(15, graph.costOf("X"));
        assertTrue(graph.neighborsOf("X").contains("Y"));
    }

    @Test
    void addingLinkWithUnknownBranchThrows() {
        BranchGraphRepository repository = new BranchGraphRepository();
        repository.addBranch("X", 15);

        assertThrows(IllegalArgumentException.class, () -> repository.addLink("X", "Unknown"));
    }

    @Test
    void batchAddRebuildsSnapshotOnce() {
        BranchGraphRepository repository = new BranchGraphRepository();
        repository.addBranches(List.of(new Branch("A", 1), new Branch("B", 2)));
        repository.addLinks(List.of(new Link("A", "B")));

        var graph = repository.getGraph();
        assertEquals(List.of("B"), graph.neighborsOf("A"));
    }
}

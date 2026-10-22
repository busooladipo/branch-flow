package com.jpmorgan.branchflow.config;


import com.jpmorgan.branchflow.repository.BranchGraphRepository;
import com.jpmorgan.branchflow.repository.BranchGraphRepository.Branch;
import com.jpmorgan.branchflow.repository.BranchGraphRepository.Link;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BranchGraphInitializer {

    private final BranchGraphRepository repository;

    public BranchGraphInitializer(BranchGraphRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedInitialData() {
        repository.addBranches(List.of(
                new Branch("A", 5),
                new Branch("B", 50),
                new Branch("C", 10),
                new Branch("D", 10),
                new Branch("E", 20),
                new Branch("F", 5)
        ));

        repository.addLinks(List.of(
                new Link("A", "B"),
                new Link("A", "C"),
                new Link("C", "B"),
                new Link("B", "D"),
                new Link("C", "E"),
                new Link("D", "E"),
                new Link("E", "D"),
                new Link("D", "F"),
                new Link("E", "F")
        ));
    }
}

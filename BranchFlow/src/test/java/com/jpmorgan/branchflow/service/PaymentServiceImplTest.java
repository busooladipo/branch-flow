package com.jpmorgan.branchflow.service;


import com.jpmorgan.branchflow.exception.NoRouteFoundException;
import com.jpmorgan.branchflow.exception.UnknownBranchException;
import com.jpmorgan.branchflow.repository.BranchGraphRepository;
import com.jpmorgan.branchflow.repository.BranchGraphRepository.Branch;
import com.jpmorgan.branchflow.repository.BranchGraphRepository.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceImplTest {
    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        BranchGraphRepository repository = new BranchGraphRepository();
        repository.addBranches(List.of(
                new Branch("A", 5), new Branch("B", 50), new Branch("C", 10),
                new Branch("D", 10), new Branch("E", 20), new Branch("F", 5)
        ));
        repository.addLinks(List.of(
                new Link("A", "B"), new Link("A", "C"), new Link("C", "B"),
                new Link("B", "D"), new Link("C", "E"), new Link("D", "E"),
                new Link("D", "F"), new Link("E", "F")
        ));

        service = new PaymentServiceImpl(repository);
    }

    @Test
    void findsCheapestRouteFromAToD() {
        assertEquals("A,B,D", service.processPayment("A", "D"));
    }

    @Test
    void findsCheapestRouteFromAToF() {
        assertEquals("A,C,E,F", service.processPayment("A", "F"));
    }

    @Test
    void returnsOriginWhenSameBranch() {
        assertEquals("A", service.processPayment("A", "A"));
    }

    @Test
    void throwsWhenOriginBranchUnknown() {
        assertThrows(UnknownBranchException.class, () -> service.processPayment("Z", "A"));
    }

    @Test
    void throwsWhenNoRouteExists() {
        BranchGraphRepository repository = new BranchGraphRepository();
        repository.addBranches(List.of(new Branch("A", 5), new Branch("B", 50)));
        repository.addLinks(List.of(new Link("B", "A")));

        PaymentServiceImpl isolatedService = new PaymentServiceImpl(repository);
        assertThrows(NoRouteFoundException.class, () -> isolatedService.processPayment("A", "B"));
    }

    @Test
    void handlesConcurrentCallsSafely() throws InterruptedException {
        int threadCount = 50;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        results.add(service.processPayment("A", "F"));
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS));
            assertEquals(threadCount, results.size());
            assertTrue(results.stream().allMatch("A,C,E,F"::equals));
        }
    }
}

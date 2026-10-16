package com.jpmorgan.branchflow.service;


import com.jpmorgan.branchflow.domain.BranchGraph;
import com.jpmorgan.branchflow.exception.NoRouteFoundException;
import com.jpmorgan.branchflow.exception.UnknownBranchException;
import com.jpmorgan.branchflow.repository.BranchGraphRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final BranchGraphRepository branchGraphRepository;

    public PaymentServiceImpl(BranchGraphRepository branchGraphRepository) {
        this.branchGraphRepository = branchGraphRepository;
    }

    @Override
    public String processPayment(String originBranch, String destinationBranch) {
        BranchGraph graph = branchGraphRepository.getGraph();

        validateBranch(graph, originBranch);
        validateBranch(graph, destinationBranch);

        if (originBranch.equals(destinationBranch)) {
            return originBranch;
        }

        // standard Dijkstra - edge weight leaving branch u is the cost charged by u
        Map<String, Integer> bestCost = new HashMap<>();
        Map<String, String> previousBranch = new HashMap<>();
        PriorityQueue<RouteNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(RouteNode::cost));

        bestCost.put(originBranch, 0);
        priorityQueue.offer(new RouteNode(originBranch, 0));

        while (!priorityQueue.isEmpty()) {
            RouteNode current = priorityQueue.poll();

            if (current.cost() > bestCost.getOrDefault(current.branch(), Integer.MAX_VALUE)) {
                continue;
            }

            if (current.branch().equals(destinationBranch)) {
                return buildPath(previousBranch, originBranch, destinationBranch);
            }

            for (String neighbor : graph.neighborsOf(current.branch())) {
                int nextCost = current.cost() + graph.costOf(current.branch());

                if (nextCost < bestCost.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    bestCost.put(neighbor, nextCost);
                    previousBranch.put(neighbor, current.branch());
                    priorityQueue.offer(new RouteNode(neighbor, nextCost));
                }
            }
        }

        throw new NoRouteFoundException(originBranch, destinationBranch);
    }

    private void validateBranch(BranchGraph graph, String branch) {
        if (branch == null || branch.isBlank() || !graph.containsBranch(branch)) {
            throw new UnknownBranchException(branch);
        }
    }

    private String buildPath(Map<String, String> previousBranch, String originBranch, String destinationBranch) {
        LinkedList<String> path = new LinkedList<>();
        String current = destinationBranch;

        while (current != null) {
            path.addFirst(current);
            if (current.equals(originBranch)) {
                break;
            }
            current = previousBranch.get(current);
        }

        return String.join(",", path);
    }

    private record RouteNode(String branch, int cost) {
    }
}
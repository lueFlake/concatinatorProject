package org.lib.implementaions;

import org.lib.domain.interfaces.Dependency;
import org.lib.domain.exceptions.FoundCycleException;

import java.util.*;

public class DependencyGraph<T> {
    private final Map<Integer, NodeWrapper<T>> nodes;
    private final Comparator<Dependency<T>> depComparator;
    private final Comparator<NodeWrapper<T>> nodeComparator;

    private static class NodeWrapper<T> {
        public Dependency<T> inner;
        public int used;

        public NodeWrapper<T> prev;
        public NodeWrapper(Dependency<T> inner) {
            this.inner = inner;
            this.used = 0;
            this.prev = null;
        }
    }

    private class NodeWrapperComparator implements Comparator<NodeWrapper<T>> {
        private final Comparator<Dependency<T>> depComparator;

        private NodeWrapperComparator(Comparator<Dependency<T>> depComparator) {
            this.depComparator = depComparator;
        }

        @Override
        public int compare(NodeWrapper<T> nw1, NodeWrapper<T> nw2) {
            return depComparator.compare(nw1.inner, nw2.inner);
        }
    }

    public DependencyGraph(List<Dependency<T>> nodes,
                           Comparator<Dependency<T>> depComparator) {
        this.depComparator = depComparator;
        this.nodeComparator = new NodeWrapperComparator(depComparator);
        this.nodes = new TreeMap<>();
        nodes.forEach(x -> {
            this.nodes.put(x.getId(), new NodeWrapper<>(x));
        });
    }

    public List<Dependency<T>> getOrder() throws FoundCycleException {
        var result = new ArrayList<Dependency<T>>();
        for (var node : nodes.values().stream().sorted(nodeComparator).toList()) {
            if (node.used == 0) {
                topSort(node, result);
            }
        }
        Collections.reverse(result);
        return result;
    }

    private void topSort(NodeWrapper<T> node, List<Dependency<T>> order) throws FoundCycleException {
        var neighs = node.inner.getDependent().stream().sorted(depComparator).toList();
        node.used = 1;
        for (var neigh : neighs) {
            var neighNode = nodes.get(neigh.getId());
            if (neighNode.used == 0) {
                neighNode.prev = node;
                topSort(neighNode, order);
            } else if (neighNode.used == 1) {
                var cycle = new ArrayList<NodeWrapper<T>>();
                findCycle(node, neighNode, cycle);
                var strings = cycle.stream().map(x->x.inner.toString()).toList();
                throw new FoundCycleException("Найден цикл: " + String.join(" <- ", strings));
            }
        }
        node.used = 2;
        order.add(node.inner);
    }

    private void findCycle(NodeWrapper<T> first, NodeWrapper<T> last, List<NodeWrapper<T>> cycle) {
        cycle.add(last);
        while (first != last) {
            cycle.add(first);
            first = first.prev;
        }
        cycle.add(last);
    }
}

package org.lib.implementaions;

import org.lib.domain.interfaces.Dependency;
import org.lib.domain.models.FoundCycleException;
import org.w3c.dom.Node;

import java.util.*;

public class DependencyGraph<T> {
    private final Map<Integer, NodeWrapper<T>> nodes;

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

    public DependencyGraph(List<Dependency<T>> nodes) {
        this.nodes = new HashMap<>();
        nodes.forEach(x -> {
            this.nodes.put(x.getId(), new NodeWrapper<>(x));
        });
    }

    public List<Dependency<T>> getOrder() throws FoundCycleException {
        var result = new ArrayList<Dependency<T>>();
        for (var node : nodes.values()) {
            if (node.used == 0) {
                topSort(node, result);
            }
        }
        Collections.reverse(result);
        return result;
    }

    private void topSort(NodeWrapper<T> node, List<Dependency<T>> order) throws FoundCycleException {
        for (var neigh : node.inner.getDependencies()) {
            var neighNode = nodes.get(neigh.getId());
            if (neighNode.used == 0) {
                neighNode.prev = node;
                topSort(neighNode, order);
            } else if (node.used == 1) {
                var cycle = new ArrayList<NodeWrapper<T>>();
                findCycle(node, neighNode, cycle);
                var strings = cycle.stream().map(x->x.inner.toString()).toList();
                throw new FoundCycleException("Найден цикл: " + String.join(", ", strings));
            }
        }
        order.add(node.inner);
    }

    private void findCycle(NodeWrapper<T> first, NodeWrapper<T> last, List<NodeWrapper<T>> cycle) {
        while (first != last) {
            cycle.add(first);
            first = first.prev;
        }
        cycle.add(last);
    }
}

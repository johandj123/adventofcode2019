package lib;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class GraphUtil {
    private GraphUtil() {
    }

    public static <T> Set<T> reachable(T start, Function<T, Iterable<T>> neighbours) {
        List<T> startList = List.of(start);
        Set<T> seen = new HashSet<>(startList);
        Deque<T> todo = new ArrayDeque<>(startList);
        while (!todo.isEmpty()) {
            T node = todo.remove();
            for (T nextNode : neighbours.apply(node)) {
                if (seen.add(nextNode)) {
                    todo.add(nextNode);
                }
            }
        }
        return seen;
    }

    public static <T> int breadthFirstSearch(T start, Function<T, Iterable<T>> neighbours, Predicate<T> endPredicate) {
        Set<T> explored = new HashSet<>();
        Set<T> current = new HashSet<>(List.of(start));
        int counter = 0;
        while (!current.isEmpty()) {
            if (current.stream().anyMatch(endPredicate)) {
                return counter;
            }
            explored.addAll(current);
            Set<T> next = new HashSet<>();
            for (T node : current) {
                for (T nextNode : neighbours.apply(node)) {
                    if (!explored.contains(nextNode)) {
                        next.add(nextNode);
                    }
                }
            }
            counter++;
            current = next;
        }
        throw new IllegalStateException("End not found");
    }

    public static <T extends Comparable<T>> int dijkstra(T start, Function<T, Map<T, Integer>> neighbours, Predicate<T> endPredicate) {
        Map<T, Integer> distances = new HashMap<>();
        SortedSet<DijkstraNodeDistance<T>> queue = new TreeSet<>(List.of(new DijkstraNodeDistance<T>(0, start)));
        while (!queue.isEmpty()) {
            DijkstraNodeDistance<T> current = queue.first();
            queue.remove(current);
            if (endPredicate.test(current.getNode())) {
                return current.getDistance();
            }
            for (Map.Entry<T, Integer> entry : neighbours.apply(current.getNode()).entrySet()) {
                T next = entry.getKey();
                int distance = current.getDistance() + entry.getValue();
                Integer oldDistance = distances.get(next);
                if (oldDistance == null || distance < oldDistance) {
                    if (oldDistance != null) {
                        queue.remove(new DijkstraNodeDistance<>(oldDistance, next));
                    }
                    queue.add(new DijkstraNodeDistance<>(distance, next));
                    distances.put(next, distance);
                }
            }
        }
        throw new IllegalStateException("End node not found");
    }

    private static class DijkstraNodeDistance<T extends Comparable<T>> implements Comparable<DijkstraNodeDistance<T>> {
        private final int distance;
        private final T node;

        public DijkstraNodeDistance(int distance, T node) {
            Objects.requireNonNull(node);
            this.distance = distance;
            this.node = node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DijkstraNodeDistance<?> that = (DijkstraNodeDistance<?>) o;
            return distance == that.distance && node.equals(that.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(distance, node);
        }

        @Override
        public int compareTo(DijkstraNodeDistance<T> o) {
            if (distance < o.distance) {
                return -1;
            }
            if (distance > o.distance) {
                return 1;
            }
            return node.compareTo(o.node);
        }

        public int getDistance() {
            return distance;
        }

        public T getNode() {
            return node;
        }
    }

}

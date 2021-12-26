import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Day18 {
    private char[][] grid;
    private int startx;
    private int starty;

    public static void main(String[] args) throws IOException {
        new Day18().start();
    }

    private void start() throws IOException {
//        readInput("example18a.txt");
//        readInput("example18b.txt");
//        readInput("example18c.txt");
        readInput("input18.txt");
        first();
        second();
    }

    private void first() {
        int allKeys = determineAllKeys();
        Set<Node> explored = new HashSet<>();
        Set<Node> current = new HashSet<>();
        current.add(new Node(startx, starty));
        int counter = 0;
        while (!done(current, allKeys)) {
            explored.addAll(current);
            Set<Node> next = new HashSet<>();
            for (Node node : current) {
                for (Node nextNode : node.getNeighbours()) {
                    if (!explored.contains(nextNode)) {
                        next.add(nextNode);
                    }
                }
            }
            counter++;
            current = next;
        }
        System.out.println("First: " + counter);
    }

    private void second() {
        int allKeys = determineAllKeys();
        grid[starty][startx - 1] = '#';
        grid[starty][startx] = '#';
        grid[starty][startx + 1] = '#';
        grid[starty - 1][startx] = '#';
        grid[starty + 1][startx] = '#';
        Map<FourNode, Integer> distances = new HashMap<>();
        SortedSet<FourNodeDistance> queue = new TreeSet<>();
        FourNode root = new FourNode(startx, starty);
        queue.add(new FourNodeDistance(0, root));
        int totalDistance = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            FourNodeDistance current = queue.first();
            queue.remove(current);
            if (current.fourNode.keys == allKeys) {
                totalDistance = current.distance;
                break;
            }
            for (Map.Entry<FourNode, Integer> entry : current.fourNode.getNeighbours().entrySet()) {
                FourNode next = entry.getKey();
                int distance = current.distance + entry.getValue();
                Integer oldDistance = distances.get(next);
                if (oldDistance == null || distance < oldDistance) {
                    if (oldDistance != null) {
                        queue.remove(new FourNodeDistance(oldDistance, next));
                    }
                    queue.add(new FourNodeDistance(distance, next));
                    distances.put(next, distance);
                }
            }
        }
        System.out.println("Second: " + totalDistance);
    }

    private int determineAllKeys() {
        int result = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                char c = grid[y][x];
                if (c >= 'a' && c <= 'z') {
                    int index = c - 'a';
                    result |= (1 << index);
                }
            }
        }
        return result;
    }

    private boolean done(Set<Node> current, int allKeys) {
        return current.stream().anyMatch(node -> node.keys == allKeys);
    }

    private void readInput(String name) throws IOException {
        List<String> lines = Files.readAllLines(new File(name).toPath());
        grid = new char[lines.size()][lines.get(0).length()];
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '@') {
                    startx = x;
                    starty = y;
                    c = '.';
                }
                grid[y][x] = c;
            }
        }
    }

    class Node {
        private int x;
        private int y;
        private int keys;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node(int x, int y, int keys) {
            this.x = x;
            this.y = y;
            this.keys = keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y && keys == node.keys;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, keys);
        }

        public List<Node> getNeighbours() {
            List<Node> nodes = new ArrayList<>();
            addNeighbourIfPossible(nodes, x - 1, y);
            addNeighbourIfPossible(nodes, x + 1, y);
            addNeighbourIfPossible(nodes, x, y - 1);
            addNeighbourIfPossible(nodes, x, y + 1);
            return nodes;
        }

        private void addNeighbourIfPossible(List<Node> nodes, int x, int y) {
            if (x < 0 || y < 0 || x >= grid[0].length || y >= grid.length) return;
            char c = grid[y][x];
            if (c >= 'a' && c <= 'z') {
                int index = c - 'a';
                nodes.add(new Node(x, y, keys | (1 << index)));
            } else if (c >= 'A' && c <= 'Z') {
                int index = c - 'A';
                if ((keys & (1 << index)) != 0) {
                    nodes.add(new Node(x, y, keys));
                }
            } else if (c == '.') {
                nodes.add(new Node(x, y, keys));
            }
        }
    }

    class FourNode {
        int[] x;
        int[] y;
        int keys;

        public FourNode(int x, int y) {
            this.x = new int[]{x - 1, x - 1, x + 1, x + 1};
            this.y = new int[]{y - 1, y + 1, y - 1, y + 1};
        }

        public FourNode(FourNode n) {
            this.x = Arrays.copyOf(n.x, n.x.length);
            this.y = Arrays.copyOf(n.y, n.y.length);
            this.keys = n.keys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FourNode fourNode = (FourNode) o;
            return keys == fourNode.keys && Arrays.equals(x, fourNode.x) && Arrays.equals(y, fourNode.y);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(keys);
            result = 31 * result + Arrays.hashCode(x);
            result = 31 * result + Arrays.hashCode(y);
            return result;
        }

        @Override
        public String toString() {
            return "FourNode{" +
                    "x=" + Arrays.toString(x) +
                    ", y=" + Arrays.toString(y) +
                    ", keys=" + keys +
                    '}';
        }

        public Map<FourNode, Integer> getNeighbours() {
            Map<FourNode, Integer> result = new HashMap<>();
            for (int i = 0; i < x.length; i++) {
                findNeighbours(result, i);
                if (!result.isEmpty()) {
                    return result;
                }
            }
            return result;
        }

        private void findNeighbours(Map<FourNode, Integer> result, int i) {
            Set<Node> explored = new HashSet<>();
            Set<Node> current = new HashSet<>();
            current.add(new Node(x[i], y[i], keys));
            int distance = 0;
            while (!current.isEmpty()) {
                explored.addAll(current);
                Set<Node> next = new HashSet<>();
                for (Node node : current) {
                    if (node.keys != this.keys) {
                        FourNode newFourNode = new FourNode(this);
                        newFourNode.x[i] = node.x;
                        newFourNode.y[i] = node.y;
                        newFourNode.keys = node.keys;
                        result.put(newFourNode, distance);
                    } else {
                        for (Node nextNode : node.getNeighbours()) {
                            if (!explored.contains(nextNode)) {
                                next.add(nextNode);
                            }
                        }
                    }
                }
                distance++;
                current = next;
            }
        }
    }

    class FourNodeDistance implements Comparable<FourNodeDistance> {
        final int distance;
        final FourNode fourNode;

        public FourNodeDistance(int distance, FourNode fourNode) {
            this.distance = distance;
            this.fourNode = fourNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FourNodeDistance that = (FourNodeDistance) o;
            return distance == that.distance && fourNode.equals(that.fourNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(distance, fourNode);
        }

        @Override
        public int compareTo(FourNodeDistance o) {
            if (distance < o.distance) {
                return -1;
            }
            if (distance > o.distance) {
                return 1;
            }
            for (int i = 0; i < fourNode.x.length; i++) {
                if (fourNode.x[i] < o.fourNode.x[i]) {
                    return -1;
                }
                if (fourNode.x[i] > o.fourNode.x[i]) {
                    return 1;
                }
                if (fourNode.y[i] < o.fourNode.y[i]) {
                    return -1;
                }
                if (fourNode.y[i] > o.fourNode.y[i]) {
                    return 1;
                }
            }
            return 0;
        }
    }
}

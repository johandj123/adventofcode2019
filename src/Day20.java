import lib.CharMatrix;
import lib.GraphUtil;
import lib.InputUtil;

import java.io.IOException;
import java.util.*;

public class Day20 {
    public static void main(String[] args) throws IOException {
        CharMatrix charMatrix = new CharMatrix(InputUtil.readAsLines("input20.txt"));
        var labels = getLabels(charMatrix);
        var teleports = getTeleports(labels);
        CharMatrix.Position start = labels.get("AA").get(0);
        CharMatrix.Position end = labels.get("ZZ").get(0);
        first(start, end, teleports);
        second(start, end, charMatrix, teleports);
    }

    private static void first(CharMatrix.Position start, CharMatrix.Position end, Map<CharMatrix.Position, CharMatrix.Position> teleports) {
        int distance = GraphUtil.breadthFirstSearch(start, position -> getNeighbours(position, teleports), end::equals);
        System.out.println(distance);
    }

    private static void second(CharMatrix.Position start, CharMatrix.Position end, CharMatrix charMatrix, Map<CharMatrix.Position, CharMatrix.Position> teleports) {
        int distance = GraphUtil.breadthFirstSearch(new Node(0, start),
                node -> getNeighbours(node, charMatrix.getWidth(), charMatrix.getHeight(), teleports),
                node -> node.level == 0 && node.position.equals(end));
        System.out.println(distance);
    }

    private static List<CharMatrix.Position> getNeighbours(CharMatrix.Position position, Map<CharMatrix.Position, CharMatrix.Position> teleports) {
        List<CharMatrix.Position> result = new ArrayList<>();
        for (CharMatrix.Position next : position.getNeighbours()) {
            if (next.get() == '.') {
                result.add(next);
            }
        }
        if (teleports.containsKey(position)) {
            result.add(teleports.get(position));
        }
        return result;
    }

    private static List<Node> getNeighbours(Node node, int w, int h,Map<CharMatrix.Position, CharMatrix.Position> teleports) {
        List<Node> result = new ArrayList<>();
        for (CharMatrix.Position next : node.position.getNeighbours()) {
            if (next.get() == '.') {
                result.add(new Node(node.level, next));
            }
        }
        if (teleports.containsKey(node.position)) {
            int x = node.position.getX();
            int y = node.position.getY();
            boolean inner = (x > 2 && y > 2 && x < w - 3 && y < h - 3);
            if (inner) {
                if (node.level < 25) {
                    result.add(new Node(node.level + 1, teleports.get(node.position)));
                }
            } else if (node.level > 0) {
                result.add(new Node(node.level - 1, teleports.get(node.position)));
            }
        }
        return result;
    }

    private static Map<String, List<CharMatrix.Position>> getLabels(CharMatrix c) {
        Map<String, List<CharMatrix.Position>> map = new HashMap<>();
        for (int y = 0; y < c.getHeight(); y++) {
            for (int x = 0; x < c.getWidth(); x++) {
                CharMatrix.Position position = c.new Position(x, y);
                if (position.get() == '.') {
                    processLabels(c, map, position, 1, 0);
                    processLabels(c, map, position, -1, 0);
                    processLabels(c, map, position, 0, 1);
                    processLabels(c, map, position, 0, -1);
                }
            }
        }
        return map;
    }

    private static Map<CharMatrix.Position, CharMatrix.Position> getTeleports(Map<String, List<CharMatrix.Position>> map) {
        Map<CharMatrix.Position, CharMatrix.Position> result = new HashMap<>();
        for (var entry : map.entrySet()) {
            if (entry.getValue().size() == 2) {
                result.put(entry.getValue().get(0), entry.getValue().get(1));
                result.put(entry.getValue().get(1), entry.getValue().get(0));
            }
        }
        return result;
    }

    private static void processLabels(CharMatrix c, Map<String, List<CharMatrix.Position>> map, CharMatrix.Position position, int dx, int dy) {
        char c1 = c.getUnbounded(position.getX() + dx, position.getY() + dy);
        char c2 = c.getUnbounded(position.getX() + dx * 2, position.getY() + dy * 2);
        if (c1 >= 'A' && c1 <= 'Z' && c2 >= 'A' && c2 <= 'Z') {
            String label;
            if (dx > 0 || dy > 0) {
                label = "" + c1 + c2;
            } else {
                label = "" + c2 + c1;
            }
            map.computeIfAbsent(label, key -> new ArrayList<>()).add(position);
        }
    }

    static class Node {
        final int level;
        final CharMatrix.Position position;

        public Node(int level, CharMatrix.Position position) {
            this.level = level;
            this.position = position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return level == node.level && Objects.equals(position, node.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, position);
        }
    }
}

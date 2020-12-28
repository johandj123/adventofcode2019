import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Day3 {
    private Map<Position, Integer> intersectionMap = new HashMap<>();
    private Map<Position, Integer> distanceMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new Day3().start();
    }

    private void start() throws IOException {
        List<String> lines = Files.readAllLines(new File("input3.txt").toPath());
        for (String line : lines) {
            processWire(line);
        }
        Position minPosition = intersectionMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .min(Comparator.comparing(entry -> entry.getKey().manhattanDistance()))
                .map(Map.Entry::getKey)
                .orElse(null);
        System.out.println("First: " + minPosition.manhattanDistance());

        List<Position> intersectionList = intersectionMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        int minDistance = intersectionList
                .stream()
                .min(Comparator.comparing(position -> distanceMap.get(position)))
                .map(position -> distanceMap.get(position))
                .get();
        System.out.println("Second: " + minDistance);
    }

    private void processWire(String wire)
    {
        Set<Position> wireSet = new HashSet<>();
        Map<Position, Integer> wireDistance = new HashMap<>();
        String[] parts = wire.split(",");
        Position position = new Position(0, 0);
        int distance = 0;
        for (String part : parts) {
            String directionLetter = part.substring(0, 1);
            int count = Integer.parseInt(part.substring(1));
            Direction direction = null;
            if ("L".equals(directionLetter)) {
                direction = new Direction(-1, 0);
            } else if ("R".equals(directionLetter)) {
                direction = new Direction(1, 0);
            } else if ("U".equals(directionLetter)) {
                direction = new Direction(0, -1);
            } else if ("D".equals(directionLetter)) {
                direction = new Direction(0, 1);
            }
            for (int i = 0; i < count; i++) {
                position = position.advance(direction);
                wireSet.add(position);
                distance++;
                if (!wireDistance.containsKey(position)) {
                    wireDistance.put(position, distance);
                }
            }
        }
        for (Position wirePosition : wireSet) {
            intersectionMap.put(wirePosition, intersectionMap.getOrDefault(wirePosition, 0) + 1);
        }
        for (Map.Entry<Position, Integer> entry : wireDistance.entrySet()) {
            distanceMap.put(entry.getKey(), distanceMap.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }

    static class Position
    {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position advance(Direction direction) {
            return new Position(x + direction.dx, y + direction.dy);
        }

        public int manhattanDistance()
        {
            return Math.abs(x) + Math.abs(y);
        }

        @Override
        public String toString() {
            return "(" + x +
                    ',' + y +
                    ')';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x &&
                    y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static class Direction
    {
        final int dx;
        final int dy;

        public Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}

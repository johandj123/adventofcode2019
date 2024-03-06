import lib.CharMatrix;
import lib.InputUtil;
import lib.MathUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day10 {
    public static void main(String[] args) throws IOException {
        CharMatrix charMatrix = new CharMatrix(InputUtil.readAsLines("input10.txt"));
        Set<Position> asteroids = new HashSet<>();
        for (int y = 0; y < charMatrix.getHeight(); y++) {
            for (int x = 0; x < charMatrix.getWidth(); x++) {
                if (charMatrix.get(x, y) == '#') {
                    asteroids.add(new Position(x, y));
                }
            }
        }
        Position station = first(asteroids);
        second(asteroids, station);
    }

    private static Position first(Set<Position> asteroids) {
        int maxCount = Integer.MIN_VALUE;
        Position maxPosition = null;
        for (Position a : asteroids) {
            int count = 0;
            for (Position b : asteroids) {
                if (a == b) {
                    continue;
                }
                int deltaX = b.x - a.x;
                int deltaY = b.y - a.y;
                if (deltaX == 0) {
                    if (IntStream.rangeClosed(Math.min(a.y, b.y) + 1, Math.max(a.y, b.y) - 1)
                            .mapToObj(y -> new Position(a.x, y))
                            .noneMatch(asteroids::contains)) {
                        count++;
                    }
                } else if (deltaY == 0) {
                    if (IntStream.rangeClosed(Math.min(a.x, b.x) + 1, Math.max(a.x, b.x) - 1)
                            .mapToObj(x -> new Position(x, a.y))
                            .noneMatch(asteroids::contains)) {
                        count++;
                    }
                } else {
                    int steps = (int) MathUtil.gcd(Math.abs(deltaX), Math.abs(deltaY));
                    int stepX = deltaX / steps;
                    int stepY = deltaY / steps;
                    int x = a.x + stepX;
                    int y = a.y + stepY;
                    boolean ok = true;
                    while (x != b.x || y != b.y) {
                        if (asteroids.contains(new Position(x, y))) {
                            ok = false;
                            break;
                        }
                        x += stepX;
                        y += stepY;
                    }
                    if (ok) {
                        count++;
                    }
                }
            }
            if (count > maxCount) {
                maxCount = count;
                maxPosition = a;
            }
        }
        System.out.println(maxCount);
        return maxPosition;
    }

    private static void second(Set<Position> asteroids, Position station) {
        asteroids.remove(station);
        Map<Angle, List<Position>> map = new HashMap<>();
        for (Position asteroid : asteroids) {
            int dx = asteroid.x - station.x;
            int dy = asteroid.y - station.y;
            Angle angle = new Angle(dx, dy);
            map.computeIfAbsent(angle, key -> new ArrayList<>()).add(asteroid);
        }
        SortedMap<Angle, Deque<Position>> sortedMap = new TreeMap<>();
        for (var entry : map.entrySet()) {
            Angle angle = entry.getKey();
            List<Position> positions = entry.getValue();
            positions.sort(Comparator.comparing(position -> position.distanceSquared(station)));
            sortedMap.put(angle, new ArrayDeque<>(positions));
        }
        List<Position> vaporizeList = new ArrayList<>();
        while (vaporizeList.size() < asteroids.size()) {
            for (var entry : sortedMap.entrySet()) {
                Deque<Position> deque = entry.getValue();
                if (!deque.isEmpty()) {
                    vaporizeList.add(deque.removeFirst());
                }
            }
        }
        Position asteroid200 = vaporizeList.get(200 - 1);
        System.out.println(asteroid200.x * 100 + asteroid200.y);
    }

    static class Position {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public long distanceSquared(Position o) {
            long dx = x - o.x;
            long dy = y - o.y;
            return dx * dx + dy * dy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }
    }

    static class Angle implements Comparable<Angle> {
        int quadrant;
        int dx;
        int dy;

        Angle(int deltaX,int deltaY) {
            quadrant = 0;
            dx = deltaX;
            dy = deltaY;
            while (!(dx >= 0 && dy < 0)) {
                int ndx = dy;
                int ndy = -dx;
                dx = ndx;
                dy = ndy;
                quadrant++;
            }
            int gcd = (int) MathUtil.gcd(dx, dy);
            dx = Math.abs(dx / gcd);
            dy = Math.abs(dy / gcd);
        }

        @Override
        public int compareTo(Angle o) {
            if (quadrant < o.quadrant) {
                return -1;
            }
            if (quadrant > o.quadrant) {
                return 1;
            }
            long a = ((long) dx) * ((long) o.dy);
            long b = ((long) dy) * ((long) o.dx);
            return Long.compare(a, b);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Angle angle = (Angle) o;
            return quadrant == angle.quadrant && dx == angle.dx && dy == angle.dy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(quadrant, dx, dy);
        }
    }
}

import lib.CharMatrix;
import lib.InputUtil;
import lib.MathUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
        first(asteroids);
    }

    private static void first(Set<Position> asteroids) {
        int maxCount = Integer.MIN_VALUE;
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
            maxCount = Math.max(maxCount, count);
        }
        System.out.println(maxCount);
    }

    static class Position {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
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
    }
}

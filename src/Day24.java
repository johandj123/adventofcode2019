import lib.CharMatrix;
import lib.InputUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
    public static void main(String[] args) throws IOException {
        CharMatrix charMatrix = new CharMatrix(InputUtil.readAsLines("input24.txt"));
        first(charMatrix);
        second(charMatrix);
    }

    private static void first(CharMatrix initial) {
        CharMatrix c = initial;
        Set<CharMatrix> seen = new HashSet<>(List.of(c));
        boolean notDone = true;
        while (notDone) {
            c = evolve(c);
            notDone = seen.add(c);
        }
        System.out.println(biodiversityRating(c));
    }

    private static void second(CharMatrix initial) {
        Set<LevelPosition> set = new HashSet<>();
        for (int y = 0; y < initial.getHeight(); y++) {
            for (int x = 0; x < initial.getWidth(); x++) {
                if (initial.get(x, y) == '#') {
                    LevelPosition levelPosition = new LevelPosition(0, x, y);
                    if (levelPosition.isValid()) {
                        set.add(levelPosition);
                    }
                }
            }
        }
        for (int i = 0; i < 200; i++) {
            set = evolve(set);
        }
        System.out.println(set.size());
    }

    private static CharMatrix evolve(CharMatrix c) {
        CharMatrix n = new CharMatrix(c.getHeight(), c.getWidth());
        for (int y = 0; y < c.getHeight(); y++) {
            for (int x = 0; x < c.getWidth(); x++) {
                var position = c.new Position(x, y);
                int aliveNeighbours = 0;
                for (var nposition : position.getNeighbours()) {
                    if (nposition.get() == '#') {
                        aliveNeighbours++;
                    }
                }
                if (position.get() == '#') {
                    if (aliveNeighbours == 1) {
                        n.set(x, y, '#');
                    }
                } else {
                    if (aliveNeighbours == 1 || aliveNeighbours == 2) {
                        n.set(x, y, '#');
                    }
                }
            }
        }
        return n;
    }

    private static Set<LevelPosition> evolve(Set<LevelPosition> set) {
        Set<LevelPosition> result = new HashSet<>();
        Set<LevelPosition> potentialAlive = set.stream()
                .flatMap(levelPosition -> levelPosition.getNeighbours().stream())
                .filter(levelPosition -> !set.contains(levelPosition))
                .collect(Collectors.toSet());
        for (LevelPosition levelPosition : set) {
            long aliveNeighbours = levelPosition.getNeighbours().stream()
                    .filter(set::contains)
                    .count();
            if (aliveNeighbours == 1) {
                result.add(levelPosition);
            }
        }
        for (LevelPosition levelPosition : potentialAlive) {
            long aliveNeighbours = levelPosition.getNeighbours().stream()
                    .filter(set::contains)
                    .count();
            if (aliveNeighbours == 1 || aliveNeighbours == 2) {
                result.add(levelPosition);
            }
        }
        return result;
    }

    private static int biodiversityRating(CharMatrix c) {
        int result = 0;
        int current = 1;
        for (int y = 0; y < c.getHeight(); y++) {
            for (int x = 0; x < c.getWidth(); x++) {
                if (c.get(x, y) == '#') {
                    result |= current;
                }
                current <<= 1;
            }
        }
        return result;
    }

    static class LevelPosition {
        final int level;
        final int x;
        final int y;

        public LevelPosition(int level, int x, int y) {
            this.level = level;
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LevelPosition that = (LevelPosition) o;
            return level == that.level && x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, x, y);
        }

        @Override
        public String toString() {
            return String.format("(%d|%d,%d)", level, x, y);
        }

        public boolean isValid() {
            return x >= 0 && x < 5 && y >= 0 && y < 5 && !(x == 2 & y == 2);
        }

        public List<LevelPosition> getNeighbours() {
            List<LevelPosition> result = new ArrayList<>();
            // Same level
            Stream.of(
                    new LevelPosition(level, x - 1, y),
                    new LevelPosition(level, x + 1, y),
                    new LevelPosition(level, x, y - 1),
                    new LevelPosition(level, x, y + 1)
            ).filter(LevelPosition::isValid).forEach(result::add);
            // Lower level (outside)
            if (x == 0) {
                result.add(new LevelPosition(level - 1, 1, 2));
            }
            if (x == 4) {
                result.add(new LevelPosition(level - 1, 3, 2));
            }
            if (y == 0) {
                result.add(new LevelPosition(level - 1, 2, 1));
            }
            if (y == 4) {
                result.add(new LevelPosition(level - 1, 2, 3));
            }
            // Higher level (inside)
            if (x == 1 && y == 2) {
                for (int i = 0; i < 5; i++) {
                    result.add(new LevelPosition(level + 1, 0, i));
                }
            }
            if (x == 3 && y == 2) {
                for (int i = 0; i < 5; i++) {
                    result.add(new LevelPosition(level + 1, 4, i));
                }
            }
            if (x == 2 && y == 1) {
                for (int i = 0; i < 5; i++) {
                    result.add(new LevelPosition(level + 1, i, 0));
                }
            }
            if (x == 2 && y == 3) {
                for (int i = 0; i < 5; i++) {
                    result.add(new LevelPosition(level + 1, i, 4));
                }
            }
            return result;
        }
    }
}

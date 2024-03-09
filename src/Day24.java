import lib.CharMatrix;
import lib.InputUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day24 {
    public static void main(String[] args) throws IOException {
        CharMatrix charMatrix = new CharMatrix(InputUtil.readAsLines("input24.txt"));
        first(charMatrix);
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
}

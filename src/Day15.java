import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.*;

public class Day15 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input15.txt");
        Computer computer = new Computer(program, new IOImpl());
        try {
            computer.runProgram();
        } catch (NoSuchElementException e) {
            // No way to end the program in a nice way when we have the full map, so we just catch the exception
        }
    }

    static class Position {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position add(Position o) {
            return new Position(x + o.x, y + o.y);
        }

        public static Position direction(int value) {
            switch (value) {
                case 1:
                    return new Position(0, -1);
                case 2:
                    return new Position(0, 1);
                case 3:
                    return new Position(-1, 0);
                case 4:
                    return new Position(1, 0);
                default:
                    throw new IllegalArgumentException("Unknown direction");
            }
        }

        public static int invertDirection(int value) {
            switch (value) {
                case 1:
                    return 2;
                case 2:
                    return 1;
                case 3:
                    return 4;
                case 4:
                    return 3;
                default:
                    throw new IllegalArgumentException("Unknown direction");
            }
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
            return String.format("%d,%d", x, y);
        }
    }

    static class IOImpl implements IO {
        private final Map<Position, Character> map = new HashMap<>();
        private Position current = new Position(0, 0);
        private final Deque<Integer> stack = new ArrayDeque<>();
        private int lastDirection;

        public IOImpl() {
            map.put(current, '.');
        }

        @Override
        public long read() {
            // Try to explore a new direction
            for (int direction = 1; direction <= 4; direction++) {
                Position next = current.add(Position.direction(direction));
                if (!map.containsKey(next)) {
                    stack.addLast(direction);
//                    System.out.println("Discover " + direction);
                    lastDirection = direction;
                    return direction;
                }
            }
            // Backtrack
            int direction = Position.invertDirection(stack.removeLast());
//            System.out.println("Backtrack " + direction);
            lastDirection = direction;
            return direction;
        }

        @Override
        public void write(long value) {
//            System.out.println("Result " + value);
            Position next = current.add(Position.direction(lastDirection));
            if (value == 1 || value == 2) {
                current = next;
                map.put(current, value == 2 ? 'O' : '.');
            } else if (value == 0) {
                map.put(next, '#');
                stack.removeLast();
            }
            if (value == 2) {
                System.out.println("Oxygen system at " + current + "; stack size " + stack.size());
            }
//            print();
        }

        public void print() {
            System.out.println("Current position: " + current);
            int x1 = map.keySet().stream().min(Comparator.comparing(p -> p.x)).orElseThrow().x;
            int x2 = map.keySet().stream().max(Comparator.comparing(p -> p.x)).orElseThrow().x;
            int y1 = map.keySet().stream().min(Comparator.comparing(p -> p.y)).orElseThrow().y;
            int y2 = map.keySet().stream().max(Comparator.comparing(p -> p.y)).orElseThrow().y;
            for (int y = y1; y <= y2; y++) {
                for (int x = x1; x <= x2; x++) {
                    if (x == current.x && y == current.y) {
                        System.out.print("X");
                    } else {
                        System.out.print(map.getOrDefault(new Position(x, y), ' '));
                    }
                }
                System.out.println();
            }
            System.out.println("==============================================================");
        }
    }
}

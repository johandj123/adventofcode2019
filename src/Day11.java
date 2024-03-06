import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Day11 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input11.txt");
        first(program);
        second(program);
    }

    private static void first(Program program) {
        PaintRobot paintRobot = new PaintRobot();
        Computer computer = new Computer(program, paintRobot);
        computer.runProgram();
        System.out.println(paintRobot.paint.size());
    }

    private static void second(Program program) {
        PaintRobot paintRobot = new PaintRobot();
        paintRobot.paint.put(new Position(0, 0), 1);
        Computer computer = new Computer(program, paintRobot);
        computer.runProgram();
        paintRobot.print();
    }

    static class PaintRobot implements IO {
        Position position = new Position(0, 0);
        int dx = 0;
        int dy = -1;
        Map<Position, Integer> paint = new HashMap<>();
        boolean state = false;

        @Override
        public long read() {
            return paint.getOrDefault(position, 0);
        }

        @Override
        public void write(long value) {
            if (!state) {
                paint.put(position, (int) value);
            } else {
                int ndx;
                int ndy;
                if (value == 0) {
                    ndx = dy;
                    ndy = -dx;
                } else {
                    ndx = -dy;
                    ndy = dx;
                }
                dx = ndx;
                dy = ndy;
                position = new Position(position.x + dx, position.y + dy);
            }
            state = !state;
        }

        void print() {
            int x1 = paint.keySet().stream().min(Comparator.comparing(position -> position.x)).orElseThrow().x;
            int y1 = paint.keySet().stream().min(Comparator.comparing(position -> position.y)).orElseThrow().y;
            int x2 = paint.keySet().stream().max(Comparator.comparing(position -> position.x)).orElseThrow().x;
            int y2 = paint.keySet().stream().max(Comparator.comparing(position -> position.y)).orElseThrow().y;
            for (int y = y1; y <= y2; y++) {
                for (int x = x1; x <= x2; x++) {
                    System.out.print(paint.getOrDefault(new Position(x, y), 0) == 0 ? '.' : '#');
                }
                System.out.println();
            }
        }
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

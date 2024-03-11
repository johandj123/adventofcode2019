import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Day19 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input19.txt");
        first(program);
        second(program);
    }

    private static void first(Program program) {
        int count = 0;
        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 50; x++) {
                if (scan(program, x, y)) {
                    count++;
                }
            }
        }
        System.out.println(count);
    }

    private static void second(Program program) {
        boolean[][] positions = new boolean[2000][2000];
        for (int y = 0; y < positions.length; y++) {
            for (int x = 0; x < positions[0].length; x++) {
                positions[y][x] = scan(program, x, y);
            }
        }
        int[][] hroom = new int[positions.length][positions[0].length];
        for (int y = 0; y < positions.length; y++) {
            int counter = 0;
            for (int x = positions[0].length - 1; x >= 0; x--) {
                if (!positions[y][x]) {
                    counter = 0;
                } else {
                    hroom[y][x] = ++counter;
                }
            }
        }
        for (int y = 0; y < positions.length - 99; y++) {
            for (int x = 0; x < positions[0].length; x++) {
                boolean ok = true;
                for (int y1 = 0; y1 < 100; y1++) {
                    if (hroom[y + y1][x] < 100) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    System.out.println(x * 10000 + y);
                    return;
                }
            }
        }
    }

    private static boolean scan(Program program,int x,int y) {
        IOImpl io = new IOImpl(x, y);
        Computer computer = new Computer(program, io);
        computer.runProgram();
        return io.output != 0;
    }

    static class IOImpl implements IO {
        final Deque<Integer> input = new ArrayDeque<>();
        Long output;

        public IOImpl(int x, int y) {
            input.offerLast(x);
            input.offerLast(y);
        }

        @Override
        public long read() {
            return input.removeFirst();
        }

        @Override
        public void write(long value) {
            output = value;
        }
    }
}

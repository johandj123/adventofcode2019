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

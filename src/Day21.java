import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

public class Day21 {
    private static final String SPRINGCODE1 = "NOT A J\nNOT B T\nOR T J\nNOT C T\nOR T J\nAND D J\nWALK\n";
    private static final String SPRINGCODE2 =
            "NOT A J\n" +
            "NOT B T\n" +
            "OR T J\n" +
            "NOT C T\n" +
            "OR T J\n" +
            "AND D J\n" +
            "NOT E T\n" +
            "NOT T T\n" +
            "OR H T\n" +
            "AND T J\n" +
            "RUN\n";

    public static void main(String[] args) throws IOException {
        Program program = new Program("input21.txt");
        first(program);
        second(program);
    }

    private static void first(Program program) {
        Computer computer = new Computer(program, new IOImpl(SPRINGCODE1));
        computer.runProgram();
    }

    private static void second(Program program) {
        Computer computer = new Computer(program, new IOImpl(SPRINGCODE2));
        computer.runProgram();
    }

    static class IOImpl implements IO {
        final Deque<Integer> inDeque;

        IOImpl(String springCode) {
            inDeque = springCode.chars().boxed().collect(Collectors.toCollection(ArrayDeque::new));
        }

        @Override
        public long read() {
            return inDeque.removeFirst();
        }

        @Override
        public void write(long value) {
            if (value > 0 && value <= 0xFF) {
                System.out.print((char) value);
            } else {
                System.out.println(value);
            }
        }
    }
}

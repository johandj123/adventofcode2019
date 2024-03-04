import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input7.txt");
        first(program);
    }

    private static void first(Program program) {
        long maxValue = Long.MIN_VALUE;
        for (int i1 = 0; i1 < 5; i1++) {
            for (int i2 = 0; i2 < 5; i2++) {
                for (int i3 = 0; i3 < 5; i3++) {
                    for (int i4 = 0; i4 < 5; i4++) {
                        for (int i5 = 0; i5 < 5; i5++) {
                            int[] phases = new int[]{i1, i2, i3, i4, i5};
                            if (noDuplicates(phases)) {
                                long value = execute(program, phases);
                                maxValue = Math.max(maxValue, value);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(maxValue);
    }

    private static boolean noDuplicates(int[] phases) {
        Set<Integer> set = Arrays.stream(phases).boxed().collect(Collectors.toSet());
        return set.size() == phases.length;
    }

    private static long execute(Program program, int[] phases) {
        long current = 0L;
        for (int phase : phases) {
            current = execute(program, phase, current);
        }
        return current;
    }

    private static long execute(Program program, int phase, long value) {
        IOImpl io = new IOImpl(phase, value);
        Computer computer = new Computer(program, io);
        computer.runProgram();
        return io.output;
    }

    static class IOImpl implements IO
    {
        private ArrayDeque<Long> input;
        private Long output;

        public IOImpl(int phase, long value) {
            input = new ArrayDeque<>(List.of((long) phase, value));
        }

        @Override
        public long read() {
            return input.removeFirst();
        }

        @Override
        public void write(long value) {
            if (output != null) {
                throw new IllegalStateException();
            }
            output = value;
        }
    }
}

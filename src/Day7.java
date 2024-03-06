import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Day7 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input7.txt");
        first(program);
        second(program);
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

    private static void second(Program program) {
        long maxValue = Long.MIN_VALUE;
        for (int i1 = 5; i1 < 10; i1++) {
            for (int i2 = 5; i2 < 10; i2++) {
                for (int i3 = 5; i3 < 10; i3++) {
                    for (int i4 = 5; i4 < 10; i4++) {
                        for (int i5 = 5; i5 < 10; i5++) {
                            int[] phases = new int[]{i1, i2, i3, i4, i5};
                            if (noDuplicates(phases)) {
                                long value = executeParallel(program, phases);
                                maxValue = Math.max(maxValue, value);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(maxValue);
    }

    private static long executeParallel(Program program, int[] phases) {
        List<BlockingQueue<Long>> queues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            queues.add(new LinkedBlockingQueue<>(1));
        }
        List<ComputerThread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BlockingQueue<Long> in = queues.get(i);
            BlockingQueue<Long> out = queues.get((i + 1) % 5);
            ComputerThread thread = new ComputerThread(program, phases[i], in, out);
            threads.add(thread);
        }
        try {
            queues.get(0).put(0L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return threads.get(4).lastOut;
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
        private final ArrayDeque<Long> input;
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

    static class BlockingQueueIO implements IO
    {
        private Integer phase;
        private final BlockingQueue<Long> in;
        private final BlockingQueue<Long> out;
        private Long lastOut;

        public BlockingQueueIO(int phase, BlockingQueue<Long> in, BlockingQueue<Long> out) {
            this.phase = phase;
            this.in = in;
            this.out = out;
        }

        @Override
        public long read() {
            if (phase != null) {
                int result = phase;
                phase = null;
                return result;
            }
            try {
                return in.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void write(long value) {
            try {
                out.put(value);
                lastOut = value;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class ComputerThread extends Thread {
        private final Program program;
        private final BlockingQueueIO io;
        private Long lastOut;

        public ComputerThread(Program program, int phase, BlockingQueue<Long> in, BlockingQueue<Long> out) {
            this.program = program;
            this.io = new BlockingQueueIO(phase, in, out);
        }

        @Override
        public void run() {
            Computer computer = new Computer(program, io);
            computer.runProgram();
            lastOut = io.lastOut;
        }
    }
}

import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Day23 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input23.txt");
        first(program);
    }

    private static void first(Program program) {
        List<Deque<Packet>> deques = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            deques.add(new ArrayDeque<>());
        }
        List<ComputerThread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            threads.add(new ComputerThread(program, new NetworkIO(i, deques)));
        }
        threads.forEach(Thread::start);
    }

    static class Packet {
        final long x;
        final long y;

        public Packet(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    static class NetworkIO implements IO {
        final int networkAddress;
        Long storedValue;
        final List<Deque<Packet>> deques;
        final long[] outValues = new long[3];
        int outCurrent = 0;

        public NetworkIO(int networkAddress, List<Deque<Packet>> deques) {
            this.networkAddress = networkAddress;
            this.deques = deques;
            storedValue = (long) networkAddress;
        }

        @Override
        public long read() {
            if (storedValue != null) {
                long result = storedValue;
                storedValue = null;
                return result;
            }
            Deque<Packet> deque = deques.get(networkAddress);
            synchronized (deque) {
                Packet packet = deque.pollFirst();
                if (packet == null) {
                    return -1;
                } else {
                    storedValue = packet.y;
                    return packet.x;
                }
            }
        }

        @Override
        public void write(long value) {
            outValues[outCurrent++] = value;
            if (outCurrent == 3) {
                outCurrent = 0;
                if (outValues[0] == 255) {
                    System.out.println(outValues[2]);
                } else {
                    deques.get((int) outValues[0]).offerLast(new Packet(outValues[1], outValues[2]));
                }
            }
        }
    }

    static class ComputerThread extends Thread {
        private final Program program;
        private NetworkIO io;

        public ComputerThread(Program program, NetworkIO io) {
            this.program = program;
            this.io = io;
        }

        @Override
        public void run() {
            Computer computer = new Computer(program, io);
            computer.runProgram();
        }
    }
}

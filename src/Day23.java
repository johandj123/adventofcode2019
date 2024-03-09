import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.*;

public class Day23 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input23.txt");
        first(program);
    }

    private static void first(Program program) {
        Shared shared = new Shared();
        List<ComputerThread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            threads.add(new ComputerThread(program, new NetworkIO(i, shared)));
        }
        threads.forEach(Thread::start);
        new NATThread(shared).start();
    }

    static class Packet {
        final long x;
        final long y;

        public Packet(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Shared {
        final List<Deque<Packet>> deques;
        Long last255x;
        Long last255y;
        Long sent0y;

        public Shared() {
            deques = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                deques.add(new ArrayDeque<>());
            }
        }
    }

    static class NetworkIO implements IO {
        final int networkAddress;
        boolean receivedNetworkAddress = false;
        boolean receivedX;
        final Shared shared;
        final long[] outValues = new long[3];
        int outCurrent = 0;

        public NetworkIO(int networkAddress, Shared shared) {
            this.networkAddress = networkAddress;
            this.shared = shared;
        }

        @Override
        public long read() {
            if (!receivedNetworkAddress) {
                receivedNetworkAddress = true;
                return networkAddress;
            }
            Deque<Packet> deque = shared.deques.get(networkAddress);
            synchronized (shared) {
                if (!receivedX) {
                    Packet packet = deque.peekFirst();
                    if (packet == null) {
                        return -1;
                    } else {
                        receivedX = true;
                        return packet.x;
                    }
                } else {
                    Packet packet = deque.removeFirst();
                    shared.notify();
                    receivedX = false;
                    return packet.y;
                }
            }
        }

        @Override
        public void write(long value) {
            outValues[outCurrent++] = value;
            if (outCurrent == 3) {
                outCurrent = 0;
                if (outValues[0] == 255) {
                    shared.last255x = outValues[1];
                    shared.last255y = outValues[2];
                    System.out.println(outValues[2]);
                } else {
                    shared.deques.get((int) outValues[0]).offerLast(new Packet(outValues[1], outValues[2]));
                }
            }
        }
    }

    static class ComputerThread extends Thread {
        private final Program program;
        private final NetworkIO io;

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

    static class NATThread extends Thread {
        private final Shared shared;

        public NATThread(Shared shared) {
            this.shared = shared;
        }

        @Override
        public void run() {
            synchronized (shared) {
                while (true) {
                    try {
                        shared.wait();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (shared.last255x != null && shared.last255y != null && shared.deques.stream().allMatch(Collection::isEmpty)) {
                        shared.deques.get(0).offerLast(new Packet(shared.last255x, shared.last255y));
                        if (Objects.equals(shared.sent0y, shared.last255y)) {
                            System.out.println("Repeated delivery of " + shared.last255y + " to network address 0");
                            System.exit(0);
                        }
                        shared.sent0y = shared.last255y;
                    }
                }
            }
        }
    }
}

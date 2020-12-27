import intcode.AsciiIO;
import intcode.Computer;
import intcode.Program;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Day17 {
    private char[][] data;
    private int width;
    private int height;

    public static void main(String[] args) throws IOException {
        new Day17().start();
    }

    private void start() throws IOException {
        Program program = new Program("input17.txt");
        first(program);
        second(program);
    }

    private void first(Program program) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Computer computer = new Computer(program, new AsciiIO(System.in, byteArrayOutputStream));
        computer.runProgram();

        String[] output = byteArrayOutputStream.toString(StandardCharsets.US_ASCII).split("\n");
        width = output[0].length();
        height = output.length;
        data = new char[output.length][output[0].length()];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = output[y].charAt(x);
            }
        }

        int sum = 0;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (isIntersection(x, y)) {
                    sum += (x * y);
                }
            }
        }
        System.out.println("First: " + sum);
    }

    private void second(Program program) {
        String fullProgram = determineFullProgram();
        System.out.println(fullProgram);
        /*
        I manually split the full program into subprograms A,B,C and the main program by starting at the beginning of the full program
        and find the largest substring of commands that occurs multiple times in the full program and assigning letter A to it.
        Then continue to find subprograms B and C in the same way.
        */
        final String mainProgram = "A,B,A,C,B,C,B,A,C,B";
        final String subProgramA = "L,6,R,8,R,12,L,6,L,8";
        final String subProgramB = "L,10,L,8,R,12";
        final String subProgramC = "L,8,L,10,L,6,L,6";
        final String continousVideoFeed = "y";
        String fullInput = String.join("\n", List.of(mainProgram, subProgramA, subProgramB, subProgramC, continousVideoFeed)) + "\n";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullInput.getBytes(StandardCharsets.US_ASCII));

        CustomAsciiIO io = new CustomAsciiIO(byteArrayInputStream, System.out);
        Computer computer = new Computer(program, io);
        computer.setMemory(0, 2);
        computer.runProgram();

        System.out.println("Second: " + io.lastValue);
    }

    private String determineFullProgram() {
        List<String> result = new ArrayList<>();
        Position position = findStart();
        Direction direction = new Direction(0, -1);
        do {
            Direction left = direction.turnLeft();
            Direction right = direction.turnRight();
            if (position.advance(left).isScaffold()) {
                direction = left;
                result.add("L");
            } else if (position.advance(right).isScaffold()) {
                direction = right;
                result.add("R");
            } else {
                break;
            }
            int count = 0;
            while (position.advance(direction).isScaffold()) {
                count++;
                position = position.advance(direction);
            }
            result.add(Integer.toString(count));
        } while (true);
        return String.join(",", result);
    }

    private boolean isIntersection(int x, int y) {
        return data[y][x] == '#' && data[y - 1][x] == '#' && data[y + 1][x] == '#' &&
                data[y][x - 1] == '#' && data[y][x + 1] == '#';
    }

    private Position findStart() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (data[y][x] == '^') {
                    return new Position(x, y);
                }
            }
        }
        throw new IllegalStateException("Start position not found");
    }

    class Position {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position advance(Direction direction) {
            return new Position(x + direction.dx, y + direction.dy);
        }

        public boolean isScaffold() {
            return (x >= 0 && y >= 0 && x < width && y < height && data[y][x] == '#');
        }
    }

    static class Direction {
        final int dx;
        final int dy;

        public Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public Direction turnLeft() {
            return new Direction(dy, -dx);
        }

        public Direction turnRight() {
            return new Direction(-dy, dx);
        }
    }

    static class CustomAsciiIO extends AsciiIO {
        long lastValue = 0;

        public CustomAsciiIO(InputStream inputStream, OutputStream outputStream) {
            super(inputStream, outputStream);
        }

        @Override
        public void write(long value) {
            lastValue = value;
            super.write(value);
        }
    }
}

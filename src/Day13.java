import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;

public class Day13 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input13.txt");
        first(program);
        second(program);
    }

    private static void first(Program program) {
        Arcade arcade = new Arcade();
        Computer computer = new Computer(program, arcade);
        computer.runProgram();
        arcade.print();
        System.out.println(arcade.blockCount());
    }

    private static void second(Program program) {
        Arcade arcade = new Arcade();
        Computer computer = new Computer(program, arcade);
        computer.setMemory(0, 2);
        computer.runProgram();
        arcade.print();
        System.out.println(arcade.score);
    }

    static class Arcade implements IO {
        int[] values = new int[3];
        int current = 0;
        int score;
        int[][] screen = new int[37][26];

        @Override
        public long read() {
            return Integer.compare(xOf(4), xOf(3));
        }

        @Override
        public void write(long value) {
            values[current++] = (int) value;
            if (current == 3) {
                current = 0;
                if (values[0] == -1 && values[1] == 0) {
                    score = values[2];
                } else {
                    screen[values[0]][values[1]] = values[2];
                }
            }
        }

        public void print() {
            for (int y = 0; y < screen[0].length; y++) {
                for (int x = 0; x < screen.length; x++) {
                    System.out.print(screen[x][y]);
                }
                System.out.println();
            }
        }

        public int blockCount() {
            int count = 0;
            for (int y = 0; y < screen[0].length; y++) {
                for (int x = 0; x < screen.length; x++) {
                    if (screen[x][y] == 2) {
                        count++;
                    }
                }
            }
            return count;
        }

        public int xOf(int value) {
            for (int y = 0; y < screen[0].length; y++) {
                for (int x = 0; x < screen.length; x++) {
                    if (screen[x][y] == value) {
                        return x;
                    }
                }
            }
            throw new IllegalStateException("Value " + value + " not found on screen");
        }
    }
}

import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;

public class Day13 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input13.txt");
        Arcade arcade = new Arcade();
        Computer computer = new Computer(program, arcade);
        computer.runProgram();
        arcade.print();
        System.out.println(arcade.blockCount());
    }

    static class Arcade implements IO {
        int[] values = new int[3];
        int current = 0;
        int[][] screen = new int[37][26];

        @Override
        public long read() {
            return 0;
        }

        @Override
        public void write(long value) {
            values[current++] = (int) value;
            if (current == 3) {
                current = 0;
                screen[values[0]][values[1]] = values[2];
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
    }
}

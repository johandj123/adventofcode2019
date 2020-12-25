import intcode.Computer;
import intcode.Program;

import java.io.IOException;

public class Day2 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input2.txt");
        System.out.println("First: " + runProgram(program, 12, 2));
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                long output = runProgram(program, noun, verb);
                if (output == 19690720L) {
                    int result = (100 * noun) + verb;
                    System.out.println("Second: " + result);
                }
            }
        }
    }

    private static long runProgram(Program program, int noun, int verb) {
        Computer computer = new Computer(program);
        computer.setMemory(1, noun);
        computer.setMemory(2, verb);
        computer.runProgram();
        return computer.getMemory(0);
    }
}

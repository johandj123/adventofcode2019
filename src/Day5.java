import intcode.Computer;
import intcode.Program;

import java.io.IOException;

public class Day5 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input5.txt");
        Computer computer = new Computer(program);
        computer.runProgram();
    }
}

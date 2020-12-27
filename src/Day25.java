import intcode.AsciiIO;
import intcode.Computer;
import intcode.Program;

import java.io.IOException;

public class Day25 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input25.txt");
        Computer computer = new Computer(program, new AsciiIO());
        computer.runProgram();
    }
}

import intcode.Computer;
import intcode.Program;

import java.io.IOException;

public class Day9 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input9.txt");
        Computer computer = new Computer(program);
        computer.runProgram();
    }
}

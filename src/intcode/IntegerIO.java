package intcode;

import java.util.Scanner;

public class IntegerIO implements IO {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public long read() {
        return scanner.nextLong();
    }

    @Override
    public void write(long value) {
        System.out.println(value);
    }
}

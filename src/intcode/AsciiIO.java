package intcode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class AsciiIO implements IO {
    private InputStream inputStream;
    private PrintStream printStream;

    public AsciiIO() {
        this.inputStream = System.in;
        this.printStream = System.out;
    }

    public AsciiIO(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.printStream = new PrintStream(outputStream);
    }

    @Override
    public long read() {
        try {
            return inputStream.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read", e);
        }
    }

    @Override
    public void write(long value) {
        printStream.print((char) value);
    }
}

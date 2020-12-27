package intcode;

public class NullIO implements IO {
    @Override
    public long read() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(long value) {
        throw new UnsupportedOperationException();
    }
}

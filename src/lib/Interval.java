package lib;

public class Interval {
    private final long start;
    private final long limit;

    private static final Interval EMPTY = new Interval(0L, 0L);

    public Interval(long start, long limit) {
        this.start = start;
        this.limit = limit;
    }

    public static Interval empty() {
        return EMPTY;
    }

    public static Interval ofClosed(long start, long end) {
        return new Interval(start, end + 1);
    }

    public static Interval ofStartAndLength(long start, long length) {
        return new Interval(start, start + length);
    }

    public boolean isEmpty() {
        return limit <= start;
    }

    public boolean isNotEmpty() {
        return limit > start;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return limit - 1;
    }

    public long getLimit() {
        return limit;
    }

    public long getLength() {
        return limit <= start ? 0L : limit - start;
    }

    public boolean in(long value) {
        return value >= start && value < limit;
    }

    public static boolean intersect(Interval a, Interval b) {
        return a.in(b.start) || a.in(b.getEnd()) || b.in(a.start) ||  b.in(a.getEnd());
    }

    public static Interval and(Interval a, Interval b) {
        if (a.isEmpty() || b.isEmpty()) {
            return EMPTY;
        }
        return new Interval(Math.max(a.start, b.start), Math.min(a.limit, b.limit));
    }

    @Override
    public String toString() {
        return "(" + start + "," + limit + ")";
    }
}

package lib;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharMatrix {
    private char[][] content;
    private char fill;

    public CharMatrix(CharMatrix charMatrix)
    {
        this.content = new char[charMatrix.content.length][];
        for (int y = 0; y < content.length; y++) {
            content[y] = Arrays.copyOf(charMatrix.content[y], charMatrix.content[y].length);
        }
        this.fill = charMatrix.fill;
    }

    public CharMatrix(char[][] content, char fill) {
        this.content = content;
        this.fill = fill;
    }

    public CharMatrix(String[] content, char fill) {
        int width = Arrays.stream(content).mapToInt(String::length).max().orElse(0);
        this.content = new char[content.length][width];
        for (int y = 0; y < content.length; y++) {
            Arrays.fill(this.content[y], fill);
            for (int x = 0; x < content[y].length(); x++) {
                this.content[y][x] = content[y].charAt(x);
            }
        }
        this.fill = fill;
    }

    public CharMatrix(String[] content)
    {
        this(content, ' ');
    }

    public CharMatrix(String content, char fill) {
        this(content.split("\n"), fill);
    }

    public CharMatrix(String content) {
        this(content.split("\n"));
    }

    public CharMatrix(List<String> content) {
        this(content.toArray(new String[0]), ' ');
    }

    public CharMatrix(List<String> content, char fill) {
        this(content.toArray(new String[0]), fill);
    }

    public CharMatrix(int height, int width, char fill) {
        this.content = new char[height][width];
        for (int y = 0; y < height; y++) {
            Arrays.fill(this.content[y], fill);
        }
        this.fill = fill;
    }

    public CharMatrix(int height, int width) {
        this(height, width, '.');
    }

    public int getWidth()
    {
        return content[0].length;
    }

    public int getHeight()
    {
        return content.length;
    }

    public boolean isValid(int x,int y)
    {
        return (x >= 0 && y >= 0 && x < getWidth() && y < getHeight());
    }

    public char get(int x,int y)
    {
        return content[y][x];
    }

    public char getUnbounded(int x,int y)
    {
        if (!isValid(x, y)) {
            return fill;
        } else {
            return content[y][x];
        }
    }

    public char getWrap(int x,int y)
    {
        return content[Math.floorMod(y, getHeight())][Math.floorMod(x, getWidth())];
    }

    public void set(int x,int y,char c)
    {
        content[y][x] = c;
    }

    public String getRow(int y)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = 0; x < content[y].length; x++) {
            stringBuilder.append(content[y][x]);
        }
        return stringBuilder.toString();
    }

    public String getColumn(int x)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < content.length; y++) {
            stringBuilder.append(content[y][x]);
        }
        return stringBuilder.toString();
    }

    public CharMatrix transpose()
    {
        String[] s = new String[getWidth()];
        for (int i = 0; i < getWidth(); i++) {
            s[i] = getColumn(i);
        }
        return new CharMatrix(s);
    }

    public CharMatrix mirrorHorizontal()
    {
        char[][] newContent = new char[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                newContent[y][x] = content[y][getWidth() - x - 1];
            }
        }
        return new CharMatrix(newContent, fill);
    }

    public CharMatrix mirrorVertical()
    {
        char[][] newContent = new char[getHeight()][getWidth()];
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                newContent[y][x] = content[getHeight() - y - 1][x];
            }
        }
        return new CharMatrix(newContent, fill);
    }

    public Optional<Position> find(char c)
    {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (content[y][x] == c) {
                    return Optional.of(new Position(x, y));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharMatrix that = (CharMatrix) o;
        return fill == that.fill && Arrays.deepEquals(content, that.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fill);
        result = 31 * result + Arrays.deepHashCode(content);
        return result;
    }

    @Override
    public String toString()
    {
        return IntStream.range(0, content.length)
                .mapToObj(this::getRow)
                .collect(Collectors.joining("\n"));
    }

    public class Position
    {
        final int x;
        final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isValid() {
            return CharMatrix.this.isValid(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public char get() {
            return CharMatrix.this.get(x, y);
        }

        public char getUnbounded() {
            return CharMatrix.this.getUnbounded(x, y);
        }

        public char getWrap() {
            return CharMatrix.this.getWrap(x, y);
        }

        public void set(char c) {
            CharMatrix.this.set(x, y, c);
        }

        public Position wrap()
        {
            return new Position(
                    Math.floorMod(x, getWidth()),
                    Math.floorMod(y, getHeight())
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public Position add(Position delta)
        {
            return new Position(x + delta.x, y + delta.y);
        }

        public Position add(int dx,int dy)
        {
            return new Position(x + dx, y + dy);
        }

        public List<Position> getNeighbours()
        {
            List<Position> result = new ArrayList<>();
            for (Position delta : new Position[] {
                    new Position(-1, 0),
                    new Position(1, 0),
                    new Position(0, -1),
                    new Position(0, 1),
            }) {
                Position next = this.add(delta);
                if (next.isValid()) {
                    result.add(next);
                }
            }
            return result;
        }

        public List<Position> getUnboundedNeighbours()
        {
            List<Position> result = new ArrayList<>();
            for (Position delta : new Position[] {
                    new Position(-1, 0),
                    new Position(1, 0),
                    new Position(0, -1),
                    new Position(0, 1),
            }) {
                result.add(this.add(delta));
            }
            return result;
        }

        public List<Position> getWrapNeighbours()
        {
            List<Position> result = new ArrayList<>();
            for (Position delta : new Position[] {
                    new Position(-1, 0),
                    new Position(1, 0),
                    new Position(0, -1),
                    new Position(0, 1),
            }) {
                Position next = this.add(delta);
                result.add(next.wrap());
            }
            return result;
        }

        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }
    }
}

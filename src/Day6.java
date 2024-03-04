import lib.InputUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day6 {
    private final Map<String,Thing> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new Day6().start();
    }

    private void start() throws IOException {
        readInput();
        first();
        second();
    }

    private void readInput() throws IOException {
        List<String> lines = InputUtil.readAsLines("input6.txt");
        for (String line : lines) {
            int index = line.indexOf(')');
            String a = line.substring(0, index);
            String b = line.substring(index + 1);
            Thing aa = map.computeIfAbsent(a, key -> new Thing());
            Thing bb = map.computeIfAbsent(b, key -> new Thing());
            aa.suborbits.add(bb);
            bb.orbits = aa;
        }
    }

    private void first() {
        List<Thing> current = List.of(map.get("COM"));
        int currentDistance = 0;
        while (!current.isEmpty()) {
            List<Thing> next = new ArrayList<>();
            for (Thing thing : current) {
                thing.distance = currentDistance;
                next.addAll(thing.suborbits);
            }

            currentDistance++;
            current = next;
        }
        int sum = map.values().stream().mapToInt(thing -> thing.distance).sum();
        System.out.println(sum);
    }

    private void second() {
        List<Thing> a = listUp(map.get("YOU"));
        List<Thing> b = listUp(map.get("SAN"));
        Thing common = a.stream().filter(b::contains).findFirst().orElseThrow();
        int da = a.indexOf(common);
        int db = b.indexOf(common);
        System.out.println(da + db);
    }

    private List<Thing> listUp(Thing root)
    {
        List<Thing> result = new ArrayList<>();
        Thing current = root;
        while (current.orbits != null) {
            current = current.orbits;
            result.add(current);
        }
        return result;
    }

    static class Thing {
        private Thing orbits;
        private final List<Thing> suborbits = new ArrayList<>();
        private int distance;
    }
}

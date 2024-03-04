package lib;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StepUtil {
    private StepUtil() {
    }

    public static <T> T performStepsWithCycleDetection(T start, int steps, Function<T, T> next) {
        T current = start;
        Map<T, Integer> map = new HashMap<>();
        int i = 0;
        int cycleLength = 0;
        while (i < steps) {
            map.put(current, i);
            current = next.apply(current);
            i++;
            Integer i1 = map.get(current);
            if (i1 != null) {
                cycleLength = i - i1;
                break;
            }
        }
        if (cycleLength > 0) {
            int cycles = (steps - i) / cycleLength;
            i += (cycles * cycleLength);
        }
        while (i < steps) {
            current = next.apply(current);
            i++;
        }
        return current;
    }
}

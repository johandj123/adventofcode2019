import lib.InputUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day16 {
    public static void main(String[] args) throws IOException {
        String input = InputUtil.readAsString("input16.txt");
        List<Integer> values = input.chars().mapToObj(c -> c - '0').collect(Collectors.toList());
        first(values);
        second(values);
    }

    private static void first(List<Integer> values) {
        for (int i = 0; i < 100; i++) {
            values = fft(values);
        }
        String result = asString(values.subList(0, 8));
        System.out.println(result);
    }

    private static void second(List<Integer> initial) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            values.addAll(initial);
        }
        int offset = Integer.parseInt(asString(values.subList(0, 7)));
        values = values.subList(offset, values.size());
        for (int i = 0; i < 100; i++) {
            fasterfft(values);
        }
        String result = asString(values.subList(0, 8));
        System.out.println(result);
    }

    private static void fasterfft(List<Integer> values) {
        List<Integer> sums = new ArrayList<>();
        int total = 0;
        sums.add(0);
        for (int value : values) {
            total += value;
            sums.add(total);
        }
        for (int i = 0; i < values.size(); i++) {
            values.set(i, Math.abs(total - sums.get(i)) % 10);
        }
    }

    private static String asString(List<Integer> values) {
        return values.stream().map(Object::toString).collect(Collectors.joining());
    }

    private static List<Integer> fft(List<Integer> input) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            List<Integer> pattern = determinePattern(i + 1, input.size());
            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += pattern.get(j) * input.get(j);
            }
            result.add(Math.abs(sum) % 10);
        }
        return result;
    }

    private static List<Integer> determinePattern(int position, int size) {
        int[] basePattern = new int[]{0, 1, 0, -1};
        List<Integer> result = new ArrayList<>();
        while (result.size() < size + 1) {
            for (int value : basePattern) {
                for (int i = 0; i < position; i++) {
                    result.add(value);
                }
            }
        }
        return result.subList(1, size + 1);
    }
}

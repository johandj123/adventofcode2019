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
    }

    private static void first(List<Integer> values) {
        for (int i = 0; i < 100; i++) {
            values = fft(values);
        }
        String result = asString(values).substring(0, 8);
        System.out.println(result);
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

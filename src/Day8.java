import lib.InputUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day8 {
    private static final int WIDTH = 25;
    private static final int HEIGHT = 6;
    private static final int LAYERPIXELS = WIDTH * HEIGHT;

    public static void main(String[] args) throws IOException {
        String input = InputUtil.readAsString("input8.txt");
        List<String> layers = new ArrayList<>();
        int layerCount = input.length() / LAYERPIXELS;
        for (int i = 0; i < layerCount; i++) {
            int start = i * LAYERPIXELS;
            layers.add(input.substring(start, start + LAYERPIXELS));
        }
        first(layers);
        second(layers);
    }

    private static void first(List<String> layers) {
        String layerWithLeastZeroes = layers.stream()
                .min(Comparator.comparing(layer -> count(layer, 0)))
                .orElseThrow();
        int first = count(layerWithLeastZeroes, 1) * count(layerWithLeastZeroes, 2);
        System.out.println(first);
    }

    private static void second(List<String> layers) {
        int position = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                char c = pixelValue(layers, position++);
                System.out.print(c);
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static int count(String layer, int value) {
        return (int) layer.chars().filter(c -> c == (value + '0')).count();
    }

    private static char pixelValue(List<String> layers, int position) {
        for (String layer : layers) {
            char c = layer.charAt(position);
            if (c == '0') {
                return '.';
            }
            if (c == '1') {
                return '#';
            }
        }
        return '?';
    }
}

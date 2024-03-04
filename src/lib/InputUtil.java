package lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputUtil {
    private InputUtil() {
    }

    public static String readAsString(String filename) throws IOException {
        return Files.readString(new File(filename).toPath()).trim();
    }

    public static List<String> readAsStringGroups(String filename) throws IOException {
        String input = readAsString(filename);
        String[] parts = input.split("\n\n");
        return Arrays.asList(parts);
    }

    public static List<String> readAsLines(String filename) throws IOException {
        return Files.readAllLines(new File(filename).toPath());
    }

    public static List<Integer> readAsIntegers(String filename) throws IOException {
        return splitIntoIntegers(readAsString(filename));
    }

    public static List<Integer> splitIntoIntegers(String input) {
        String[] parts = input.trim().split("\\s+");
        return Arrays.stream(parts).map(Integer::parseInt).collect(Collectors.toList());
    }

    public static List<Long> splitIntoLongs(String input) {
        String[] parts = input.trim().split("\\s+");
        return Arrays.stream(parts).map(Long::parseLong).collect(Collectors.toList());
    }
}

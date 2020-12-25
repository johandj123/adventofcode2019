import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {
    public static void main(String[] args) throws IOException {
        List<Integer> input = Files.readAllLines(new File("input1.txt").toPath())
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        int firstTotal = input.stream()
                .map(Day1::fuelRequirement)
                .reduce(Integer::sum)
                .orElse(0);
        System.out.println("First: " + firstTotal);
        int secondTotal = input.stream()
                .map(Day1::repeatedFuelRequirement)
                .reduce(Integer::sum)
                .orElse(0);
        System.out.println("Second: " + secondTotal);
    }

    private static int fuelRequirement(int mass) {
        return Math.max(0, (mass / 3) - 2);
    }

    private static int repeatedFuelRequirement(int mass) {
        int fuel = fuelRequirement(mass);
        if (fuel > 0) {
            fuel += repeatedFuelRequirement(fuel);
        }
        return fuel;
    }
}

import lib.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day12 {
    public static void main(String[] args) {
        first();
        second();
    }

    private static void first() {
        List<Moon> moons = getInputData();
        for (int i = 0; i < 1000; i++) {
            applyStep(moons);
        }
        int first = moons.stream().mapToInt(Moon::energy).sum();
        System.out.println(first);
    }

    private static void second() {
        List<Moon> moons = getInputData();
        List<Moon> initial = getInputData();
        List<Optional<Long>> periods = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            periods.add(Optional.empty());
        }
        long steps = 0;
        while (periods.stream().anyMatch(Optional::isEmpty)) {
            applyStep(moons);
            steps++;
            if (periods.get(0).isEmpty() && match(moons, initial, Moon::getX, Moon::getVx)) {
                periods.set(0, Optional.of(steps));
            }
            if (periods.get(1).isEmpty() && match(moons, initial, Moon::getY, Moon::getVy)) {
                periods.set(1, Optional.of(steps));
            }
            if (periods.get(2).isEmpty() && match(moons, initial, Moon::getZ, Moon::getVz)) {
                periods.set(2, Optional.of(steps));
            }
        }
        long second = MathUtil.lcm(periods.stream().map(Optional::orElseThrow).collect(Collectors.toList()));
        System.out.println(second);
    }

    private static boolean match(List<Moon> moons, List<Moon> initial, Function<Moon, Integer> positionFunction, Function<Moon, Integer> velocityFunction) {
        for (int i = 0; i < moons.size(); i++) {
            Moon a = moons.get(i);
            Moon b = initial.get(i);
            if (!Objects.equals(positionFunction.apply(a), positionFunction.apply(b)) || !Objects.equals(velocityFunction.apply(a), velocityFunction.apply(b))) {
                return false;
            }
        }
        return true;
    }

    private static void applyStep(List<Moon> moons) {
        for (Moon a : moons) {
            for (Moon b : moons) {
                if (a != b) {
                    a.applyGravity(b);
                }
            }
        }
        for (Moon moon : moons) {
            moon.applyVelocity();
        }
    }

    private static List<Moon> getInputData() {
        return List.of(
                new Moon(3, 2, -6),
                new Moon(-13, 18, 10),
                new Moon(-8, -1, 13),
                new Moon(5, 10, 4)
        );
//        return List.of(
//                new Moon(-8, -10, 0),
//                new Moon(5, 5, 10),
//                new Moon(2, -7, 3),
//                new Moon(9, -8, -3)
//        );
//        return List.of(
//                new Moon(-1, 0, 2),
//                new Moon(2, -10, -7),
//                new Moon(4, -8, 8),
//                new Moon(3, 5, -1)
//        );
    }

    static class Moon {
        int x;
        int y;
        int z;
        int vx;
        int vy;
        int vz;

        public Moon(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void applyGravity(Moon o) {
            vx += updateGravity(x, o.x);
            vy += updateGravity(y, o.y);
            vz += updateGravity(z, o.z);
        }

        public void applyVelocity() {
            x += vx;
            y += vy;
            z += vz;
        }

        private int updateGravity(int a, int b) {
            return Integer.compare(b, a);
        }

        private int potentialEnergy() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }

        private int kineticEnergy() {
            return Math.abs(vx) + Math.abs(vy) + Math.abs(vz);
        }

        public int energy() {
            return potentialEnergy() * kineticEnergy();
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getVx() {
            return vx;
        }

        public int getVy() {
            return vy;
        }

        public int getVz() {
            return vz;
        }
    }
}

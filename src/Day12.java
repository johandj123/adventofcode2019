import java.util.List;

public class Day12 {
    public static void main(String[] args) {
        List<Moon> moons = List.of(
                new Moon(3, 2, -6),
                new Moon(-13, 18, 10),
                new Moon(-8, -1, 13),
                new Moon(5, 10, 4)
        );
        for (int i = 0; i < 1000; i++) {
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
        int first = moons.stream().mapToInt(Moon::energy).sum();
        System.out.println(first);
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
    }
}

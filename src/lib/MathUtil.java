package lib;

import java.util.Arrays;
import java.util.List;

public class MathUtil {
    private MathUtil() {
    }

    public static long gcd(long a,long b)
    {
        while (b != 0) {
            long bnew = Math.floorMod(a, b);
            a = b;
            b = bnew;
        }
        return a;
    }

    public static long lcm(long a, long b)
    {
        return (a * b) / gcd(a, b);
    }

    public static long lcm(long... values)
    {
        return Arrays.stream(values)
                .reduce(MathUtil::lcm)
                .orElseThrow();
    }

    public static long lcm(List<Long> values)
    {
        return values.stream()
                .reduce(MathUtil::lcm)
                .orElseThrow();
    }

    public static class ChineseRemainderTheoremEquation
    {
        /**
         * x = c mod m
         * @param c
         * @param m
         */
        public ChineseRemainderTheoremEquation(long c, long m) {
            this.c = c;
            this.m = m;
        }

        final long c;
        final long m;
        long n;
        long w;
    }

    public static long solveUsingChineseRemainderTheorem(List<ChineseRemainderTheoremEquation> equations) {
        long mproduct = equations
                .stream()
                .map(equation -> equation.m)
                .reduce(1L, (a, b) -> a * b);

        for (ChineseRemainderTheoremEquation equation : equations) {
            equation.n = mproduct / equation.m;
            equation.w = egcdx(equation.n, equation.m);
            if (equation.w < 0) {
                equation.w += equation.m;
            }
        }

        long solution = equations
                .stream()
                .map(equation -> equation.c * equation.n * equation.w)
                .reduce(0L, Long::sum);
        return Math.floorMod(solution, mproduct);
    }

    /**
     * Solves for x in ax+by=gcd(a,b)=1
     * @param a
     * @param b
     * @return
     */
    private static long egcdx(long a,long b)
    {
        long x = 0;
        long y = 1;
        long u = 1;
        long v = 0;
        while (a != 0) {
            long q = b / a;
            long r = Math.floorMod(b, a);
            long m = x - u * q;
            long n = y - v * q;

            b = a;
            a = r;
            x = u;
            y = v;
            u = m;
            v = n;
        }

        if (b != 1) {
            throw new IllegalStateException("Input values are not coprime");
        }

        return x;
    }
}

import lib.InputUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 {
    public static void main(String[] args) throws IOException {
        List<String> input = InputUtil.readAsLines("input22.txt");
        first(input);
        second(input);
    }

    private static void first(List<String> input) {
        Deck deck = new Deck(10007);
        for (String s : input) {
            if ("deal into new stack".equals(s)) {
                deck.dealIntoNewStack();
            } else if (s.startsWith("cut")) {
                deck.cutNCards(Integer.parseInt(s.substring(4)));
            } else if (s.startsWith("deal with increment")) {
                deck.dealWithIncrementN(Integer.parseInt(s.substring(20)));
            } else {
                throw new IllegalArgumentException("Unknown shuffle instruction: " + s);
            }
        }
        for (int i = 0; i < deck.cards.size(); i++) {
            if (deck.cards.get(i) == 2019) {
                System.out.println(i);
            }
        }
    }

    private static void second(List<String> input) {
        BigInteger numberOfCards = BigInteger.valueOf(119315717514047L);
        BigInteger times = BigInteger.valueOf(101741582076661L);
        LinearFunction linearFunction = input.stream()
                .map(s -> asLinearFunction(s, numberOfCards))
                .reduce((a, b) -> a.compose(b, numberOfCards))
                .orElseThrow();
        linearFunction = linearFunction.repeat(times, numberOfCards);
        System.out.println(linearFunction.apply(BigInteger.valueOf(2020L)).mod(numberOfCards));
    }

    private static LinearFunction asLinearFunction(String s,BigInteger numberOfCards) {
        if ("deal into new stack".equals(s)) {
            return new LinearFunction(BigInteger.ONE.negate(), BigInteger.ONE.negate().subtract(numberOfCards));
        } else if (s.startsWith("cut")) {
            return new LinearFunction(BigInteger.ONE, new BigInteger(s.substring(4)).mod(numberOfCards));
        } else if (s.startsWith("deal with increment")) {
            BigInteger z = new BigInteger(s.substring(20)).modInverse(numberOfCards);
            return new LinearFunction(z.mod(numberOfCards), BigInteger.ZERO);
        } else {
            throw new IllegalArgumentException("Unknown shuffle instruction: " + s);
        }
    }

    static class Deck {
        List<Integer> cards;

        public Deck(int numberOfCards) {
            cards = IntStream.range(0, numberOfCards).boxed().collect(Collectors.toList());
        }

        public void dealIntoNewStack() {
            Collections.reverse(cards);
        }

        public void cutNCards(int n) {
            if (n > 0) {
                Deque<Integer> deque = new ArrayDeque<>(cards);
                for (int i = 0; i < n; i++) {
                    deque.offerLast(deque.removeFirst());
                }
                cards = new ArrayList<>(deque);
            } else if (n < 0) {
                Deque<Integer> deque = new ArrayDeque<>(cards);
                for (int i = 0; i < -n; i++) {
                    deque.offerFirst(deque.removeLast());
                }
                cards = new ArrayList<>(deque);
            }
        }

        public void dealWithIncrementN(int n) {
            List<Integer> newCards = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                newCards.add(null);
            }
            int index = 0;
            for (int i = 0; i < cards.size(); i++) {
                newCards.set(index, cards.get(i));
                index = (index + n) % cards.size();
            }
            cards = newCards;
        }
    }

    /**
     * Represents linear function f(x)=ax+b
     */
    static class LinearFunction {
        public static final LinearFunction IDENTITY = new LinearFunction(BigInteger.ONE, BigInteger.ZERO);

        private final BigInteger a;
        private final BigInteger b;

        public LinearFunction(BigInteger a, BigInteger b) {
            this.a = a;
            this.b = b;
        }

        public BigInteger apply(BigInteger x) {
            return a.multiply(x).add(b);
        }

        public LinearFunction compose(LinearFunction o,BigInteger modulo) {
            return new LinearFunction(a.multiply(o.a).mod(modulo), a.multiply(o.b).add(b).mod(modulo));
        }

        public LinearFunction repeat(BigInteger times,BigInteger modulo) {
            if (BigInteger.ZERO.equals(times)) {
                return IDENTITY;
            } else if (BigInteger.ZERO.equals(times.mod(BigInteger.TWO))) {
                LinearFunction linearFunction = this.compose(this, modulo);
                return linearFunction.repeat(times.divide(BigInteger.TWO), modulo);
            } else {
                LinearFunction linearFunction = repeat(times.subtract(BigInteger.ONE), modulo);
                return compose(linearFunction, modulo);
            }
        }
    }
}

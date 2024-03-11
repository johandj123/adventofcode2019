import lib.InputUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22 {
    public static void main(String[] args) throws IOException {
        List<String> input = InputUtil.readAsLines("input22.txt");
        first(input);
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
}

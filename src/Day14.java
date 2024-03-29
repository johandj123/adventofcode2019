import lib.InputUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14 {
    public static void main(String[] args) throws IOException {
        Map<String, Reaction> reactions = InputUtil.readAsLines("input14.txt")
                .stream()
                .map(Reaction::new)
                .collect(Collectors.toMap(reaction -> reaction.out.chemical, reaction -> reaction));
        first(reactions);
        second(reactions);
    }

    private static void first(Map<String, Reaction> reactions) {
        long ore = determineOreCountForFuelCount(reactions, 1L);
        System.out.println(ore);
    }

    private static void second(Map<String, Reaction> reactions) {
        long l = 1L;
        long h = 100000000L;
        while (l < h - 1) {
            long m = (l + h) / 2;
            if (determineOreCountForFuelCount(reactions, m) < 1000000000000L) {
                l = m;
            } else {
                h = m;
            }
        }
        System.out.println(l);
    }

    private static long determineOreCountForFuelCount(Map<String, Reaction> reactions, long fuel) {
        Map<String, Long> map = new HashMap<>();
        map.put("FUEL", fuel);
        while (!mapDone(map)) {
            map = performInverseReactions(map, reactions);
        }
        long ore = map.get("ORE");
        return ore;
    }

    private static Map<String, Long> performInverseReactions(Map<String, Long> map, Map<String, Reaction> reactions) {
        Map<String, Long> result = new HashMap<>(map);
        for (var entry : map.entrySet()) {
            Reaction reaction = reactions.get(entry.getKey());
            if (reaction == null) {
                continue;
            }
            if (entry.getValue() > 0) {
                long reactionCount = (entry.getValue() + reaction.out.count - 1) / reaction.out.count;
                modifyMap(result, entry.getKey(), -reactionCount * reaction.out.count);
                for (var cwq : reaction.in) {
                    modifyMap(result, cwq.chemical, reactionCount * cwq.count);
                }
            }
        }
        return result;
    }

    private static void modifyMap(Map<String, Long> map, String chemical, long countModification) {
        long currentCount = map.getOrDefault(chemical, 0L);
        currentCount += countModification;
        if (currentCount == 0) {
            map.remove(chemical);
        } else {
            map.put(chemical, currentCount);
        }
    }

    private static boolean mapDone(Map<String, Long> map) {
        return map.entrySet().stream().allMatch(entry -> "ORE".equals(entry.getKey()) || entry.getValue() <= 0);
    }

    static class Reaction {
        ChecimalWithQuantity out;
        List<ChecimalWithQuantity> in;

        Reaction(String s) {
            String[] sp = s.split(" => ");
            String[] spsp = sp[0].split(", ");
            in = Arrays.stream(spsp).map(ChecimalWithQuantity::new).collect(Collectors.toList());
            out = new ChecimalWithQuantity(sp[1]);
        }

        @Override
        public String toString() {
            return String.format("%s => %s", in, out);
        }
    }

    static class ChecimalWithQuantity {
        long count;
        String chemical;

        ChecimalWithQuantity(String s) {
            String[] sp = s.split(" ");
            count = Long.parseLong(sp[0]);
            chemical = sp[1];
        }

        @Override
        public String toString() {
            return String.format("%d %s", count, chemical);
        }
    }
}

import intcode.Computer;
import intcode.IO;
import intcode.Program;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day25 {
    public static void main(String[] args) throws IOException {
        Program program = new Program("input25.txt");
        Computer computer = new Computer(program, new IOImpl());
        computer.runProgram();
    }

    static class IOImpl implements IO {
        private static final Set<String> CURSED_ITEMS = Set.of("infinite loop", "photons", "giant electromagnet", "molten lava", "escape pod");
        public static final String SECURITY_CHECKPOINT = "Security Checkpoint";

        private Mode mode = Mode.COLLECT;
        private Deque<Character> input = new ArrayDeque<>();
        private List<Character> output = new ArrayList<>();
        private Room currentRoom = new Room();
        private Deque<Direction> trail = new ArrayDeque<>();
        private Deque<Direction> securityTrail;
        private Direction securityEnter;
        private List<Direction> directions = new ArrayList<>();
        private List<String> items = new ArrayList<>();
        private List<String> collectedItems = new ArrayList<>();
        private int currentMask = 0;
        private Deque<Integer> grayCodes;

        @Override
        public long read() {
            if (!input.isEmpty()) {
                return input.removeFirst();
            }
            if (mode == Mode.COLLECT) {
                collect();
            }
            if (mode == Mode.GOTO_SECURITY) {
                if (!securityTrail.isEmpty()) {
                    Direction direction = securityTrail.removeFirst();
                    addInput(direction.name().toLowerCase());
                    currentRoom = currentRoom.links.get(direction);
                } else {
                    mode = Mode.PASS_SECURITY;
                }
            }
            if (mode == Mode.PASS_SECURITY) {
                if (grayCodes == null) {
                    grayCodes = new ArrayDeque<>(generateGrayCodes(collectedItems.size()));
                }
                int wantedMask = grayCodes.peekFirst();
                if ((currentMask ^ wantedMask) == 0) {
                    addInput(securityEnter.name().toLowerCase());
                    grayCodes.removeFirst();
                } else {
                    for (int i = 0; i < collectedItems.size(); i++) {
                        int bitmask = 1 << i;
                        if ((currentMask & bitmask) == 0 && (wantedMask & bitmask) != 0) {
                            addInput("drop " + collectedItems.get(i));
                            currentMask |= bitmask;
                            break;
                        } else if ((currentMask & bitmask) != 0 && (wantedMask & bitmask) == 0) {
                            addInput("take " + collectedItems.get(i));
                            currentMask &= (~bitmask);
                            break;
                        }
                    }
                }
            }
            if (!input.isEmpty()) {
                return input.removeFirst();
            }
            throw new RuntimeException("No command");
        }

        private void collect() {
            // Collect items if available
            if (!items.isEmpty()) {
                for (String item : items) {
                    if (!CURSED_ITEMS.contains(item)) {
                        addInput("take " + item);
                        items.remove(item);
                        collectedItems.add(item);
                        return;
                    }
                }
            }
            // Go to unexplored room
            if (SECURITY_CHECKPOINT.equals(currentRoom.name)) {
                if (securityTrail == null) {
                    securityTrail = new ArrayDeque<>(trail);
                    for (Direction direction : directions) {
                        if (!currentRoom.links.containsKey(direction)) {
                            securityEnter = direction;
                        }
                    }
                }
            } else {
                for (Direction direction : directions) {
                    if (!currentRoom.links.containsKey(direction)) {
                        addInput(direction.name().toLowerCase());
                        Room newRoom = new Room();
                        currentRoom.links.put(direction, newRoom);
                        newRoom.links.put(direction.inverse(), currentRoom);
                        currentRoom = newRoom;
                        trail.offerLast(direction);
                        return;
                    }
                }
            }
            // Backtrack
            if (!trail.isEmpty()) {
                Direction direction = trail.removeLast().inverse();
                addInput(direction.name().toLowerCase());
                currentRoom = currentRoom.links.get(direction);
                return;
            }
            // We have explored all rooms, collect all collectable items and are back at the start
            mode = Mode.GOTO_SECURITY;
        }

        private void addInput(String s) {
            s.chars().forEach(c -> input.offerLast((char) c));
            input.offerLast('\n');
            System.out.println(s);
        }

        @Override
        public void write(long value) {
            System.out.print((char) value);
            if (value != '\n') {
                output.add((char) value);
            } else {
                String s = output.stream().map(String::valueOf).collect(Collectors.joining());
                output.clear();
                processInput(s);
            }
        }

        private void processInput(String s) {
            if (s.startsWith("==")) {
                if (mode == Mode.COLLECT || mode == Mode.GOTO_SECURITY) {
                    String name = s.replaceAll("==", "").trim();
                    if (currentRoom.name != null && !name.equals(currentRoom.name)) {
                        throw new RuntimeException("Current room does not match " + currentRoom.name + "/" + name);
                    }
                    currentRoom.name = name;
                    directions.clear();
                    items.clear();
                }
            }
            if (s.startsWith("- ")) {
                String name = s.substring(2);
                Direction direction = Direction.determineDirection(name);
                if (direction != null) {
                    directions.add(direction);
                } else {
                    items.add(name);
                }
            }
        }

        private static List<Integer> generateGrayCodes(int count) {
            List<Integer> current = new ArrayList<>(List.of(0));
            for (int i = 0; i < count; i++) {
                List<Integer> next = new ArrayList<>(current);
                int highBit = 1 << i;
                for (int j = current.size() - 1; j >= 0; j--) {
                    next.add(highBit | j);
                }
                current = next;
            }
            return current;
        }
    }

    static class Room {
        String name;
        Map<Direction, Room> links = new EnumMap<>(Direction.class);
    }

    enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST;

        private static final Map<Direction, Direction> INVERSE_MAP = Map.of(NORTH, SOUTH, SOUTH, NORTH, WEST, EAST, EAST, WEST);

        static Direction determineDirection(String s) {
            s = s.toUpperCase();
            for (Direction direction : Direction.values()) {
                if (s.equals(direction.name())) {
                    return direction;
                }
            }
            return null;
        }

        Direction inverse() {
            return INVERSE_MAP.get(this);
        }
    }

    enum Mode {
        COLLECT,
        GOTO_SECURITY,
        PASS_SECURITY
    }
}

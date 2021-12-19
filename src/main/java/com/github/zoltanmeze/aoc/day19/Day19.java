package com.github.zoltanmeze.aoc.day19;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Day19 implements Runnable {

    public static void main(String[] args) {
        new Day19().run();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    static final List<Function<Coordinate, Coordinate>> ROTATIONS = List.of(

        // Bring each sides to the front by rotating Y or X (6 possible states)
        // For each front take all rotations of Z (4 possible states)
        //
        //       +--------+         ROTATE_X -> (x,-z,y)
        //      /        / |        ROTATE_Y -> (z,y,-x)
        //     +--------+  |        ROTATE_Z -> (-y,x,z)
        //     |        |  +
        //   y |        | /
        //     |        |/ -z
        //     +--------+
        //         x

        // A
        c -> Coordinate.of(c.x, c.y, c.z),
        c -> Coordinate.of(-c.y, c.x, c.z),
        c -> Coordinate.of(-c.x, -c.y, c.z),
        c -> Coordinate.of(c.y, -c.x, c.z),

        // B
        c -> Coordinate.of(c.z, c.y, -c.x),
        c -> Coordinate.of(-c.y, c.z, -c.x),
        c -> Coordinate.of(-c.z, -c.y, -c.x),
        c -> Coordinate.of(c.y, -c.z, -c.x),

        // C
        c -> Coordinate.of(-c.x, c.y, -c.z),
        c -> Coordinate.of(-c.y, -c.x, -c.z),
        c -> Coordinate.of(c.x, -c.y, -c.z),
        c -> Coordinate.of(c.y, c.x, -c.z),

        // D
        c -> Coordinate.of(-c.z, c.y, c.x),
        c -> Coordinate.of(-c.y, -c.z, c.x),
        c -> Coordinate.of(c.z, -c.y, c.x),
        c -> Coordinate.of(c.y, c.z, c.x),

        // E
        c -> Coordinate.of(c.x, -c.z, c.y),
        c -> Coordinate.of(c.z, c.x, c.y),
        c -> Coordinate.of(-c.x, c.z, c.y),
        c -> Coordinate.of(-c.z, -c.x, c.y),

        // F
        c -> Coordinate.of(c.x, c.z, -c.y),
        c -> Coordinate.of(-c.z, c.x, -c.y),
        c -> Coordinate.of(-c.x, -c.z, -c.y),
        c -> Coordinate.of(c.z, -c.x, -c.y)
    );

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Set<Coordinate>> input = parseInput();
        State[] states = compute(input);

        Set<Coordinate> coordinates = new HashSet<>();
        for (State state : states) {
            for (Coordinate coordinate : state.coordinates) {
                coordinates.add(coordinate.add(state.relative));
            }
        }
        return coordinates.size();
    }

    public Object partTwo() {
        List<Set<Coordinate>> input = parseInput();

        State[] states = compute(input);

        int max = 0;
        for (int i = 0; i < states.length - 1; i++) {
            for (int j = i + 1; j < states.length; j++) {
                int temp = states[i].relative.manhattan(states[j].relative);
                max = Math.max(max, temp);
            }
        }
        return max;
    }

    public State[] compute(List<Set<Coordinate>> input) {
        Map<Integer, List<Set<Coordinate>>> rotations = new HashMap<>();

        for (int i = 1; i < input.size(); i++) {
            rotations.put(i, rotate(input.get(i)));
        }

        State[] states = new State[input.size()];
        states[0] = State.of(Coordinate.of(0, 0, 0), input.get(0));

        return compute(rotations, states, 1) ? states : new State[0];
    }

    public boolean compute(Map<Integer, List<Set<Coordinate>>> rotations, State[] states, int depth) {
        if (depth == states.length) {
            return true;
        }
        for (int i = 0; i < states.length; i++) {
            if (states[i] != null) {
                continue;
            }
            for (int j = 0; j < rotations.get(i).size(); j++) {
                Set<Coordinate> current = rotations.get(i).get(j);
                for (int k = 0; k < states.length; k++) {
                    if (i == k || states[k] == null) {
                        continue;
                    }
                    // returns current coordinates relative to k coordinates if at least 12 is matching
                    for (Coordinate relative : relatives(current, states[k].coordinates)) {
                        // relative.add(states[k].relative -> relative to 0
                        states[i] = State.of(relative.add(states[k].relative), current);
                        if (compute(rotations, states, depth + 1)) {
                            return true;
                        }
                    }
                }
            }
            states[i] = null;
        }
        return false;
    }

    private Set<Coordinate> relatives(Set<Coordinate> first, Set<Coordinate> second) {
        Map<Coordinate, Integer> common = new HashMap<>();
        for (Coordinate c1 : first) {
            for (Coordinate c2 : second) {
                common.compute(c2.copy().subtract(c1), (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        return common.entrySet()
            .stream()
            .filter(entry -> entry.getValue() >= 12)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private List<Set<Coordinate>> rotate(Set<Coordinate> coordinates) {
        List<Set<Coordinate>> results = new ArrayList<>();
        for (Function<Coordinate, Coordinate> rotation : ROTATIONS) {
            Set<Coordinate> subResults = new HashSet<>();
            for (Coordinate coordinate : coordinates) {
                subResults.add(rotation.apply(coordinate));
            }
            results.add(subResults);
        }
        return results;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class State {
        Coordinate relative;
        Set<Coordinate> coordinates;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    static class Coordinate implements Comparable<Coordinate> {

        int x;
        int y;
        int z;

        public Coordinate add(Coordinate other) {
            x += other.x;
            y += other.y;
            z += other.z;
            return this;
        }

        public Coordinate subtract(Coordinate other) {
            x -= other.x;
            y -= other.y;
            z -= other.z;
            return this;
        }

        public int manhattan(Coordinate other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z);
        }

        public Coordinate copy() {
            return new Coordinate(x, y, z);
        }

        @Override
        public int compareTo(Coordinate o) {
            int value = Integer.compare(this.x, o.x);
            if (value == 0) {
                value = Integer.compare(this.y, o.y);
            }
            if (value == 0) {
                value = Integer.compare(this.z, o.z);
            }
            return value;
        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }

    @SneakyThrows
    public List<Set<Coordinate>> parseInput() {
        File file = ResourceUtils.getResourceFile("day19.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Set<Coordinate>> all = new ArrayList<>();
            scanner.useDelimiter("\n\n");
            while (scanner.hasNext()) {
                Set<Coordinate> coordinates = new HashSet<>();
                try (Scanner scanner1 = new Scanner(scanner.next())) {
                    if (scanner1.hasNextLine()) {
                        scanner1.nextLine();
                    }
                    while (scanner1.hasNextLine()) {
                        String[] line = scanner1.nextLine().split(",");
                        coordinates.add(Coordinate.of(Integer.parseInt(line[0]), Integer.parseInt(line[1]), Integer.parseInt(line[2])));
                    }
                }
                all.add(coordinates);
            }
            return all;
        }
    }
}

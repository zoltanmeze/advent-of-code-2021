package com.github.zoltanmeze.aoc.day22;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

@Slf4j
public class Day22 implements Runnable {

    public static void main(String[] args) {
        new Day22().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Cube> input = parseInput();

        long time = System.currentTimeMillis();

        Cube limit = Cube.of(
            Coordinate.of(-50, -50, -50),
            Coordinate.of(50, 50, 50)
        );

        List<Cube> cubes = new ArrayList<>();
        for (Cube cube : input) {
            if (!cube.overlap(limit)) {
                continue;
            }
            cubes.add(cube.intersect(limit));
        }

        List<Cube> results = compute(cubes);
        long sum = 0;
        for (Cube cube : results) {
            sum += cube.volume();
        }

        System.out.println("TIME: " + (System.currentTimeMillis() - time) + "ms");

        return sum;
    }

    public Object partTwo() {
        List<Cube> input = parseInput();

        long time = System.currentTimeMillis();

        List<Cube> results = compute(input);

        long sum = 0;
        for (Cube cube : results) {
            sum += cube.volume();
        }
        System.out.println("TIME: " + (System.currentTimeMillis() - time) + "ms");

        return sum;
    }

    private List<Cube> compute(List<Cube> cubes) {
        Stack<State> stack = new Stack<>();
        for (int i = cubes.size() - 1; i >= 0; i--) {
            stack.add(State.of(cubes.get(i), 0));
        }
        cubes = new ArrayList<>(stack.size());
        while (!stack.isEmpty()) {
            State state = stack.pop();
            boolean overlap = false;
            // Continue from index, no need to check everything from the beginning
            for (int i = state.index; i < cubes.size(); i++) {
                Cube prev = cubes.get(i);
                if (state.cube.overlap(prev)) {
                    overlap = true;
                    for (Cube next : state.cube.subtract(prev)) {
                        stack.add(State.of(next, i));
                    }
                    if (!state.cube.on) {
                        cubes.remove(i);
                        cubes.addAll(prev.subtract(state.cube));
                    }
                    break;
                }
            }
            if (!overlap && state.cube.on) {
                cubes.add(state.cube);
            }
        }
        return cubes;
    }

    @Data(staticConstructor = "of")
    static class State {
        final Cube cube;
        final int index;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @RequiredArgsConstructor(staticName = "of")
    static class Cube {

        final Coordinate start;
        final Coordinate end;
        boolean on;

        public boolean overlap(Cube other) {
            return ((start.x <= other.start.x && other.start.x <= end.x) || (other.start.x <= start.x && start.x <= other.end.x)) &&
                ((start.y <= other.start.y && other.start.y <= end.y) || (other.start.y <= start.y && start.y <= other.end.y)) &&
                ((start.z <= other.start.z && other.start.z <= end.z) || (other.start.z <= start.z && start.z <= other.end.z));
        }

        public long volume() {
            return (abs(end.x - start.x) + 1L) * (abs(end.y - start.y) + 1L) * (abs(end.z - start.z) + 1L);
        }

        public List<Cube> subtract(Cube other) {
            List<Cube> cubes = new LinkedList<>();

            int maxZ = max(start.z, other.start.z);
            int minZ = min(end.z, other.end.z);

            // FRONT
            if (other.start.z > start.z) {
                cubes.add(Cube.of(
                    Coordinate.of(start.x, start.y, start.z),
                    Coordinate.of(end.x, end.y, other.start.z - 1),
                    on
                ));
            }
            // BACK
            if (other.end.z < end.z) {
                cubes.add(Cube.of(
                    Coordinate.of(start.x, start.y, other.end.z + 1),
                    Coordinate.of(end.x, end.y, end.z),
                    on
                ));
            }
            // LEFT
            if (other.start.x > start.x) {
                cubes.add(Cube.of(
                    Coordinate.of(start.x, start.y, maxZ),
                    Coordinate.of(other.start.x - 1, end.y, minZ),
                    on
                ));
            }
            // RIGHT
            if (other.end.x < end.x) {
                cubes.add(Cube.of(
                    Coordinate.of(other.end.x + 1, start.y, maxZ),
                    Coordinate.of(end.x, end.y, minZ),
                    on
                ));
            }
            // BOTTOM
            if (other.start.y > start.y) {
                cubes.add(Cube.of(
                    Coordinate.of(max(start.x, other.start.x), start.y, maxZ),
                    Coordinate.of(min(end.x, other.end.x), other.start.y - 1, minZ),
                    on
                ));
            }
            // TOP
            if (other.end.y < end.y) {
                cubes.add(Cube.of(
                    Coordinate.of(max(start.x, other.start.x), other.end.y + 1, maxZ),
                    Coordinate.of(min(end.x, other.end.x), end.y, minZ),
                    on
                ));
            }
            return cubes;
        }

        public Cube intersect(Cube limit) {
            return Cube.of(
                Coordinate.of(max(start.x, limit.start.x), max(start.y, limit.start.y), max(start.z, limit.start.z)),
                Coordinate.of(min(end.x, limit.end.x), min(end.y, limit.end.y), min(end.z, limit.end.z)),
                on);
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    static class Coordinate {

        int x;
        int y;
        int z;

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }
    }

    @SneakyThrows
    public List<Cube> parseInput() {
        File file = ResourceUtils.getResourceFile("day22.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Cube> results = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\.\\.|[\\s,]?[x-z]=");

                boolean on = "on".equals(line[0]);

                Coordinate start = Coordinate.of(Integer.parseInt(line[1]), Integer.parseInt(line[3]), Integer.parseInt(line[5]));
                Coordinate end = Coordinate.of(Integer.parseInt(line[2]), Integer.parseInt(line[4]), Integer.parseInt(line[6]));

                results.add(Cube.of(start, end, on));
            }
            return results;
        }
    }
}

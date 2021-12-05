package com.github.zoltanmeze.aoc.day05;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Day05 implements Runnable {

    public static void main(String[] args) {
        new Day05().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Pair<Coordinate, Coordinate>> input = parseInput();
        Map<Coordinate, Integer> map = new HashMap<>();

        for (Pair<Coordinate, Coordinate> coordinatePair : input) {
            Coordinate c1 = coordinatePair.left;
            Coordinate c2 = coordinatePair.right;
            if (c1.x == c2.x) {
                int max = Math.max(c1.y, c2.y);
                for (int i = Math.min(c1.y, c2.y); i <= max; i++) {
                    map.compute(Coordinate.of(c1.x, i), (coordinate, value) -> value == null ? 1 : ++value);
                }
            } else if (c1.y == c2.y) {
                int max = Math.max(c1.x, c2.x);
                for (int i = Math.min(c1.x, c2.x); i <= max; i++) {
                    map.compute(Coordinate.of(i, c1.y), (coordinate, value) -> value == null ? 1 : ++value);
                }
            }
        }
        return map.values()
            .stream()
            .filter(x -> x > 1)
            .count();
    }

    public Object partTwo() {
        List<Pair<Coordinate, Coordinate>> input = parseInput();
        Map<Coordinate, Integer> map = new HashMap<>();

        for (Pair<Coordinate, Coordinate> coordinatePair : input) {
            Coordinate c1 = coordinatePair.left;
            Coordinate c2 = coordinatePair.right;
            if (c1.x == c2.x) {
                int max = Math.max(c1.y, c2.y);
                for (int i = Math.min(c1.y, c2.y); i <= max; i++) {
                    map.compute(Coordinate.of(c1.x, i), (coordinate, value) -> value == null ? 1 : ++value);
                }
            } else if (c1.y == c2.y) {
                int max = Math.max(c1.x, c2.x);
                for (int i = Math.min(c1.x, c2.x); i <= max; i++) {
                    map.compute(Coordinate.of(i, c1.y), (coordinate, value) -> value == null ? 1 : ++value);
                }
            } else {
                int abs = Math.abs(c1.x - c2.x);
                if (abs == Math.abs(c1.y - c2.y)) {
                    for (int i = 0; i <= abs; i++) {
                        int x = c1.x + (c1.x < c2.x ? i : -i);
                        int y = c1.y + (c1.y < c2.y ? i : -i);
                        map.compute(Coordinate.of(x, y), (coordinate, value) -> value == null ? 1 : ++value);
                    }
                }
            }
        }
        return map.values()
            .stream()
            .filter(x -> x > 1)
            .count();
    }

    @SneakyThrows
    public List<Pair<Coordinate, Coordinate>> parseInput() {
        File file = ResourceUtils.getResourceFile("day05.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Pair<Coordinate, Coordinate>> results = new ArrayList<>();
            scanner.useDelimiter("\n");
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] parts = line.split("(,| -> )");

                Coordinate c1 = Coordinate.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                Coordinate c2 = Coordinate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

                results.add(Pair.of(c1, c2));
            }
            return results;
        }
    }

    @Data(staticConstructor = "of")
    public static class Coordinate {
        final int x;
        final int y;
    }

    @Data(staticConstructor = "of")
    public static class Pair<L, R> {
        final L left;
        final R right;
    }
}

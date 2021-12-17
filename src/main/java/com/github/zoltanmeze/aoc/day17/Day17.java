package com.github.zoltanmeze.aoc.day17;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

@Slf4j
public class Day17 implements Runnable {

    public static void main(String[] args) {
        new Day17().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Area input = parseInput();
        return compute(input).maxY;
    }

    public Object partTwo() {
        Area input = parseInput();
        return compute(input).count;
    }

    private Result compute(Area target) {
        Result result = new Result(Integer.MIN_VALUE, 0);
        for (int i = 0; i <= target.end.x; i++) {
            for (int j = target.start.y; j <= Math.abs(target.start.y); j++) {
                Integer highest;
                if ((highest = canReachTarget(target, Coordinate.of(i, j))) != null) {
                    result.maxY = Math.max(result.maxY, highest);
                    result.count++;
                }
            }
        }
        return result;
    }

    private Integer canReachTarget(Area target, Coordinate velocity) {
        Coordinate current = Coordinate.of(0, 0);

        int maxY = Integer.MIN_VALUE;

        while (current.x <= target.end.x && current.y > target.start.y) {
            current.add(velocity);

            maxY = Math.max(maxY, current.y);

            if (current.inBounds(target.start, target.end)) {
                return maxY;
            }
            int incVelocityX = 0;

            if (velocity.x > 0) {
                incVelocityX = -1;
            } else if (velocity.x < 0) {
                incVelocityX = 1;
            } else if (current.x < target.start.x || current.x > target.end.x) {
                // 299282 / 519029 can be terminated early here
                return null;
            }
            velocity.add(incVelocityX, -1);
        }
        return null;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class Coordinate {
        int x;
        int y;

        public void add(Coordinate other) {
            add(other.x, other.y);
        }

        public void add(int x, int y) {
            this.x += x;
            this.y += y;
        }

        public boolean inBounds(Coordinate start, Coordinate end) {
            return x >= start.x && y >= start.y && x <= end.x && y <= end.y;
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class Result {
        int maxY;
        int count;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class Area {
        Coordinate start;
        Coordinate end;
    }

    @SneakyThrows
    public Area parseInput() {
        File file = ResourceUtils.getResourceFile("day17.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            if (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("=|\\.\\.|,\\s+");

                Coordinate c1 = Coordinate.of(Integer.parseInt(line[1]), Integer.parseInt(line[4]));
                Coordinate c2 = Coordinate.of(Integer.parseInt(line[2]), Integer.parseInt(line[5]));

                return Area.of(c1, c2);
            }
            throw new RuntimeException("meh");
        }
    }
}

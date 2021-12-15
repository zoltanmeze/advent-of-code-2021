package com.github.zoltanmeze.aoc.day15;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

@Slf4j
public class Day15 implements Runnable {

    public static void main(String[] args) {
        new Day15().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        int[][] arr = parseInput();
        return calculate(arr, Coordinate.of(0, 0), Coordinate.of(arr.length - 1, arr[0].length - 1));
    }

    public Object partTwo() {
        int[][] arr = parseInput();
        int[][] newArr = new int[arr.length * 5][arr[0].length * 5];

        int x = 0, y = 0;
        for (int cx = 0; cx < 5; cx++) {
            for (int cy = 0; cy < 5; cy++) {
                for (int i = 0; i < arr.length; i++) {
                    for (int j = 0; j < arr[i].length; j++) {
                        newArr[i + x][j + y] = ((arr[i][j] - 1 + cy + cx) % 9) + 1;
                    }
                }
                y += arr[0].length;
                y %= newArr[0].length;
            }
            x += arr.length;
            x %= newArr.length;
        }
        return calculate(newArr, Coordinate.of(0, 0), Coordinate.of(newArr.length - 1, newArr[0].length - 1));
    }

    private long calculate(int[][] arr, Coordinate start, Coordinate end) {
        Long[][] costs = new Long[arr.length][arr[0].length];

        Coordinate[] nextDirections = new Coordinate[] {
            Coordinate.of(1, 0),
            Coordinate.of(-1, 0),
            Coordinate.of(0, -1),
            Coordinate.of(0, 1)
        };

        Queue<State> queue = new PriorityQueue<>();
        queue.add(State.of(start, 0));

        State state = null;
        while (!queue.isEmpty() && !(state = queue.poll()).coordinate.equals(end)) {
            Long cost = costs[state.coordinate.x][state.coordinate.y];
            if (cost != null && state.cost > cost) {
                // Won't change anything, skip over
                continue;
            }
            for (Coordinate nextDirection : nextDirections) {
                Coordinate next = state.coordinate.add(nextDirection);
                if (!next.inBounds(start, end)) {
                    continue;
                }
                long nextCost = state.cost + arr[next.x][next.y];
                cost = costs[next.x][next.y];
                if (cost == null || nextCost < cost) {
                    queue.offer(State.of(next, nextCost));
                    costs[next.x][next.y] = nextCost;
                }
            }
        }
        if (state == null) {
            throw new RuntimeException("Scheisse, end never reached");
        }
        return state.cost;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class State implements Comparable<State> {
        Coordinate coordinate;
        long cost;

        @Override
        public int compareTo(State o) {
            return Long.compare(cost, o.cost);
        }
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class Coordinate {
        int x;
        int y;

        public Coordinate add(Coordinate other) {
            return Coordinate.of(x + other.x, y + other.y);
        }

        public boolean inBounds(Coordinate start, Coordinate end) {
            return x >= start.x && y >= start.y && x <= end.x && y <= end.y;
        }
    }

    @SneakyThrows
    public int[][] parseInput() {
        File file = ResourceUtils.getResourceFile("day15.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<int[]> results = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                int[] arr = line.chars()
                    .map(x -> x - '0')
                    .toArray();

                results.add(arr);
            }
            return results.toArray(new int[0][]);
        }
    }
}

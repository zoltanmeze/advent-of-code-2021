package com.github.zoltanmeze.aoc.day11;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day11 implements Runnable {

    public static void main(String[] args) {
        new Day11().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        int[][] input = parseInput();
        int sum = 0;
        for (int d = 0; d < 100; d++) {
            boolean[][] flashed = new boolean[input.length][input[0].length];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    sum += flash(input, flashed, i, j);
                }
            }
        }
        return sum;
    }

    private int flash(int[][] input, boolean[][] flashed, int x, int y) {
        if (x < 0 || y < 0 || x >= input.length || y >= input[x].length || flashed[x][y]) {
            return 0;
        }
        int sum = 0;
        if (++input[x][y] > 9) {
            input[x][y] = 0;
            flashed[x][y] = true;
            sum += 1
                + flash(input, flashed, x - 1, y)
                + flash(input, flashed, x, y - 1)
                + flash(input, flashed, x + 1, y)
                + flash(input, flashed, x, y + 1)
                + flash(input, flashed, x - 1, y - 1)
                + flash(input, flashed, x - 1, y + 1)
                + flash(input, flashed, x + 1, y - 1)
                + flash(input, flashed, x + 1, y + 1);
        }
        return sum;
    }

    public Object partTwo() {
        int[][] input = parseInput();
        int day = 0, sum, goal = input.length * input[0].length;
        do {
            sum = 0;
            day += 1;
            boolean[][] flashed = new boolean[input.length][input[0].length];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    sum += flash(input, flashed, i, j);
                }
            }
        } while (sum != goal); // to infinite and beyond
        return day;
    }

    @SneakyThrows
    public int[][] parseInput() {
        File file = ResourceUtils.getResourceFile("day11.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<int[]> results = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                int[] arr = line.chars()
                    .map(x -> x - 48)
                    .toArray();
                results.add(arr);
            }
            return results.toArray(new int[0][]);
        }
    }
}

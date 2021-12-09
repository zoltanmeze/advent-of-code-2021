package com.github.zoltanmeze.aoc.day09;

import com.github.zoltanmeze.aoc.day05.Day05.Pair;
import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day09 implements Runnable {

    public static void main(String[] args) {
        new Day09().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        int[][] arr = parseInput();
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int num = arr[i][j];
                if ((i < arr.length - 1 && num >= arr[i + 1][j]) ||
                    (j < arr[i].length - 1 && num >= arr[i][j + 1]) ||
                    (i > 0 && num >= arr[i - 1][j]) ||
                    (j > 0 && num >= arr[i][j - 1])) {
                    continue;
                }
                sum += 1 + arr[i][j];
            }
        }
        return sum;
    }

    public Object partTwo() {
        int[][] arr = parseInput();
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                int num = arr[i][j];
                if ((i < arr.length - 1 && num >= arr[i + 1][j]) ||
                    (j < arr[i].length - 1 && num >= arr[i][j + 1]) ||
                    (i > 0 && num >= arr[i - 1][j]) ||
                    (j > 0 && num >= arr[i][j - 1])) {
                    continue;
                }
                pairs.add(Pair.of(i, j));
            }
        }
        List<Integer> results = new ArrayList<>();
        for (Pair<Integer, Integer> pair : pairs) {
            boolean[][] visited = new boolean[arr.length][arr[0].length];
            int result = visit(arr, pair.getLeft(), pair.getRight(), visited);
            results.add(result);
        }
        return results.stream()
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .reduce(1, (x, y) -> x * y);
    }

    private int visit(int[][] arr, int i, int j, boolean[][] visited) {
        int num = arr[i][j];
        if (num == 9 || visited[i][j]) {
            return 0;
        }
        int sum = 1;
        visited[i][j] = true;
        if (i - 1 >= 0) {
            sum += visit(arr, i - 1, j, visited);
        }
        if (j - 1 >= 0) {
            sum += visit(arr, i, j - 1, visited);
        }
        if (i + 1 < arr.length) {
            sum += visit(arr, i + 1, j, visited);
        }
        if (j + 1 < arr[0].length) {
            sum += visit(arr, i, j + 1, visited);
        }
        return sum;
    }

    @SneakyThrows
    public int[][] parseInput() {
        File file = ResourceUtils.getResourceFile("day09.txt");
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

package com.github.zoltanmeze.aoc.day07;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class Day07 implements Runnable {

    public static void main(String[] args) {
        new Day07().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Integer> input = parseInput()
            .stream()
            .sorted()
            .collect(Collectors.toList());

        int mid = input.get(input.size() / 2);

        return input.stream()
            .mapToInt(x -> x - mid)
            .map(Math::abs)
            .sum();
    }

    public Object partTwo() {
        List<Integer> input = parseInput()
            .stream()
            .sorted()
            .collect(Collectors.toList());

        System.out.println("SUM: " + input.stream().mapToInt(Integer::intValue).sum());
        System.out.println("SIZE: " + input.size());

        //System.out.println(Math.round(44 / 10f));

        int avg = Math.round(input.stream().mapToInt(Integer::intValue).sum() / (float) input.size());

        System.out.println(avg);

        // 5  (1 + 2 + 3 + 4 + 5) 5 * 3
        // 10 (1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10) 55

        return input.stream()
            .mapToInt(x -> sumFromOneToN(Math.abs(x - avg)))
            .sum();
    }

    private int sumFromOneToN(int t) {
        // sum of integers from a to l: n(a + l)/2
        return t * (t + 1) / 2;
    }

    @SneakyThrows
    public List<Integer> parseInput() {
        File file = ResourceUtils.getResourceFile("day07.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter(",");
            List<Integer> results = new ArrayList<>();
            while (scanner.hasNext()) {
                String num = scanner.next().trim();
                results.add(Integer.parseInt(num));
            }
            return results;
        }
    }
}

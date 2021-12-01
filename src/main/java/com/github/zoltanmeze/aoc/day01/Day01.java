package com.github.zoltanmeze.aoc.day01;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day01 implements Runnable {

    public static void main(String[] args) {
        new Day01().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Integer> input = parseInput();
        int count = 0;
        int prev = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            if (input.get(i) > prev) {
                count++;
            }
            prev = input.get(i);
        }
        return count;
    }


    public Object partTwo() {
        List<Integer> input = parseInput();
        int count = 0;
        int prev = input.get(0) + input.get(1) + input.get(2);
        for (int i = 1; i < input.size() - 2; i++) {
            int next = prev - input.get(i - 1) + input.get(i + 2);
            if (next > prev) {
                count++;
            }
            prev = next;
        }
        return count;
    }

    @SneakyThrows
    public List<Integer> parseInput() {
        File file = ResourceUtils.getResourceFile("day01.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Integer> results = new ArrayList<>();
            while (scanner.hasNext()) {
                results.add(Integer.valueOf(scanner.nextLine()));
            }
            return results;
        }
    }
}

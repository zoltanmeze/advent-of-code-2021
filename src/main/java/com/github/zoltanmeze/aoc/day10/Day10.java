package com.github.zoltanmeze.aoc.day10;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

@Slf4j
public class Day10 implements Runnable {

    public static void main(String[] args) {
        new Day10().run();
    }

    Map<Character, Character> pairs = Map.of(
        '<', '>',
        '(', ')',
        '[', ']',
        '{', '}'
    );

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<String> input = parseInput();
        long results = 0;
        Map<Character, Integer> values = Map.of(
            '>', 25137,
            ')', 3,
            ']', 57,
            '}', 1197
        );
        for (String str : input) {
            Stack<Character> stack = new Stack<>();
            for (char ch : str.toCharArray()) {
                if (pairs.containsKey(ch)) {
                    stack.push(ch);
                    continue;
                }
                char schar = stack.pop();
                if (pairs.get(schar) != ch) {
                    results += values.get(ch);
                }
            }
        }
        return results;
    }

    public Object partTwo() {
        List<String> input = parseInput();
        List<Long> result = new ArrayList<>();

        Map<Character, Integer> values = Map.of(
            '<', 4,
            '(', 1,
            '[', 2,
            '{', 3
        );

        for (String str : input) {
            Stack<Character> stack = new Stack<>();
            for (char ch : str.toCharArray()) {
                if (pairs.containsKey(ch)) {
                    stack.push(ch);
                    continue;
                }
                char schar = stack.pop();
                if (pairs.get(schar) != ch) {
                    stack.clear();
                    break;
                }
            }
            // Skip corrupted lines
            if (stack.isEmpty()) {
                continue;
            }
            long results = 0;
            while (!stack.isEmpty()) {
                char ch = stack.pop();
                results *= 5;
                results += values.get(ch);
            }
            result.add(results);
        }
        result = result.stream()
            .sorted()
            .collect(Collectors.toList());
        return result.get(result.size() / 2);
    }

    @SneakyThrows
    public List<String> parseInput() {
        File file = ResourceUtils.getResourceFile("day10.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<String> results = new ArrayList<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                results.add(line);
            }
            return results;
        }
    }
}

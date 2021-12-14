package com.github.zoltanmeze.aoc.day14;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Day14 implements Runnable {

    public static void main(String[] args) {
        new Day14().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Input input = parseInput();
        return generate(input.getTemplate(), input.getRules(), 10);
    }

    public Object partTwo() {
        Input input = parseInput();
        return generate(input.getTemplate(), input.getRules(), 40);
    }

    private long generate(String template, Map<String, Character> rules, int iteration) {
        Map<Character, Long> common = new HashMap<>();

        char[] temp = template.toCharArray();
        Map<String, Long> chars = new HashMap<>();
        for (int j = 0; j < temp.length - 1; j++) {
            chars.compute(temp[j] + "" + temp[j + 1], (k, v) -> (v == null) ? 1L : v + 1);
        }
        for (int i = 0; i < iteration; i++) {
            Map<String, Long> charsCopy = new HashMap<>();
            chars.forEach((k, v) -> {
                Character ch = rules.get(k);

                String str1 = k.charAt(0) + "" + ch;
                String str2 = ch + "" + k.charAt(1);

                charsCopy.compute(str1, (k1, v1) -> v1 == null ? v : v1 + v); // Add 1st string +v times
                charsCopy.compute(str2, (k1, v1) -> v1 == null ? v : v1 + v); // Add 2nd string +v times
            });
            chars = new HashMap<>(charsCopy);
        }
        chars.forEach((k, v) -> {
            // Count only first character, second character will appear as first in other iteration
            common.compute(k.charAt(0), (k1, v1) -> v1 == null ? v : v + v1);
        });
        // Last character from template is missing, since we count only 1st characters in previous loop
        // Other option is to count both 1st and 2nd and divide end result by 2
        common.compute(temp[temp.length - 1], (k1, v1) -> v1 == null ? 1L : v1 + 1);

        LongSummaryStatistics summaryStatistics = common.values().stream()
            .mapToLong(Long::longValue)
            .summaryStatistics();

        return summaryStatistics.getMax() - summaryStatistics.getMin();
    }

    @Data
    static class Input {
        String template;
        Map<String, Character> rules;
    }

    @SneakyThrows
    public Input parseInput() {
        File file = ResourceUtils.getResourceFile("day14.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter("\n\n");
            Input input = new Input();
            if (scanner.hasNext()) {
                String line = scanner.next();
                input.setTemplate(line);
            }
            Map<String, Character> map = new HashMap<>();
            if (scanner.hasNext()) {
                try (Scanner scanner1 = new Scanner(scanner.next())) {
                    while (scanner1.hasNextLine()) {
                        String[] line = scanner1.nextLine().split(" -> ");
                        if (map.containsKey(line[0])) {
                            throw new RuntimeException("Contains");
                        }
                        map.put(line[0], line[1].charAt(0));
                    }
                }
            }
            input.setRules(map);
            return input;
        }
    }
}

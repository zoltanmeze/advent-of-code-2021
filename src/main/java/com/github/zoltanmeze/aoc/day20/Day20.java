package com.github.zoltanmeze.aoc.day20;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class Day20 implements Runnable {

    public static void main(String[] args) {
        new Day20().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Input input = parseInput();

        long minX = 0, minY = 0;
        long maxX = input.getImage().get(0L).size() - 1, maxY = input.getImage().size() - 1;

        int step = 3;

        int[] ends = {0, input.getAlgorithm().get(0)};

        Map<Long, Map<Long, Integer>> results = input.image;
        for (int i = 0; i < 2; i++) {
            minX -= step;
            minY -= step;
            maxX += step;
            maxY += step;

            results = compute(results, input.algorithm, minX, maxX, minY, maxY, ends[i % 2], ends[(i + 1) % 2]);
        }
        return results.values()
            .stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .mapToLong(Integer::longValue)
            .sum();
    }

    private Map<Long, Map<Long, Integer>> compute(Map<Long, Map<Long, Integer>> image,
                                                  List<Integer> algorithm,
                                                  long minX, long maxX,
                                                  long minY, long maxY,
                                                  int prevInfinity, int nextInfity) {

        Map<Long, Map<Long, Integer>> results = new HashMap<>();
        for (long j = minY; j <= maxY; j++) {
            for (long i = minX; i <= maxX; i++) {
                Integer nine = getNine(image, i, j, prevInfinity);
                int value = nine == null ? nextInfity : algorithm.get(nine);
                setPixel(results, i, j, value);
            }
        }
        return results;
    }

    private Integer getNine(Map<Long, Map<Long, Integer>> image, long x, long y, int prevInfinity) {
        int c = 0;
        int res = 0;
        int nulls = 0;
        for (long j = y + 1; j >= y - 1; j--) {
            for (long i = x + 1; i >= x - 1; i--) {
                Integer pixel = getPixel(image, i, j);
                if (pixel == null) {
                    nulls += 1;
                    pixel = prevInfinity;
                }
                res += pixel * Math.pow(2, c);
                c++;
            }
        }
        if (nulls == 9) {
            return null;
        }
        return res;
    }

    private Integer getPixel(Map<Long, Map<Long, Integer>> image, long x, long y) {
        return image.getOrDefault(y, Collections.emptyMap()).getOrDefault(x, null);
    }

    private void setPixel(Map<Long, Map<Long, Integer>> image, long x, long y, int value) {
        image.compute(y, (k1, v1) -> {
            Map<Long, Integer> map = v1;
            if (v1 == null) {
                map = new HashMap<>();
            }
            map.put(x, value);
            return map;
        });
    }

    public Object partTwo() {
        Input input = parseInput();

        long minX = 0, minY = 0;
        long maxX = input.getImage().get(0L).size() - 1, maxY = input.getImage().size() - 1;

        int step = 3;

        int[] ends = {0, input.getAlgorithm().get(0)};

        Map<Long, Map<Long, Integer>> results = input.image;
        for (int i = 0; i < 50; i++) {
            minX -= step;
            minY -= step;
            maxX += step;
            maxY += step;

            results = compute(results, input.algorithm, minX, maxX, minY, maxY, ends[i % 2], ends[(i + 1) % 2]); // 2 -> 0, 4 -> 255
        }

        return results.values()
            .stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .mapToLong(Integer::longValue)
            .sum();
    }

    @Data
    static class Input {
        List<Integer> algorithm;

        Map<Long, Map<Long, Integer>> image;
    }

    @SneakyThrows
    public Input parseInput() {
        File file = ResourceUtils.getResourceFile("day20.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter("\n\n");
            Input input = new Input();
            if (scanner.hasNext()) {
                input.setAlgorithm(
                    scanner.next()
                        .chars()
                        .mapToObj(ch -> {
                            if (ch == '#') {
                                return 1;
                            }
                            return 0;
                        }).collect(Collectors.toList())
                );
            }
            Map<Long, Map<Long, Integer>> map = new HashMap<>();
            long j = 0;
            try (Scanner scanner1 = new Scanner(scanner.next())) {
                while (scanner1.hasNextLine()) {
                    AtomicLong i = new AtomicLong(0);
                    map.put(j, scanner1.nextLine()
                        .chars()
                        .mapToObj(ch -> {
                            if (ch == '#') {
                                return 1;
                            }
                            return 0;
                        }).collect(Collectors.toMap(k -> i.getAndIncrement(), v -> v)));
                    j++;
                }
            }
            input.setImage(map);
            return input;
        }
    }
}

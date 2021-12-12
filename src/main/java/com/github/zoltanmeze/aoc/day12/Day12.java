package com.github.zoltanmeze.aoc.day12;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

@Slf4j
public class Day12 implements Runnable {

    static final String START = "start";
    static final String END = "end";

    public static void main(String[] args) {
        new Day12().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Map<String, Set<String>> input = parseInput();

        long nanos = 0;
        int t = 0;

        nanos = System.currentTimeMillis();
        t = visitStack(input, false);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        nanos = System.currentTimeMillis();
        t = visit(input, false);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        nanos = System.currentTimeMillis();
        t = visit2(input, false);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        return visit(START, input, new HashSet<>(), "");
    }

    public Object partTwo() {
        Map<String, Set<String>> input = parseInput();

        long nanos = 0;
        int t = 0;

        nanos = System.currentTimeMillis();
        t = visitStack(input, true);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        nanos = System.currentTimeMillis();
        t = visit(input, true);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        nanos = System.currentTimeMillis();
        t = visit2(input, true);
        System.out.println((System.currentTimeMillis() - nanos) + " - " + t);

        return t;
    }

    private int visit(Map<String, Set<String>> input, boolean visitTwice) {
        Set<String> visited = new HashSet<>();
        return visit(START, input, visited, visitTwice ? null : "");
    }

    private int visit(String current, Map<String, Set<String>> input, Set<String> visited, String twice) {
        boolean isSmall;
        if (END.equals(current)) {
            return 1;
        } else if ((isSmall = isSmallCave(current)) && visited.contains(current)) {
            if (twice != null || START.equals(current)) {
                return 0;
            }
            twice = current;
        }
        int sum = 0;
        visited.add(current);
        for (String curr : input.get(current)) {
            sum += visit(curr, input, visited, twice);
        }
        if (isSmall && !current.equals(twice)) {
            visited.remove(current);
        }
        return sum;
    }

    private int visit2(Map<String, Set<String>> input, boolean visitTwice) {
        Set<String> visited = new HashSet<>();
        visited.add(START);
        return visit2(START, input, visited, visitTwice ? null : "");
    }

    private int visit2(String current, Map<String, Set<String>> input, Set<String> visited, String twice) {
        int sum = 0;
        boolean isSmall;
        for (String next : input.get(current)) {
            String newTwice = twice;
            if (END.equals(next)) {
                sum += 1;
                continue;
            } else if ((isSmall = isSmallCave(next)) && visited.contains(next)) {
                if (twice != null || START.equals(next)) {
                    continue;
                }
                newTwice = next;
            }
            visited.add(next);
            sum += visit2(next, input, visited, newTwice);
            if (isSmall && !next.equals(newTwice)) {
                visited.remove(next);
            }
        }
        return sum;
    }

    @Data(staticConstructor = "of")
    static class Visit {
        final String current;
        final String twice;
        final Set<String> visited;
    }

    private int visitStack(Map<String, Set<String>> input, boolean visitTwice) {
        int sum = 0;
        Stack<Visit> stack = new Stack<>();
        Visit visit = Visit.of(START, visitTwice ? null : "", new HashSet<>());

        do {
            String current = visit.getCurrent();
            String twice = visit.getTwice();

            Set<String> visited = new HashSet<>(visit.getVisited());
            visited.add(current);

            for (String next : input.get(current)) {
                String newTwice = twice;
                if (END.equals(next)) {
                    sum += 1;
                    continue;
                } else if ((isSmallCave(next)) && visited.contains(next)) {
                    if (twice != null || START.equals(next)) {
                        continue;
                    }
                    newTwice = next;
                }
                stack.add(Visit.of(next, newTwice, visited));
            }
            if (stack.isEmpty()) {
                break;
            }
            visit = stack.pop();
        } while (true);
        return sum;
    }

    private static boolean isSmallCave(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public Map<String, Set<String>> parseInput() {
        File file = ResourceUtils.getResourceFile("day12.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            Map<String, Set<String>> results = new HashMap<>();
            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().split("-");
                results.compute(line[0], (k, v) -> {
                    if (v == null) {
                        v = new HashSet<>();
                    }
                    v.add(line[1]);
                    return v;
                });
                results.compute(line[1], (k, v) -> {
                    if (v == null) {
                        v = new HashSet<>();
                    }
                    v.add(line[0]);
                    return v;
                });
            }
            return results;
        }
    }
}

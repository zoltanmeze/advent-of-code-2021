package com.github.zoltanmeze.aoc.day08;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Day08 implements Runnable {

    public static void main(String[] args) {
        new Day08().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<SevenSegmentDisplay> input = parseInput();
        Set<Integer> lengths = Set.of(2, 3, 4, 7);
        int result = 0;
        for (SevenSegmentDisplay segments : input) {
            for (Set<Character> chars : segments.output) {
                if (lengths.contains(chars.size())) {
                    result++;
                }
            }
        }
        return result;
    }

    public Object partTwo() {
        /*
               aaaa
              b    c
              b    c
               dddd
              e    f
              e    f
               gggg
         */

        List<SevenSegmentDisplay> input = parseInput();
        long result = 0;

        for (SevenSegmentDisplay segments : input) {
            Map<Integer, List<Set<Character>>> inputPerLength = segments.input.stream()
                .collect(Collectors.groupingBy(Set::size));

            Map<Integer, Set<Character>> numbers = new HashMap<>();
            Map<Character, Set<Character>> mappings = new HashMap<>();

            numbers.put(1, inputPerLength.get(2).get(0)); // It shouldn't fail if the input is valid, maybe add check
            numbers.put(4, inputPerLength.get(4).get(0));
            numbers.put(7, inputPerLength.get(3).get(0));
            numbers.put(8, inputPerLength.get(7).get(0));

            // Initial mappings
            // 1 gives c and f
            calculateMappings(Set.of('c', 'f'), mappings, numbers.get(1), Collections.emptyList());
            // 7 - 1 gives a
            calculateMappings(Set.of('a'), mappings, numbers.get(7), List.of(numbers.get(1)));
            // 4 - 1 gives b and d
            calculateMappings(Set.of('b', 'd'), mappings, numbers.get(4), List.of(numbers.get(1)));
            // 8 - 4 - 7 gives e and g
            calculateMappings(Set.of('e', 'g'), mappings, numbers.get(8), List.of(numbers.get(7), numbers.get(4)));

            // Two groups left lenght 6 and length 5
            // Find 0, 6 and 9 using the initial values, all missing only one segment
            // 0: a b c e f g -> 8 - d -> b
            // 6: a b d e f g -> 8 - c -> f
            // 9: a b c d f g -> 8 - e -> g
            // 2: a c d e g
            // 3: a c d f g
            // 5: a b d f g
            List<Set<Character>> size6inputs = inputPerLength.get(6);
            Set<Character> all = numbers.get(8);
            numbers.put(0, calculate('b', 'd', mappings, all, size6inputs)); // find 0, pins b and d from mappings, it contains b but not d
            numbers.put(6, calculate('f', 'c', mappings, all, size6inputs)); // find 6, pins f and c from mappings, it contains f but not c
            numbers.put(9, calculate('g', 'e', mappings, all, size6inputs)); // find 9, pins b and d from mappings, it contains e but not g

            // Resolve the rest, numbers with 5 segments: 2, 3, 5
            // We have everything we need
            numbers.put(2, resolveNumberFromMappings(Set.of('a', 'c', 'd', 'e', 'g'), mappings));
            numbers.put(3, resolveNumberFromMappings(Set.of('a', 'c', 'd', 'f', 'g'), mappings));
            numbers.put(5, resolveNumberFromMappings(Set.of('a', 'b', 'd', 'f', 'g'), mappings));

            int sum = 0;
            for (Set<Character> out : segments.output) {
                sum *= 10;
                sum += toNumber(numbers, out);
            }
            System.out.println(sum);
            result += sum;
        }
        return result;
    }


    private int toNumber(Map<Integer, Set<Character>> numbers, Set<Character> chars) {
        return numbers.entrySet()
            .stream()
            .filter(x -> x.getValue().equals(chars))
            .mapToInt(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("go back to sleep"));
    }

    private Set<Character> resolveNumberFromMappings(Set<Character> chars, Map<Character, Set<Character>> mappings) {
        return chars.stream()
            .map(mappings::get)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    private Set<Character> calculate(char include, char exclude,
                                     Map<Character, Set<Character>> mappings, Set<Character> all, List<Set<Character>> input) {
        Set<Character> temp = new HashSet<>(all);
        temp.removeAll(mappings.get(exclude));
        temp = findInputThatContains(input, temp);
        // Remove contained characters from excluded
        Set<Character> newIncluded = new HashSet<>(mappings.get(include));
        newIncluded.retainAll(temp);
        mappings.replace(include, newIncluded);
        mappings.get(exclude).removeAll(temp);
        return temp;
    }

    public Set<Character> findInputThatContains(List<Set<Character>> input, Set<Character> toFind) {
        return input.stream()
            .filter(in -> in.containsAll(toFind))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Meh"));
    }

    private void calculateMappings(Set<Character> characters, Map<Character, Set<Character>> mappings, Set<Character> from, List<Set<Character>> excludeAll) {
        Set<Character> temp = new HashSet<>(from);
        for (Set<Character> exclude : excludeAll) {
            temp.removeAll(exclude);
        }
        for (Character ch : characters) {
            mappings.put(ch, temp);
        }
    }


    @SneakyThrows
    public List<SevenSegmentDisplay> parseInput() {
        File file = ResourceUtils.getResourceFile("day08.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<SevenSegmentDisplay> results = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\|");

                List<Set<Character>> inputs = new ArrayList<>();
                List<Set<Character>> outputs = new ArrayList<>();

                try (Scanner scanner1 = new Scanner(line[0])) {
                    scanner1.useDelimiter("\\s");
                    while (scanner1.hasNext()) {
                        inputs.add(scanner1.next().chars().mapToObj(x -> (char) x).collect(Collectors.toSet()));
                    }
                }
                try (Scanner scanner1 = new Scanner(line[1])) {
                    scanner1.useDelimiter("\\s");
                    while (scanner1.hasNext()) {
                        outputs.add(scanner1.next().chars().mapToObj(x -> (char) x).collect(Collectors.toSet()));
                    }
                }

                results.add(SevenSegmentDisplay.of(inputs, outputs));
            }
            return results;
        }
    }

    @Data(staticConstructor = "of")
    static class SevenSegmentDisplay {
        final List<Set<Character>> input;
        final List<Set<Character>> output;
    }
}

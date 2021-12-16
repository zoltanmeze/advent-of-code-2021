package com.github.zoltanmeze.aoc.day16;

import static java.util.Map.entry;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.LongStream;

@Slf4j
public class Day16 implements Runnable {

    public static void main(String[] args) {
        new Day16().run();
    }

    static Map<Character, String> HEX_TO_BIN = Map.ofEntries(
        entry('0', "0000"),
        entry('1', "0001"),
        entry('2', "0010"),
        entry('3', "0011"),
        entry('4', "0100"),
        entry('5', "0101"),
        entry('6', "0110"),
        entry('7', "0111"),
        entry('8', "1000"),
        entry('9', "1001"),
        entry('A', "1010"),
        entry('B', "1011"),
        entry('C', "1100"),
        entry('D', "1101"),
        entry('E', "1110"),
        entry('F', "1111"));

    static Map<Integer, Function<LongStream, Long>> AGGREGATE_OPS = Map.ofEntries(
        entry(0, (stream) -> stream.reduce(0, Math::addExact)),
        entry(1, (stream) -> stream.reduce(1, Math::multiplyExact)),
        entry(2, (stream) -> stream.reduce(0, Math::min)),
        entry(3, (stream) -> stream.reduce(0, Math::max))
    );

    static Map<Integer, BiFunction<Long, Long, Long>> BINARY_OPS = Map.ofEntries(
        entry(5, (x, y) -> x > y ? 1L : 0),
        entry(6, (x, y) -> x < y ? 1L : 0),
        entry(7, (x, y) -> Objects.equals(x, y) ? 1L : 0)
    );

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        int[] input = parseInput();
        return calculate(input).version;
    }

    public Object partTwo() {
        int[] input = parseInput();
        return calculate(input).value;
    }

    private State calculate(int[] bits) {
        int start = 0, end = bits.length;

        int version = (int) getNumberFromBinary(bits, start, start += 2);
        int typeId = (int) getNumberFromBinary(bits, start += 1, start += 2);
        log.info("Version: {} Type ID: {}", version, typeId);

        if (typeId == 4) {
            List<Integer> literalBits = new ArrayList<>();
            for (start += 1; start < end; start += 5) {
                for (int j = start + 1; j < start + 5; j++) {
                    literalBits.add(bits[j]);
                }
                if (bits[start] == 0) {
                    break;
                }
            }
            long literal = getNumberFromBinary(literalBits);
            log.info("Literal: {}", literal);
            return State.of(literal, version, start + 5);
        }
        int lengthTypeId = bits[start += 1] == 0 ? 15 : 11;
        log.info("Length Type ID: {}", lengthTypeId);

        long lengthSubPackets = getNumberFromBinary(bits, start += 1, start += lengthTypeId - 1);
        log.info("Sub-packet length: {}", lengthSubPackets);

        start += 1;

        List<Long> subPackageResults = new ArrayList<>();
        for (int c = 0; c < lengthSubPackets; c++) {
            State next = calculate(Arrays.copyOfRange(bits, start, end));
            start += next.start;
            version += next.version;
            if (lengthTypeId == 15) {
                c += next.start;
            }
            subPackageResults.add(next.value);
        }
        long result;
        if (typeId < 4) {
            LongStream stream = subPackageResults.stream()
                .mapToLong(Long::longValue);
            result = AGGREGATE_OPS.get(typeId).apply(stream);
        } else {
            long value1 = subPackageResults.get(0);
            long value2 = subPackageResults.get(1);
            result = BINARY_OPS.get(typeId).apply(value1, value2);
        }
        return State.of(result, version, start);
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class State {
        long value;
        int version;
        int start;
    }

    private long getNumberFromBinary(List<Integer> chars) {
        return getNumberFromBinary(chars.stream()
            .mapToInt(Integer::intValue).toArray(), 0, chars.size() - 1);
    }

    private long getNumberFromBinary(int[] chars, int start, int end) {
        long sum = 0;
        for (int i = end, c = 0; i >= start; i--, c++) {
            sum += chars[i] * Math.pow(2, c);
        }
        return sum;
    }

    @SneakyThrows
    public int[] parseInput() {
        File file = ResourceUtils.getResourceFile("day16.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            if (scanner.hasNextLine()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : scanner.nextLine().toCharArray()) {
                    stringBuilder.append(HEX_TO_BIN.get(c));
                }
                return stringBuilder.toString().chars()
                    .map(x -> x - '0')
                    .toArray();
            }
            throw new RuntimeException("pff");
        }
    }
}

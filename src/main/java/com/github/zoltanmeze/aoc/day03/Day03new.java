package com.github.zoltanmeze.aoc.day03;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day03new implements Runnable {

    public static void main(String[] args) {
        new Day03new().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    private Integer partOne() {
        List<int[]> input = parseInput();
        int[] sumBits = sumBits(input);

        int g = 0, e = 0;
        for (int i = sumBits.length - 1; i >= 0; i--) {
            if (sumBits[i] > input.size() / 2) {
                g |= 1 << i;
            } else {
                e |= 1 << i;
            }
        }
        log.info("G: " + g); // 22
        log.info("E: " + e); // 9
        return g * e;
    }

    private Integer partTwo() {
        List<int[]> input = parseInput();

        int o = toInt(calculate(input, false));
        int r = toInt(calculate(input, true));

        log.info("O: " + o);
        log.info("R: " + r);

        return o * r;
    }

    private int toInt(int[] arr) {
        int sum = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            sum += Math.pow(2, (arr.length - 1 - i)) * arr[i];
        }
        return sum;
    }

    private int[] calculate(List<int[]> input, boolean leastCommon) {
        int[] sumBits = sumBits(input);
        int lastIndex = sumBits.length - 1;
        for (int i = 0; i <= lastIndex; i++) {
            List<int[]> results = new ArrayList<>();
            int expected = (leastCommon ^ ((float)sumBits[lastIndex - i] >= input.size() / 2f)) ? 1 : 0;
            for (int[] bits : input) {
                if (bits[i] == expected) {
                    results.add(bits);
                } else {
                    // No need to recalculate sumBits
                    for (int j = i + 1; j < bits.length; j++) {
                        sumBits[lastIndex - j] -= bits[j];
                    }
                }
            }
            if (results.size() == 1) {
                return results.get(0);
            }
            input = results;
        }
        throw new RuntimeException("Not good :)");
    }

    private int[] sumBits(List<int[]> input) {
        int[] sum = new int[0];
        for (int[] arr : input) {
            // Just in case, the inputs are the same length
            // The array is reversed so shouldn't be a problem to resize, no shifting required
            if (sum.length < arr.length) {
                sum = Arrays.copyOf(sum, arr.length);
            }
            for (int i = arr.length - 1; i >= 0; i--) {
                sum[arr.length - 1 - i] += arr[i];
            }
        }
        return sum;
    }

    @SneakyThrows
    public List<int[]> parseInput() {
        File file = ResourceUtils.getResourceFile("day03.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<int[]> results = new ArrayList<>();
            while (scanner.hasNext()) {
                int[] line = scanner.nextLine().chars()
                    .map(x -> x - 48)
                    .toArray();
                results.add(line);
            }
            return results;
        }
    }
}

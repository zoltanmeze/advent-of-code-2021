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
public class Day03newnew implements Runnable {

    public static void main(String[] args) {
        new Day03newnew().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        //log.info("Part two: {}", partTwo());
    }

    private Integer partOne() {
        List<Integer> input = parseInput();
        int[] sumBits = sumBits(input);
        int g = 0, e = 0;
        for (int i = sumBits.length - 1; i >= 0; i--) {
            boolean b = sumBits[i] > input.size() / 2;
            g <<= 1;
            g |= b ? 1 : 0;
            e <<= 1;
            e |= (b ? 0 : 1);
        }
        log.info("G: " + g); // 22
        log.info("E: " + e); // 9
        return g * e;
    }

    private int[] sumBits(List<Integer> input) {
        int[] sum = new int[5];
        for (Integer arr : input) {
            // Just in case, the inputs are the same length
            // The array is reversed so shouldn't be a problem to resize, no shifting required
            for (int i = 0; arr != 0; i++) {
                sum[i] += arr & 0b1;
                arr >>= 1;
            }
        }
        return sum;
    }

    @SneakyThrows
    public List<Integer> parseInput() {
        File file = ResourceUtils.getResourceFile("day03.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Integer> results = new ArrayList<>();
            while (scanner.hasNext()) {
                results.add(Integer.parseInt(scanner.nextLine(), 2));
            }
            return results;
        }
    }
}

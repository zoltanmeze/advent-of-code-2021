package com.github.zoltanmeze.aoc.day06;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day06 implements Runnable {

    public static void main(String[] args) {
        new Day06().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Integer> input = parseInput();
        return calc(input, 80);
    }

    public Object partTwo() {
        List<Integer> input = parseInput();
        return calc(input, 256);
    }

    private void printArray(long[] arr, int start) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[(i + start) % arr.length] + (i != arr.length - 1 ? "," : ""));
        }
        System.out.println("]");
    }

    public Object calc(List<Integer> input, int days) {
        int size = 9;
        long[] ages = new long[size];
        for (Integer a : input) {
            ages[a]++;
        }
        /*for (int i = 0; i < days; i++) {
            long[] agesnew = new long[ages.length];
            arraycopy(ages, 1, agesnew, 0, ages.length - 1); // copy array with shift to 1 left
            agesnew[6] += ages[0];
            agesnew[8] = ages[0];
            ages = agesnew;
            printArray(ages, 0);
        }*/
        // Shift the starting index, not the whole array
        for (int i = 0; i < days; i++) {
            // eq to ages[(i + 6) % size] += ages[(i - 1) % size] with i = 1 and i <= days
            ages[(i + 7) % size] += ages[i % size];
        }
        long num = 0;
        for (long age : ages) {
            num += age;
        }
        return num;
    }

    @SneakyThrows
    public List<Integer> parseInput() {
        File file = ResourceUtils.getResourceFile("day06.txt");
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

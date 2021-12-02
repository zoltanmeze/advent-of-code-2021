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
public class Day03 implements Runnable {

    public static void main(String[] args) {
        new Day03().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<int[]> input = parseInput();
        int[] bits = new int[1];

        for (int[] ints : input) {
            if (bits.length < ints.length) {
                bits = Arrays.copyOf(bits, ints.length);
            }
            for (int i = ints.length - 1; i >= 0; i--) {
                bits[ints.length - 1 - i] += ints[i];
            }
        }

        int g = 0;
        int e = 0;

        for (int i = bits.length - 1; i >= 0; i--) {
            boolean b = bits[i] > input.size() / 2;

            g += b ? Math.pow(2, i) : 0;
            e += b ? 0 : Math.pow(2, i);
        }

        System.out.println(g);
        System.out.println(e);

        return g * e;
    }


    public Object partTwo() {
        List<int[]> input = parseInput();
        int[] bits = new int[1];

        for (int[] ints : input) {
            if (bits.length < ints.length) {
                bits = Arrays.copyOf(bits, ints.length);
            }
            for (int i = ints.length - 1; i >= 0; i--) {
                bits[ints.length - 1 - i] += ints[i];
            }
        }

        for (int[] ints : calculate(bits, input, false)) {
            System.out.println(Arrays.toString(ints));
        }

        System.out.println("meh");
        for (int[] ints : calculate(bits, input, true)) {
            System.out.println(Arrays.toString(ints));
        }


        int i = toInt(calculate(bits, input, false).get(0));
        int j = toInt(calculate(bits, input, true).get(0));

        System.out.println(i + " " + j);

        return i * j;
    }


    private int toInt(int[] arr) {
        int sum = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            sum += Math.pow(2, (arr.length - 1 - i)) * arr[i];
        }
        return sum;
    }

    private List<int[]> calculate(int[] bits, List<int[]> input, boolean revers) {
        int[] newbits = Arrays.copyOf(bits, bits.length);
        List<int[]> res = new ArrayList<>(input);
        for (int i = 0; i < bits.length; i++) {
            List<int[]> newres = new ArrayList<>();
            int expected = (revers ^ ((float)newbits[newbits.length - 1 - i] >= res.size() / 2f)) ? 1 : 0;
            for (int[] ints : res) {
                if (ints[i] == expected) {
                    newres.add(ints);
                }
            }
            res = newres;
            if (res.size() == 1) {
                return res;
            }
            newbits = new int[bits.length];
            for (int[] ints : res) {
                for (int j = newbits.length - 1; j >= 0; j--) {
                    newbits[ints.length - 1 - j] += ints[j];
                }
            }
        }
        return res;
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

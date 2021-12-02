package com.github.zoltanmeze.aoc.day01;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day02 implements Runnable {

    public static void main(String[] args) {
        new Day02().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<Command> input = parseInput();
        Pair result = Pair.of(0, 0);

        for (Command command : input) {
            switch (command.direction) {
                case UP:
                    result = Pair.of(result.x, result.y - command.value);
                    break;
                case DOWN:
                    result = Pair.of(result.x, result.y + command.value);
                    break;
                case FORWARD:
                    result = Pair.of(result.x + command.value, result.y);
                    break;
            }
        }
        log.info("Result: {}", result);
        return result.x * result.y;
    }


    public Object partTwo() {
        List<Command> input = parseInput();
        Pair result = Pair.of(0, 0);
        int aim = 0;

        for (Command command : input) {
            switch (command.direction) {
                case UP:
                    aim -= command.value;
                    break;
                case DOWN:
                    aim += command.value;
                    break;
                case FORWARD:
                    result = Pair.of(result.x + command.value, result.y + aim * command.value);
                    break;
            }
        }
        log.info("Result: {}", result);
        return result.x * result.y;
    }

    @SneakyThrows
    public List<Command> parseInput() {
        File file = ResourceUtils.getResourceFile("day02.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Command> results = new ArrayList<>();
            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().split("\\s");
                results.add(Command.of(Direction.valueOf(line[0].toUpperCase()), Integer.parseInt(line[1])));
            }
            return results;
        }
    }

    @ToString
    @RequiredArgsConstructor(staticName = "of")
    private static class Command {
        final Direction direction;
        final int value;
    }

    enum Direction {
        UP,
        DOWN,
        FORWARD
    }

    @Data
    @RequiredArgsConstructor(staticName = "of")
    private static class Pair {
        final int x;
        final int y;
    }
}

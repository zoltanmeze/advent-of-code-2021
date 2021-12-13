package com.github.zoltanmeze.aoc.day13;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class Day13 implements Runnable {

    public static void main(String[] args) {
        new Day13().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Origami input = parseInput();
        OrigamiState state = fold(input.getCoordinates(), input.getInstructions().get(0));
        return state.coordinates.size();
    }

    public Object partTwo() {
        Origami input = parseInput();
        Set<Coordinate> coordinates = input.getCoordinates();
        OrigamiState state = null;
        for (Coordinate instruction : input.getInstructions()) {
            state = fold(coordinates, instruction);
            coordinates = state.getCoordinates();
        }
        if (state == null) {
            throw new RuntimeException();
        }
        return print(state);
    }

    private OrigamiState fold(Set<Coordinate> coordinates, Coordinate instruction) {
        OrigamiState state = new OrigamiState();
        state.setCoordinates(new HashSet<>());
        state.setMax(Coordinate.of(instruction.x, instruction.y));
        for (Coordinate coordinate : coordinates) {
            if (instruction.x != 0 && coordinate.x > instruction.x) {
                int x = coordinate.x - ((coordinate.x - instruction.x) * 2);
                state.coordinates.add(Coordinate.of(x, coordinate.y));
                state.max.y = Math.max(coordinate.y, state.max.y);
            } else if (instruction.y != 0 && coordinate.y > instruction.y) {
                int y = coordinate.y - ((coordinate.y - instruction.y) * 2);
                state.coordinates.add(Coordinate.of(coordinate.x, y));
                state.max.x = Math.max(coordinate.x, state.max.x);
            } else {
                state.coordinates.add(coordinate);
            }
        }
        return state;
    }

    private String print(OrigamiState state) {
        StringBuilder sb = new StringBuilder("\n");
        for (int j = 0; j <= state.max.y; j++) {
            for (int i = 0; i <= state.max.x; i++) {
                sb.append(state.coordinates.contains(Coordinate.of(i, j)) ? "#" : " ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @SneakyThrows
    public Origami parseInput() {
        File file = ResourceUtils.getResourceFile("day13.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter("\\v\\v");
            Origami origami = new Origami();
            Set<Coordinate> coordinates = new HashSet<>();
            if (scanner.hasNext()) {
                try (Scanner scanner1 = new Scanner(scanner.next())) {
                    while (scanner1.hasNextLine()) {
                        String[] line = scanner1.nextLine().split(",");
                        Coordinate coordinate = Coordinate.of(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                        coordinates.add(coordinate);
                    }
                }
            }
            List<Coordinate> instructions = new ArrayList<>();
            if (scanner.hasNext()) {
                try (Scanner scanner1 = new Scanner(scanner.next())) {
                    while (scanner1.hasNextLine()) {
                        String[] line = scanner1.nextLine().split("\\s|=");
                        int value = Integer.parseInt(line[3]);
                        if (Objects.equals(line[2], "x")) {
                            instructions.add(Coordinate.of(value, 0));
                        } else {
                            instructions.add(Coordinate.of(0, value));
                        }
                    }
                }
            }
            origami.setCoordinates(coordinates);
            origami.setInstructions(instructions);
            return origami;
        }
    }

    @Data
    private static class Origami {
        Set<Coordinate> coordinates;
        List<Coordinate> instructions;
    }

    @Data
    private static class OrigamiState {
        Set<Coordinate> coordinates;
        Coordinate max;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    private static class Coordinate {
        private int x;
        private int y;
    }
}

package com.github.zoltanmeze.aoc.day21;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class Day21 implements Runnable {

    public static void main(String[] args) {
        new Day21().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        Player[] input = parseInput();

        int turn = 0;
        int dice = 0;
        int roll = 0;

        Player player;
        do {
            player = input[turn];
            for (int i = 0; i < 3; i++) {
                dice += 1;
                dice %= 101;
                if (dice == 0) {
                    dice = 1;
                }
                player.position += dice;
                int mod;
                while ((mod = player.position / 11) > 0) {
                    player.position %= 11;
                    player.position += mod;
                }
            }
            roll += 3;
            player.score += player.position;

            turn += 1;
            turn %= input.length;
        } while (player.score < 1000);

        return roll * input[turn].score;
    }

    public Object partTwo() {
        Player[] input = parseInput();

        long time = System.currentTimeMillis();
        Map<State, long[]> cache = new HashMap<>();
        long[] results = compute(State.of(input, 0), 21, cache, 3);

        System.out.println(cache.size());
        System.out.println("TIME: " + (System.currentTimeMillis() - time));

        long max = 0;
        for (long result : results) {
            if (result > max) {
                max = result;
            }
        }
        return max;
    }

    private long[] compute(State state, int limit, Map<State, long[]> cache, int sides) {
        long[] results = cache.get(state);
        if (results != null) {
            return results;
        }
        results = new long[state.players.length];
        for (int i = sides; i > 0; i--) {
            for (int j = sides; j > 0; j--) {
                for (int k = sides; k > 0; k--) {
                    State nextState = state.clone();
                    nextState.turn = (nextState.turn + 1) % 2;

                    Player player = nextState.players[state.turn];
                    player.position += i + j + k;

                    int mod = player.position / 11;
                    if (mod > 0) {
                        player.position %= 11;
                        player.position += mod;
                    }
                    player.score += player.position;

                    if (player.score >= limit) {
                        results[state.turn] += 1;
                        continue;
                    }
                    long[] sub = compute(nextState, limit, cache, sides);
                    for (int n = 0; n < sub.length; n++) {
                        results[n] += sub[n];
                    }
                }
            }
        }
        cache.put(state, results);
        return results;
    }

    @Data(staticConstructor = "of")
    @AllArgsConstructor(staticName = "of")
    static class Player implements Cloneable {
        int position;
        int score;

        @Override
        public Player clone() {
            try {
                Player clone = (Player) super.clone();
                clone.position = position;
                clone.score = score;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    @Data(staticConstructor = "of")
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode
    static class State implements Cloneable {
        Player[] players;
        int turn;

        @Override
        public State clone() {
            try {
                State clone = (State) super.clone();
                clone.players = new Player[players.length];
                for (int i = 0; i < players.length; i++) {
                    clone.players[i] = players[i].clone();
                }
                clone.turn = turn;
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    @SneakyThrows
    public Player[] parseInput() {
        File file = ResourceUtils.getResourceFile("day21.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<Player> results = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\\s");

                results.add(Player.of(Integer.parseInt(line[4]), 0));
            }

            return results.toArray(new Player[0]);
        }
    }
}

package com.github.zoltanmeze.aoc.day04;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day04 implements Runnable {

    public static void main(String[] args) {
        new Day04().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        BingBoards input = parseInput();
        return solve(input, true);
    }

    public Object partTwo() {
        BingBoards input = parseInput();
        return solve(input, false);
    }

    private int solve(BingBoards input, boolean first) {
        int[][][] boards = input.getBoards();
        int[][][] boardCompletions = new int[boards.length][][];

        Integer lastBoards = null, lastNumber = null;
        int boardsCompleted = 0;

        for (int i = 0; i < boards.length; i++) {
            int[][] board = boards[i];
            boardCompletions[i] = new int[board.length + 1][board[0].length + 1];
        }

        for (int number : input.getNumbers()) {
            for (int j = 0; j < boards.length; j++) {
                int[][] board = boards[j];
                int[][] boardCompletion = boardCompletions[j];
                if (boardCompletion[0][0] == -1) {
                    continue; // already completed
                }
                for (int x = 0; x < board.length; x++) {
                    for (int y = 0; y < board[x].length; y++) {
                        if (board[x][y] != number) {
                            continue;
                        }
                        // Duplicates? In case we don't need this, replace boardCompletion with two single dimensional array
                        if (boardCompletion[x + 1][y + 1] == 1) {
                            continue;
                        }
                        boardCompletion[x + 1][y + 1] = 1;
                        boardCompletion[x + 1][0]++;
                        boardCompletion[0][y + 1]++;
                        if (boardCompletion[x + 1][0] == board.length || boardCompletion[0][y + 1] == board[x].length) {
                            // First or all boards are completed so it's already last
                            if (first || ++boardsCompleted == boards.length) {
                                return calculate(board, boardCompletion, number);
                            }
                            lastBoards = j;
                            lastNumber = number;
                            boardCompletion[0][0] = -1; // Make 0,0 useful
                        }
                    }
                }
            }
        }
        if (lastBoards == null || lastNumber == null) {
            throw new RuntimeException("Meh, shit went wrong");
        }
        return calculate(boards[lastBoards], boardCompletions[lastBoards], lastNumber);
    }

    private int calculate(int[][] board, int[][] boardCompletion, int number) {
        int sum = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (boardCompletion[i + 1][j + 1] == 0) {
                    sum += board[i][j];
                }
            }
        }
        return sum * number;
    }

    @SneakyThrows
    public BingBoards parseInput() {
        File file = ResourceUtils.getResourceFile("day04.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            scanner.useDelimiter("\n\n");
            BingBoards bingBoards = new BingBoards();
            if (scanner.hasNext()) {
                List<Integer> numbers = new ArrayList<>();
                try (Scanner scanner1 = new Scanner(scanner.nextLine())) {
                    scanner1.useDelimiter(",");
                    while (scanner1.hasNextInt()) {
                        numbers.add(scanner1.nextInt());
                    }
                }
                bingBoards.setNumbers(numbers);
                List<int[][]> boards = new ArrayList<>();
                while (scanner.hasNext()) {
                    try (Scanner scanner1 = new Scanner(scanner.next())) {
                        int[][] board = new int[5][5];
                        for (int i = 0; i < board.length; i++) {
                            for (int j = 0; j < board[i].length; j++) {
                                if (!scanner1.hasNext()) {
                                    throw new RuntimeException();
                                }
                                board[i][j] = scanner1.nextInt();
                            }
                        }
                        boards.add(board);
                    }
                }
                bingBoards.setBoards(boards.toArray(new int[0][][]));
            }
            return bingBoards;
        }
    }
}

package com.github.zoltanmeze.aoc.day18;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Day18Old implements Runnable {

    public static void main(String[] args) {
        new Day18Old().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {

        long time = System.currentTimeMillis();

        List<NumberPair> input = parseInput();

        System.out.println("PARSING: " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();

        NumberPair root = input.get(0);

        for (int i = 1; i < input.size(); i++) {
            NumberPair nextRoot = NumberPair.of(null, null);
            NumberPair right = input.get(i);

            root.prev = nextRoot;
            right.prev = nextRoot;

            nextRoot.nodes.add(root);
            nextRoot.nodes.add(right);

            boolean t;
            do {
                t = false;
                while (compute(nextRoot, 0)) {
                    t = true;
                }
                if (split(nextRoot)) {
                    t = true;
                }

            } while (t);

            root = nextRoot;
        }
        System.out.println("TIME: " + (System.currentTimeMillis() - time));

        return magnitude(root);
    }

    private long magnitude(NumberPair current) {
        if (current.isRegular()) {
            return current.value;
        }
        long sum = 0;
        int size = current.nodes.size();
        if (size >= 1) {
            sum += (3 * magnitude(current.nodes.get(0)));
        }
        if (size == 2) {
            sum += (2 * magnitude(current.nodes.get(1)));
        }
        return sum;
    }

    private boolean compute(NumberPair current, int n) {
        if (n >= 4) {
            if (current.isRegularPair()) {
                if (current.nodes.isEmpty()) {
                    System.out.println("MEH");
                }

                //System.out.println("EXPLODE: " + current);

                int leftValue = current.nodes.get(0).value;
                int rightValue = current.nodes.get(1).value;

                current.nodes.clear();
                current.value = 0;


                NumberPair left = findLeft(current.prev, current);
                NumberPair right = findRight(current.prev, current);

                //System.out.println("LEFT: " + left);
                //System.out.println("RIGHT: " + right);

                if (left != null) {
                    left.value += leftValue;
                }
                if (right != null) {
                    right.value += rightValue;
                }

                return true;
            }
        }
        for (NumberPair node : current.nodes) {
            if (compute(node, n + 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean split(NumberPair current) {
        if (current.value != null && current.value >= 10) {
            int left = current.value / 2;
            int right = (current.value + 1) / 2;

            current.value = null;

            current.nodes.add(NumberPair.of(left, current));
            current.nodes.add(NumberPair.of(right, current));

            return true;
        }
        for (NumberPair node : current.nodes) {
            if (split(node)) {
                return true;
            }
        }
        return false;
    }

    private NumberPair findLeft(NumberPair current, NumberPair pair) {
        if (current == null) {
            return null;
        }
        if (current.nodes.get(1) == pair) {
            return findLastRegular(current.nodes.get(0));
        }
        return findLeft(current.prev, pair.prev);
    }

    private NumberPair findRight(NumberPair current, NumberPair pair) {
        if (current == null) {
            return null;
        }
        if (current.nodes.get(0) == pair) {
            return findFirstRegular(current.nodes.get(1));
        }
        return findRight(current.prev, pair.prev);
    }

    private NumberPair findFirstRegular(NumberPair current) {
        if (current.isRegular()) {
            return current;
        }
        return findFirstRegular(current.nodes.get(0));
    }

    private NumberPair findLastRegular(NumberPair current) {
        if (current.isRegular()) {
            return current;
        }
        return findLastRegular(current.nodes.get(1));
    }

    public Object partTwo() {
        List<NumberPair> input = parseInput();

        long time = System.currentTimeMillis();

        long max = 0;

        for (int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.size(); j++) {
                if (i == j) {
                    continue;
                }
                NumberPair root = input.get(i).deepCopy();

                NumberPair nextRoot = NumberPair.of(null, null);
                NumberPair right = input.get(j).deepCopy();

                root.prev = nextRoot;
                right.prev = nextRoot;

                nextRoot.nodes.add(root);
                nextRoot.nodes.add(right);

                boolean t;
                do {
                    t = false;
                    while (compute(nextRoot, 0)) {
                        t = true;
                    }
                    if (split(nextRoot)) {
                        t = true;
                    }

                } while (t);


                max = Math.max(max, magnitude(nextRoot));
            }
        }

        System.out.println("TIME: " + (System.currentTimeMillis() - time));

        return max;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    @EqualsAndHashCode(exclude = "prev")
    @NoArgsConstructor
    //@ToString(exclude = "prev")
    static class NumberPair {
        Integer value;
        NumberPair prev;

        final List<NumberPair> nodes = new ArrayList<>();

        boolean isRegular() {
            return value != null;
        }

        boolean isRegularPair() {
            return nodes.size() == 2 && nodes.stream()
                .map(NumberPair::isRegular)
                .reduce(true, (x, y) -> x && y);
        }

        public NumberPair deepCopy() {
            NumberPair root = NumberPair.of(value, null);
            for (NumberPair node : nodes) {
                NumberPair nodeCopy = node.deepCopy();
                nodeCopy.prev = root;
                root.nodes.add(nodeCopy);
            }
            return root;
        }

        @Override
        public String toString() {
            if (value != null && nodes.isEmpty()) {
                return String.valueOf(value);
            } else if (value == null && !nodes.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder("[");
                for (int i = 0; i < nodes.size(); i++) {
                    stringBuilder.append(nodes.get(i).toString());
                    if (i != nodes.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("]");
                return stringBuilder.toString();
            } else {
                return "MEH";
            }
        }

        public String toString2() {
            return toString2(0);
        }

        public String toString2(int n) {
            if (value != null && nodes.isEmpty()) {
                return "" + " ".repeat(String.valueOf(value).length());
            } else if (value == null && !nodes.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder("" + n);
                for (int i = 0; i < nodes.size(); i++) {
                    stringBuilder.append(nodes.get(i).toString2(n + 1));
                    if (i != nodes.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("]");
                return stringBuilder.toString();
            } else {
                return "MEH";
            }
        }
    }

    @SneakyThrows
    public List<NumberPair> parseInput() {
        File file = ResourceUtils.getResourceFile("day18.txt");
        try (
            FileReader fileReader = new FileReader(file);
            Scanner scanner = new Scanner(fileReader)
        ) {
            List<NumberPair> list = new ArrayList<>();
            while (scanner.hasNextLine()) {
                char[] chars = scanner.nextLine().toCharArray();
                NumberPair root = NumberPair.of(null, null);
                NumberPair node = root;
                for (char aChar : chars) {
                    switch (aChar) {
                        case '[':
                            if (node.value != null) {
                                NumberPair next = NumberPair.of(node.value, node);
                                node.nodes.add(next);
                                node.value = null;
                            }
                            NumberPair next = NumberPair.of(null, node);
                            node.nodes.add(next);
                            node = next;
                            break;
                        case ']':
                            node = node.prev;
                            break;
                        case ',':
                            continue;
                        default:
                            next = NumberPair.of(aChar - '0', node);
                            node.nodes.add(next);
                            break;
                    }
                }
                root = root.nodes.get(0);
                root.prev = null;
                list.add(root);
            }
            return list;
        }
    }
}

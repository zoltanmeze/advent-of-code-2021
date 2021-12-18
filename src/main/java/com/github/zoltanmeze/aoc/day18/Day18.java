package com.github.zoltanmeze.aoc.day18;

import com.github.zoltanmeze.aoc.utilities.ResourceUtils;
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
public class Day18 implements Runnable {

    public static void main(String[] args) {
        new Day18().run();
    }

    @Override
    public void run() {
        log.info("Part one: {}", partOne());
        log.info("Part two: {}", partTwo());
    }

    public Object partOne() {
        List<NumberPair> input = parseInput();

        long time = System.currentTimeMillis();

        NumberPair current = input.get(0);
        for (int i = 1; i < input.size(); i++) {
            NumberPair right = input.get(i);
            current = add(current, right);
        }
        log.info("time: {}", System.currentTimeMillis() - time);
        return magnitude(current);
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
                NumberPair left = input.get(i).deepCopy();
                NumberPair right = input.get(j).deepCopy();
                max = Math.max(magnitude(add(left, right)), max);
            }
        }
        log.info("time: {}", System.currentTimeMillis() - time);
        return max;
    }

    private NumberPair add(NumberPair left, NumberPair right) {
        NumberPair root = left.add(right);
        boolean reduced;
        do {
            reduced = true;
            while (explode(root, 0)) {
                reduced = false;
            }
            reduced &= !split(root);
        } while (!reduced);
        return root;
    }

    private boolean explode(NumberPair current, int n) {
        if (current == null) {
            return false;
        } else if (n == 4 && current.isRegularPair()) {
            int leftValue = current.left.value;
            int rightValue = current.right.value;

            NumberPair left = findLeftRegular(current.parent, current);
            if (left != null) {
                left.value += leftValue;
            }

            NumberPair right = findRightRegular(current.parent, current);
            if (right != null) {
                right.value += rightValue;
            }

            current.left = null;
            current.right = null;
            current.value = 0;

            return true;
        }
        return n < 4 && (explode(current.left, n + 1) || explode(current.right, n + 1));
    }

    private boolean split(NumberPair current) {
        if (current == null) {
            return false;
        } else if (current.isRegular()) {
            if (current.value < 10) {
                return false;
            }
            current.left = new NumberPair(current.value / 2, current);
            current.right = new NumberPair((current.value + 1) / 2, current);
            current.value = null;
            return true;
        }
        return split(current.left) || split(current.right);
    }

    private NumberPair findLeftRegular(NumberPair current, NumberPair exploded) {
        if (current == null) {
            return null;
        }
        // Exploded value is on the right, leftmost regular must be the rightmost value on the left side
        if (current.right == exploded) {
            return findRightMostRegular(current.left);
        }
        return findLeftRegular(current.parent, exploded.parent);
    }

    private NumberPair findRightMostRegular(NumberPair current) {
        if (current.isRegular()) {
            return current;
        }
        return findRightMostRegular(current.right);
    }

    private NumberPair findLeftMostRegular(NumberPair current) {
        if (current.isRegular()) {
            return current;
        }
        return findLeftMostRegular(current.left);
    }

    private NumberPair findRightRegular(NumberPair current, NumberPair exploded) {
        if (current == null) {
            return null;
        }
        // Exploded value is on the left, rightmost regular must be the leftmost value on the right side
        if (current.left == exploded) {
            return findLeftMostRegular(current.right);
        }
        return findRightRegular(current.parent, exploded.parent);
    }

    private long magnitude(NumberPair current) {
        if (current.isRegular()) {
            return current.value;
        }
        return 3 * magnitude(current.left) + 2 * magnitude(current.right);
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(exclude = "parent")
    static class NumberPair {
        Integer value;

        NumberPair parent;

        NumberPair left;
        NumberPair right;

        public NumberPair(Integer value, NumberPair parent) {
            this(value);
            this.parent = parent;
        }

        public NumberPair(Integer value) {
            this.value = value;
        }

        public boolean isRegular() {
            return value != null;
        }

        public boolean isRegularPair() {
            return !isRegular() && left.isRegular() && right.isRegular();
        }

        public NumberPair add(NumberPair second) {
            NumberPair root = new NumberPair();

            this.parent = root;
            root.left = this;

            second.parent = root;
            root.right = second;

            return root;
        }

        public NumberPair addChild(Integer value) {
            NumberPair child = new NumberPair(value);
            child.parent = this;
            if (this.left == null) {
                this.left = child;
            } else {
                this.right = child;
            }
            return child;
        }

        public NumberPair deepCopy() {
            if (isRegular()) {
                return new NumberPair(value);
            }
            return left.deepCopy().add(right.deepCopy());
        }

        @Override
        public String toString() {
            if (isRegular()) {
                return String.valueOf(value);
            }
            return "[" + left + "," + right + "]";
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
                NumberPair root = new NumberPair();
                NumberPair current = root;
                for (char ch : scanner.nextLine().toCharArray()) {
                    switch (ch) {
                        case '[':
                            current = current.addChild(null);
                            break;
                        case ']':
                            current = current.parent;
                            break;
                        case ',':
                            continue;
                        default:
                            current.addChild(ch - '0');
                            break;
                    }
                }
                root = root.left;
                root.parent = null;
                list.add(root);
            }
            return list;
        }
    }
}

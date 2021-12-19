package com.github.zoltanmeze.aoc.day19;

import static com.github.zoltanmeze.aoc.day19.Day19.Coordinate;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("SuspiciousNameCombination")
public class GenerateRotations {

    static Function<Coordinate, Coordinate> ROTATE_X = c -> Coordinate.of(c.x, -c.z, c.y);
    static Function<Coordinate, Coordinate> ROTATE_Y = c -> Coordinate.of(c.z, c.y, -c.x);
    static Function<Coordinate, Coordinate> ROTATE_Z = c -> Coordinate.of(-c.y, c.x, c.z);

    static final List<Function<Coordinate, Coordinate>> ROTATIONS = List.of(

        Coordinate::copy,
        ROTATE_Z,
        ROTATE_Z.andThen(ROTATE_Z),
        ROTATE_Z.andThen(ROTATE_Z).andThen(ROTATE_Z),

        ROTATE_Y,
        ROTATE_Y.andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Z).andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Z).andThen(ROTATE_Z).andThen(ROTATE_Z),

        ROTATE_Y.andThen(ROTATE_Y),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Z).andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Z).andThen(ROTATE_Z).andThen(ROTATE_Z),

        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Y),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Y).andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Y).andThen(ROTATE_Z).andThen(ROTATE_Z),
        ROTATE_Y.andThen(ROTATE_Y).andThen(ROTATE_Y).andThen(ROTATE_Z).andThen(ROTATE_Z).andThen(ROTATE_Z),

        ROTATE_X,
        ROTATE_X.andThen(ROTATE_Z),
        ROTATE_X.andThen(ROTATE_Z).andThen(ROTATE_Z),
        ROTATE_X.andThen(ROTATE_Z).andThen(ROTATE_Z).andThen(ROTATE_Z),

        ROTATE_X.andThen(ROTATE_X).andThen(ROTATE_X),
        ROTATE_X.andThen(ROTATE_X).andThen(ROTATE_X).andThen(ROTATE_Z),
        ROTATE_X.andThen(ROTATE_X).andThen(ROTATE_X).andThen(ROTATE_Z).andThen(ROTATE_Z),
        ROTATE_X.andThen(ROTATE_X).andThen(ROTATE_X).andThen(ROTATE_Z).andThen(ROTATE_Z).andThen(ROTATE_Z)
    );

    public static void main(String[] args) {
        Coordinate c = Coordinate.of('x', 'y', 'z');
        int i = 1;
        for (Function<Coordinate, Coordinate> rotation : ROTATIONS) {
            System.out.println(print(rotation.apply(c)));
            if (i++ % 4 == 0) {
                System.out.println();
            }
        }
    }

    static String print(Coordinate c) {
        return (Math.signum(c.x) == -1 ? "-" : "") + "c." + ((char) Math.abs(c.x)) + "," +
            (Math.signum(c.y) == -1 ? "-" : "") + "c." + ((char) Math.abs(c.y)) + "," +
            (Math.signum(c.z) == -1 ? "-" : "") + "c." + ((char) Math.abs(c.z));
    }


}

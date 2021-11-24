package com.futurteam.wordexalt.utils;

import com.futurteam.wordexalt.logic.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CheckersUtils {
    private static final Point[][] Checkers = new Point[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];

    public static void init(final int width) {
        final int cellSize = width / Constants.MAP_WIDTH;
        final int halfCellSize = cellSize / 2;
        for (int y = 0; y < Checkers.length; y++) {
            for (int x = 0; x < Checkers[y].length; x++) {
                Checkers[y][x] = new Point(
                        halfCellSize + x * cellSize,
                        halfCellSize + y * cellSize
                );
            }
        }
    }

    public static Point get(final int x, final int y) {
        return Checkers[y][x];
    }

    public static List<Point> initRoute() {
        final List<Point> route = new ArrayList<>();
        for (int y = 0; y < Checkers.length; y++) {
            final List<Point> line = Arrays.asList(Checkers[y].clone());
            if (y % 2 == 1) {
                Collections.reverse(line);
            }

            route.addAll(line);
        }

        return route;
    }
}

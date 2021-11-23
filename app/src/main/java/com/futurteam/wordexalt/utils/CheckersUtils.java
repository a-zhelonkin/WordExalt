package com.futurteam.wordexalt.utils;

import com.futurteam.wordexalt.logic.Point;

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
}

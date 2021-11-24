package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;

import com.futurteam.wordexalt.logic.Point;
import com.futurteam.wordexalt.utils.Constants;

public abstract class BasePlanner implements Planner {

    protected static final Point[] DIRECTIONS = new Point[]{
            new Point(+1, +1), new Point(0, +1), new Point(-1, +1),
            new Point(+1, +0), /*                   */ new Point(-1, +0),
            new Point(+1, -1), new Point(0, -1), new Point(-1, -1),
    };

    protected final char[][] _map;
    protected final byte _width;
    protected final byte _height;

    protected BasePlanner(@NonNull final char[][] map) {
        _map = map;
        _width = (byte) map[0].length;
        _height = (byte) map.length;
    }

    protected BasePlanner(@NonNull final String line) {
        _map = new char[Constants.MAP_HEIGHT][Constants.MAP_WIDTH];
        _map[0][0] = line.charAt(0);
        _map[0][1] = line.charAt(1);
        _map[0][2] = line.charAt(2);
        _map[0][3] = line.charAt(3);
        _map[0][4] = line.charAt(4);
        _map[1][4] = line.charAt(5);
        _map[1][3] = line.charAt(6);
        _map[1][2] = line.charAt(7);
        _map[1][1] = line.charAt(8);
        _map[1][0] = line.charAt(9);
        _map[2][0] = line.charAt(10);
        _map[2][1] = line.charAt(11);
        _map[2][2] = line.charAt(12);
        _map[2][3] = line.charAt(13);
        _map[2][4] = line.charAt(14);
        _map[3][4] = line.charAt(15);
        _map[3][3] = line.charAt(16);
        _map[3][2] = line.charAt(17);
        _map[3][1] = line.charAt(18);
        _map[3][0] = line.charAt(19);
        _map[4][0] = line.charAt(20);
        _map[4][1] = line.charAt(21);
        _map[4][2] = line.charAt(22);
        _map[4][3] = line.charAt(23);
        _map[4][4] = line.charAt(24);
        _width = Constants.MAP_WIDTH;
        _height = Constants.MAP_HEIGHT;
    }

}

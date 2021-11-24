package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;

import java.util.ArrayList;

public final class GraphPlanner extends BasePlanner {

    @NonNull
    private final Node[] _roots;

    public GraphPlanner(@NonNull final char[][] map) {
        super(map);
        this._roots = new Node[_width * _height];
    }

    public GraphPlanner(@NonNull final String line) {
        super(line);
        this._roots = new Node[_width * _height];
    }

    @Override
    public void prepare() {
        for (byte y = 0; y < _height; y++) {
            for (byte x = 0; x < _width; x++) {
                final int rootIndex = y * _width + x;
                @Nullable Node root = _roots[rootIndex];
                if (root == null) {
                    root = new Node(null, x, y, _map[y][x]);
                    root.childs = new ArrayList<>(8);
                    _roots[rootIndex] = root;
                }

                for (@NonNull final Point direction : DIRECTIONS) {
                    final byte newX = (byte) (root.x + direction.x);
                    if (newX < 0 || _width <= newX)
                        continue;

                    final byte newY = (byte) (root.y + direction.y);
                    if (newY < 0 || _height <= newY)
                        continue;

                    if (root.ExistsChild(newX, newY))
                        continue;

                    final int childIndex = newY * _width + newX;
                    @Nullable Node child = _roots[childIndex];
                    if (child == null) {
                        child = new Node(null, newX, newY, _map[newY][newX]);
                        child.childs = new ArrayList<>(8);
                        child.childs.add(root);
                        _roots[childIndex] = child;
                    }

                    root.childs.add(child);
                }
            }
        }
    }

    @Nullable
    @Override
    public Node check(@NonNull final String word) {
        for (@NonNull final Node root : _roots) {
            @Nullable final Node checked = check(root, null, word, 0);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }

    @Nullable
    private static Node check(@NonNull final Node cursor,
                              @Nullable final Node route,
                              @NonNull final String word,
                              int index) {
        final char letter = word.charAt(index);
        if (cursor.letter != letter)
            return null;

        if (route != null && route.ExistsRoute(cursor.x, cursor.y))
            return null;

        @NonNull final Node nextRoute = new Node(route, cursor.x, cursor.y, letter);

        index++;
        if (index == word.length()) {
            return nextRoute;
        }

        for (@NonNull final Node child : cursor.childs) {
            @Nullable final Node checked = check(child, nextRoute, word, index);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }

}

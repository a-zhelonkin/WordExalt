package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;
import com.futurteam.wordexalt.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public final class TreePlanner extends BasePlanner {

    @NonNull
    private final Node[] _roots = new Node[Constants.MAP_LENGTH];

    public TreePlanner(@NonNull final char[][] map) {
        super(map);
    }

    public TreePlanner(@NonNull final String line) {
        super(line);
    }

    public void prepare() {
        for (byte y = 0; y < _height; y++) {
            for (byte x = 0; x < _width; x++) {
                @NonNull final Node item = new Node(null, x, y, _map[y][x]);
                item.childs = deepIn(item, x, y, 0);

                _roots[y * _width + x] = item;
            }
        }
    }

    @Nullable
    private List<Node> deepIn(@NonNull final Node parent, final byte x, final byte y, final int level) {
        if (level == 8)
            return null;

        @NonNull final List<Node> list = new ArrayList<>(7);
        for (@NonNull final Point delta : DIRECTIONS) {
            final byte newX = (byte) (x + delta.x);
            if (newX < 0 || _width <= newX)
                continue;

            final byte newY = (byte) (y + delta.y);
            if (newY < 0 || _height <= newY)
                continue;

            if (parent.Exists(newX, newY))
                continue;

            @NonNull final Node node = new Node(parent, newX, newY, _map[newY][newX]);
            node.childs = deepIn(node, newX, newY, level + 1);

            list.add(node);
        }

        return list;
    }

    @Nullable
    public Node check(@NonNull final String word) {
        for (@NonNull final Node root : _roots) {
            @Nullable final Node checked = check(root, word, 0);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }

    @Nullable
    private static Node check(@NonNull final Node node, @NonNull final String word, int index) {
        char letter = word.charAt(index);
        if (node.letter != letter)
            return null;

        index++;
        if (word.length() == index)
            return node;

        if (node.childs == null || node.childs.isEmpty())
            return null;

        for (@NonNull final Node child : node.childs) {
            @Nullable final Node checked = check(child, word, index);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }
}

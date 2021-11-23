package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;

import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;

import java.util.ArrayList;
import java.util.List;

public final class TreePlanner implements Planner {

    private static final Point[] Directions = new Point[]{
            new Point(+1, +1), new Point(0, +1), new Point(-1, +1),
            new Point(+1, +0), /*                   */ new Point(-1, +0),
            new Point(+1, -1), new Point(0, -1), new Point(-1, -1),
    };

    private final char[][] _map;
    private final byte _width;
    private final byte _height;
    private final Node[] _roots;

    public static TreePlanner fromLine(String line) {
        char[][] map = new char[5][5];
        map[0][0] = line.charAt(0);
        map[0][1] = line.charAt(1);
        map[0][2] = line.charAt(2);
        map[0][3] = line.charAt(3);
        map[0][4] = line.charAt(4);
        map[1][4] = line.charAt(5);
        map[1][3] = line.charAt(6);
        map[1][2] = line.charAt(7);
        map[1][1] = line.charAt(8);
        map[1][0] = line.charAt(9);
        map[2][0] = line.charAt(10);
        map[2][1] = line.charAt(11);
        map[2][2] = line.charAt(12);
        map[2][3] = line.charAt(13);
        map[2][4] = line.charAt(14);
        map[3][4] = line.charAt(15);
        map[3][3] = line.charAt(16);
        map[3][2] = line.charAt(17);
        map[3][1] = line.charAt(18);
        map[3][0] = line.charAt(19);
        map[4][0] = line.charAt(20);
        map[4][1] = line.charAt(21);
        map[4][2] = line.charAt(22);
        map[4][3] = line.charAt(23);
        map[4][4] = line.charAt(24);
        return new TreePlanner(map);
    }

    public TreePlanner(char[][] map) {
        _map = map;
        _width = (byte) map[0].length;
        _height = (byte) map.length;
        _roots = new Node[_width * _height];
    }

    public void prepare() {
        for (byte y = 0; y < _height; y++) {
            for (byte x = 0; x < _width; x++) {
                Node item = new Node(null, x, y, _map[y][x]);
                item.childs = DeepIn(x, y, item, 0);

                _roots[y * _width + x] = item;
            }
        }
    }

    private List<Node> DeepIn(final byte x, final byte y, Node parent, int level) {
        if (level == 8)
            return null;

        List<Node> list = new ArrayList<>(7);
        for (Point delta : Directions) {
            byte newX = (byte) (x + delta.x);
            if (newX < 0 || _width <= newX)
                continue;

            byte newY = (byte) (y + delta.y);
            if (newY < 0 || _height <= newY)
                continue;

            if (parent.Exists(newX, newY))
                continue;

            Node node = new Node(parent, newX, newY, _map[newY][newX]);
            node.childs = DeepIn(newX, newY, node, level + 1);

            list.add(node);
        }

        return list;
    }

    public Node Check(@NonNull final String word) {
        for (Node root : _roots) {
            Node checked = Check(root, word, 0);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }

    private static Node Check(@NonNull final Node node, @NonNull final String word, int index) {
        char letter = word.charAt(index);
        if (node.letter != letter)
            return null;

        index++;
        if (word.length() == index)
            return node;

        if (node.childs == null || node.childs.isEmpty())
            return null;

        for (Node child : node.childs) {
            Node checked = Check(child, word, index);
            if (checked == null)
                continue;

            return checked;
        }

        return null;
    }
}

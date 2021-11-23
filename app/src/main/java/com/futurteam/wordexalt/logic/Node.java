package com.futurteam.wordexalt.logic;

import androidx.annotation.Nullable;

import java.util.List;

public final class Node {

    @Nullable
    public final Node parent;
    public final byte x;
    public final byte y;
    public final char letter;
    public List<Node> childs;

    public Node(@Nullable final Node parent, final byte x, final byte y, final char letter) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.letter = letter;
    }

    public boolean Exists(final byte x, final byte y) {
        return this.x == x && this.y == y || (parent != null && parent.Exists(x, y));
    }
}

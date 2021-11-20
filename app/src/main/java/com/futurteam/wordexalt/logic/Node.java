package com.futurteam.wordexalt.logic;

import java.util.List;

public final class Node {
    public byte X;
    public byte Y;
    public char Letter;
    public Node Parent;
    public List<Node> Childs;

    public boolean Exists(byte x, byte y) {
        return X == x && Y == y || (Parent != null && Parent.Exists(x, y));
    }
}

package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;

public final class GraphPlanner extends BasePlanner {

    public GraphPlanner(@NonNull final char[][] map) {
        super(map);
    }

    public GraphPlanner(@NonNull final String line) {
        super(line);
    }

    @Override
    public void prepare() {

    }

    @Nullable
    @Override
    public Node check(@NonNull String word) {
        return null;
    }

}

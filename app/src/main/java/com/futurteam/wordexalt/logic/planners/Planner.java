package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;

public interface Planner {

    void prepare();

    @Nullable
    Node Check(@NonNull final String word);

}

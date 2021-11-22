package com.futurteam.wordexalt.logic.planners;

import androidx.annotation.NonNull;

public interface Planner {

    void prepare();

    boolean Check(@NonNull final String word);

}

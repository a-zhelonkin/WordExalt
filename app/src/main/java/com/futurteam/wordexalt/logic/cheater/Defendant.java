package com.futurteam.wordexalt.logic.cheater;

import android.content.res.Resources;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.futurteam.wordexalt.R;
import com.futurteam.wordexalt.logic.Node;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import me.incrdbl.android.wordbyword.generator.CalculatedWord;
import me.incrdbl.android.wordbyword.generator.GameFieldWorkFlow;

public final class Defendant {

    private final static GameFieldWorkFlow gameFieldWorkFlow = new GameFieldWorkFlow();

    public static void init(@NonNull final Resources resources) {
        final InputStream inputStream = resources.openRawResource(R.raw.dictionary_ru);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
        final String collect = bufferedReader.lines().collect(Collectors.joining());

        gameFieldWorkFlow.initializeEncryptedFieldGenerator(collect, 0);
        final int initialized = gameFieldWorkFlow.initialized();
    }

    @NonNull
    public static Stack<Pair<String, Node>> resolve(@NonNull final String line) {
        final String[] letters = explode(line.toUpperCase());
        final CalculatedWord[] words = gameFieldWorkFlow.getPossibleWordsForLetters(letters);
        final Stack<Pair<String, Node>> nodes = new Stack<>();
        final Set<String> exists = new HashSet<>(words.length);

        for (final CalculatedWord word : words) {
            if (exists.contains(word.a)) {
                continue;
            }

            exists.add(word.a);

            Node node = null;
            for (final int index : word.b) {
                final byte y = (byte) (index / 5);
                final byte x = (byte) (index % 5);
                node = new Node(node, x, y, line.charAt(index));
            }

            nodes.add(new Pair<>(word.a, node));
        }

        return nodes.stream()
                .sorted(Comparator.comparingDouble(n -> n.first.length()))
                .collect(Collectors.toCollection(Stack::new));
    }

    private static String[] explode(final String s) {
        final String[] result = new String[s.length()];
        for (int index = 0; index < result.length; index++) {
            result[index] = String.valueOf(s.charAt(index));
        }

        return result;
    }

}

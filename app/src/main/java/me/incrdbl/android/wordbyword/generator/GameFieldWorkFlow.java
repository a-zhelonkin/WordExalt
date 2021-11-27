package me.incrdbl.android.wordbyword.generator;

public final class GameFieldWorkFlow {

    static {
        System.loadLibrary("wordfieldprocess");
    }

    public final native void deinitialize();

    public final native String[] generateField();

    public final native String[] generateFieldWithRequiredWords(String[] paramArrayOfString);

    public final native String[] generateFieldWithRequiredWordsAndWordCount(int paramInt1, int paramInt2, String[] paramArrayOfString);

    public final native String[] generateFieldWordCount(int paramInt1, int paramInt2);

    public final native CalculatedWord[] getPossibleWordsForLetters(String[] paramArrayOfString);

    public final native int initializeEncryptedFieldGenerator(String paramString, int paramInt);

    public final native int initializeFieldGenerator(String paramString, int paramInt);

    public final native int initialized();

    public final native int isWordExist(String paramString);
}

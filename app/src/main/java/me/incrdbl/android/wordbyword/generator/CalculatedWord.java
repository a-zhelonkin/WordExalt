package me.incrdbl.android.wordbyword.generator;

public final class CalculatedWord {
    public String a;
    public int[] b;

    public CalculatedWord(String paramString, int[] paramArrayOfint) {
        this.a = paramString;
        if (paramArrayOfint != null) {
            this.b = paramArrayOfint;
        } else {
            this.b = new int[0];
        }
    }
}

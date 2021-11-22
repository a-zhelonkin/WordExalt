package com.futurteam.wordexalt.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;

public final class WordsOverlayView extends View {

    @NonNull
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Nullable
    private Node node;

    public WordsOverlayView(final Context context) {
        super(context);
        init();
    }

    public WordsOverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordsOverlayView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint.setAlpha(32);
        paint.setStrokeWidth(10);
        setFocusableInTouchMode(false);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        canvas.drawLine(0, 0, width, height, paint);
    }

    public void setNode(@Nullable final Node node) {
        this.node = node;
    }
}

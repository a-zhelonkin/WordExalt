package com.futurteam.wordexalt.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;
import com.futurteam.wordexalt.utils.CheckersUtils;

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
        paint.setAlpha(64);
        paint.setStrokeWidth(16);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.MAGENTA);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        if (node == null)
            return;

        paint.setStrokeWidth(16);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);

        Node previous = node;
        Node current = node.parent;
        while (current != null) {
            Point start = CheckersUtils.get(previous.x, previous.y);
            Point stop = CheckersUtils.get(current.x, current.y);

            canvas.drawLine(start.x, start.y, stop.x, stop.y, paint);

            previous = current;
            current = current.parent;

            if (current == null) {
                paint.setStrokeWidth(32);
                paint.setColor(Color.RED);
                canvas.drawPoint(stop.x, stop.y, paint);
                break;
            }
        }

        node = null;
    }

    public void setNode(@Nullable final Node node) {
        this.node = node;
        invalidate();
    }
}

package com.futurteam.wordexalt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.futurteam.wordexalt.components.WordsOverlayView;
import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;
import com.futurteam.wordexalt.logic.planners.Planner;
import com.futurteam.wordexalt.logic.planners.TreePlanner;
import com.futurteam.wordexalt.utils.CheckersUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class GlobalActionBarService extends AccessibilityService {

    private static final String TAG = GlobalActionBarService.class.getName();

    private TextView wordTextView;
    private WordsOverlayView overlayView;

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        final LayoutInflater inflater = LayoutInflater.from(this);
        final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        final Display.Mode mode = windowManager.getDefaultDisplay().getMode();
        final int screenWidth = mode.getPhysicalWidth();
        final int screenHeight = mode.getPhysicalHeight();
        CheckersUtils.init(screenWidth);

        final WindowManager.LayoutParams topLayoutParams = new WindowManager.LayoutParams();
        topLayoutParams.alpha = 0.75f;
        topLayoutParams.format = PixelFormat.TRANSLUCENT;
        topLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        topLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        topLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        topLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        topLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        final FrameLayout topRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_top, topRoot);
        windowManager.addView(topRoot, topLayoutParams);
        Button searchButton = topRoot.findViewById(R.id.search);
        wordTextView = topRoot.findViewById(R.id.word);
        Button nextButton = topRoot.findViewById(R.id.next);
        SeekBar overlaySlider = topRoot.findViewById(R.id.overlayGap);

        final WindowManager.LayoutParams overlayLayoutParams = new WindowManager.LayoutParams();
        overlayLayoutParams.alpha = 0.5f;
        overlayLayoutParams.format = PixelFormat.TRANSLUCENT;
        overlayLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        overlayLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        final FrameLayout overlayRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_overlay, overlayRoot);
        windowManager.addView(overlayRoot, overlayLayoutParams);
        overlayView = overlayRoot.findViewById(R.id.overlay);

        searchButton.setOnClickListener(view -> {
            final List<Point> initRoute = CheckersUtils.initRoute();
            swipe(initRoute, () -> {
                final Stack<Pair<String, Node>> words = extractWords();
                if (words == null)
                    return;

                swipe(words);
            }, null);
        });

        overlaySlider.setMax(Math.abs(screenHeight - screenWidth));
        overlaySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) overlayView.getLayoutParams();
                layoutParams.topMargin = progress;
                overlayView.setLayoutParams(layoutParams);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Nullable
    private Stack<Pair<String, Node>> extractWords() {
        CharSequence lettersText = null;
        final AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        final int childCount = rootInActiveWindow.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            final AccessibilityNodeInfo child = rootInActiveWindow.getChild(childIndex);
            final CharSequence childText = child.getText();
            if (childText == null)
                continue;

            Log.d(TAG, "Child text: " + childText);
            if (childText.length() == 25) {
                lettersText = childText;
            }
        }

        if (lettersText == null) {
            wordTextView.setText("❌");
            return null;
        }

        final String line = lettersText.toString().toLowerCase(Locale.ROOT);
        final Planner planner = new TreePlanner(line);
        planner.prepare();

        final Resources res = getResources();
        final String[] lib = res.getStringArray(R.array.words);

        final Stack<Pair<String, Node>> words = new Stack<>();
        for (final String word : lib) {
            final Node checked = planner.check(word);
            if (checked == null)
                continue;

            words.add(new Pair<>(word, checked));
        }

        return words;
    }

    private void swipe(@NonNull final Stack<Pair<String, Node>> words) {
        if (words.size() > 0) {
            final Pair<String, Node> pop = words.pop();
            wordTextView.setText(pop.first);
            overlayView.setNode(pop.second);
            swipe(pop.second, () -> swipe(words), words::clear);
        }
    }

    private void swipe(@NonNull final Node node,
                       @Nullable final Runnable onCompleted,
                       @Nullable final Runnable onCancelled) {
        final List<Point> route = new ArrayList<>();

        Node currentNode = node;
        while (currentNode != null) {
            final Point point = CheckersUtils.get(currentNode.x, currentNode.y);
            route.add(point);
            currentNode = currentNode.parent;
        }

        Collections.reverse(route);
        swipe(route, onCompleted, onCancelled);
    }

    private void swipe(@NonNull final List<Point> route,
                       @Nullable final Runnable onCompleted,
                       @Nullable final Runnable onCancelled) {
        final int yFix = ((ConstraintLayout.LayoutParams) overlayView.getLayoutParams()).topMargin;
        final GestureResultCallback gestureResultCallback = new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                if (onCompleted == null)
                    return;

                onCompleted.run();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                if (onCancelled == null)
                    return;

                onCancelled.run();
            }
        };

        Point previousPoint = route.get(0);
        Point currentPoint = route.get(1);

        final Path path = new Path();
        path.moveTo(previousPoint.x, previousPoint.y + yFix);
        path.lineTo(currentPoint.x, currentPoint.y + yFix);

        boolean willContinue = route.size() > 2;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 5, 75L, willContinue);
        builder.addStroke(stroke);
        dispatchGesture(builder.build(), willContinue ? null : gestureResultCallback, null);

        final int routeSize = route.size();
        final int lastIndex = routeSize - 1;
        for (int index = 1; index < lastIndex; index++) {
            final int nextIndex = index + 1;

            previousPoint = route.get(index);
            currentPoint = route.get(nextIndex);

            path.rewind();
            path.moveTo(previousPoint.x, previousPoint.y + yFix);
            path.lineTo(currentPoint.x, currentPoint.y + yFix);

            willContinue = nextIndex < lastIndex;
            builder = new GestureDescription.Builder();
            stroke = stroke.continueStroke(path, 5, 75L, willContinue);
            builder.addStroke(stroke);
            dispatchGesture(builder.build(), willContinue ? null : gestureResultCallback, null);
        }
    }
}
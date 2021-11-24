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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.futurteam.wordexalt.components.WordsOverlayView;
import com.futurteam.wordexalt.logic.Node;
import com.futurteam.wordexalt.logic.Point;
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

    private final Stack<Pair<String, Node>> words = new Stack<>();
    private TreePlanner planner = null;

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        LayoutInflater inflater = LayoutInflater.from(this);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display.Mode mode = windowManager.getDefaultDisplay().getMode();
        int screenWidth = mode.getPhysicalWidth();
        int screenHeight = mode.getPhysicalHeight();
        CheckersUtils.init(screenWidth);

        WindowManager.LayoutParams topLayoutParams = new WindowManager.LayoutParams();
        topLayoutParams.alpha = 0.75f;
        topLayoutParams.format = PixelFormat.TRANSLUCENT;
        topLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        topLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        topLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        topLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        topLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        FrameLayout topRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_top, topRoot);
        windowManager.addView(topRoot, topLayoutParams);
        Button searchButton = topRoot.findViewById(R.id.search);
        wordTextView = topRoot.findViewById(R.id.word);
        Button nextButton = topRoot.findViewById(R.id.next);
        SeekBar overlaySlider = topRoot.findViewById(R.id.overlayGap);

        WindowManager.LayoutParams overlayLayoutParams = new WindowManager.LayoutParams();
        overlayLayoutParams.alpha = 0.5f;
        overlayLayoutParams.format = PixelFormat.TRANSLUCENT;
        overlayLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        overlayLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        FrameLayout overlayRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_overlay, overlayRoot);
        windowManager.addView(overlayRoot, overlayLayoutParams);
        overlayView = overlayRoot.findViewById(R.id.overlay);

        searchButton.setOnClickListener(view -> {
            CharSequence lettersText = null;
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            int childCount = rootInActiveWindow.getChildCount();
            for (int childIndex = 0; childIndex < childCount; childIndex++) {
                AccessibilityNodeInfo child = rootInActiveWindow.getChild(childIndex);
                CharSequence childText = child.getText();
                if (childText == null)
                    continue;

                Log.d(TAG, "Child text: " + childText);
                if (childText.length() == 25) {
                    lettersText = childText;
                }
            }

            if (lettersText == null) {
                wordTextView.setText("❌");
                return;
            }

            words.clear();
            String line = lettersText.toString().toLowerCase(Locale.ROOT);
            planner = TreePlanner.fromLine(line);
            planner.prepare();

            Resources res = getResources();
            String[] lib = res.getStringArray(R.array.words);

            for (String word : lib) {
                Node checked = planner.Check(word);
                if (checked == null)
                    continue;

                words.add(new Pair<>(word, checked));
            }

            Log.d(TAG, "Found words: " + words.size());
            Pair<String, Node> pop = words.pop();
            wordTextView.setText(pop.first);
            overlayView.setNode(pop.second);
        });

        nextButton.setOnClickListener(view -> swipeAll());

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

    private void swipeAll() {
        if (words.size() > 0) {
            final Pair<String, Node> pop = words.pop();
            wordTextView.setText(pop.first);
            overlayView.setNode(pop.second);
            swipe(pop.second, this::swipeAll);
        }
    }

    private void swipe(@NonNull final Node node, @NonNull final Runnable onEnd) {
        final int yFix = ((ConstraintLayout.LayoutParams) overlayView.getLayoutParams()).topMargin;
        final List<Point> route = new ArrayList<>();

        final GestureResultCallback gestureResultCallback = new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                onEnd.run();
            }
        };

        Node currentNode = node;
        while (currentNode != null) {
            final Point point = CheckersUtils.get(currentNode.x, currentNode.y);
            route.add(point);
            currentNode = currentNode.parent;
        }

        Collections.reverse(route);

        Point previousPoint = route.get(0);
        Point currentPoint = route.get(1);

        final Path path = new Path();
        path.moveTo(previousPoint.x, previousPoint.y + yFix);
        path.lineTo(currentPoint.x, currentPoint.y + yFix);

        boolean willContinue = route.size() > 2;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 50L, willContinue);
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
            stroke = stroke.continueStroke(path, 0, 50L, willContinue);
            builder.addStroke(stroke);
            dispatchGesture(builder.build(), willContinue ? null : gestureResultCallback, null);
        }
    }
}
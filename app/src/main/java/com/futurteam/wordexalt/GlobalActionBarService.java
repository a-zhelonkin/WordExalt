package com.futurteam.wordexalt;

import android.accessibilityservice.AccessibilityService;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.Log;
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

import androidx.constraintlayout.widget.ConstraintLayout;

import com.futurteam.wordexalt.components.WordsOverlayView;
import com.futurteam.wordexalt.logic.planners.TreePlanner;

import java.util.Locale;
import java.util.Stack;

public class GlobalActionBarService extends AccessibilityService {

    private static final String TAG = GlobalActionBarService.class.getName();

    private final Stack<String> words = new Stack<>();
    private TreePlanner planner = null;

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        LayoutInflater inflater = LayoutInflater.from(this);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display.Mode mode = windowManager.getDefaultDisplay().getMode();
        int screenWidth = mode.getPhysicalWidth();
        int screenHeight = mode.getPhysicalHeight();

        WindowManager.LayoutParams topLayoutParams = new WindowManager.LayoutParams();
        topLayoutParams.alpha = 0.5f;
        topLayoutParams.format = PixelFormat.TRANSLUCENT;
        topLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        topLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        topLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        topLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        topLayoutParams.gravity = Gravity.TOP;
        FrameLayout topRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_top, topRoot);
        windowManager.addView(topRoot, topLayoutParams);
        Button searchButton = topRoot.findViewById(R.id.search);
        TextView wordTextView = topRoot.findViewById(R.id.word);
        Button nextButton = topRoot.findViewById(R.id.next);
        SeekBar overlaySlider = topRoot.findViewById(R.id.overlayGap);

        WindowManager.LayoutParams overlayLayoutParams = new WindowManager.LayoutParams();
        overlayLayoutParams.alpha = 0.5f;
        overlayLayoutParams.format = PixelFormat.TRANSLUCENT;
        overlayLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        overlayLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        overlayLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        FrameLayout overlayRoot = new FrameLayout(this);
        inflater.inflate(R.layout.layout_overlay, overlayRoot);
        windowManager.addView(overlayRoot, overlayLayoutParams);
        WordsOverlayView overlayView = overlayRoot.findViewById(R.id.overlay);

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

            planner = TreePlanner.fromLine(lettersText.toString().toLowerCase(Locale.ROOT));
            planner.prepare();

            Resources res = getResources();
            String[] lib = res.getStringArray(R.array.words);

            for (String word : lib) {
                if (planner.Check(word)) {
                    words.add(word);
                }
            }

            Log.d(TAG, "Found words: " + words.size());
            wordTextView.setText(words.pop());
        });

        nextButton.setOnClickListener(view -> {
            if (words.isEmpty())
                return;

            wordTextView.setText(words.pop());
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
}
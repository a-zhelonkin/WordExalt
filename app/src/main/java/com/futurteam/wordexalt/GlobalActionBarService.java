package com.futurteam.wordexalt;

import android.accessibilityservice.AccessibilityService;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.futurteam.wordexalt.logic.Planner;

import java.util.Locale;
import java.util.Stack;

public class GlobalActionBarService extends AccessibilityService {

    private static final String TAG = GlobalActionBarService.class.getName();

    private final Stack<String> words = new Stack<>();
    private Planner planner = null;

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        FrameLayout mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);


        Button searchButton = mLayout.findViewById(R.id.search);
        TextView wordTextView = mLayout.findViewById(R.id.word);
        Button nextButton = mLayout.findViewById(R.id.next);

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

            planner = Planner.fromLine(lettersText.toString().toLowerCase(Locale.ROOT));
            planner.Prepare();

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
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
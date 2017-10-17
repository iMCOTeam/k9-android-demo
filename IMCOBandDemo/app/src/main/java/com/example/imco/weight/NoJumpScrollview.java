package com.example.imco.weight;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

/**
 * Created by mai on 17-10-16.
 */

public class NoJumpScrollview extends ScrollView {

    public NoJumpScrollview(Context context) {
        super(context, null);
    }
    public NoJumpScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        return child instanceof EditText;
    }
}

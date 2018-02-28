package de.slgdev.messenger.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.util.AttributeSet;

public class MessageTextView extends AppCompatTextView {
    public MessageTextView(Context context) {
        super(context);
    }

    public MessageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        Layout layout = getLayout();
        if (layout != null) {
            int lineWidth = (int) Math.ceil(getMaxLineWidth(layout));
            int maxWidth = lineWidth + getCompoundPaddingLeft() + getCompoundPaddingRight();

            widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public float getMaxLineWidth(Layout layout) {
        float maxWidth = 0.0f;

        for (int i = 0; i < layout.getLineCount(); i++) {
            if (layout.getLineWidth(i) > maxWidth) {
                maxWidth = layout.getLineWidth(i);
            }
        }

        return maxWidth;
    }
}
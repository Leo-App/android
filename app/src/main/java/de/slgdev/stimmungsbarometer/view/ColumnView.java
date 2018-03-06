package de.slgdev.stimmungsbarometer.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import de.slgdev.stimmungsbarometer.utility.Ergebnis;

public class ColumnView extends StatistikView {
    private Ergebnis[] data;
    private float      columnWidth;

    public ColumnView(Context context) {
        super(context);
    }

    public ColumnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColumnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void init() {
        super.init();

        baseLineX = width / 24;
        abstandX = width / 12;
        columnWidth = width / 6;
    }

    @Override
    void redraw() {
        if (data != null) {
            Paint p = new Paint();
            if (data[0].value >= 1 && data[0].value <= 5) {
                canvasIch.drawRect(
                        baseLineX,
                        (float) (baseLineY - (5 - data[0].value) * abstandY),
                        baseLineX + columnWidth,
                        baseLineY - 1,
                        p
                );
            }

            if (data[1].value >= 1 && data[1].value <= 5) {
                canvasSchueler.drawRect(
                        baseLineX + columnWidth + abstandX,
                        (float) (baseLineY - (5 - data[1].value) * abstandY),
                        baseLineX + columnWidth + abstandX + columnWidth,
                        baseLineY - 1,
                        p
                );
            }

            if (data[2].value >= 1 && data[2].value <= 5) {
                canvasLehrer.drawRect(
                        baseLineX + (columnWidth + abstandX) * 2,
                        (float) (baseLineY - (5 - data[2].value) * abstandY),
                        baseLineX + (columnWidth + abstandX) * 2 + columnWidth,
                        baseLineY - 1,
                        p
                );
            }

            if (data[3].value >= 1 && data[3].value <= 5) {
                canvasAlle.drawRect(
                        baseLineX + (columnWidth + abstandX) * 3,
                        (float) (baseLineY - (5 - data[3].value) * abstandY),
                        baseLineX + (columnWidth + abstandX) * 3 + columnWidth,
                        baseLineY - 1,
                        p
                );
            }
        }
    }

    public void setData(Ergebnis[] data) {
        this.data = data;
        recreateCharts = true;
    }
}
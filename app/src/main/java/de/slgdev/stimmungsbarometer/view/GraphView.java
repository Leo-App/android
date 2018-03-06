package de.slgdev.stimmungsbarometer.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import de.slgdev.stimmungsbarometer.utility.Ergebnis;

public class GraphView extends StatistikView {
    private Ergebnis[][] data;
    private float        radius;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void init() {
        super.init();

        baseLineX = width / 20;
        radius = 8;
    }

    @Override
    void redraw() {
        if (data != null) {
            if (data[3].length > 1) {
                abstandX = width * 9 / ((data[3].length - 1) * 10);
            } else {
                abstandX = width * 9 / 10;
            }

            for (int i = 0; i < data[3].length; i++) {
                Paint p = new Paint();
                if (i < data[0].length && data[0][i].value > 0) {
                    //p.setColor(colorIch);
                    canvasIch.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[0][i].value) * abstandY), radius, p);
                }
                if (i < data[1].length && data[1][i].value > 0) {
                    //p.setColor(colorSchueler);
                    canvasSchueler.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[1][i].value) * abstandY), radius, p);
                }
                if (i < data[2].length && data[2][i].value > 0) {
                    //p.setColor(colorLehrer);
                    canvasLehrer.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[2][i].value) * abstandY), radius, p);
                }
                if (i < data[3].length && data[3][i].value > 0) {
                    //p.setColor(colorAlle);
                    canvasAlle.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[3][i].value) * abstandY), radius, p);
                }
            }

            int previousIch      = 0;
            int previousSchueler = 0;
            int previousLehrer   = 0;
            int previousAlle     = 0;
            for (int i = 1; i < data[3].length; i++) {
                Paint p = new Paint();
                p.setStrokeWidth(radius);
                if (i < data[0].length && data[0][i].value > 0) {
                    if (data[0][previousIch].value > 0) {
                        //p.setColor(colorIch);
                        canvasIch.drawLine(width - (baseLineX + previousIch * abstandX), (float) (baseLineY - (5 - data[0][previousIch].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[0][i].value) * abstandY), p);
                    }
                    previousIch = i;
                }
                if (i < data[1].length && data[1][i].value > 0) {
                    if (data[1][previousSchueler].value > 0) {
                        //p.setColor(colorSchueler);
                        canvasSchueler.drawLine(width - (baseLineX + previousSchueler * abstandX), (float) (baseLineY - (5 - data[1][previousSchueler].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[1][i].value) * abstandY), p);
                    }
                    previousSchueler = i;
                }
                if (i < data[2].length && data[2][i].value > 0) {
                    if (data[2][previousLehrer].value > 0) {
                        //p.setColor(colorLehrer);
                        canvasLehrer.drawLine(width - (baseLineX + previousLehrer * abstandX), (float) (baseLineY - (5 - data[2][previousLehrer].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[2][i].value) * abstandY), p);
                    }
                    previousLehrer = i;
                }
                if (i < data[3].length && data[3][i].value > 0) {
                    if (data[3][previousAlle].value > 0) {
                        //p.setColor(colorAlle);
                        canvasAlle.drawLine(width - (baseLineX + previousAlle * abstandX), (float) (baseLineY - (5 - data[3][previousAlle].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[3][i].value) * abstandY), p);
                    }
                    previousAlle = i;
                }
            }
        }
    }

    public void setData(Ergebnis[][] data) {
        this.data = data;
        recreateCharts = true;
    }
}

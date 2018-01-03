package de.slg.stimmungsbarometer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.slg.leoapp.R;
import de.slg.stimmungsbarometer.utility.Ergebnis;

import static de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity.drawA;
import static de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity.drawI;
import static de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity.drawL;
import static de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity.drawS;

public class StatistikView extends View {
    public static Bitmap       bitmapBack;
    private       int          height;
    private       int          width;
    private       Ergebnis[][] data;
    private       Bitmap       bitmapIch, bitmapSchueler, bitmapLehrer, bitmapAlle;
    private final Canvas canvasBack;
    private final Canvas canvasIch, canvasSchueler, canvasLehrer, canvasAlle;
    private final Paint paint;
    boolean recreateCharts;
    private boolean isInitialized;
    private float   baseLineY, baseLineX, abstandX, abstandY, radius;

    public StatistikView(Context context) {
        super(context);
        canvasBack = new Canvas();
        canvasIch = new Canvas();
        canvasSchueler = new Canvas();
        canvasLehrer = new Canvas();
        canvasAlle = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            init();
        if (recreateCharts)
            createCharts();
        canvas.drawBitmap(bitmapBack, 0, 0, paint);
        if (drawI)
            canvas.drawBitmap(bitmapIch, 0, 0, paint);
        if (drawS)
            canvas.drawBitmap(bitmapSchueler, 0, 0, paint);
        if (drawL)
            canvas.drawBitmap(bitmapLehrer, 0, 0, paint);
        if (drawA)
            canvas.drawBitmap(bitmapAlle, 0, 0, paint);
    }

    private void init() {
        height = getHeight();
        width = getWidth();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        baseLineY = height * 99 / 100;
        abstandY = baseLineY * 99 / 400;
        baseLineX = width / 20;
        radius = 8;
        if (bitmapIch == null || bitmapSchueler == null || bitmapLehrer == null || bitmapAlle == null) {
            createCharts();
        }
        if (bitmapBack == null) {
            bitmapBack = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            canvasBack.setBitmap(bitmapBack);
            drawBackground();
        }
        isInitialized = true;
    }

    private void createCharts() {
        if (data[3].length > 1)
            abstandX = width * 9 / ((data[3].length - 1) * 10);
        else
            abstandX = width * 9 / 10;
        bitmapIch = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasIch.setBitmap(bitmapIch);
        bitmapSchueler = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasSchueler.setBitmap(bitmapSchueler);
        bitmapLehrer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasLehrer.setBitmap(bitmapLehrer);
        bitmapAlle = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasAlle.setBitmap(bitmapAlle);
        if (data == null) {
            recreateCharts = true;
        } else {
            drawPoints();
            drawGraphs();
            recreateCharts = false;
        }
    }

    private void drawBackground() {
        canvasBack.drawColor(ContextCompat.getColor(getContext(), android.R.color.background_light));
        Paint p1 = new Paint();
        p1.setStrokeWidth(3);
        p1.setColor(ContextCompat.getColor(getContext(), R.color.colorBadMood));
        canvasBack.drawLine(0, baseLineY, width, baseLineY, p1);
        Paint p2 = new Paint();
        p2.setStrokeWidth(3);
        p2.setColor(ContextCompat.getColor(getContext(), R.color.colorDissatisfied));
        canvasBack.drawLine(0, baseLineY - abstandY, width, baseLineY - abstandY, p2);
        Paint p3 = new Paint();
        p3.setStrokeWidth(3);
        p3.setColor(ContextCompat.getColor(getContext(), R.color.colorNeutral));
        canvasBack.drawLine(0, baseLineY - (2 * abstandY), width, baseLineY - (2 * abstandY), p3);
        Paint p4 = new Paint();
        p4.setStrokeWidth(3);
        p4.setColor(ContextCompat.getColor(getContext(), R.color.colorSatisfied));
        canvasBack.drawLine(0, baseLineY - (3 * abstandY), width, baseLineY - (3 * abstandY), p4);
        Paint p5 = new Paint();
        p5.setStrokeWidth(3);
        p5.setColor(ContextCompat.getColor(getContext(), R.color.colorVerySatisfied));
        canvasBack.drawLine(0, baseLineY - (4 * abstandY), width, baseLineY - (4 * abstandY), p5);
    }

    private void drawPoints() {
        for (int i = 0; i < data[3].length; i++) {
            Paint p = new Paint();
            if (i < data[0].length && data[0][i].value > 0) {
                p.setColor(ContextCompat.getColor(getContext(), R.color.colorIch));
                canvasIch.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[0][i].value) * abstandY), radius, p);
            }
            if (i < data[1].length && data[1][i].value > 0) {
                p.setColor(ContextCompat.getColor(getContext(), R.color.colorSchueler));
                canvasSchueler.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[1][i].value) * abstandY), radius, p);
            }
            if (i < data[2].length && data[2][i].value > 0) {
                p.setColor(ContextCompat.getColor(getContext(), R.color.colorLehrer));
                canvasLehrer.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[2][i].value) * abstandY), radius, p);
            }
            if (i < data[3].length && data[3][i].value > 0) {
                p.setColor(ContextCompat.getColor(getContext(), R.color.colorAlle));
                canvasAlle.drawCircle(width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[3][i].value) * abstandY), radius, p);
            }
        }
    }

    private void drawGraphs() {
        int previousIch      = 0;
        int previousSchueler = 0;
        int previousLehrer   = 0;
        int previousAlle     = 0;
        for (int i = 1; i < data[3].length; i++) {
            Paint p = new Paint();
            p.setStrokeWidth(radius);
            if (i < data[0].length && data[0][i].value > 0) {
                if (data[0][previousIch].value > 0) {
                    p.setColor(ContextCompat.getColor(getContext(), R.color.colorIch));
                    canvasIch.drawLine(width - (baseLineX + previousIch * abstandX), (float) (baseLineY - (5 - data[0][previousIch].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[0][i].value) * abstandY), p);
                }
                previousIch = i;
            }
            if (i < data[1].length && data[1][i].value > 0) {
                if (data[1][previousSchueler].value > 0) {
                    p.setColor(ContextCompat.getColor(getContext(), R.color.colorSchueler));
                    canvasSchueler.drawLine(width - (baseLineX + previousSchueler * abstandX), (float) (baseLineY - (5 - data[1][previousSchueler].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[1][i].value) * abstandY), p);
                }
                previousSchueler = i;
            }
            if (i < data[2].length && data[2][i].value > 0) {
                if (data[2][previousLehrer].value > 0) {
                    p.setColor(ContextCompat.getColor(getContext(), R.color.colorLehrer));
                    canvasLehrer.drawLine(width - (baseLineX + previousLehrer * abstandX), (float) (baseLineY - (5 - data[2][previousLehrer].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[2][i].value) * abstandY), p);
                }
                previousLehrer = i;
            }
            if (i < data[3].length && data[3][i].value > 0) {
                if (data[3][previousAlle].value > 0) {
                    p.setColor(ContextCompat.getColor(getContext(), R.color.colorAlle));
                    canvasAlle.drawLine(width - (baseLineX + previousAlle * abstandX), (float) (baseLineY - (5 - data[3][previousAlle].value) * abstandY), width - (baseLineX + i * abstandX), (float) (baseLineY - (5 - data[3][i].value) * abstandY), p);
                }
                previousAlle = i;
            }
        }
    }

    public void setData(Ergebnis[][] data) {
        this.data = data;
    }
}

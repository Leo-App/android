package de.slg.stimmungsbarometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.View;

import de.slg.leoapp.R;

public class StatistikView extends View {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private boolean isInitialized;
    private Paint paint;
    private Ergebnis[][] data;
    private int baseLineY, baseLineX, abstandX, abstandY, radius;

    private boolean ich;
    private boolean schueler;
    private boolean lehrer;
    private boolean alle;

    public StatistikView(Context context, Ergebnis[][] data) {
        super(context);
        this.data = data;
        bitmapCanvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    private void init() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        bitmapCanvas.setBitmap(bitmap);

        paint.setColor(ContextCompat.getColor(getContext(), android.R.color.background_light));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        baseLineY = bitmapCanvas.getHeight() * 99 / 100;
        abstandY = baseLineY * 9 / 40;
        baseLineX = bitmapCanvas.getWidth() / 20;
        if (data[3].length != 0)
            abstandX = bitmapCanvas.getWidth() * 9 / (data[3].length * 10);
        else
            abstandX = bitmapCanvas.getWidth() * 9 / 10;
        radius = 5;

        isInitialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            init();
        ich = StimmungsbarometerActivity.drawIch;
        schueler = StimmungsbarometerActivity.drawSchueler;
        lehrer = StimmungsbarometerActivity.drawLehrer;
        alle = StimmungsbarometerActivity.drawAlle;
        drawBackground();
        drawPoints();
        drawGraph();
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawBackground() {
        bitmapCanvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.background_light));

        Paint p1 = new Paint();
        p1.setStrokeWidth(3);
        p1.setColor(ContextCompat.getColor(getContext(), R.color.colorBadMood));
        bitmapCanvas.drawLine(0, baseLineY, getWidth(), baseLineY, p1);

        Paint p2 = new Paint();
        p2.setStrokeWidth(3);
        p2.setColor(ContextCompat.getColor(getContext(), R.color.colorDissatisfied));
        bitmapCanvas.drawLine(0, baseLineY - abstandY, getWidth(), baseLineY - abstandY, p2);

        Paint p3 = new Paint();
        p3.setStrokeWidth(3);
        p3.setColor(ContextCompat.getColor(getContext(), R.color.colorNeutral));
        bitmapCanvas.drawLine(0, baseLineY - (2 * abstandY), getWidth(), baseLineY - (2 * abstandY), p3);

        Paint p4 = new Paint();
        p4.setStrokeWidth(3);
        p4.setColor(ContextCompat.getColor(getContext(), R.color.colorSatisfied));
        bitmapCanvas.drawLine(0, baseLineY - (3 * abstandY), getWidth(), baseLineY - (3 * abstandY), p4);

        Paint p5 = new Paint();
        p5.setStrokeWidth(3);
        p5.setColor(ContextCompat.getColor(getContext(), R.color.colorVerySatisfied));
        bitmapCanvas.drawLine(0, baseLineY - (4 * abstandY), getWidth(), baseLineY - (4 * abstandY), p5);
    }

    private void drawPoints() {
        for (Ergebnis[] current : data) {
            for (int i = 0; i < current.length; i++) {
                if (current[i].value != -1) {
                    Paint p = new Paint();
                    if (current[i].ich)
                        if (this.ich)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorIch));
                        else
                            break;
                    else if (current[i].schueler)
                        if (this.schueler)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorSchueler));
                        else
                            break;
                    else if (current[i].lehrer)
                        if (this.lehrer)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorLehrer));
                        else
                            break;
                    else if (current[i].alle)
                        if (this.alle)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorAlle));
                        else
                            break;
                    bitmapCanvas.drawCircle(bitmapCanvas.getWidth() - (baseLineX + i * abstandX), (float) (baseLineY - (5 - current[i].value) * abstandY), radius, p);
                }
            }
        }
    }

    private void drawGraph() {
        for (Ergebnis[] current : data) {
            int previous = 0;
            for (int i = 0; i < current.length; i++) {
                if (current[i].value != -1) {
                    Paint p = new Paint();
                    p.setStrokeWidth(3);
                    if (current[i].ich)
                        if (this.ich)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorIch));
                        else
                            break;
                    else if (current[i].schueler)
                        if (this.schueler)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorSchueler));
                        else
                            break;
                    else if (current[i].lehrer)
                        if (this.lehrer)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorLehrer));
                        else
                            break;
                    else if (current[i].alle)
                        if (this.alle)
                            p.setColor(ContextCompat.getColor(getContext(), R.color.colorAlle));
                        else
                            break;
                    if (i != previous) {
                        bitmapCanvas.drawLine(bitmapCanvas.getWidth() - (baseLineX + previous * abstandX), (float) (baseLineY - (5 - current[previous].value) * abstandY), bitmapCanvas.getWidth() - (baseLineX + i * abstandX), (float) (baseLineY - (5 - current[i].value) * abstandY), p);
                    }
                    previous = i;
                }
            }
        }
    }
}
package de.slg.stimmungsbarometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import de.slg.leoapp.R;

public class StatistikView extends View {

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private boolean isInitialized;
    private Paint paint;
    private Ergebnis[][] data;
    private int baseLineY, baseLineX, abstandX, abstandY, radius;

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
        bitmapCanvas.drawColor(getResources().getColor(android.R.color.background_light));

        paint.setColor(Color.WHITE);
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

        drawBackground();
        drawPoints();
        drawGraph();

        isInitialized = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            init();
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawBackground() {
        Paint p1 = new Paint();
        p1.setStrokeWidth(3);
        p1.setColor(getResources().getColor(R.color.colorBadMood));
        bitmapCanvas.drawLine(0, baseLineY, bitmapCanvas.getWidth(), baseLineY, p1);

        Paint p2 = new Paint();
        p2.setStrokeWidth(3);
        p2.setColor(getResources().getColor(R.color.colorDissatisfied));
        bitmapCanvas.drawLine(0, baseLineY - abstandY, bitmapCanvas.getWidth(), baseLineY - abstandY, p2);

        Paint p3 = new Paint();
        p3.setStrokeWidth(3);
        p3.setColor(getResources().getColor(R.color.colorNeutral));
        bitmapCanvas.drawLine(0, baseLineY - (2 * abstandY), bitmapCanvas.getWidth(), baseLineY - (2 * abstandY), p3);

        Paint p4 = new Paint();
        p4.setStrokeWidth(3);
        p4.setColor(getResources().getColor(R.color.colorSatisfied));
        bitmapCanvas.drawLine(0, baseLineY - (3 * abstandY), bitmapCanvas.getWidth(), baseLineY - (3 * abstandY), p4);

        Paint p5 = new Paint();
        p5.setStrokeWidth(3);
        p5.setColor(getResources().getColor(R.color.colorVerySatisfied));
        bitmapCanvas.drawLine(0, baseLineY - (4 * abstandY), bitmapCanvas.getWidth(), baseLineY - (4 * abstandY), p5);
    }

    private void drawPoints() {
        for (Ergebnis[] current : data) {
            for (int i = 0; i < current.length; i++) {
                if (current[i].value != -1) {
                    Paint p = new Paint();
                    if (current[i].ich)
                        p.setColor(getResources().getColor(R.color.colorIch));
                    else if (current[i].schueler)
                        p.setColor(getResources().getColor(R.color.colorSchueler));
                    else if (current[i].lehrer)
                        p.setColor(getResources().getColor(R.color.colorLehrer));
                    else if (current[i].alle)
                        p.setColor(getResources().getColor(R.color.colorAlle));
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
                        p.setColor(getResources().getColor(R.color.colorIch));
                    else if (current[i].schueler)
                        p.setColor(getResources().getColor(R.color.colorSchueler));
                    else if (current[i].lehrer)
                        p.setColor(getResources().getColor(R.color.colorLehrer));
                    else if (current[i].alle)
                        p.setColor(getResources().getColor(R.color.colorAlle));
                    if (i != previous) {
                        bitmapCanvas.drawLine(bitmapCanvas.getWidth() - (baseLineX + previous * abstandX), (float) (baseLineY - (5 - current[previous].value) * abstandY), bitmapCanvas.getWidth() - (baseLineX + i * abstandX), (float) (baseLineY - (5 - current[i].value) * abstandY), p);
                    }
                    previous = i;
                }
            }
        }
    }
}
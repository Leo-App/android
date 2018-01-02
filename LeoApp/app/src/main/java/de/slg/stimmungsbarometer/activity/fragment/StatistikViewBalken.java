package de.slg.stimmungsbarometer.activity.fragment;

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
import static de.slg.stimmungsbarometer.activity.fragment.StatistikView.bitmapBack;

public class StatistikViewBalken extends View {
    private Ergebnis[] data;
    private int        height;
    private int        width;
    private Bitmap     bitmapIch, bitmapSchueler, bitmapLehrer, bitmapAlle;
    private final Canvas canvasBack;
    private final Canvas canvasIch, canvasSchueler, canvasLehrer, canvasAlle;
    private final Paint paint;
    boolean recreateCharts;
    private boolean isInitialized;
    private float   baseLineY, baseLineX, abstandX, abstandY, breite;

    public StatistikViewBalken(Context context) {
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

    public void setData(Ergebnis[] data) {
        this.data = data;
    }

    private void init() {
        height = getHeight();
        width = getWidth();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        baseLineY = height * 99 / 100;
        abstandY = baseLineY * 9 / 40;
        baseLineX = width / 24;
        abstandX = width / 12;
        breite = width / 6;
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
        bitmapIch = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasIch.setBitmap(bitmapIch);
        bitmapSchueler = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasSchueler.setBitmap(bitmapSchueler);
        bitmapLehrer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasLehrer.setBitmap(bitmapLehrer);
        bitmapAlle = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvasAlle.setBitmap(bitmapAlle);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorIch));
        canvasIch.drawRect(baseLineX, (float) (baseLineY - data[0].value * abstandY), baseLineX + breite, baseLineY, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorSchueler));
        canvasSchueler.drawRect(baseLineX + breite + abstandX, (float) (baseLineY - data[1].value * abstandY), baseLineX + breite + abstandX + breite, baseLineY, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorLehrer));
        canvasLehrer.drawRect(baseLineX + (breite + abstandX) * 2, (float) (baseLineY - data[2].value * abstandY), baseLineX + (breite + abstandX) * 2 + breite, baseLineY, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAlle));
        canvasAlle.drawRect(baseLineX + (breite + abstandX) * 3, (float) (baseLineY - data[3].value * abstandY), baseLineX + (breite + abstandX) * 3 + breite, baseLineY, paint);
        recreateCharts = false;
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
}
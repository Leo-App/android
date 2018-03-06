package de.slgdev.stimmungsbarometer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.slgdev.leoapp.R;

import static de.slgdev.stimmungsbarometer.activity.StimmungsbarometerActivity.drawA;
import static de.slgdev.stimmungsbarometer.activity.StimmungsbarometerActivity.drawI;
import static de.slgdev.stimmungsbarometer.activity.StimmungsbarometerActivity.drawL;
import static de.slgdev.stimmungsbarometer.activity.StimmungsbarometerActivity.drawS;

public abstract class StatistikView extends View {
    private static Bitmap bitmapBack;

    private boolean isInitialized;
    boolean recreateCharts;

    private Bitmap bitmapIch;
    private Bitmap bitmapSchueler;
    private Bitmap bitmapLehrer;
    private Bitmap bitmapAlle;

    final   Canvas canvasIch;
    final   Canvas canvasSchueler;
    final   Canvas canvasLehrer;
    final   Canvas canvasAlle;

    private final PorterDuffColorFilter filterIch;
    private final PorterDuffColorFilter filterSchueler;
    private final PorterDuffColorFilter filterLehrer;
    private final PorterDuffColorFilter filterAlle;

    final Paint paint;

    int   height;
    int   width;

    float baseLineX;
    float abstandX;

    float baseLineY;
    float abstandY;

    public StatistikView(Context context) {
        super(context);
    }

    public StatistikView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatistikView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        canvasIch = new Canvas();
        canvasSchueler = new Canvas();
        canvasLehrer = new Canvas();
        canvasAlle = new Canvas();

        paint = new Paint();

        filterIch = new PorterDuffColorFilter(
                ContextCompat.getColor(
                        getContext(),
                        R.color.colorIch
                ),
                PorterDuff.Mode.SRC_ATOP
        );
        filterSchueler = new PorterDuffColorFilter(
                ContextCompat.getColor(
                        getContext(),
                        R.color.colorSchueler
                ),
                PorterDuff.Mode.SRC_ATOP
        );
        filterLehrer = new PorterDuffColorFilter(
                ContextCompat.getColor(
                        getContext(),
                        R.color.colorLehrer
                ),
                PorterDuff.Mode.SRC_ATOP
        );
        filterAlle = new PorterDuffColorFilter(
                ContextCompat.getColor(
                        getContext(),
                        R.color.colorAlle
                ),
                PorterDuff.Mode.SRC_ATOP
        );

        isInitialized = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized) {
            init();
        }
        if (recreateCharts) {
            createCharts();
        }

        paint.setColorFilter(null);
        canvas.drawBitmap(bitmapBack, 0, 0, paint);

        if (drawI) {
            paint.setColorFilter(filterIch);
            canvas.drawBitmap(bitmapIch, 0, 0, paint);
        }
        if (drawS) {
            paint.setColorFilter(filterSchueler);
            canvas.drawBitmap(bitmapSchueler, 0, 0, paint);
        }
        if (drawL) {
            paint.setColorFilter(filterLehrer);
            canvas.drawBitmap(bitmapLehrer, 0, 0, paint);
        }
        if (drawA) {
            paint.setColorFilter(filterAlle);
            canvas.drawBitmap(bitmapAlle, 0, 0, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getSize(widthMeasureSpec) / 8 * 7,
                        MeasureSpec.AT_MOST
                )
        );
    }

    @CallSuper
    void init() {
        height = getHeight();
        width = getWidth();

        baseLineY = height - 10;
        abstandY = (height - 20) / 4;

        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        drawBackground();

        isInitialized = true;
    }

    private void drawBackground() {
        if (bitmapBack == null) {
            bitmapBack = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.RGB_565
            );

            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmapBack);

            canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.background_light));

            Paint p1 = new Paint();
            p1.setStrokeWidth(3);
            p1.setColor(ContextCompat.getColor(getContext(), R.color.colorBadMood));
            canvas.drawLine(0, baseLineY, width, baseLineY, p1);

            Paint p2 = new Paint();
            p2.setStrokeWidth(3);
            p2.setColor(ContextCompat.getColor(getContext(), R.color.colorDissatisfied));
            canvas.drawLine(0, baseLineY - abstandY, width, baseLineY - abstandY, p2);

            Paint p3 = new Paint();
            p3.setStrokeWidth(3);
            p3.setColor(ContextCompat.getColor(getContext(), R.color.colorNeutral));
            canvas.drawLine(0, baseLineY - (2 * abstandY), width, baseLineY - (2 * abstandY), p3);

            Paint p4 = new Paint();
            p4.setStrokeWidth(3);
            p4.setColor(ContextCompat.getColor(getContext(), R.color.colorSatisfied));
            canvas.drawLine(0, baseLineY - (3 * abstandY), width, baseLineY - (3 * abstandY), p4);

            Paint p5 = new Paint();
            p5.setStrokeWidth(3);
            p5.setColor(ContextCompat.getColor(getContext(), R.color.colorVerySatisfied));
            canvas.drawLine(0, baseLineY - (4 * abstandY), width, baseLineY - (4 * abstandY), p5);
        }
    }

    private void createCharts() {
        if (height != 0 && width != 0) {
            if (bitmapIch == null) {
                bitmapIch = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ALPHA_8
                );
                canvasIch.setBitmap(bitmapIch);
            } else {
                bitmapIch.eraseColor(0);
            }

            if (bitmapSchueler == null) {
                bitmapSchueler = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ALPHA_8
                );
                canvasSchueler.setBitmap(bitmapSchueler);
            } else {
                bitmapSchueler.eraseColor(0);
            }

            if (bitmapLehrer == null) {
                bitmapLehrer = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ALPHA_8
                );
                canvasLehrer.setBitmap(bitmapLehrer);
            } else {
                bitmapLehrer.eraseColor(0);
            }

            if (bitmapAlle == null) {
                bitmapAlle = Bitmap.createBitmap(
                        width,
                        height,
                        Bitmap.Config.ALPHA_8
                );
                canvasAlle.setBitmap(bitmapAlle);
            } else {
                bitmapAlle.eraseColor(0);
            }

            redraw();

            recreateCharts = false;
        }
    }

    abstract void redraw();
}

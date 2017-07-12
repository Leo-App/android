package de.slg.stundenplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.slg.leoapp.R;

public class StundenplanView extends View {

    private final Context context;
    private Bitmap bitmap;
    private final Canvas bitmapCanvas;
    private boolean isInitialized;
    private final Paint paint;
    private int baseLineY, baseLineX, abstandX, abstandY, paddingX, paddingY, baseline2Y;

    public StundenplanView(Context context) {
        super(context);
        this.context = context;
        bitmapCanvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    public StundenplanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        bitmapCanvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    public StundenplanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        bitmapCanvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    private void init() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        bitmapCanvas.setBitmap(bitmap);
        bitmapCanvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.background_light));

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        baseLineY = bitmapCanvas.getHeight() / 10;
        paddingY = bitmapCanvas.getHeight() / 100;
        baseline2Y = baseLineY + 3 * paddingY;
        abstandY = (bitmapCanvas.getHeight() - baseline2Y - baseLineY) / 10;
        baseLineX = bitmapCanvas.getWidth() / 20;
        abstandX = (bitmapCanvas.getWidth() - baseLineX * 2) / 5;
        paddingX = bitmapCanvas.getWidth() / 100;

        drawBackground();

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
        bitmapCanvas.drawLine(baseLineX, baseLineY, bitmapCanvas.getWidth() - baseLineX, baseLineY, p1);
        bitmapCanvas.drawLine(baseLineX, bitmapCanvas.getHeight() - baseLineY, bitmapCanvas.getWidth() - baseLineX, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawLine(baseLineX, baseLineY, baseLineX, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawLine(bitmapCanvas.getWidth() - baseLineX, baseLineY, bitmapCanvas.getWidth() - baseLineX, bitmapCanvas.getHeight() - baseLineY, p1);
        p1.setTextSize(2 * paddingY);
        bitmapCanvas.drawText(context.getString(R.string.montag), baseLineX + paddingX, baseLineY + paddingY * 2, p1);
        bitmapCanvas.drawLine(baseLineX + abstandX, baseLineY, baseLineX + abstandX, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawText(context.getString(R.string.dienstag), baseLineX + abstandX + paddingX, baseLineY + paddingY * 2, p1);
        bitmapCanvas.drawLine(baseLineX + abstandX * 2, baseLineY, baseLineX + abstandX * 2, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawText(context.getString(R.string.mittwoch), baseLineX + abstandX * 2 + paddingX, baseLineY + paddingY * 2, p1);
        bitmapCanvas.drawLine(baseLineX + abstandX * 3, baseLineY, baseLineX + abstandX * 3, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawText(context.getString(R.string.donnerstag), baseLineX + abstandX * 3 + paddingX, baseLineY + paddingY * 2, p1);
        bitmapCanvas.drawLine(baseLineX + abstandX * 4, baseLineY, baseLineX + abstandX * 4, bitmapCanvas.getHeight() - baseLineY, p1);
        bitmapCanvas.drawText(context.getString(R.string.freitag), baseLineX + abstandX * 4 + paddingX, baseLineY + paddingY * 2, p1);
        bitmapCanvas.drawLine(baseLineX, baseline2Y, bitmapCanvas.getWidth() - baseLineX, baseline2Y, p1);
        Stundenplanverwalter sV = new Stundenplanverwalter(getContext(), "meinefaecher.txt");
        for (int i = 1; i < 10; i++) {
            int yValue = baseline2Y + (i - 1) * abstandY;
            Fach[] f = sV.gibFacherSortStunde(i);
            String[] namen = new String[5];
            for (Fach aF : f) {
                namen[Integer.parseInt(aF.gibTag()) - 1] = aF.gibName().split(" ")[0];
                if (aF.gibName().equals("") && !aF.gibNotiz().equals("notiz")) {
                    namen[Integer.parseInt(aF.gibTag()) - 1] = aF.gibNotiz().split(" ")[0];
                }
            }
            for (int m = 0; m < namen.length; m++) {
                if (namen[m] != null) {
                    bitmapCanvas.drawText(namen[m], baseLineX + abstandX * m + paddingX, yValue + paddingY * 2, p1);
                }
            }
            bitmapCanvas.drawLine(baseLineX, yValue + abstandY, bitmapCanvas.getWidth() - baseLineX, yValue + abstandY, p1);
        }
    }
}
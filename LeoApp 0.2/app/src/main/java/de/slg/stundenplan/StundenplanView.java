package de.slg.stundenplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class StundenplanView extends View {
    private final Canvas canvas;
    private final Paint paint;
    Bitmap bitmap;
    private boolean isInitialized;
    private int height, width, baseLineY, baseLineX, abstandX, abstandY, paddingX, paddingY, baseline2Y;

    public StundenplanView(Context context) {
        super(context);
        canvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    public StundenplanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        canvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    public StundenplanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        canvas = new Canvas();
        paint = new Paint();
        isInitialized = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isInitialized)
            init();
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void init() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        canvas.setBitmap(bitmap);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.white));

        height = getHeight();
        width = getWidth();

        baseLineX = width / 20;
        paddingX = width / 100;
        abstandX = (width - baseLineX * 2) / 5;

        baseLineY = height / 10;
        paddingY = height / 100;
        baseline2Y = baseLineY + 3 * paddingY;
        abstandY = (height - baseline2Y - baseLineY) / 10;

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setTextSize(2 * paddingY);

        drawTimetable();

        isInitialized = true;
    }

    private void drawTimetable() {
        canvas.drawLine(baseLineX, baseLineY, width - baseLineX, baseLineY, paint);
        canvas.drawLine(baseLineX, height - baseLineY, width - baseLineX, height - baseLineY, paint);
        canvas.drawLine(baseLineX, baseLineY, baseLineX, height - baseLineY, paint);
        canvas.drawLine(width - baseLineX, baseLineY, width - baseLineX, height - baseLineY, paint);

        canvas.drawText(getContext().getString(R.string.montag), baseLineX + paddingX, baseLineY + paddingY * 2, paint);
        canvas.drawLine(baseLineX + abstandX, baseLineY, baseLineX + abstandX, height - baseLineY, paint);
        canvas.drawText(getContext().getString(R.string.dienstag), baseLineX + abstandX + paddingX, baseLineY + paddingY * 2, paint);
        canvas.drawLine(baseLineX + abstandX * 2, baseLineY, baseLineX + abstandX * 2, height - baseLineY, paint);
        canvas.drawText(getContext().getString(R.string.mittwoch), baseLineX + abstandX * 2 + paddingX, baseLineY + paddingY * 2, paint);
        canvas.drawLine(baseLineX + abstandX * 3, baseLineY, baseLineX + abstandX * 3, height - baseLineY, paint);
        canvas.drawText(getContext().getString(R.string.donnerstag), baseLineX + abstandX * 3 + paddingX, baseLineY + paddingY * 2, paint);
        canvas.drawLine(baseLineX + abstandX * 4, baseLineY, baseLineX + abstandX * 4, height - baseLineY, paint);
        canvas.drawText(getContext().getString(R.string.freitag), baseLineX + abstandX * 4 + paddingX, baseLineY + paddingY * 2, paint);
        canvas.drawLine(baseLineX, baseline2Y, width - baseLineX, baseline2Y, paint);

        Fach[][] gewaehlteFaecher = new Fach[5][];
        for (int i = 0; i < gewaehlteFaecher.length; i++) {
            gewaehlteFaecher[i] = Utils.getStundDB().gewaehlteFaecherAnTag(i + 1);
        }
        for (int i = 1; i < 10; i++) {
            int yValue = baseline2Y + (i - 1) * abstandY;
            for (int j = 0; j < 5; j++) {
                Fach[] tag = gewaehlteFaecher[j];
                if (i - 1 < tag.length) {
                    Fach f = tag[i - 1];
                    String text;
                    if (f.gibName().equals("") && !f.gibNotiz().equals("")) {
                        text = f.gibNotiz().split(" ")[0];
                    } else {
                        text = f.gibName().split(" ")[0];
                    }
                    canvas.drawText(text, baseLineX + abstandX * j + paddingX, yValue + paddingY * 2, paint);
                }
            }
            canvas.drawLine(baseLineX, yValue + abstandY, width - baseLineX, yValue + abstandY, paint);
        }
    }
}
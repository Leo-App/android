package de.slg.stundenplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.slg.leoapp.R;

public class StundenplanView extends View {
    private final Canvas canvas;
    private final Paint  paint;
    Bitmap bitmap;
    private boolean isInitialized;
    private int     height, width, baseLineY, baseLineX, abstandX, abstandY, paddingX, paddingY, baseline2Y, baseline2X;

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
        //horizontal
        baseLineX = width / 20; //Entfernung vom Rand
        paddingX = width / 100;
        baseline2X = baseLineX + 6 * paddingX;
        abstandX = (width - baseline2X - baseLineX) / 10;
        //vertikal
        baseLineY = height / 20; //Entfernung vom Rand unten
        paddingY = height / 100;
        abstandY = (height - baseLineY * 2) / 5;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setTextSize(2 * paddingY);
        drawTimetable();
        isInitialized = true;
    }

    private void drawTimetable() { //todo und der rest auch noch...
        Path p = new Path();
        //Rahmen (bleibt so)
        canvas.drawLine(baseLineX, baseLineY, width - baseLineX, baseLineY, paint); //Linie oben
        canvas.drawLine(baseLineX, height - baseLineY, width - baseLineX, height - baseLineY, paint); //Linie unten
        canvas.drawLine(baseLineX, baseLineY, baseLineX, height - baseLineY, paint); //Linie links
        canvas.drawLine(width - baseLineX, baseLineY, width - baseLineX, height - baseLineY, paint); //Linie rechts
        //Spalten
        canvas.drawText(getContext().getString(R.string.montag), baseLineY + paddingY, baseLineX + paddingX, paint);
        //Das Problem ist die Textrichtung aber später....
        canvas.drawLine(baseLineX, height - baseLineY - abstandY, width - baseLineX, height - baseLineY - abstandY, paint);
        //canvas.drawText(getContext().getString(R.string.dienstag), baseLineY + abstandY + paddingY, baseLineX + paddingX, paint);
        canvas.drawLine(baseLineX, height - baseLineY - abstandY * 2, width - baseLineX, height - baseLineY - abstandY * 2, paint);
        //canvas.drawText(getContext().getString(R.string.mittwoch), baseLineY + abstandY * 2 + paddingY, baseLineX + paddingX, paint);
        canvas.drawLine(baseLineX, height - baseLineY - abstandY * 3, width - baseLineX, height - baseLineY - abstandY * 3, paint);
        //canvas.drawText(getContext().getString(R.string.donnerstag), baseLineY + abstandY * 3 + paddingY, baseLineX + paddingX, paint);
        canvas.drawLine(baseLineX, height - baseLineY - abstandY * 4, width - baseLineX, height - baseLineY - abstandY * 4, paint);
        //canvas.drawText(getContext().getString(R.string.freitag), baseLineY + abstandY * 4 + paddingY, baseLineX + paddingX, paint);
        canvas.drawLine(baseline2X, baseLineY, baseline2X, height - baseLineY, paint); //WochentagZeile
        /*Fach[][] gewaehlteFaecher = new Fach[5][];
        for (int i = 0; i < gewaehlteFaecher.length; i++) {
            gewaehlteFaecher[i] = Utils.getController().getStundplanDataBase().gewaehlteFaecherAnTag(i + 1);
        }
        for (int i = 1; i < 10; i++) {
            int yValue = baseline2Y + (i - 1) * abstandY;
            for (int j = 0; j < 5; j++) {
                Fach[] tag = gewaehlteFaecher[j];
                if (i - 1 < tag.length) {
                    Fach   f = tag[i - 1];
                    String text;
                    if (f.getName().equals("") && !f.getNotiz().equals("")) {
                        text = f.getNotiz().split(" ")[0];
                    } else {
                        text = f.getName().split(" ")[0];
                    }
                    canvas.drawText(text, baseLineX + abstandX * j + paddingX, yValue + paddingY * 2, paint);
                }
            }
            canvas.drawLine(baseLineX, yValue + abstandY, width - baseLineX, yValue + abstandY, paint);
        }*/
    }
}
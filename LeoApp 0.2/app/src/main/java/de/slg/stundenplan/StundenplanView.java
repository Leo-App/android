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
import de.slg.leoapp.Utils;
import de.slg.leoapp.UtilsController;

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
        //baseline2X = baseLineX + 6 * paddingX;
        //abstandX = (width - baseline2X - baseLineX) / 10;
        abstandX = (width - baseLineX * 2) / 5;
        //vertikal
        baseLineY = height / 20; //Entfernung vom Rand unten
        paddingY = height / 100;
        baseline2Y = baseLineY + 6 * paddingY;
        //abstandY = (height - baseLineY * 2) / 5;
        abstandY = (height - baseline2Y - baseLineY) / 10;
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setTextSize(2 * paddingY);
        drawTimetable();
        isInitialized = true;
    }

    private void drawTimetable() {
        Path p = new Path();
        //Rahmen (bleibt so)
        canvas.drawLine(baseLineX, baseLineY, width - baseLineX, baseLineY, paint); //Linie oben
        canvas.drawLine(baseLineX, height - baseLineY, width - baseLineX, height - baseLineY, paint); //Linie unten
        canvas.drawLine(baseLineX, baseLineY, baseLineX, height - baseLineY, paint); //Linie links
        canvas.drawLine(width - baseLineX, baseLineY, width - baseLineX, height - baseLineY, paint); //Linie rechts
        canvas.drawLine(baseLineX, baseline2Y, width - baseLineX, baseline2Y, paint); //WochentagZeile
        //Spalten
        canvas.drawLine(baseLineX + abstandX, baseLineY, baseLineX + abstandX, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX * 2 , baseLineY, baseLineX + abstandX * 2, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX * 3 , baseLineY, baseLineX + abstandX * 3, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX * 4 , baseLineY, baseLineX + abstandX * 4, height - baseLineY, paint);
        canvas.drawText(getContext().getString(R.string.montag), baseLineX + paddingX * 6, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.dienstag), baseLineX + abstandX + paddingX * 6, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.mittwoch), baseLineX + abstandX * 2 + paddingX * 6, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.donnerstag), baseLineX + abstandX * 3 + paddingX * 6, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.freitag), baseLineX + abstandX * 4 + paddingX * 6, baseline2Y - paddingY * 2, paint);
        Fach[][] gewaehlteFaecher = new Fach[5][];
        for (int i = 0; i < gewaehlteFaecher.length; i++) {
            gewaehlteFaecher[i] = de.slg.leoapp.Utils.getController().getStundenplanDatabase().gewaehlteFaecherAnTag(i + 1);
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
                    }  else {
                        text = f.getName();//.split(" ")[0];
                    }
                    canvas.drawText(text, baseLineX + abstandX * j + paddingX, yValue + paddingY * 5, paint);
                }
            }
            canvas.drawLine(baseLineX, yValue + abstandY, width - baseLineX, yValue + abstandY, paint);
        }
    }
}
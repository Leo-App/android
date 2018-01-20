package de.slg.stundenplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.stundenplan.utility.Fach;

public class StundenplanView extends View {
    private final Canvas  canvas;
    private final Paint   paint;
    public        Bitmap  bitmap;
    private       boolean isInitialized;
    private       int     height, width, baseLineY, baseLineX, abstandX, abstandY, paddingX, paddingY, baseline2Y, abstandX2;

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
        abstandX2 = (width - baseLineX * 2) / 7;
        abstandX = (width - abstandX2 - baseLineX * 2) / 5;
        //vertikal
        baseLineY = height / 20; //Entfernung vom Rand unten
        paddingY = height / 100;
        baseline2Y = baseLineY + 6 * paddingY;
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
        canvas.drawLine(baseLineX + abstandX2, baseLineY, baseLineX + abstandX2, height - baseLineY, paint); //Stunden-Spalte

        canvas.drawLine(baseLineX + abstandX2 + abstandX, baseLineY, baseLineX + abstandX2 + abstandX, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX2 + abstandX * 2, baseLineY, baseLineX + abstandX2 + abstandX * 2, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX2 + abstandX * 3, baseLineY, baseLineX + abstandX2 + abstandX * 3, height - baseLineY, paint);
        canvas.drawLine(baseLineX + abstandX2 + abstandX * 4, baseLineY, baseLineX + abstandX2 + abstandX * 4, height - baseLineY, paint);
        paint.setTextSize(width / 75);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(getContext().getString(R.string.hour), baseLineX + paddingX * 4, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.montag), baseLineX + abstandX2 + paddingX * 5, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.dienstag), baseLineX + abstandX2 + abstandX + paddingX * 5, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.mittwochchcc), baseLineX + abstandX2 + abstandX * 2 + paddingX * 5, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.donnerstag), baseLineX + abstandX2 + abstandX * 3 + paddingX * 5, baseline2Y - paddingY * 2, paint);
        canvas.drawText(getContext().getString(R.string.freitag), baseLineX + abstandX2 + abstandX * 4 + paddingX * 5, baseline2Y - paddingY * 2, paint);
        canvas.drawText("08:00 - 08:45", baseLineX + paddingX * 3, baseLineY + abstandY + paddingY * 3, paint);
        canvas.drawText("08:50 - 09:35", baseLineX + paddingX * 3, baseLineY + abstandY * 2 + paddingY * 3, paint);
        canvas.drawText("09:50 - 10:35", baseLineX + paddingX * 3, baseLineY + abstandY * 3 + paddingY * 3, paint);
        canvas.drawText("10:40 - 11:25", baseLineX + paddingX * 3, baseLineY + abstandY * 4 + paddingY * 3, paint);
        canvas.drawText("11:40 - 12:25", baseLineX + paddingX * 3, baseLineY + abstandY * 5 + paddingY * 3, paint);
        canvas.drawText("12:30 - 13:15", baseLineX + paddingX * 3, baseLineY + abstandY * 6 + paddingY * 3, paint);
        canvas.drawText("13:30 - 14:15", baseLineX + paddingX * 3, baseLineY + abstandY * 7 + paddingY * 3, paint);
        canvas.drawText("14:20 - 15:05", baseLineX + paddingX * 3, baseLineY + abstandY * 8 + paddingY * 3, paint);
        canvas.drawText("15:10 - 15:55", baseLineX + paddingX * 3, baseLineY + abstandY * 9 + paddingY * 3, paint);
        canvas.drawText("16:00 - 16:45", baseLineX + paddingX * 3, baseLineY + abstandY * 10 + paddingY * 3, paint);
        SQLiteConnectorStundenplan database         = new SQLiteConnectorStundenplan(getContext());
        Fach[][]                   gewaehlteFaecher = new Fach[5][];
        for (int i = 0; i < gewaehlteFaecher.length; i++) {
            gewaehlteFaecher[i] = database.gewaehlteFaecherAnTag(i + 1);
        }
        database.close();
        for (int i = 1; i < 10; i++) {
            int yValue = baseline2Y + (i - 1) * abstandY;
            for (int j = 0; j < 5; j++) {
                Fach[] tag = gewaehlteFaecher[j];
                if (i - 1 < tag.length) {
                    Fach   f = tag[i - 1];
                    String text;
                    if ("".equals(f.getName()) && !"".equals(f.getNotiz()) && f.getNotiz() != null) {
                        text = f.getNotiz().split(" ")[0];
                    } else {
                        if (Utils.getUserPermission() != User.PERMISSION_LEHRER) {
                            text = f.getName();
                        } else {
                            text = f.getKuerzel();
                        }
                    }
                    canvas.drawText(text, baseLineX + abstandX2 + abstandX * j + paddingX, yValue + paddingY * 5, paint);
                }
            }
            canvas.drawLine(baseLineX, yValue + abstandY, width - baseLineX, yValue + abstandY, paint);
        }
    }
}
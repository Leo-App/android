package de.slg.stimmungsbarometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.List;
import de.slg.leoapp.R;

public class ZeitraumFragment extends Fragment {
    int zeitraum, height, width;
    private Ergebnis[][] data;
    private StatistikView view;
    private static Bitmap bitmapBack;
    private Bitmap bitmapIch, bitmapSchueler, bitmapLehrer, bitmapAlle;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        if (data == null) {
            data = new Ergebnis[4][0];
        }
        view = new StatistikView(getContext());
        return view;
    }

    void fillData() {
        Ergebnis[][] allData = StimmungsbarometerActivity.getData();
        data = new Ergebnis[4][];
        for (int i = 0; i < allData.length; i++) {
            List<Ergebnis> list = new List<>();
            if (zeitraum == 3) {
                list.adapt(allData[i]);
            } else if (zeitraum == 2) {
                for (Ergebnis e : allData[i])
                    if (vorherigesJahr(e.date))
                        list.append(e);
            } else if (zeitraum == 1) {
                for (Ergebnis e : allData[i])
                    if (vorherigerMonat(e.date))
                        list.append(e);
            } else if (zeitraum == 0) {
                for (Ergebnis e : allData[i])
                    if (vorherigeWoche(e.date))
                        list.append(e);
            }
            if (list.length() > 0) {
                list.toFirst();
                Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
                c1.setTime(list.getContent().date);
                c2.setTime(new Date());
                if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH) || c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH) || c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
                    list.insertBefore(new Ergebnis(c2.getTime(), -1, list.getContent().ich, list.getContent().schueler, list.getContent().lehrer, list.getContent().alle));
                for (list.toFirst(); list.hasAccess() && list.hasNext(); list.next()) {
                    Ergebnis current = list.getContent(), next = list.getNext();
                    if (!vorherigerTag(current.date, next.date)) {
                        Calendar c = new GregorianCalendar();
                        c.setTime(new Date(current.date.getTime()));
                        c.add(Calendar.DAY_OF_MONTH, -1);
                        list.insertBehind(new Ergebnis(c.getTime(), -1, current.ich, current.schueler, current.lehrer, current.alle));
                    }
                }
                list.toLast();
                Ergebnis last = list.getContent();
                c1.setTime(last.date);
                c1.add(Calendar.DAY_OF_MONTH, -1);
                if (zeitraum == 2) {
                    while (vorherigesJahr(c1.getTime())) {
                        list.append(new Ergebnis(c1.getTime(), -1, last.ich, last.schueler, last.lehrer, last.alle));
                        c1.add(Calendar.DAY_OF_MONTH, -1);
                    }
                } else if (zeitraum == 1) {
                    while (vorherigerMonat(c1.getTime())) {
                        list.append(new Ergebnis(c1.getTime(), -1, last.ich, last.schueler, last.lehrer, last.alle));
                        c1.add(Calendar.DAY_OF_MONTH, -1);
                    }
                } else if (zeitraum == 0) {
                    while (vorherigeWoche(c1.getTime())) {
                        list.append(new Ergebnis(c1.getTime(), -1, last.ich, last.schueler, last.lehrer, last.alle));
                        c1.add(Calendar.DAY_OF_MONTH, -1);
                    }
                }
            }
            data[i] = list.fill(new Ergebnis[list.length()]);
        }
    }

    private boolean vorherigerTag(Date pDate1, Date pDate2) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(pDate1);
        c2.setTime(pDate2);
        c2.add(Calendar.DAY_OF_MONTH, 1);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean vorherigeWoche(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 7);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }

    private boolean vorherigerMonat(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 30);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }

    private boolean vorherigesJahr(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 365);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }

    void update() {
        if (view != null)
            view.invalidate();
    }

    private class StatistikView extends View {
        private final Canvas canvasBack;
        private final Canvas canvasIch, canvasSchueler, canvasLehrer, canvasAlle;
        private final Paint paint;
        private boolean isInitialized;
        private int baseLineY, baseLineX, abstandX, abstandY, radius;

        StatistikView(Context context) {
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

            canvas.drawBitmap(bitmapBack, 0, 0, paint);

            if (StimmungsbarometerActivity.drawIch) {
                canvas.drawBitmap(bitmapIch, 0, 0, paint);
            }
            if (StimmungsbarometerActivity.drawSchueler) {
                canvas.drawBitmap(bitmapSchueler, 0, 0, paint);
            }
            if (StimmungsbarometerActivity.drawLehrer) {
                canvas.drawBitmap(bitmapLehrer, 0, 0, paint);
            }
            if (StimmungsbarometerActivity.drawAlle) {
                canvas.drawBitmap(bitmapAlle, 0, 0, paint);
            }
        }

        private void init() {
            height = getHeight();
            width = getWidth();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            baseLineY = height * 99 / 100;
            abstandY = baseLineY * 9 / 40;
            baseLineX = width / 20;
            if (data[3].length != 0)
                abstandX = width * 9 / (data[3].length * 10);
            else
                abstandX = width * 9 / 10;
            radius = 4;

            if (bitmapIch == null || bitmapSchueler == null || bitmapLehrer == null || bitmapAlle == null) {
                bitmapIch = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasIch.setBitmap(bitmapIch);
                bitmapSchueler = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasSchueler.setBitmap(bitmapSchueler);
                bitmapLehrer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasLehrer.setBitmap(bitmapLehrer);
                bitmapAlle = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvasAlle.setBitmap(bitmapAlle);

                drawPoints();
                drawGraphs();
            }

            if (bitmapBack == null) {
                bitmapBack = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                canvasBack.setBitmap(bitmapBack);
                drawBackground();
            }


            isInitialized = true;
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
            for (int i = 0; i < data[0].length; i++) {
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
            int previousIch = 0;
            int previousSchueler = 0;
            int previousLehrer = 0;
            int previousAlle = 0;
            for (int i = 1; i < data[0].length; i++) {
                Paint p = new Paint();
                p.setStrokeWidth(3);
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
    }
}
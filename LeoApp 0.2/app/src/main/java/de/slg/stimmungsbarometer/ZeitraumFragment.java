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
    public int zeitraum;
    private Ergebnis[][] data;
    private StatistikView view;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        fillData();
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
        private Bitmap bitmap;
        private final Canvas bitmapCanvas;
        private boolean isInitialized;
        private final Paint paint;
        private int baseLineY, baseLineX, abstandX, abstandY, radius;

        private boolean ich;
        private boolean schueler;
        private boolean lehrer;
        private boolean alle;

        StatistikView(Context context) {
            super(context);
            bitmapCanvas = new Canvas();
            paint = new Paint();
            isInitialized = false;
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
            radius = 4;

            isInitialized = true;
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
                    if (current[i].value > 0) {
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
                    if (current[i].value > 0) {
                        if (current[previous].value > 0) {
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
                        }
                        previous = i;
                    }
                }
            }
        }
    }
}
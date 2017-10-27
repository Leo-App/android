package de.slg.leoapp;

import android.view.View;

/**
 * ItemAnimator
 *
 * Mithilfe dieser Klasse lassen sich manuelle Layoutanimationen umsetzen, Subklassen implementieren die Methoden doInIteration() und doOnFinal()
 *
 * @author Gianni
 * @version 2017.2310
 * @since 0.5.6
 *
 */

public abstract class ItemAnimator<ContentType extends View> {

    private int iterations;
    private int interval;
    private ContentType view;

    public ItemAnimator(ContentType view) {
        this.view = view;
    }

    /**
     * Wird in jeder Iteration des ItemAnimators im Main-Thread ausgeführt und sorgt für Änderungen während der Animation.
     *
     * @param view Das View-Objekt, das animiert wird
     *
     */
    protected abstract void doInIteration(ContentType view);

    /**
     * Wird nach beendeter Animation im Main-Thread ausgeführt.
     *
     * @param view Das View-Objekt, das animiert wurde
     *
     */
    protected abstract void doOnFinal(ContentType view);

    /**
     * Startet die Animation mit den vorher gesetzten Parametern (siehe {@link #setInterval(int) setInterval} und {@link #setIterations(int) setIterations}). Wurden keine festgelegt, wird die Animation nicht gestartet.
     */
    public final void execute() {
        if(interval+iterations < 0)
            return;

        Thread t = new Thread(new RunThread());
        t.start();

        doOnFinal(view);

    }

    /**
     * Setzt den Intervall, in dem Layoutänderungen umgesetzt werden sollen.
     *
     * @param interval Der zu setzende Intervall, in Millisekunden.
     * @return Das aktuelle ItemAnimator Objekt
     */
    public final ItemAnimator<ContentType> setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    /**
     * Legt die Anzahl der Iterationen fest, also wie oft {@link #doInIteration(ContentType) doInIteration} insgesamt aufgerufen wird.
     *
     * @param iterations Die Anzahl der Iterationen
     * @return Das aktuelle ItemAnimator Objekt
     */
    public final ItemAnimator<ContentType> setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    /**
     * Animations-Thread
     *
     * Nach interval Millisekunden wird {@link #doInIteration(ContentType) doInIteration} aufgerufen. Der Thread läuft im Hintergrund.
     */
    private class RunThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                doInIteration(view);
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

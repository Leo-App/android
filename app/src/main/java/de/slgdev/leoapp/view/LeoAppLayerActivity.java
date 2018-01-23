package de.slgdev.leoapp.view;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import de.slgdev.leoapp.R;

public abstract class LeoAppLayerActivity extends ActionLogActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(getContentView());
        initToolbar();
    }

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgeben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    protected abstract @LayoutRes
    int getContentView();

    /**
     * Soll die ID der Toolbar zurückgeben.
     *
     * @return Toolbar-ID
     */
    protected abstract @IdRes
    int getToolbarId();

    /**
     * Soll die String-Ressource des Titels der Toolbar zurückgeben.
     *
     * @return Text-ID, zb. R.string.title_main
     */
    protected abstract @StringRes
    int getToolbarTextId();

    /**
     * Allgemeine Methode zum Einrichten der Toolbar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activites) aus.
     * Überschreibende Methoden müssen super.initToolbar() aufrufen.
     */
    @CallSuper
    protected void initToolbar() {
        Toolbar toolbar = findViewById(getToolbarId());
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(getString(getToolbarTextId()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

package de.slg.leoapp.view;

import android.os.Bundle;
import android.support.annotation.IdRes;

/**
 * LeoAppFeatureActivity.
 * <p>
 * Abstrakte Subklasse von ActionLogActivity. Erweitert die Logging Funktionalität um Methoden zum Einrichten der Toolbar und des Navigationdrawers.
 * Dementsprechend sollte diese Klasse nur von Activities verwendet werden, bei denen ein Navigationdrawer sinnvoll/erwünscht ist.
 *
 * @author Gianni
 * @since 0.6.0
 * @version 2017.1211
 *
 */

public abstract class LeoAppFeatureActivity extends ActionLogActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
    }

    /**
     * Muss in der Implementation die Ressourcen-ID des DrawerLayouts zurückgeben.
     *
     * @return id des DrawerLayouts, zB. R.id.drawer
     */
    protected abstract @IdRes int getDrawerLayoutId();

    /**
     * Soll die ID des NavigationViews zurückgeben.
     *
     * @return NavigationView-ID
     */
    protected abstract @IdRes int getNavigationId();

    /**
     * Soll die ID der Toolbar zurückgeben
     *
     * @return Toolbar-ID
     */
    protected abstract @IdRes int getToolbarId();

    /**
     * Soll die ID des gehighlighteten Items in der Navigation zurückgeben. In der Regel also die des aktuellen Features.
     *
     * @return Menü-ID, zB. R.id.startseite
     */
    protected abstract @IdRes int getNavigationHighlightId();

    protected void initNavigationDrawer() {
        //TODO: Allgemeine Implementation
    }

    protected void initToolbar() {
        //TODO: Allgemeine Implementation
    }

}

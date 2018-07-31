package de.leoapp_slg.core.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout

/**
 * LeoAppNavigationActivity.
 * <p>
 * Abstrakte Subklasse von ActionLogActivity. Erweitert die Logging Funktionalität um Methoden zum Einrichten der Toolbar und des Navigationdrawers.
 * Dementsprechend sollte diese Klasse nur von Activities verwendet werden, bei denen ein Navigationdrawer sinnvoll/erwünscht ist. Bei den übrigen Activities muss dann
 * natürlich die Toolbar eigens implementiert werden.
 * Subklassen müssen nicht mehr {@link #setContentView(int)} aufrufen, sondern nur noch {@link #getContentView()} überschreiben.
 *
 * @author Gianni
 * @version 2017.2411
 * @since 0.6.0
 */
abstract class LeoAppNavActivity : ActionLogActivity() {

    private val navigationView: NavigationView = null
    private val drawerLayout: DrawerLayout? = null

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgaben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    @LayoutRes
    protected abstract fun getContentView(): Int

    /**
     * Muss in der Implementation die Ressourcen-ID des DrawerLayouts zurückgeben.
     *
     * @return id des DrawerLayouts, zB. R.id.drawer
     */
    @IdRes
    protected abstract fun getDrawerLayoutId(): Int

    /**
     * Soll die ID des NavigationViews zurückgeben.
     *
     * @return NavigationView-ID
     */
    @IdRes
    protected abstract fun getNavigationId(): Int

    /**
     * Soll die ID der Toolbar zurückgeben.
     *
     * @return Toolbar-ID
     */
    @IdRes
    protected abstract fun getToolbarId(): Int

    /**
     * Soll die String-Ressource des Titels der Toolbar zurückgeben.
     *
     * @return Text-ID, zb. R.string.title_main
     */
    @StringRes
    protected abstract fun getToolbarTextId(): Int

    /**
     * Soll die ID des gehighlighteten Items in der Navigation zurückgeben. In der Regel also die des aktuellen Features.
     *
     * @return Menü-ID, zB. R.id.startseite
     */
    @IdRes
    protected abstract fun getNavigationHighlightId(): Int

    /**
     * Liefert das NavigationView Objekt der aktuellen Activity. Erlaubt Zugriff von Subklassen auf den NavigationDrawer.
     *
     * @return NavigationView der Activity
     */
    protected fun getNavigationView(): NavigationView {
        return navigationView
    }

    /**
     * Liefert das DrawerLayout der aktuellen Activity. Erlaubt Zugriff von Subklassen auf den NavigationDrawer.
     *
     * @return DrawerLayout der Activity
     */
    protected fun getDrawerLayout(): DrawerLayout {
        return drawerLayout
    }

}
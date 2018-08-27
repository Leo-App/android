@file:Suppress("MemberVisibilityCanBePrivate", "WeakerAccess", "unused")

package de.slg.leoapp.core.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.google.android.material.bottomappbar.BottomAppBar
import de.slg.leoapp.core.R

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
abstract class LeoAppFeatureActivity : ActionLogActivity() {

    private lateinit var navigationView: BottomNavigationDrawer

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgaben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    @LayoutRes
    protected abstract fun getContentView(): Int

    /**
     * Soll die ID des gehighlighteten Items in der Navigation zurückgeben. In der Regel also die des aktuellen Features.
     *
     * @return Menü-ID, zB. R.id.startseite
     */
    @IdRes
    protected abstract fun getNavigationHighlightId(): Int

    @CallSuper
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(getContentView())
        initToolbar()
        initNavigationDrawer()
    }

    /**
     * Allgemeine Methode zum Einrichten des NavigationDrawers. Alle Änderungen wirken sich auf die gesamte App (Alle Navigationsmenüs) aus.
     * Überschreibende Methoden müssen super.initNavigationDrawer() aufrufen.
     */
    @CallSuper
    protected fun initNavigationDrawer() {
        navigationView = BottomNavigationDrawer(getNavigationHighlightId())
    }

    /**
     * Allgemeine Methode zum Einrichten der Toolbar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activities) aus.
     * Überschreibende Methoden müssen super.initToolbar() aufrufen.
     */
    @CallSuper
    protected fun initToolbar() {
        val appBar: BottomAppBar = findViewById(R.id.appBar)
        appBar.replaceMenu(R.menu.app_toolbar_default)
        appBar.setNavigationOnClickListener {
            navigationView.show(supportFragmentManager, "navigation_drawer")
        }
    }

    protected fun getAppBar(): BottomAppBar {
        return findViewById(R.id.appBar)
    }
}
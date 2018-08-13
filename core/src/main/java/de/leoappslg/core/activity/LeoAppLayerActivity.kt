@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.leoappslg.core.activity

import android.os.Bundle
import android.view.MenuItem

import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import de.leoapp_slg.core.R
import de.leoappslg.core.activity.ActionLogActivity

abstract class LeoAppLayerActivity : ActionLogActivity() {

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(getContentView())
        initToolbar()
    }

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgeben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    @LayoutRes
    protected abstract fun getContentView(): Int

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
     * Allgemeine Methode zum Einrichten der Toolbar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activites) aus.
     * Überschreibende Methoden müssen super.initToolbar() aufrufen.
     */
    @CallSuper
    protected fun initToolbar() {
        val toolbar: Toolbar = findViewById(getToolbarId())
        toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        toolbar.title = getString(getToolbarTextId())
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_left)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    @CallSuper
    override fun onOptionsItemSelected(mi: MenuItem): Boolean {
        if (mi.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}
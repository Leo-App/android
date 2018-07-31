package de.leoapp_slg.core.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import de.leoapp_slg.core.R

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

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(getContentView())
        initToolbar()
        initNavigationDrawer()
    }

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

    /**
     * Allgemeine Methode zum Einrichten des NavigationDrawers. Alle Änderungen wirken sich auf die gesamte App (Alle Navigationsmenüs) aus.
     * Überschreibende Methoden müssen super.initNavigationDrawer() aufrufen.
     */
    @CallSuper
    protected fun initNavigationDrawer() {
        drawerLayout = findViewById(getDrawerLayoutId())
        navigationView = findViewById(getNavigationId())


        navigationView.setCheckedItem(getNavigationHighlightId())

        //drawerLayout.closeDrawers()

        //navigationView.setNavigationItemSelectedListener {}

//        val username: TextView = navigationView.getHeaderView(0).findViewById(R.id.username)
//        username.text = Utils.User.getName()

//        val grade: TextView = navigationView.getHeaderView(0).findViewById(R.id.grade)
//        if (Utils.User.getPermission() == User.PERMISSION_LEHRER)
//            grade.setText(Utils.getLehrerKuerzel())
//        else
//            grade.setText(Utils.getUserStufe())

//        val mood: ImageView = navigationView.getHeaderView(0).findViewById(R.id.profile_image)
//        mood.setOnClickListener(View.OnClickListener {
//            drawerLayout.closeDrawers()
//            startActivity(Intent(applicationContext, ProfileActivity.class))
//        })
    }

    /**
     * Allgemeine Methode zum Einrichten der Toolbar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activities) aus.
     * Überschreibende Methoden müssen super.initToolbar() aufrufen.
     */
    @CallSuper
    protected fun initToolbar() {
        val toolbar: Toolbar = findViewById(getToolbarId());
        toolbar.setTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        toolbar.title = getString(getToolbarTextId())
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    @Override
    @CallSuper
    override fun onOptionsItemSelected(mi: MenuItem): Boolean {
        if (mi.itemId == android.R.id.home) {
            getDrawerLayout().openDrawer(GravityCompat.START)
        }
        return true;
    }

    @Override
    @CallSuper
    override fun onResume() {
        super.onResume()
        navigationView.setCheckedItem(getNavigationHighlightId())
    }
}
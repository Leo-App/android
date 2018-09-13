@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package de.slg.leoapp.core.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.slg.leoapp.core.R
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.core.utility.setTint

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

    private lateinit var navigationView: BottomSheetBehavior<View>
    private lateinit var appBar: BottomAppBar
    private lateinit var actionButton: FloatingActionButton

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgaben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    @LayoutRes
    protected abstract fun getContentView(): Int

    /**
     * Setzt das Icon des FloatingActionButtons, wird diese Methode nicht überschrieben, wird kein FAB angezeigt.
     *
     * @return id des FAB Icons
     */
    @DrawableRes
    protected open fun getActionIcon(): Int = 0

    /**
     * Setzt die Aktion, die on-click ausgeführt werden soll. Java Implementierungen nutzen
     * besser {@see getActionListener()}.
     */
    protected open fun getAction(): (View) -> Unit = {}

    /**
     * Setzt die Aktion, die on-click ausgeführt werden soll. Kotlin Implementierungen nutzen
     * besser {@see getAction()}.
     */
    //Alternative method to getAction for Java compatibility
    protected open fun getActionListener(): View.OnClickListener? = null

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

    override fun onResume() {
        super.onResume()
        navigationView.state = BottomSheetBehavior.STATE_HIDDEN
    }

    /**
     * Allgemeine Methode zum Einrichten des NavigationDrawers. Alle Änderungen wirken sich auf die gesamte App (Alle Navigationsmenüs) aus.
     */
    @CallSuper
    protected fun initNavigationDrawer() {
        navigationView = BottomSheetBehavior.from(findViewById(R.id.bottomNavigationDrawer))

        val menuWrapper: RecyclerView = findViewById(R.id.bottomNavigationMenu)
        menuWrapper.layoutManager = LinearLayoutManager(applicationContext)
        menuWrapper.adapter = DrawerAdapter()

        initBottomSheetBehavior()

        findViewById<View>(R.id.profileWrapper).setOnClickListener {
            startActivity(Intent(applicationContext, Utils.Activity.getProfileReference()))
        }
    }

    /**
     * Allgemeine Methode zum Einrichten der AppBar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activities) aus.
     */
    @CallSuper
    protected open fun initToolbar() {
        appBar = findViewById(R.id.appBar)
        appBar.replaceMenu(R.menu.app_toolbar_default)
        appBar.setNavigationOnClickListener {
            //findViewById<View>(R.id.shadow).visibility = View.VISIBLE
            navigationView.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            actionButton.hide()
        }
        appBar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                startActivity(Intent(applicationContext, Utils.Activity.getSettingsReference()))
            } else if (item.itemId == R.id.action_profile) {
                startActivity(Intent(applicationContext, Utils.Activity.getProfileReference()))
            }

            if (javaClass == Utils.Activity.getSettingsReference()
                    || javaClass == Utils.Activity.getProfileReference()) {
                finish()
            }

            true
        }

        actionButton = findViewById(R.id.action_main)
        if (getActionIcon() != 0) {
            actionButton.setImageResource(getActionIcon())
            actionButton.visibility = View.VISIBLE
            if (getActionListener() != null) {
                actionButton.setOnClickListener(getActionListener())
            } else {
                actionButton.setOnClickListener(getAction())
            }
        }
    }

    private fun initBottomSheetBehavior() {
        val shadow: View = findViewById(R.id.shadow)
        shadow.setOnClickListener {
            //navigationView.state = BottomSheetBehavior.STATE_HIDDEN
            //shadow.visibility = View.GONE
        }

        val close: View = findViewById(R.id.close)
        close.setOnClickListener {
            //navigationView.state = BottomSheetBehavior.STATE_HIDDEN
            //shadow.visibility = View.GONE
        }
        close.visibility = View.GONE

        navigationView.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offset: Float) {
                if (view.top > actionButton.bottom) {
                    actionButton.hide()
                } else {
                    actionButton.show()
                }
            }

            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    shadow.visibility = View.GONE
                    actionButton.show()
                }
                //shadow.visibility = View.GONE
//                close.visibility =
//                        if (newState == BottomSheetBehavior.STATE_EXPANDED)
//                            View.VISIBLE
//                        else
//                            View.GONE
            }
        })
    }

    override fun onBackPressed() {
        if (navigationView.state == BottomSheetBehavior.STATE_EXPANDED || navigationView.state == BottomSheetBehavior.STATE_HALF_EXPANDED)
            navigationView.state = BottomSheetBehavior.STATE_HIDDEN
        else
            super.onBackPressed()
    }

    protected fun getAppBar(): BottomAppBar {
        if (!::appBar.isInitialized) {
            appBar = findViewById(R.id.appBar)
        }

        return appBar
    }

    protected fun getNavigationDrawer(): BottomSheetBehavior<View> {
        if (!::navigationView.isInitialized) {
            navigationView = BottomSheetBehavior.from(findViewById(R.id.bottomNavigationDrawer))
        }

        return navigationView
    }

    protected fun getActionButton(): FloatingActionButton {
        if (!::actionButton.isInitialized) {
            actionButton = findViewById(R.id.action_main)
        }

        return actionButton
    }

    inner class DrawerAdapter : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_navigation_item, parent, false))
        }

        override fun getItemCount(): Int {
            return Utils.Menu.getEntries().size()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val menuEntry: MenuEntry = Utils.Menu.getEntries().getObjectAt(position)!!

            val icon: ImageView = holder.itemView.findViewById(R.id.icon)
            val title: TextView = holder.itemView.findViewById(R.id.featureTitle)

            icon.setImageResource(menuEntry.getIcon())

            if (menuEntry.getId() == getNavigationHighlightId()) {
                icon.setTint(android.R.color.black)
            } else {
                icon.setTint(R.color.colorTextGrey)
            }

            title.text = menuEntry.getTitle()

            holder.itemView.setOnClickListener {
                startActivity(menuEntry.getIntent(applicationContext))
                //navigationView.state = BottomSheetBehavior.STATE_HIDDEN
            }

            (holder.itemView as CardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            if (menuEntry.getId() == getNavigationHighlightId())
                                R.color.colorAccent
                            else
                                android.R.color.white
                    )
            )
        }
    }

}
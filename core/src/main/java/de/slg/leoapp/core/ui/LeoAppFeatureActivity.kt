@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package de.slg.leoapp.core.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.slg.leoapp.core.R
import de.slg.leoapp.core.data.User
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.ui.image.BackgroundEffect
import de.slg.leoapp.core.ui.image.CircularImageView
import de.slg.leoapp.core.utility.Utils
import de.slg.leoapp.core.utility.setTint
import org.jetbrains.anko.backgroundColorResource

/**
 * LeoAppNavigationActivity.
 * <p>
 * Abstrakte Subklasse von ActionLogActivity. Erweitert die Logging Funktionalität um Methoden zum
 * Einrichten der Toolbar und des Navigationdrawers. Dementsprechend sollte diese Klasse nur von
 * Activities verwendet werden, bei denen ein Navigationdrawer sinnvoll/erwünscht ist.
 * Bei den übrigen Activities muss dann natürlich die Toolbar eigens implementiert werden.
 * Subklassen müssen nicht mehr {@link #setContentView(int)} aufrufen, sondern nur noch
 * {@link #getContentView()} überschreiben.
 *
 * @author Gianni
 * @version 2017.2411
 * @since 0.6.0
 */
abstract class LeoAppFeatureActivity : ActionLogActivity() {

    private lateinit var navigationView: BottomSheetBehavior<View>
    private lateinit var appBar: BottomAppBar
    private lateinit var actionButton: FloatingActionButton
    private lateinit var coordinator: CoordinatorLayout

    private var defaultAppBarListener: Toolbar.OnMenuItemClickListener

    private var navigationDrawerShowing = false

    init {
        defaultAppBarListener = Toolbar.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(applicationContext, Utils.Activity.getSettingsReference()))
                    finishIfNecessary()
                }
                R.id.action_profile -> {
                    startActivity(Intent(applicationContext, Utils.Activity.getProfileReference()))
                    finishIfNecessary()
                }
            }
            true
        }
    }

    @CallSuper
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(getContentView())
        if (this is BackgroundEffect) {
            applyBackgroundEffect()
        }
        initAppBar()
        initNavigationDrawer()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        closeNavigationDrawer()
    }

    override fun onBackPressed() {
        if (navigationView.state == BottomSheetBehavior.STATE_EXPANDED || navigationView.state == BottomSheetBehavior.STATE_HALF_EXPANDED)
            navigationView.state = BottomSheetBehavior.STATE_HIDDEN
        else
            super.onBackPressed()
    }

    /**
     * Muss in der Implementierung die Ressourcen-ID des Activity-Layouts zurückgeben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    @LayoutRes
    protected abstract fun getContentView(): Int

    /**
     * Setzt das Icon des FloatingActionButtons. Wird diese Methode nicht überschrieben,
     * wird kein FAB angezeigt.
     *
     * @return id des FAB Icons
     */
    @DrawableRes
    protected open fun getActionIcon(): Int = 0

    /**
     * Setzt die Aktion, die on-click ausgeführt werden soll. Java Implementierungen nutzen
     * besser {@see #getActionListener()}.
     */
    protected open fun getAction(): (View) -> Unit = {}

    /**
     * Setzt die Aktion, die on-click ausgeführt werden soll. Kotlin Implementierungen nutzen
     * besser {@link #getAction()}.
     */
    //Alternative method to getAction for Java compatibility
    protected open fun getActionListener(): View.OnClickListener? = null

    /**
     * Soll die ID des gehighlighteten Items in der Navigation zurückgeben. In der Regel also
     * die des aktuellen Features.
     *
     * @return Menü-ID, zB. R.id.startseite
     */
    @IdRes
    protected abstract fun getNavigationHighlightId(): Int

    /**
     * Allgemeine Methode zum Einrichten des NavigationDrawers.
     * Alle Änderungen wirken sich auf die gesamte App (Alle Navigationsmenüs) aus.
     */
    @CallSuper
    protected fun initNavigationDrawer() {
        navigationView = BottomSheetBehavior.from(findViewById(R.id.bottomNavigationDrawer))
        coordinator = findViewById(R.id.coordinator)
        coordinator.setOnClickListener { closeNavigationDrawer() }
        coordinator.isClickable = navigationDrawerShowing

        val menuWrapper: RecyclerView = findViewById(R.id.bottomNavigationMenu)
        menuWrapper.layoutManager = LinearLayoutManager(applicationContext)
        menuWrapper.adapter = DrawerAdapter()

        val close: View = findViewById(R.id.close)
        close.setOnClickListener {
            closeNavigationDrawer()
        }
        close.visibility = View.GONE

        val user = User(applicationContext)

        val picture: CircularImageView = findViewById(R.id.profilePicture)
        picture.setImageBitmap(user.profilePicture.getPictureOrPlaceholder())

        val name: TextView = findViewById(R.id.name)
        name.text = user.getFullName()

        val loginName: TextView = findViewById(R.id.loginName)
        loginName.text = user.loginName

        //TODO do we still need that? vvv
        navigationView.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offset: Float) {
                //stub
            }

            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    coordinator.backgroundColorResource = android.R.color.transparent
                    navigationDrawerShowing = false
                    coordinator.isClickable = navigationDrawerShowing
                }
//                close.visibility = if (newState == BottomSheetBehavior.STATE_EXPANDED)
//                    View.VISIBLE
//                else
//                    View.GONE
            }
        })
        //TODO do we still need that? ^^^

        findViewById<View>(R.id.profileWrapper).setOnClickListener {
            startActivity(Intent(applicationContext, Utils.Activity.getProfileReference()))
            finishIfNecessary()
        }
    }

    /**
     * Allgemeine Methode zum Einrichten der AppBar. Alle Änderungen wirken sich auf die gesamte App
     * (NUR Feature-Toolbars - Keine der sonstigen Activities) aus.
     */
    @CallSuper
    protected open fun initAppBar() {
        appBar = findViewById(R.id.appBar)
        appBar.replaceMenu(R.menu.app_toolbar_default)
        appBar.setNavigationOnClickListener { openNavigationDrawer() }
        appBar.setOnMenuItemClickListener(defaultAppBarListener)

        actionButton = findViewById(R.id.action_main)
        if (usesActionButton()) {
            actionButton.visibility = View.VISIBLE
            actionButton.setImageResource(getActionIcon())
            if (getActionListener() != null) {
                actionButton.setOnClickListener(getActionListener())
            } else {
                actionButton.setOnClickListener(getAction())
            }
        }
    }

    /**
     * Öffnet den NavigationDrawer.
     */
    fun openNavigationDrawer() {
        if (!navigationDrawerShowing) {
            navigationDrawerShowing = true
            coordinator.isClickable = navigationDrawerShowing
            navigationView.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            coordinator.backgroundColorResource = R.color.colorShadow
        }
    }

    /**
     * Schließt den NavigationDrawer.
     */
    fun closeNavigationDrawer() {
        if (navigationDrawerShowing) {
            navigationDrawerShowing = false
            coordinator.isClickable = navigationDrawerShowing
            coordinator.backgroundColorResource = android.R.color.transparent
            navigationView.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    /**
     * Gibt die aktuelle Instanz der BottomAppBar zurück.
     */
    protected fun getAppBar(): BottomAppBar {
        if (!::appBar.isInitialized) {
            appBar = findViewById(R.id.appBar)
        }

        return appBar
    }

    /**
     * Gibt die aktuelle Instanz des BottomNavigationDrawers zurück.
     */
    protected fun getNavigationDrawer(): BottomSheetBehavior<View> {
        if (!::navigationView.isInitialized) {
            navigationView = BottomSheetBehavior.from(findViewById(R.id.bottomNavigationDrawer))
        }

        return navigationView
    }

    /**
     * Gibt die aktuelle Instanz des FloatingActionButtons zurück.
     */
    protected fun getActionButton(): FloatingActionButton {
        if (!::actionButton.isInitialized) {
            actionButton = findViewById(R.id.action_main)
        }

        return actionButton
    }

    /**
     * Fügt eine neue Aktion für den Menüeintrag menuId zum AppBar-OnClickListener hinzu. Hat der
     * entsprechende Eintrag bereits eine Aktion, wird diese überschrieben.
     * Java Implementierungen nutzen besser {@link BottomAppBar#addMenuListener()}
     */
    protected fun BottomAppBar.addMenuAction(@IdRes menuId: Int, action: (MenuItem) -> Unit) {
        setOnMenuItemClickListener {
            if (it.itemId == menuId) action(it)
            defaultAppBarListener.onMenuItemClick(it)
            true
        }
    }

    /**
     * Fügt eine neue Aktion für den Menüeintrag menuId zum AppBar-OnClickListener hinzu. Hat der
     * entsprechende Eintrag bereits eine Aktion, wird diese überschrieben.
     * Kotlin Implementierungen nutzen besser {@link BottomAppBar#addMenuAction()}
     */
    protected fun BottomAppBar.addMenuListener(@IdRes menuId: Int, action: Toolbar.OnMenuItemClickListener) {
        setOnMenuItemClickListener {
            if (it.itemId == menuId) action.onMenuItemClick(it)
            defaultAppBarListener.onMenuItemClick(it)
            true
        }
    }

    private fun usesActionButton() = getActionIcon() != 0

    private fun finishIfNecessary() {
        when (javaClass) {
            Utils.Activity.getSettingsReference(),
            Utils.Activity.getProfileReference() -> finish()
        }
    }

    inner class DrawerAdapter : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.core_bottom_navigation_item, parent, false))
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
                closeNavigationDrawer()
            }

            holder.itemView.isClickable = menuEntry.getId() != getNavigationHighlightId()

            (holder.itemView as CardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                            applicationContext,
                            if (menuEntry.getId() == getNavigationHighlightId()) R.color.colorAccent
                            else android.R.color.white
                    )
            )
        }
    }

}
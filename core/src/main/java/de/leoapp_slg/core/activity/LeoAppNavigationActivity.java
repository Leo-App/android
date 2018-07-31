package de.leoapp_slg.core.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import de.leoapp_slg.core.R;

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

public abstract class LeoAppNavigationActivity extends ActionLogActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(getContentView());
        initToolbar();
        initNavigationDrawer();
    }

    /**
     * Muss in der Implementation die Ressourcen-ID des Activity-Layouts zurückgaben.
     *
     * @return id des Activity-Layouts, zB. R.layout.startseite
     */
    protected abstract @LayoutRes
    int getContentView();

    /**
     * Muss in der Implementation die Ressourcen-ID des DrawerLayouts zurückgeben.
     *
     * @return id des DrawerLayouts, zB. R.id.drawer
     */
    protected abstract @IdRes
    int getDrawerLayoutId();

    /**
     * Soll die ID des NavigationViews zurückgeben.
     *
     * @return NavigationView-ID
     */
    protected abstract @IdRes
    int getNavigationId();

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
     * Soll die ID des gehighlighteten Items in der Navigation zurückgeben. In der Regel also die des aktuellen Features.
     *
     * @return Menü-ID, zB. R.id.startseite
     */
    protected abstract @IdRes
    int getNavigationHighlightId();

    /**
     * Liefert das NavigationView Objekt der aktuellen Activity. Erlaubt Zugriff von Subklassen auf den NavigationDrawer.
     *
     * @return NavigationView der Activity
     */
    protected final NavigationView getNavigationView() {
        return navigationView;
    }

    /**
     * Liefert das DrawerLayout der aktuellen Activity. Erlaubt Zugriff von Subklassen auf den NavigationDrawer.
     *
     * @return DrawerLayout der Activity
     */
    protected final DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    /**
     * Allgemeine Methode zum Einrichten des NavigationDrawers. Alle Änderungen wirken sich auf die gesamte App (Alle Navigationsmenüs) aus.
     * Überschreibende Methoden müssen super.initNavigationDrawer() aufrufen.
     */
    @CallSuper
    protected void initNavigationDrawer() {
        drawerLayout = findViewById(getDrawerLayoutId());
        navigationView = findViewById(getNavigationId());

        navigationView.setCheckedItem(getNavigationHighlightId());

//        navigationView.setNavigationItemSelectedListener(menuItem -> {
//            drawerLayout.closeDrawers();
//
//            if (menuItem.getItemId() == getNavigationHighlightId())
//                return true;
//
//            Intent i;
//            switch (menuItem.getItemId()) {
//                case R.id.foodmarks:
//                    i = new Intent(getApplicationContext(), EssensbonActivity.class);
//                    break;
//                case R.id.messenger:
//                    i = new Intent(getApplicationContext(), MessengerActivity.class);
//                    break;
//                case R.id.newsboard:
//                    i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
//                    break;
//                case R.id.stundenplan:
//                    i = new Intent(getApplicationContext(), StundenplanActivity.class);
//                    break;
//                case R.id.barometer:
//                    i = new Intent(getApplicationContext(), StimmungsbarometerActivity.class);
//                    break;
//                case R.id.klausurplan:
//                    i = new Intent(getApplicationContext(), KlausurplanActivity.class);
//                    break;
//                case R.id.startseite:
//                    i = null;
//                    break;
//                case R.id.umfragen:
//                    i = new Intent(getApplicationContext(), SurveyActivity.class);
//                    break;
//                case R.id.itsolver:
//                    i = new Intent(getApplicationContext(), ITActivity.class);
//                    break;
//                case R.id.settings:
//                    i = new Intent(getApplicationContext(), PreferenceActivity.class);
//                    break;
//                case R.id.profile:
//                    i = new Intent(getApplicationContext(), ProfileActivity.class);
//                    break;
//                case R.id.about:
//                    i = new Intent(getApplicationContext(), InfoActivity.class);
//                    break;
//                default:
//                    i = null;
//                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
//            }
//
//            if (i != null) {
//                startActivity(i);
//            }
//            if (getNavigationHighlightId() != R.id.startseite) {
//                finish();
//            }
//
//            return true;
//        });
//
//        TextView username = navigationView.getHeaderView(0).findViewById(R.id.username);
//        username.setText(Utils.User.getName());
//
//        TextView grade = navigationView.getHeaderView(0).findViewById(R.id.grade);
//        if (Utils.User.getPermission() == User.PERMISSION_LEHRER)
//            grade.setText(Utils.getLehrerKuerzel());
//        else
//            grade.setText(Utils.getUserStufe());
//
//        ImageView mood = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
//        mood.setOnClickListener(view -> {
//            drawerLayout.closeDrawers();
//            startActivity(new Intent(LeoAppNavigationActivity.this, ProfileActivity.class));
//
//            if (getNavigationHighlightId() != R.id.startseite) {
//                finish();
//            }
//        });
    }

    /**
     * Allgemeine Methode zum Einrichten der Toolbar. Alle Änderungen wirken sich auf die gesamte App (NUR Feature-Toolbars - Keine der sonstigen Activities) aus.
     * Überschreibende Methoden müssen super.initToolbar() aufrufen.
     */
    @CallSuper
    protected void initToolbar() {
        Toolbar toolbar = findViewById(getToolbarId());
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(getString(getToolbarTextId()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            getDrawerLayout().openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(getNavigationHighlightId());
    }
}

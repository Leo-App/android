package de.slg.schwarzes_brett.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.slg.leoapp.R;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.schwarzes_brett.dialog.NewEntryDialog;
import de.slg.schwarzes_brett.task.FileDownloadTask;
import de.slg.schwarzes_brett.task.SyncNewsTask;
import de.slg.schwarzes_brett.task.UpdateViewTrackerTask;

/**
 * SchwarzesBrettActivity.
 * <p>
 * Anzeige des digitalen Schwarzen-Bretts, hier wird eine ausklappbare Liste mit allen Neuigkeiten, die entweder per Webinterface oder per App hinzugefügt wurden, angezeigt.
 * Einzelne Einträge lassen sich für mehr Informationen aufklappen. Mit einem ausreichenden Permissionlevel wird ein FAB mit der Option, neue Einträge zu verfassen, angezeigt.
 *
 * @author Gianni, Kim, Moritz.
 * @version 2017.1811
 * @since 0.0.1
 */
public class SchwarzesBrettActivity extends LeoAppFeatureActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 42;

    private static SQLiteConnectorSchwarzesBrett sqLiteConnector;
    private static SQLiteDatabase                sqLiteDatabase;

    private List<String>              groupList;
    private List<String>              childList;
    private Map<String, List<String>> entriesMap;

    private String rawLocation;

    private static int getRemoteId(int position) {
        //Maybe cache already transformed ids to avoid excessive RAM usage
        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorSchwarzesBrett(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
        String stufe = Utils.getUserStufe();

        Cursor cursor;

        switch (stufe) {
            case "":
            case "TEA":
                return -1;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, new String[]{SQLiteConnectorSchwarzesBrett.EINTRAEGE_REMOTE_ID}, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Sek II' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, new String[]{SQLiteConnectorSchwarzesBrett.EINTRAEGE_REMOTE_ID, SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL, SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG}, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Sek I' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
        }

        cursor.moveToPosition(position);
        if (cursor.getCount() <= position)
            return -1;
        int ret = cursor.getInt(0);
        cursor.close();
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.getNotificationManager().cancel(NotificationHandler.ID_SCHWARZES_BRETT);
        Utils.getController().registerSchwarzesBrettActivity(this);

        receive();

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorSchwarzesBrett(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        initButton();
        initExpandableListView();
        initSwipeToRefresh();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_schwarzesbrett;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.actionBarSchwarzesBrett;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_news;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.newsboard;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationHandler.ID_SCHWARZES_BRETT);
        receive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        sqLiteConnector.close();
        sqLiteDatabase = null;
        sqLiteConnector = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        receive();
    }

    @Override
    protected String getActivityTag() {
        return "SchwarzesBrettActivity";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new FileDownloadTask().execute(rawLocation);
        }
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerSchwarzesBrettActivity(null);
    }

    private void initSwipeToRefresh() {
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SyncNewsTask(swipeLayout).execute();
            }
        });

        swipeLayout.setColorSchemeColors(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
    }

    private void initButton() {
        View button = findViewById(R.id.floatingActionButton);

        if (Utils.getUserPermission() == User.PERMISSION_LEHRER || Utils.getUserPermission() == User.PERMISSION_ADMIN) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new NewEntryDialog(SchwarzesBrettActivity.this).show();
                }
            });
        }
    }

    private void initExpandableListView() {
        createGroupList();

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.eintraege);

        ExpandableListAdapter expandableListAdapter = Utils.getUserPermission() > User.PERMISSION_SCHUELER
                ? new ExpandableListAdapter(entriesMap, groupList, createViewList())
                : new ExpandableListAdapter(entriesMap, groupList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int remoteID = getRemoteId(groupPosition);
                Utils.logError(remoteID);
                if (Utils.getUserPermission() == User.PERMISSION_LEHRER || de.slg.schwarzes_brett.utility.Utils.messageAlreadySeen(remoteID))
                    return false;
                String cache = Utils.getController().getPreferences().getString("pref_key_cache_vieweditems", "");
                if (!cache.equals(""))
                    cache += "-";
                Utils.getController().getPreferences()
                        .edit()
                        .putString("pref_key_cache_vieweditems", cache + "1:" + remoteID)
                        .apply();
                if (Utils.checkNetwork())
                    new UpdateViewTrackerTask().execute(remoteID);
                return false;
            }
        });

        if (groupList.size() == 0) {
            findViewById(R.id.textView6).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textView6).setVisibility(View.GONE);
        }
    }

    private ArrayList<Integer> createViewList() {
        ArrayList<Integer> viewList = new ArrayList<>();
        Cursor             cursor   = sqLiteDatabase.rawQuery("SELECT " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_VIEWS + " FROM " + SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            viewList.add(cursor.getInt(0));
        }
        cursor.close();
        return viewList;
    }

    private void createGroupList() {
        groupList = new ArrayList<>();

        String stufe = Utils.getUserStufe();
        Cursor cursor;
        switch (stufe) {
            case "":
            case "TEA":
                cursor = sqLiteDatabase.query(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, new String[]{SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL, SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG}, null, null, null, null, null);
                break;
            case "EF":
            case "Q1":
            case "Q2":
                cursor = sqLiteDatabase.query(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, new String[]{SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL, SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG}, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Sek II' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
            default:
                cursor = sqLiteDatabase.query(SQLiteConnectorSchwarzesBrett.TABLE_EINTRAEGE, new String[]{SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_TITEL, SQLiteConnectorSchwarzesBrett.EINTRAEGE_INHALT, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ERSTELLDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ABLAUFDATUM, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ANHANG}, SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = '" + stufe + "' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Sek I' OR " + SQLiteConnectorSchwarzesBrett.EINTRAEGE_ADRESSAT + " = 'Alle'", null, null, null, null);
                break;
        }

        entriesMap = new LinkedHashMap<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            groupList.add(cursor.getString(1));

            Date erstelldatum = new Date(cursor.getLong(3));
            Date ablaufdatum  = new Date(cursor.getLong(4));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);

            String[] children;

            if (cursor.getString(5).equals("null")) {
                children = new String[]{cursor.getString(0),
                        cursor.getString(2),
                        simpleDateFormat.format(erstelldatum) +
                                " - " + simpleDateFormat.format(ablaufdatum)
                };
            } else {
                children = new String[]{cursor.getString(0),
                        cursor.getString(2),
                        simpleDateFormat.format(erstelldatum) +
                                " - " + simpleDateFormat.format(ablaufdatum),
                        cursor.getString(5)};
            }

            loadChildren(children);
            entriesMap.put(cursor.getString(1), childList);
        }
        cursor.close();
    }

    private void loadChildren(String[] children) {
        childList = new ArrayList<>();
        Collections.addAll(childList, children);
    }

    private void receive() {
        new SyncNewsTask(null).execute();
    }

    public void refreshUI() {
        initExpandableListView();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final Map<String, List<String>> eintraege;
        private final List<String>              titel;
        @Nullable
        private       ArrayList<Integer>        views;

        ExpandableListAdapter(Map<String, List<String>> eintraege, List<String> titel) {
            this.eintraege = eintraege;
            this.titel = titel;
        }

        ExpandableListAdapter(Map<String, List<String>> eintraege, List<String> titel, @Nullable ArrayList<Integer> views) {
            this.eintraege = eintraege;
            this.titel = titel;
            this.views = views;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_title, null);
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText((String) getGroup(groupPosition));
            TextView textViewStufe = (TextView) convertView.findViewById(R.id.textViewStufe);
            textViewStufe.setText(eintraege.get(titel.get(groupPosition)).get(0));
            if (views != null) {
                TextView textViewViews = (TextView) convertView.findViewById(R.id.textViewViews);
                textViewViews.setVisibility(View.VISIBLE);
                if (views.size() > groupPosition) {
                    String viewString = views.get(groupPosition) > 999 ? "999+" : String.valueOf(views.get(groupPosition));
                    textViewViews.setText(viewString);
                } else {
                    textViewViews.setText("0");
                }
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textViewStufe.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                textViewStufe.setLayoutParams(params);
            }
            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (isLastChild) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt, null);

                final TextView textViewDate = (TextView) convertView.findViewById(R.id.textView);
                textViewDate.setText(eintraege.get(titel.get(groupPosition)).get(2));
            } else if (childPosition == 0) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child, null);

                final TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(eintraege.get(titel.get(groupPosition)).get(1));
            } else {
                convertView = getLayoutInflater().inflate(R.layout.list_item_expandable_child_alt, null);

                final String location = eintraege.get(titel.get(groupPosition)).get(3);

                final View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rawLocation = location;

                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SchwarzesBrettActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        } else {
                            new FileDownloadTask().execute(rawLocation);
                        }
                    }
                };

                final ImageView iv = (ImageView) convertView.findViewById(R.id.imageViewIcon);
                iv.setImageResource(R.drawable.ic_file_download);
                iv.setColorFilter(Color.rgb(0x00, 0x91, 0xea));
                iv.setOnClickListener(listener);

                final TextView textView = (TextView) convertView.findViewById(R.id.textView);
                textView.setText(location.substring(location.lastIndexOf('/') + 1));
                textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                textView.setOnClickListener(listener);
            }

            return convertView;
        }

        @Override
        public int getGroupCount() {
            return titel.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return eintraege.get(titel.get(groupPosition)).size() - 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return titel.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return eintraege.get(titel.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
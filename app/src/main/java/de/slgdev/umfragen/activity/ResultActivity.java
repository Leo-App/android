package de.slgdev.umfragen.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragenSpeichern;
import de.slgdev.leoapp.view.LeoAppLayerActivity;
import de.slgdev.umfragen.dialog.ResultDialogManual;
import de.slgdev.umfragen.utility.ResultListing;

public class ResultActivity extends LeoAppLayerActivity {

    private SQLiteConnectorUmfragenSpeichern databaseConnector;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        databaseConnector = new SQLiteConnectorUmfragenSpeichern(this);
        initListView();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_umfragen_results;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_survey_result;
    }

    @Override
    protected void onDestroy() {
        databaseConnector.close();
        super.onDestroy();
    }

    @Override
    protected String getActivityTag() {
        return "ResultActivity";
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        ListAdapter adapter = new ResultListAdapter(this, databaseConnector.getSavedInfos());

        listView.setAdapter(adapter);
    }

    private class ResultListAdapter extends ArrayAdapter<String> {

        private ResultListing[] content;
        private Context context;

        ResultListAdapter(@NonNull Context context, ResultListing[] content) {
            super(context, R.layout.list_item_survey_result);
            this.content = content;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(final int position, View v, @NonNull ViewGroup group) {
            if (v == null) {
                v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_survey_result, null);
            }

            TextView title = findViewById(R.id.title);
            title.setText(content[position].title);

            findViewById(R.id.wrapper).setOnClickListener(view -> new ResultDialogManual(ResultActivity.this, content[position].description, content[position].answers).show());

            return v;
        }

    }

}

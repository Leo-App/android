package de.slg.leoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialActivity extends AppCompatActivity {
    private String[] categories;
    private int category = 0;
    private TextView title;
    private ViewPager viewPager;
    private TutorialFragment[][] fragments;
    private String[][] descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        categories = new String[]{
                getString(R.string.title_home),
                getString(R.string.title_foodmarks),
                getString(R.string.title_testplan),
                getString(R.string.title_messenger),
                getString(R.string.title_news),
                getString(R.string.title_survey),
                getString(R.string.title_plan)};

        descriptions = new String[categories.length][];
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(getString(R.string.title_home))) {
                Log.e("TAG", "Startseite");
                descriptions[i] = new String[]{"Home 1", "Home 2", "Home 3", "Home 4"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_foodmarks))) {
                Log.e("TAG", "Klausurplan");
                descriptions[i] = new String[]{"QR 1", "QR 2", "QR 3"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_testplan))) {
                Log.e("TAG", "Klausurplan");
                descriptions[i] = new String[]{"Test 1", "Test 2", "Test 3"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_messenger))) {
                Log.e("TAG", "Messenger");
                descriptions[i] = new String[]{"Message 1", "Message 2"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_news))) {
                Log.e("TAG", "Schwarzes Brett");
                descriptions[i] = new String[]{"News 1", "News 2", "News 3", "News 4", "News 5"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_survey))) {
                Log.e("TAG", "Stimmungsbarometer");
                descriptions[i] = new String[]{"Survey 1"};
                continue;
            }
            if (categories[i].equals(getString(R.string.title_plan))) {
                Log.e("TAG", "Stundenplan");
                descriptions[i] = new String[]{"Schedule 1", "Schedule 2", "Schedule 3"};
                continue;
            }
        }

        fragments = new TutorialFragment[categories.length][];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = new TutorialFragment[descriptions[i].length];
            for (int j = 0; j < fragments[i].length; j++) {
                fragments[i][j] = new TutorialFragment();
                fragments[i][j].setDescription(descriptions[i][j]);
            }
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[category][position];
            }

            @Override
            public int getCount() {
                return fragments[category].length;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        title = (TextView) findViewById(R.id.categoryTitle);
        title.setText(categories[category]);

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.nextTopic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextCategory();
            }
        });
        findViewById(R.id.previousTopic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCategory();
            }
        });
    }

    private void nextCategory() {
        category++;
        if (category >= categories.length)
            category = 0;
        refreshUI();
    }

    private void previousCategory() {
        category--;
        if (category < 0)
            category = categories.length - 1;
        refreshUI();
    }

    private void refreshUI() {
        title.setText(categories[category]);
        viewPager.getAdapter().notifyDataSetChanged();
    }

//    private void addView(String topic, @DrawableRes int imageRessource, String text) {
//
//    }

    public static class TutorialFragment extends Fragment {
        private View v;
        private ImageView image;
        private TextView text;
        private String description;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (v == null) {
                v = inflater.inflate(R.layout.fragment_tutorial, container, false);

                image = (ImageView) v.findViewById(R.id.imageView);
                text = (TextView) v.findViewById(R.id.description);
            }

            image.setImageResource(R.drawable.leo_splash);
            text.setText(description);

            return v;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
package de.slg.leoapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialDialog extends AppCompatDialog {
    private float lastX;
    private AdapterViewFlipper viewFlipper;

    public TutorialDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tutorial);

        viewFlipper = (AdapterViewFlipper) findViewById(R.id.adapterViewFlipper);
        View v1 = getLayoutInflater().inflate(R.layout.tutorial_item, null);
        View v2 = getLayoutInflater().inflate(R.layout.tutorial_item, null);
        View v3 = getLayoutInflater().inflate(R.layout.tutorial_item, null);
        View v4 = getLayoutInflater().inflate(R.layout.tutorial_item, null);

        TextView t1 = (TextView) v1.findViewById(R.id.description);
        t1.setText("Description 1");
        TextView t2 = (TextView) v2.findViewById(R.id.description);
        t2.setText("Description 2");
        TextView t3 = (TextView) v3.findViewById(R.id.description);
        t3.setText("Description 3");
        TextView t4 = (TextView) v4.findViewById(R.id.description);
        t4.setText("Description 4");

        viewFlipper.addView(v1, 0);
        viewFlipper.addView(v2, 1);
        viewFlipper.addView(v3, 2);
        viewFlipper.addView(v4, 3);

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        float currentX = event.getX();
                        if (lastX < currentX) {
                            if (viewFlipper.getDisplayedChild() == 0)
                                break;

                            viewFlipper.setInAnimation(getContext(), R.anim.slide_in_from_left);
                            viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_to_right);
                            viewFlipper.setDisplayedChild(viewFlipper.getDisplayedChild() - 1);
                        } else if (lastX > currentX) {
                            if (viewFlipper.getDisplayedChild() == viewFlipper.getChildCount() - 1)
                                break;

                            viewFlipper.setInAnimation(getContext(), R.anim.slide_in_from_right);
                            viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_to_left);
                            viewFlipper.setDisplayedChild(viewFlipper.getDisplayedChild() + 1);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void addView(String topic, @DrawableRes int imageRessource, String description) {

    }

    private class TutorialAdapter extends ArrayAdapter {
        Drawable[] items;
        String[] descriptions;

        public TutorialAdapter(@NonNull Context context, @LayoutRes int resource, List<Drawable> items, List<String> descriptions) {
            super(context, resource);
            this.items = items.fill(new Drawable[items.size()]);
            this.descriptions = descriptions.fill(new String[descriptions.size()]);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.tutorial_item, null);

                final ImageView image = (ImageView) view.findViewById(R.id.imageView);
                final TextView description = (TextView) view.findViewById(R.id.description);

                image.setImageDrawable(items[position]);
                description.setText(descriptions[position]);
            }
            return view;
        }

        @Override
        public int getCount() {
            return items.length;
        }
    }
}
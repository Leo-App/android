package de.slgdev.leoapp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

/**
 * InfoFragmentBuilder.
 * <p>
 * Builder für Intro-Fragments.
 *
 * @author Gianni
 * @version 2017.2312
 * @since 0.7.0
 */
public class DisclaimerFragmentBuilder {

    private @StringRes
    int title;
    private @StringRes
    int content;
    private @ColorRes
    int color;

    private static int position;

    static {
        position = 0;
    }

    public IntroFragment build() {
        return IntroFragment.newInstance(title, content, color, position++);
    }

    public DisclaimerFragmentBuilder setTitle(@StringRes int title) {
        this.title = title;
        return this;
    }

    public DisclaimerFragmentBuilder setContent(@StringRes int content) {
        this.content = content;
        return this;
    }

    public DisclaimerFragmentBuilder setColor(@ColorRes int color) {
        this.color = color;
        return this;
    }

    public DisclaimerFragmentBuilder setPosition(int position) {
        DisclaimerFragmentBuilder.position = position;
        return this;
    }

    public static class IntroFragment extends AbstractOrderedFragment implements ISlideBackgroundColorHolder {

        private static IntroFragment newInstance(@StringRes int title,
                                                 @StringRes int content,
                                                 @ColorRes int color,
                                                 int position) {

            IntroFragment fragmentInfo = new IntroFragment();

            Bundle bundle = new Bundle(5);
            bundle.putInt("title", title);
            bundle.putInt("content", content);
            bundle.putInt("color", color);
            bundle.putInt("position", position);

            fragmentInfo.setArguments(bundle);

            return fragmentInfo;
        }

        private int title;
        private int content;
        private int color;
        private int position;

        private LinearLayout background;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.title = getArguments().getInt("title");
            this.content = getArguments().getInt("content");
            this.color = getArguments().getInt("color");
            this.position = getArguments().getInt("position");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_intro_disclaimer, container, false);

            background = v.findViewById(R.id.main);

            TextView textViewTitle = v.findViewById(R.id.title);
            TextView textViewDesc  = v.findViewById(R.id.description);

            textViewTitle.setText(title);
            textViewDesc.setText(Html.fromHtml(Utils.getString(content)));

            background.setBackgroundColor(ContextCompat.getColor(getContext(), color));

            return v;
        }

        @Override
        public int getDefaultBackgroundColor() {
            return color;
        }

        @Override
        public void setBackgroundColor(int backgroundColor) {
            background.setBackgroundColor(backgroundColor);
        }

        @Override
        public int getPosition() {
            return position;
        }
    }

}
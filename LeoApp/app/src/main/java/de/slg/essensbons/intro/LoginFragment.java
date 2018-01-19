package de.slg.essensbons.intro;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import de.slg.leoapp.R;
import de.slg.leoapp.activity.fragment.AbstractOrderedFragment;

public class LoginFragment extends AbstractOrderedFragment implements ISlideBackgroundColorHolder {

    public static LoginFragment newInstance(@StringRes int title,
                                            @StringRes int content,
                                            @ColorRes int color,
                                            int position) {

        LoginFragment fragmentInfo = new LoginFragment();

        Bundle bundle = new Bundle(4);
        bundle.putInt("title", title);
        bundle.putInt("content", content);
        bundle.putInt("color", color);
        bundle.putInt("order", position);

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
        this.position = getArguments().getInt("order");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_intro_login, container, false);
        background = v.findViewById(R.id.main);

        TextView textViewTitle = v.findViewById(R.id.title);
        TextView textViewDesc  = v.findViewById(R.id.description);

        textViewTitle.setText(title);
        textViewDesc.setText(content);

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

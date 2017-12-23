package de.slg.leoapp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import de.slg.leoapp.R;
import de.slg.leoapp.activity.IntroActivity;
import de.slg.leoapp.task.RegistrationTask;
import de.slg.leoapp.utility.ResponseCode;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.VerificationListener;

public class VerificationFragment extends AbstractOrderedFragment implements ISlideBackgroundColorHolder {

    public static VerificationFragment newInstance(@StringRes int title,
                                                   @StringRes int content,
                                                   @ColorRes int color,
                                                   int position) {

        VerificationFragment fragmentInfo = new VerificationFragment();

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
    private EditText     name;
    private EditText     password;

    private ResponseCode verificationResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.title    = getArguments().getInt("title");
        this.content  = getArguments().getInt("content");
        this.color    = getArguments().getInt("color");
        this.position = getArguments().getInt("order");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_intro_verification, container, false);
        background = (LinearLayout) v.findViewById(R.id.main);

        TextView textViewTitle = (TextView) v.findViewById(R.id.title);
        TextView textViewDesc  = (TextView) v.findViewById(R.id.description);
        name     = (EditText) v.findViewById(R.id.editText1);
        password = (EditText) v.findViewById(R.id.editText2);

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

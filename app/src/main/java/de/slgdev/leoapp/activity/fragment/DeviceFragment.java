package de.slgdev.leoapp.activity.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
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

import de.slgdev.leoapp.R;

public class DeviceFragment extends AbstractOrderedFragment implements ISlideBackgroundColorHolder {

    public static DeviceFragment newInstance(@StringRes int title,
                                             @StringRes int content,
                                             @ColorRes int color,
                                             int position) {

        DeviceFragment fragmentInfo = new DeviceFragment();

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_intro_device, container, false);
        background = v.findViewById(R.id.main);

        TextView textViewTitle = v.findViewById(R.id.title);
        TextView textViewDesc  = v.findViewById(R.id.description);
        EditText deviceField   = v.findViewById(R.id.editText1);

        textViewTitle.setText(title);
        textViewDesc.setText(content);

        String bluetoothName = getLocalBluetoothName();
        deviceField.setText(bluetoothName);

        deviceField.setOnClickListener(et -> {
            if (deviceField.getText().toString().equals(bluetoothName))
                deviceField.setText("");
        });

        deviceField.setOnFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus && deviceField.getText().toString().equals(""))
                deviceField.setText(bluetoothName);
        });


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

    @SuppressLint("HardwareIds")
    private String getLocalBluetoothName(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null)
            return "";

        String name = mBluetoothAdapter.getName();
        return name == null ? mBluetoothAdapter.getAddress() : name;
    }

}

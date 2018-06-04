package de.slgdev.umfragen.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.task.UserDetailTask;
import de.slgdev.leoapp.utility.User;

public final class UserInformationDialog extends AlertDialog {

    private final int     userid;
    private final Context context;

    public UserInformationDialog(@NonNull Context context, int userid) {
        super(context);
        this.userid  = userid;
        this.context = context;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_user_info);

        TextView id          = findViewById(R.id.textViewID);
        TextView name        = findViewById(R.id.textViewName);
        TextView defaultName = findViewById(R.id.textViewDefaultName);
        TextView stufe       = findViewById(R.id.textViewStufe);
        TextView permission  = findViewById(R.id.textViewPermission);
        TextView createDate  = findViewById(R.id.textViewCreateDate);

        findViewById(R.id.buttonOK).setOnClickListener(v -> dismiss());

        String[] permissions = new String[]{"Schüler", "Lehrer", "Admin"};
        DateFormat format = new SimpleDateFormat("dd.mm.yy", Locale.GERMAN);

        new UserDetailTask().addListener(o -> {
            User u = (User) o[0];

            findViewById(R.id.progressBar).setVisibility(View.GONE);

            id.setText(context.getString(R.string.user_info_id, u.uid));
            name.setText(context.getString(R.string.user_info_name, u.uname));
            defaultName.setText(context.getString(R.string.user_info_defaultname, u.udefaultname));
            stufe.setText(context.getString(R.string.user_info_level, u.ustufe));
            permission.setText(context.getString(R.string.user_info_permission, permissions[u.upermission-1]));
            createDate.setText(context.getString(R.string.user_info_createdate, format.format(u.ucreatedate)));

            id.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            defaultName.setVisibility(View.VISIBLE);
            stufe.setVisibility(View.VISIBLE);
            permission.setVisibility(View.VISIBLE);
            createDate.setVisibility(View.VISIBLE);

        }).execute(userid);

    }

}

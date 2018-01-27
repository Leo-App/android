package de.slg.stundenplan.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.slg.leoapp.R;

public class CreateCourseDialog extends AlertDialog {
    EditText fachEingabe;
    EditText lehrerEingabe;

    Button cancel;
    Button confirm;

    //Den Rest bitte später


    public CreateCourseDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_course);

        fachEingabe = findViewById(R.id.course_name);
        lehrerEingabe = findViewById(R.id.lehrer_edit);

        cancel = findViewById(R.id.buttonDialog1);
        confirm = findViewById(R.id.buttonDialog2);

        //Den Rest später

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Datenbank und so ein Mist
                //Und generell noch anderer Mist weil man nichts sieht aber nachdenken ist heute nicht so meine stärke
                dismiss();
            }
        });

        fachEingabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fachEingabe = findViewById(R.id.course_name);
            }
        });

        lehrerEingabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lehrerEingabe = findViewById(R.id.lehrer_edit);
            }
        });

    }


}

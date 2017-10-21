package de.slg.schwarzes_brett;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.slg.leoapp.R;

/**
 * Ergebnisdialog
 *
 * Dieser Dialog wird zum Anzeigen der Umfrageergebnisse verwendet
 *
 * @author Gianni
 * @version 2017.2110
 * @since 0.5.6
 *
 */
class ResultDialog extends AlertDialog {

    private Context     context;
    private int         id;

    private TextView    t1, t2, op1, op2, op3, op4, op5;
    private TextView[]  answers;
    private ProgressBar load, p1, p2, p3, p4, p5;
    private Button      b1;

    ResultDialog(@NonNull Context context, int id) {
        super(context);
        this.context = context;
        this.id = id;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_survey_result);

        b1 = (Button) findViewById(R.id.buttonOK);

        t1 = (TextView) findViewById(R.id.question);
        t2 = (TextView) findViewById(R.id.votes);

        op1 = (TextView) findViewById(R.id.answer1);
        op2 = (TextView) findViewById(R.id.answer2);
        op3 = (TextView) findViewById(R.id.answer3);
        op4 = (TextView) findViewById(R.id.answer4);
        op5 = (TextView) findViewById(R.id.answer5);

        t1.setVisibility(View.INVISIBLE);
        t2 = (TextView) findViewById(R.id.votes);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                stopLoading();
            }
        });
    }

    private void stopLoading() {

    }

}

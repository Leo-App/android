package de.slg.startseite.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import de.slg.leoapp.R;
import de.slg.startseite.CardType;
import de.slg.startseite.activity.MainActivity;

/**
 * CardAddDialog.
 * <p>
 * Dialog zum Hinzufügen von Navigationselementen zur Startseite. Die ImageButtons entsprechen hier den Features (angepasster Tag), neue Buttons müssen also im
 * XML mit Feature-Tag hinzugefügt werden.
 *
 * @author Gianni
 * @version 2017.1311
 * @since 0.5.4
 */
public class CardAddDialog extends AlertDialog {

    private final MainActivity  mainActivity;
    private       ImageButton[] buttons;
    private       View[]        backgrounds;
    private       int           checkedItems;

    /**
     * Konstruktor.
     *
     * @param mainActivity MainActivity-Objekt.
     */
    public CardAddDialog(@NonNull MainActivity mainActivity) {
        super(mainActivity);
        this.mainActivity = mainActivity;
        checkedItems = 0;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_add_card);
        buttons = new ImageButton[]{
                findViewById(R.id.imageButton1),
                findViewById(R.id.imageButton2),
                findViewById(R.id.imageButton3),
                findViewById(R.id.imageButton4),
                findViewById(R.id.imageButton5),
                findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7),
                findViewById(R.id.imageButton8)
        };

        backgrounds = new View[]{
                findViewById(R.id.highlight1),
                findViewById(R.id.highlight2),
                findViewById(R.id.highlight3),
                findViewById(R.id.highlight4),
                findViewById(R.id.highlight5),
                findViewById(R.id.highlight6),
                findViewById(R.id.highlight7),
                findViewById(R.id.highlight8)
        };

        initOptions();
        initSendButton();
    }

    private void initOptions() {
        for (int i = 0; i < buttons.length; i++) {
            final ImageButton b  = buttons[i];
            final View vb = backgrounds[i];
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(vb.getTag() != null) {
                        checkedItems--;
                        b.setColorFilter(ContextCompat.getColor(mainActivity, R.color.colorPrimary));
                        vb.setVisibility(View.INVISIBLE);
                        vb.setTag(null);
                    } else {
                        checkedItems++;
                        b.setColorFilter(ContextCompat.getColor(mainActivity, android.R.color.white));
                        vb.setVisibility(View.VISIBLE);
                        vb.setTag(true);
                    }

                    if(checkedItems > 0)
                        findViewById(R.id.buttonDialog2).setEnabled(true);
                    else
                        findViewById(R.id.buttonDialog2).setEnabled(false);

                }
            });
        }
    }

    private void initSendButton() {
        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < buttons.length; i++) {

                    ImageButton b = buttons[i];
                    View vb = backgrounds[i];

                    if(vb.getTag() != null)
                        mainActivity.addCard(CardType.valueOf(b.getTag().toString()));

                }
                dismiss();
            }
        });
    }
}

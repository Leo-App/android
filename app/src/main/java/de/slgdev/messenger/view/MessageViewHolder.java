package de.slgdev.messenger.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.messenger.utility.Message;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    final MessageTextView nachricht;
    final TextView        absender;
    final TextView        datum;
    final TextView        uhrzeit;
    final View            progressbar;
    final LinearLayout    wrapper1;
    final LinearLayout    wrapper2;

    public MessageViewHolder(Context context) {
        super(
                (
                        (LayoutInflater)
                                context.getSystemService(
                                        Context.LAYOUT_INFLATER_SERVICE
                                )
                )
                        .inflate(
                                R.layout.list_item_message,
                                null,
                                false
                        )
        );

        nachricht = itemView.findViewById(R.id.nachricht);

        absender = itemView.findViewById(R.id.absender);

        datum = itemView.findViewById(R.id.textViewDate);

        uhrzeit = itemView.findViewById(R.id.datumRight);

        progressbar = itemView.findViewById(R.id.progressBar);

        wrapper1 = itemView.findViewById(R.id.chatbubblewrapper);
        wrapper2 = itemView.findViewById(R.id.chatbubblewrapper2);
    }

    void adaptMessage(Message message) {
        nachricht.setText(message.mtext);

        absender.setText(message.uname);

        uhrzeit.setText(message.getTime());

        datum.setText(message.getDate());

        if (message.getTime().equals("")) {
            progressbar.setVisibility(View.VISIBLE);

            uhrzeit.setVisibility(View.GONE);
        } else {
            progressbar.setVisibility(View.GONE);

            uhrzeit.setVisibility(View.VISIBLE);
        }
    }

    void setGravity(int gravity) {
        wrapper1.setGravity(gravity);
        wrapper2.setGravity(gravity);
    }

    void setTextColor(@ColorInt int color) {
        nachricht.setTextColor(color);
        uhrzeit.setTextColor(color);
    }
}
package de.slgdev.messenger.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;

public class MessageAdapter extends RecyclerView.Adapter {
    private final Context context;

    private final Chat.ChatType ctype;

    private final boolean[] selected;
    private final Message[] messagesArray;

    private final View.OnClickListener     clickListener;
    private final View.OnLongClickListener longClickListener;

    public MessageAdapter(Context context, Message[] messagesArray, View.OnClickListener clickListener, View.OnLongClickListener longClickListener, boolean[] selected, Chat.ChatType ctype) {
        this.context = context;
        this.messagesArray = messagesArray;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.selected = selected;
        this.ctype = ctype;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageViewHolder viewHolder = new MessageViewHolder(context);
        viewHolder.itemView.setOnLongClickListener(longClickListener);
        viewHolder.itemView.setOnClickListener(clickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder viewHolder = (MessageViewHolder) holder;

        final Message current = messagesArray[position];

        viewHolder.adaptMessage(current);

        if (current.uid == Utils.getUserID()) {
            viewHolder.setGravity(Gravity.END);
            viewHolder.setTextColor(
                    ContextCompat.getColor(
                            context,
                            android.R.color.background_light
                    )
            );

            viewHolder.itemView.findViewById(R.id.chatbubble).setEnabled(false);

            viewHolder.absender.setVisibility(View.GONE);
        } else {
            viewHolder.setGravity(Gravity.START);
            viewHolder.setTextColor(
                    ContextCompat.getColor(
                            context,
                            android.R.color.background_dark
                    )
            );

            viewHolder.itemView.findViewById(R.id.chatbubble).setEnabled(true);

            if (ctype == Chat.ChatType.PRIVATE) {
                viewHolder.absender.setVisibility(View.GONE);
            } else {
                viewHolder.absender.setVisibility(View.VISIBLE);
            }
        }

        if (position == 0 || !gleicherTag(current.mdate, messagesArray[position - 1].mdate)) {
            viewHolder.datum.setVisibility(View.VISIBLE);

            viewHolder.wrapper2.setPadding(0, (int) GraphicUtils.dpToPx(3), 0, 0);
        } else {
            viewHolder.datum.setVisibility(View.GONE);

            if (current.uid == messagesArray[position - 1].uid) {
                viewHolder.absender.setVisibility(View.GONE);
                viewHolder.wrapper2.setPadding(0, 0, 0, 0);
            } else {
                viewHolder.wrapper2.setPadding(0, (int) GraphicUtils.dpToPx(3), 0, 0);
            }
        }

        if (selected[position]) {
            viewHolder.wrapper1.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.colorAccentTransparent
                    )
            );
        } else {
            viewHolder.wrapper1.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            android.R.color.transparent
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return messagesArray.length;
    }

    private boolean gleicherTag(Date pDate1, Date pDate2) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(pDate1);
        c2.setTime(pDate2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }
}
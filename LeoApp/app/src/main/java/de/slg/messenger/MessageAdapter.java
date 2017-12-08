package de.slg.messenger;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.Utils;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;

public class MessageAdapter extends RecyclerView.Adapter {
    private final Context                  context;
    private final boolean[]                selected;
    private final Message[]                messagesArray;
    private final View.OnClickListener     clickListener;
    private final View.OnLongClickListener longClickListener;
    private final Chat.ChatType            ctype;

    public MessageAdapter(Context context, Message[] messagesArray, View.OnClickListener clickListener, View.OnLongClickListener longClickListener, boolean[] selected, Chat.ChatType ctype) {
        this.context = context;
        this.messagesArray = messagesArray;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.selected = selected;
        this.ctype = ctype;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message current = messagesArray[position];

        final View         v           = holder.itemView;
        final TextView     datum       = (TextView) v.findViewById(R.id.textViewDate);
        final TextView     nachricht   = (TextView) v.findViewById(R.id.nachricht);
        final TextView     absender    = (TextView) v.findViewById(R.id.absender);
        final TextView     uhrzeit     = (TextView) v.findViewById(R.id.datum);
        final LinearLayout layout      = (LinearLayout) v.findViewById(R.id.chatbubblewrapper);
        final View         chatbubble  = v.findViewById(R.id.chatbubble);
        final View         progressbar = v.findViewById(R.id.progressBar);

        nachricht.setText(current.mtext);
        absender.setText(current.uname);
        uhrzeit.setText(current.getTime());
        datum.setText(current.getDate());

        final boolean mine = current.uid == Utils.getUserID();
        if (mine) {
            layout.setGravity(Gravity.END);
            absender.setVisibility(View.GONE);
            nachricht.setTextColor(ContextCompat.getColor(context, android.R.color.background_light));
            uhrzeit.setTextColor(ContextCompat.getColor(context, android.R.color.background_light));
        } else {
            layout.setGravity(Gravity.START);
            nachricht.setTextColor(ContextCompat.getColor(context, android.R.color.background_dark));
            uhrzeit.setTextColor(ContextCompat.getColor(context, android.R.color.background_dark));
            if (ctype == Chat.ChatType.PRIVATE) {
                absender.setVisibility(View.GONE);
            } else {
                absender.setVisibility(View.VISIBLE);
            }
        }
        chatbubble.setEnabled(mine);

        final boolean send = uhrzeit.getText().toString().equals("");
        if (send) {
            uhrzeit.setVisibility(View.GONE);
            progressbar.setVisibility(View.VISIBLE);
        } else {
            uhrzeit.setVisibility(View.VISIBLE);
            progressbar.setVisibility(View.GONE);
        }

        final boolean first = position == 0 || !gleicherTag(current.mdate, messagesArray[position - 1].mdate);
        if (first) {
            datum.setVisibility(View.VISIBLE);
            layout.setPadding((int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(3), (int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(3));
        } else {
            datum.setVisibility(View.GONE);
            if (current.uid == messagesArray[position - 1].uid) {
                absender.setVisibility(View.GONE);
                layout.setPadding((int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(0), (int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(3));
            } else {
                layout.setPadding((int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(3), (int) GraphicUtils.dpToPx(6), (int) GraphicUtils.dpToPx(3));
            }
        }

        if (selected[position]) {
            v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccentTransparent));
        } else {
            v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        if (current.mread && (position == 0 || !messagesArray[position - 1].mread)) {
            v.findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
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
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder() {
            super(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_message, null));

            TextView nachricht = (TextView) itemView.findViewById(R.id.nachricht);
            nachricht.setMaxWidth(GraphicUtils.getDisplayWidth() * 2 / 3);
            TextView absender = (TextView) itemView.findViewById(R.id.absender);
            absender.setMaxWidth(GraphicUtils.getDisplayWidth() * 2 / 3);

            itemView.setOnLongClickListener(longClickListener);
            itemView.setOnClickListener(clickListener);
        }
    }
}

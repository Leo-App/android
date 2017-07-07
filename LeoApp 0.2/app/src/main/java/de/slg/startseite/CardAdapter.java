package de.slg.startseite;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.slg.leoapp.GraphicUtils;
import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Card> cards;

    {

        cards = new List<>();

        String card_config = Start.pref.getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;TUTORING;NEWS;SURVEY;SCHEDULE;SUBSTITUTION");

        for(String card : card_config.split(";")) {

            CardType type = CardType.valueOf(card);

            InfoCard c;
            MiscCard m;

            switch (type) {

                case FOODMARKS:
                    cards.append(c = new InfoCard(true));
                    c.title = Utils.getString(R.string.title_foodmarks);
                    c.descr = Utils.getString(R.string.summary_info_foodmark);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    c.icon = R.drawable.qrcode;

                    break;
                case TESTPLAN:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_testplan);
                    c.descr = Utils.getString(R.string.summary_info_testplan);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    c.enabled = Utils.isVerified();
                    c.icon = R.drawable.ic_content_paste_white_24dp;
                    break;
                case MESSENGER:
                    cards.append(c = new InfoCard(true));
                    c.title = Utils.getString(R.string.title_messenger);
                    c.descr = Utils.getString(R.string.summary_info_messenger);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    c.enabled = Utils.isVerified();
                    c.icon = R.drawable.ic_question_answer_white_24dp;
                    break;
                case TUTORING:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_tutoring);
                    c.descr = Utils.getString(R.string.summary_info_tutoring);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    c.enabled = Utils.isVerified();
                    c.icon = R.drawable.ic_people_white_24dp;
                    break;
                case NEWS:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_news);
                    c.descr = Utils.getString(R.string.summary_info_news);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    c.icon = R.drawable.ic_event_note_white_24dp;
                    break;
                case SURVEY:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_survey);
                    c.descr = Utils.getString(R.string.summary_info_survey);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    c.icon = R.drawable.ic_insert_emoticon_white_24dp;
                    break;
                case SCHEDULE:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_plan);
                    c.descr = Utils.getString(R.string.summary_info_schedule);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    c.enabled = Utils.isVerified();
                    c.icon = R.drawable.ic_event_white_24dp;
                    break;
                case SUBSTITUTION:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_subst);
                    c.descr = Utils.getString(R.string.summary_info_subst);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    c.icon = R.drawable.ic_account_switch;
                    break;
                case WEATHER:
                    cards.append(m = new MiscCard(false, type));
                    m.title = Utils.getString(R.string.title_subst);

                    break;
                case NEXT_TEST:
                    cards.append(m = new MiscCard(false, type));
                    m.title = Utils.getString(R.string.title_subst);
                    break;

            }

        }

    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        Button button;
        ImageView icon;
        RelativeLayout content;
        CardView wrapper;

        CardViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.info_title0);
            description = (TextView) itemView.findViewById(R.id.info_text0);
            button = (Button) itemView.findViewById(R.id.buttonCardView0);
            content = (RelativeLayout) itemView.findViewById(R.id.info_content0);
            icon = (ImageView) itemView.findViewById(R.id.info_card_icon);
            wrapper = (CardView)  itemView.findViewById(R.id.card_preset);
        }

    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        switch (viewType) {

            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cardview_feature_small, parent, false);

                return new CardViewHolder(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cardview_feature_large, parent, false);

                return new CardViewHolder(itemView);
            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cardview_feature_quick, parent, false);

                CardViewHolder ret = new CardViewHolder(itemView);

                ret.wrapper.requestLayout();
                ret.wrapper.getLayoutParams().height = GraphicUtils.getDisplayWidth()/2-(int)GraphicUtils.dpToPx(20);
                ret.wrapper.getLayoutParams().width = ret.wrapper.getLayoutParams().height;

                ret.icon.getLayoutParams().height = (ret.wrapper.getLayoutParams().height/100)*65; //Icon 65% of quick tile
                ret.icon.getLayoutParams().width = (ret.wrapper.getLayoutParams().height/100)*65;

                return ret;
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cardview_feature_small, parent, false);

                return new CardViewHolder(itemView);

        }

    }

    @Override
    public int getItemViewType(int position) {
        cards.toIndex(position);
        Card ref = cards.getContent();

        boolean quickLayout = Start.pref.getBoolean("pref_key_card_config_quick", false);

        return quickLayout ? 2 : ref.large ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        cards.toIndex(position);
        Card c = cards.getContent();


        if (c instanceof InfoCard) {
            InfoCard ref = (InfoCard) c;
            holder.button.setText(ref.buttonDescr);
            holder.button.setOnClickListener(ref.buttonListener);
            holder.title.setText(ref.title);
            holder.description.setText(ref.descr);
            holder.content.setVisibility(View.GONE);
            holder.icon.setImageResource(ref.icon);
            if(!ref.enabled)
                holder.icon.setColorFilter(Color.GRAY);
        } else {
            MiscCard ref = (MiscCard) c;
            holder.button.setText(ref.type.toString());
            holder.title.setText("");
            holder.description.setText("");
            holder.content.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return cards.length();
    }

}

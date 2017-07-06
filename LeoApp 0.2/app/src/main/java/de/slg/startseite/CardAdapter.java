package de.slg.startseite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Card> cards;

    {

        cards = new List<>();

        String card_config = Start.pref.getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;TUTORING;NEWS;SURVEY;SCHEDULE;SUBSTITUTION");

        for(String card : card_config.split(";")) {

            CardType type = CardType.valueOf(card);

            InfoCard c;

            switch (type) {

                case FOODMARKS:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_foodmarks);
                    c.descr = Utils.getString(R.string.summary_info_foodmark);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    break;
                case TESTPLAN:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_testplan);
                    c.descr = Utils.getString(R.string.summary_info_testplan);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    break;
                case MESSENGER:
                    cards.append(c = new InfoCard(true));
                    c.title = Utils.getString(R.string.title_messenger);
                    c.descr = Utils.getString(R.string.summary_info_messenger);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    break;
                case TUTORING:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_tutoring);
                    c.descr = Utils.getString(R.string.summary_info_tutoring);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    break;
                case NEWS:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_news);
                    c.descr = Utils.getString(R.string.summary_info_news);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    break;
                case SURVEY:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_survey);
                    c.descr = Utils.getString(R.string.summary_info_survey);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    break;
                case SCHEDULE:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_plan);
                    c.descr = Utils.getString(R.string.summary_info_schedule);
                    c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                    break;
                case SUBSTITUTION:
                    cards.append(c = new InfoCard(false));
                    c.title = Utils.getString(R.string.title_subst);
                    c.descr = Utils.getString(R.string.summary_info_subst);
                    c.buttonDescr = Utils.getString(R.string.button_info_try);
                    break;
                default:
                    cards.append(new MiscCard(false, type));

            }

        }

    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        Button button;
        ImageView icon;
        RelativeLayout content;

        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.info_title0);
            description = (TextView) itemView.findViewById(R.id.info_text0);
            button = (Button) itemView.findViewById(R.id.buttonCardView0);

            content = (RelativeLayout) itemView.findViewById(R.id.info_content0);

            icon = (ImageView) itemView.findViewById(R.id.info_card_icon);
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

                return new CardViewHolder(itemView);
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

        return quickLayout ? 0 : ref.large ? 1 : 2;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        cards.toIndex(position);
        Card c = cards.getContent();

        if (c instanceof InfoCard) {
            InfoCard ref = (InfoCard) c;
            holder.button.setText(ref.buttonDescr);
            holder.title.setText(ref.title);
            holder.description.setText(ref.descr);
            holder.content.setVisibility(View.GONE);
        } else {
            MiscCard ref = (MiscCard) c;
            holder.button.setText("");
            holder.title.setText("");
            holder.description.setText("");
            holder.content.setVisibility(View.VISIBLE);

            switch (ref.type) { //TODO: Layout in content entsprechend anpassen


                case WEATHER:
                    break;
                case NEXT_TEST:
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return cards.length();
    }

}

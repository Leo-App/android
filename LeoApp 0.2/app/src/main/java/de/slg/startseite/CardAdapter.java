package de.slg.startseite;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;


import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.GraphicUtils;
import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.leoview.InfoButton;
import de.slg.messenger.MessengerActivity;
import de.slg.nachhilfe.NachhilfeboerseActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;


class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements RecyclerViewItemListener {

    final List<Card> cards;

    {
        cards = new List<>();
        String card_config = Utils.getController().getPreferences().getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;NEWS;SURVEY;SCHEDULE;COMING_SOON");
        for (String card : card_config.split(";")) {
            if (card.length() > 0) {
                CardType type = CardType.valueOf(card);
                addToList(type);
            }
        }
    }

    void addToList(CardType type) {
        Card c;
        switch (type) {
            case FOODMARKS:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_foodmarks);
                c.desc = Utils.getString(R.string.summary_info_foodmark);
                c.icon = R.drawable.qrcode;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), EssensQRActivity.class));
                    }
                };

                break;
            case TESTPLAN:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_testplan);
                c.desc = Utils.getString(R.string.summary_info_testplan);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.content_paste;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), KlausurplanActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case MESSENGER:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_messenger);
                c.desc = Utils.getString(R.string.summary_info_messenger);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.messenger;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), MessengerActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                Log.e("Test", c.buttonListener.toString());
                break;
            case TUTORING:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_tutoring);
                c.desc = Utils.getString(R.string.summary_info_tutoring);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_people_white_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), NachhilfeboerseActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case NEWS:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_news);
                c.desc = Utils.getString(R.string.summary_info_news);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_event_note_white_24px;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), SchwarzesBrettActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case SURVEY:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_survey);
                c.desc = Utils.getString(R.string.summary_info_survey);
                c.icon = R.drawable.emoticon;
                c.enabled = true;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), StimmungsbarometerActivity.class));
                    }
                };
                break;
            case SCHEDULE:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_plan);
                c.desc = Utils.getString(R.string.summary_info_schedule);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.schedule;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), StundenplanActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case SUBSTITUTION:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_subst);
                c.desc = Utils.getString(R.string.summary_info_subst);
                c.icon = R.drawable.ic_account_switch;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), WrapperSubstitutionActivity.class));
                    }
                };
                break;
            case COMING_SOON:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.coming_soon);
                c.desc = Utils.getString(R.string.coming_soon);
                c.icon = R.drawable.ic_priority_high_white_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false))
                            new ComingSoonDialog(Utils.getController().getMainActivity()).show();
                    }
                };
                break;

        }
    }

    void updateCustomCards() {
        int i = 0;
        for (cards.toFirst(); cards.hasAccess(); cards.next()) {
            if (cards.getContent() instanceof Card)
                notifyItemChanged(i);
            i++;
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
                        .inflate(R.layout.layout_cardview_feature_quick, parent, false);
                CardViewHolder ret = new CardViewHolder(itemView);
                ret.wrapper.requestLayout();
                ret.wrapper.getLayoutParams().height = GraphicUtils.getDisplayWidth() / 2 - (int) GraphicUtils.dpToPx(20);
                ret.wrapper.getLayoutParams().width = ret.wrapper.getLayoutParams().height;
                ret.icon.getLayoutParams().height = (ret.wrapper.getLayoutParams().height / 100) * 66; //Icon 65% of quick tile
                ret.icon.getLayoutParams().width = (ret.wrapper.getLayoutParams().height / 100) * 66;
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
        boolean quickLayout = Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false);

        return quickLayout ? 1 : 2;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {

        cards.toIndex(position);
        Card c = cards.getContent();
        boolean quick = Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false);
        if (MainActivity.editing)
            holder.wrapper.setCardElevation(25);
        else
            holder.wrapper.setCardElevation(5);

        holder.button.addTooltip(c.title, c.desc);

        if (!quick) {
            holder.title.setText(c.title);
            if (c.title.equals(Utils.getString(R.string.coming_soon)))
                holder.icon.setColorFilter(Color.GRAY);
            else
                holder.icon.setColorFilter(null);
        }

        if(!c.enabled) {
            if (!quick) {
                holder.title.setTextColor(Color.GRAY);
                holder.imageButton.setColorFilter(Color.GRAY);
            }
            holder.icon.setColorFilter(Color.GRAY);
        }


        holder.button.setOnClickListener(c.buttonListener);
        holder.icon.setImageResource(c.icon);


    }


    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onItemDismiss(int position) {
        cards.toIndex(position);
        cards.remove();
        notifyItemRemoved(position);
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final ImageButton imageButton;
        final ImageView icon;
        final RelativeLayout content;
        final CardView wrapper;
        final Button btn;
        final InfoButton button;

        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.info_title0);
            content = (RelativeLayout) itemView.findViewById(R.id.info_content0);
            icon = (ImageView) itemView.findViewById(R.id.info_card_icon);
            wrapper = (CardView) itemView.findViewById(R.id.card_preset);
            btn = (Button) itemView.findViewById(R.id.buttonCard);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton10);
            button = new InfoButton(btn);
        }
    }

}
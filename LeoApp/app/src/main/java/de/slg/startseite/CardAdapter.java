package de.slg.startseite;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.slg.essensbons.activity.EssensQRActivity;
import de.slg.klausurplan.activity.KlausurplanActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.dialog.InformationDialog;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.List;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.InfoButton;
import de.slg.messenger.activity.MessengerActivity;
import de.slg.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slg.startseite.activity.MainActivity;
import de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity;
import de.slg.stundenplan.activity.StundenplanActivity;
import de.slg.umfragen.activity.SurveyActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;

/**
 * CardAdapter
 * <p>
 * Adapter für den RecyclerView der Startseiten-Navigation.
 *
 * @author Gianni
 * @version 2017.1811
 * @since 0.0.1
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements RecyclerViewItemListener {

    /**
     * Liste aller in der MainActivity angezeigten Cards.
     */
    public final List<Card> cards;

    {
        cards = new List<>();

        String card_config = Utils.getController().getPreferences().getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;NEWS;SURVEY;SCHEDULE;POLL;COMING_SOON;");

        for (String card : card_config.split(";")) {
            if (card.length() > 0) {
                CardType type = CardType.valueOf(card);
                addToList(type);
            }
        }
    }

    /**
     * Fügt eine Card mit dem Typ type zu {@link #cards} hinzu und setzt verschiedene Parameter.
     *
     * @param type Feature, zu dem die Card führen soll. (Siehe {@link CardType})
     */
    public void addToList(CardType type) {
        Card c;
        switch (type) {
            case FOODMARKS:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_foodmarks);
                c.desc = Utils.getString(R.string.summary_info_foodmark);
                c.icon = R.drawable.qrcode;
                c.enabled = Utils.isVerified();
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), EssensQRActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };

                break;
            case TESTPLAN:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_testplan);
                c.desc = Utils.getString(R.string.summary_info_testplan);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.icon_klausurplan;
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

                break;
            case NEWS:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.title_news);
                c.desc = Utils.getString(R.string.summary_info_news);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_pin;
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
                c.enabled = Utils.isVerified();
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), StimmungsbarometerActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
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
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_account_switch;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), WrapperSubstitutionActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case POLL: //Case hinzugefügt
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.umfragen);
                c.desc = Utils.getString(R.string.beschreibungUmfrage);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.icon_survey;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getController().getMainActivity().startActivity(new Intent(Utils.getContext(), SurveyActivity.class));
                        else
                            Utils.getController().getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case COMING_SOON:
                cards.append(c = new Card(type));
                c.title = Utils.getString(R.string.coming_soon);
                c.desc = Utils.getString(R.string.desc_coming_soon);
                c.enabled = true;
                c.icon = R.drawable.alert_decagram;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new InformationDialog(Utils.getController().getActiveActivity()).setText(R.string.dialog_comingsoon).show();
                    }
                };
                break;
        }
    }

    /**
     * Aktualisiert den Status aller Cards.
     */
    void updateCards() {
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
                ret.wrapper.getLayoutParams().height = GraphicUtils.getDisplayWidth() / 2 - (int) GraphicUtils.dpToPx(17);
                ret.wrapper.getLayoutParams().width = ret.wrapper.getLayoutParams().height;
                ret.icon.getLayoutParams().height = (ret.wrapper.getLayoutParams().height / 100) * 65; //Icon 65% of quick tile
                ret.icon.getLayoutParams().width = (ret.wrapper.getLayoutParams().height / 100) * 65;
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
        Card    c     = cards.getContent();
        boolean quick = Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false);
        if (MainActivity.editing) {
            holder.wrapper.setCardElevation(15);
            holder.button.setTooltipEnabled(false);
        } else {
            holder.wrapper.setCardElevation(5);
            holder.button.setTooltipEnabled(true);
        }

        holder.button.addTooltip(c.title, c.desc);

        if (!quick) {
            holder.title.setText(c.title);
            if (c.title.equals(Utils.getString(R.string.coming_soon)))
                holder.icon.setColorFilter(Color.GRAY);
            else
                holder.icon.setColorFilter(null);
        }

        if (!c.enabled) {
            if (!quick) {
                holder.title.setTextColor(Color.GRAY);
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

        final TextView       title;
        final ImageView      icon;
        final RelativeLayout content;
        final CardView       wrapper;
        final Button         btn;
        final InfoButton     button;

        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.info_title0);
            content = (RelativeLayout) itemView.findViewById(R.id.info_content0);
            icon = (ImageView) itemView.findViewById(R.id.info_card_icon);
            wrapper = (CardView) itemView.findViewById(R.id.card_preset);
            btn = (Button) itemView.findViewById(R.id.buttonCard);
            button = new InfoButton(btn);
        }
    }
}
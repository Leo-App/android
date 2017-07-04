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

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    List<Card> cards;

    {

        cards = new List<>();
        //TODO: Liste befüllen nach eingestellter Reihenfolge/Inhalt/Layout - Speichern in Preferences?

    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description;
        public Button button;
        public ImageView icon;
        public RelativeLayout content;

        public CardViewHolder(View itemView) {
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

        boolean quickLayout = false; //TODO: Richtigen Wert abrufen - Preferences?

        return quickLayout ? 0 : ref.large ? 1 : 2;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        cards.toIndex(position);
        Card c = cards.getContent();

        if(c instanceof InfoCard) {
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
                case ALARM:
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

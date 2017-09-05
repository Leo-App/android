package de.slg.startseite;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.GraphicUtils;
import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.nachhilfe.NachhilfeboerseActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;

import static android.view.View.GONE;
import static android.view.View.generateViewId;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> implements RecyclerViewItemListener {

    final List<Card> cards;

    {
        cards = new List<>();
        String card_config = Utils.getPreferences().getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;NEWS;SURVEY;SCHEDULE;COMING_SOON");
        for (String card : card_config.split(";")) {
            if (card.length() > 0) {
                CardType type = CardType.valueOf(card);
                addToList(type);
            }
        }
    }

    void addToList(CardType type) {
        InfoCard c;
        MiscCard m;
        switch (type) {
            case FOODMARKS:
                cards.append(c = new InfoCard(true, type));
                c.title = Utils.getString(R.string.title_foodmarks);
                c.descr = Utils.getString(R.string.summary_info_foodmark);
                c.buttonDescr = Utils.getString(R.string.button_info_try);
                c.icon = R.drawable.qrcode;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), EssensQRActivity.class));
                    }
                };
                break;
            case TESTPLAN:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_testplan);
                c.descr = Utils.getString(R.string.summary_info_testplan);
                c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.content_paste;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), KlausurplanActivity.class));
                        else
                            Utils.getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case MESSENGER:
                cards.append(c = new InfoCard(true, type));
                c.title = Utils.getString(R.string.title_messenger);
                c.descr = Utils.getString(R.string.summary_info_messenger);
                c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.messenger;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), MessengerActivity.class));
                        else
                            Utils.getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case TUTORING:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_tutoring);
                c.descr = Utils.getString(R.string.summary_info_tutoring);
                c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_people_white_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), NachhilfeboerseActivity.class));
                        else
                            Utils.getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case NEWS:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_news);
                c.descr = Utils.getString(R.string.summary_info_news);
                c.buttonDescr = Utils.getString(R.string.button_info_try);
                c.icon = R.drawable.ic_event_note_white_24px;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), SchwarzesBrettActivity.class));
                    }
                };
                break;
            case SURVEY:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_survey);
                c.descr = Utils.getString(R.string.summary_info_survey);
                c.buttonDescr = Utils.getString(R.string.button_info_try);
                c.icon = R.drawable.emoticon;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), StimmungsbarometerActivity.class));
                    }
                };
                break;
            case SCHEDULE:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_plan);
                c.descr = Utils.getString(R.string.summary_info_schedule);
                c.buttonDescr = Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
                c.enabled = Utils.isVerified();
                c.icon = R.drawable.schedule;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isVerified())
                            Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), StundenplanActivity.class));
                        else
                            Utils.getMainActivity().showVerificationDialog();
                    }
                };
                break;
            case SUBSTITUTION:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_subst);
                c.descr = Utils.getString(R.string.summary_info_subst);
                c.buttonDescr = Utils.getString(R.string.button_info_try);
                c.icon = R.drawable.ic_account_switch;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.getMainActivity().startActivity(new Intent(Utils.getContext(), WrapperSubstitutionActivity.class));
                    }
                };
                break;
            case COMING_SOON:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.coming_soon);
                c.descr = Utils.getString(R.string.todo_description);
                c.buttonDescr = null;
                c.icon = R.drawable.ic_priority_high_white_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.getPreferences().getBoolean("pref_key_card_config_quick", false))
                            new ComingSoonDialog(Utils.getMainActivity()).show();
                    }
                };
                break;
            case WEATHER:
                cards.append(m = new MiscCard(type));
                m.title = Utils.getString(R.string.card_title_weather);
                break;
        }
    }

    void updateCustomCards() {
        int i = 0;
        for (cards.toFirst(); cards.hasAccess(); cards.next()) {
            if (cards.getContent() instanceof MiscCard)
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
                        .inflate(R.layout.layout_cardview_feature_large, parent, false);
                return new CardViewHolder(itemView);
            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_cardview_feature_quick, parent, false);
                CardViewHolder ret = new CardViewHolder(itemView);
                ret.wrapper.requestLayout();
                ret.wrapper.getLayoutParams().height = GraphicUtils.getDisplayWidth() / 2 - (int) GraphicUtils.dpToPx(20);
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
        Card    ref         = cards.getContent();
        boolean quickLayout = Utils.getPreferences().getBoolean("pref_key_card_config_quick", false);
        return quickLayout ? 2 : ref.large ? 1 : 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        cards.toIndex(position);
        Card    c     = cards.getContent();
        boolean quick = Utils.getPreferences().getBoolean("pref_key_card_config_quick", false);
        if (MainActivity.editing)
            holder.wrapper.setCardElevation(25);
        else
            holder.wrapper.setCardElevation(5);
        holder.button.setEnabled(!MainActivity.editing);
        if (c instanceof InfoCard) {
            InfoCard ref = (InfoCard) c;
            holder.button.setText(ref.buttonDescr);
            holder.button.setOnClickListener(ref.buttonListener);
            holder.title.setText(ref.title);
            if (ref.buttonDescr == null && !quick)
                holder.button.setVisibility(GONE);
            if (ref.title.equals(Utils.getString(R.string.coming_soon)))
                holder.icon.setColorFilter(Color.GRAY);
            else
                holder.icon.setColorFilter(null);
            holder.description.setText(ref.descr);
            holder.content.setVisibility(GONE);
            holder.icon.setImageResource(ref.icon);
            if (!ref.enabled)
                holder.icon.setColorFilter(Color.GRAY);
        } else if (c instanceof MiscCard) {
            holder.button.setVisibility(GONE);
            holder.title.setVisibility(GONE);
            holder.description.setVisibility(GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(GONE);
            ImageView weatherIcon = new ImageView(Utils.getContext());
            weatherIcon.setId(generateViewId());
            weatherIcon.setImageResource(R.drawable.weather_partlycloudy);
            TextView temperature = new TextView(Utils.getContext());
            temperature.setId(generateViewId());
            TextView humidity = new TextView(Utils.getContext());
            new WeatherUpdateTask().execute(weatherIcon, temperature, humidity);
            weatherIcon.setColorFilter(Color.rgb(0x00, 0x91, 0xea));
            if (quick) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 25, 0, 0);
                layoutParams.height = (holder.wrapper.getLayoutParams().height / 100) * 60;
                layoutParams.width = (holder.wrapper.getLayoutParams().height / 100) * 60;
                weatherIcon.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams layoutParamsTextViewTemp = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                layoutParamsTextViewTemp.addRule(RelativeLayout.BELOW, weatherIcon.getId());
                layoutParamsTextViewTemp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                temperature.setLayoutParams(layoutParamsTextViewTemp);
                temperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                temperature.setTextColor(Color.rgb(0x00, 0x91, 0xea));
                RelativeLayout.LayoutParams layoutParamsTextViewHumid = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                layoutParamsTextViewHumid.addRule(RelativeLayout.BELOW, temperature.getId());
                layoutParamsTextViewHumid.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                humidity.setLayoutParams(layoutParamsTextViewHumid);
                humidity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                humidity.setTextColor(Color.rgb(0x00, 0x91, 0xea));
                holder.content.addView(temperature);
                holder.content.addView(humidity);
            } else {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(Utils.getString(R.string.card_title_weather));
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                layoutParams.setMargins(20, 0, 0, 0);
                layoutParams.height = (holder.wrapper.getLayoutParams().height / 100) * 70;
                layoutParams.width = (holder.wrapper.getLayoutParams().height / 100) * 70;
                weatherIcon.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams layoutParamsTextViewTemp = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                layoutParamsTextViewTemp.addRule(RelativeLayout.RIGHT_OF, weatherIcon.getId());
                layoutParamsTextViewTemp.addRule(RelativeLayout.ABOVE, humidity.getId());
                //   layoutParamsTextViewTemp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                layoutParamsTextViewTemp.setMargins(80, 0, 0, 0);
                temperature.setLayoutParams(layoutParamsTextViewTemp);
                temperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
                temperature.setTextColor(Color.rgb(0x00, 0x91, 0xea));
                RelativeLayout.LayoutParams layoutParamsTextViewHumid = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                layoutParamsTextViewHumid.addRule(RelativeLayout.BELOW, temperature.getId());
                layoutParamsTextViewHumid.addRule(RelativeLayout.RIGHT_OF, weatherIcon.getId());
                //  layoutParamsTextViewHumid.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                layoutParamsTextViewHumid.setMargins(80, 0, 0, 0);
                humidity.setId(generateViewId());
                humidity.setLayoutParams(layoutParamsTextViewHumid);
                humidity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                humidity.setTextColor(Color.rgb(0x00, 0x91, 0xea));
                RelativeLayout wrapper = new RelativeLayout(Utils.getContext());
                wrapper.addView(temperature);
                wrapper.addView(humidity);
                RelativeLayout.LayoutParams layoutParamsWrapper = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                layoutParamsWrapper.addRule(RelativeLayout.CENTER_VERTICAL, temperature.getId());
                layoutParamsWrapper.addRule(RelativeLayout.RIGHT_OF, weatherIcon.getId());
                wrapper.setLayoutParams(layoutParamsWrapper);
                holder.content.addView(wrapper);
            }
            holder.content.addView(weatherIcon);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        cards.toIndex(fromPosition);
        Card temp = cards.getContent();
        cards.remove();
        cards.toIndex(toPosition);
        cards.insertBefore(temp);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemMoved(toPosition, fromPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        cards.toIndex(position);
        notifyItemRemoved(position);
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        final TextView       title;
        final TextView       description;
        final Button         button;
        final ImageView      icon;
        final RelativeLayout content;
        final CardView       wrapper;

        CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.info_title0);
            description = (TextView) itemView.findViewById(R.id.info_text0);
            button = (Button) itemView.findViewById(R.id.buttonCardView0);
            content = (RelativeLayout) itemView.findViewById(R.id.info_content0);
            icon = (ImageView) itemView.findViewById(R.id.info_card_icon);
            wrapper = (CardView) itemView.findViewById(R.id.card_preset);
        }
    }

    private class WeatherUpdateTask extends AsyncTask<View, Void, String[]> {

        private ImageView icon;
        private TextView  temp, humid;

        @Override
        protected String[] doInBackground(View... params) {
            icon = (ImageView) params[0];
            temp = (TextView) params[1];
            humid = (TextView) params[2];
            try {
                if (!Utils.checkNetwork())
                    return null;
                URL            apiURL = new URL(Utils.BASE_URL + "getWeatherData.php");
                BufferedReader b      = new BufferedReader(new InputStreamReader(apiURL.openConnection().getInputStream()));
                String         current;
                StringBuilder  json   = new StringBuilder();
                while ((current = b.readLine()) != null)
                    json.append(current);
                StringBuilder weatherCode = new StringBuilder();
                StringBuilder tempCode    = new StringBuilder();
                StringBuilder humidCode   = new StringBuilder();
                int           indexStart  = json.indexOf("\"description\":");
                for (int i = indexStart + 15; json.charAt(i) != '"'; i++)
                    weatherCode.append(json.charAt(i));
                indexStart = json.indexOf("\"temp\":");
                for (int i = indexStart + 7; json.charAt(i) != ','; i++)
                    tempCode.append(json.charAt(i));
                indexStart = json.indexOf("\"humidity\":");
                for (int i = indexStart + 11; json.charAt(i) != ','; i++)
                    humidCode.append(json.charAt(i));
                return new String[]{weatherCode.toString(), tempCode.toString(), humidCode.toString()};
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String... strings) {
            if (strings == null) {
                icon.setImageResource(R.drawable.weather_no_connection_alt);
                icon.setColorFilter(Color.GRAY);
                temp.setText("-");
                temp.setTextColor(Color.GRAY);
                humid.setText(Utils.getString(R.string.snackbar_no_connection_info));
                humid.setTextColor(Color.GRAY);
                return;
            }
            switch (strings[0]) {
                case "clear sky":
                    icon.setImageResource(R.drawable.weather_sunny);
                    break;
                case "few clouds":
                    icon.setImageResource(R.drawable.weather_partlycloudy);
                    break;
                case "scattered clouds":
                    icon.setImageResource(R.drawable.weather_cloudy);
                    break;
                case "broken clouds":
                    icon.setImageResource(R.drawable.weather_cloudy);
                    break;
                case "rain":
                    icon.setImageResource(R.drawable.weather_pouring);
                    break;
                case "thunderstorm":
                    icon.setImageResource(R.drawable.weather_lightning);
                    break;
                case "snow":
                    icon.setImageResource(R.drawable.weather_snowy);
                    break;
                case "mist":
                    icon.setImageResource(R.drawable.weather_fog);
                    break;
            }
            double        temp = Double.parseDouble(strings[1]) - 273.15;
            DecimalFormat df   = new DecimalFormat("#.#");
            this.temp.setText(df.format(temp) + "\u2103");
            this.humid.setText(strings[2] + "%");
        }
    }
}

package de.slg.startseite;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.GraphicUtils;
import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;
import de.slg.messenger.OverviewWrapper;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

import static android.view.View.GONE;
import static android.view.View.generateViewId;

class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    final List<Card> cards;

    {

        cards = new List<>();

        String card_config = Start.pref.getString("pref_key_card_config",
                "FOODMARKS;TESTPLAN;MESSENGER;TUTORING;NEWS;SURVEY;SCHEDULE;SUBSTITUTION");

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
                        Utils.getMainActivity().startActivity(new Intent(Utils.context, WrapperQRActivity.class));
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
                            Utils.getMainActivity().startActivity(new Intent(Utils.context, KlausurplanActivity.class));
                        else
                            Utils.getMainActivity().showDialog();
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
                            Utils.getMainActivity().startActivity(new Intent(Utils.context, OverviewWrapper.class));
                        else
                            Utils.getMainActivity().showDialog();
                    }
                };
                break;
            case TUTORING:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_tutoring);
                c.descr = Utils.getString(R.string.summary_info_tutoring);
                c.buttonDescr = Utils.getString(R.string.coming_soon); // Utils.getString(Utils.isVerified() ? R.string.button_info_try : R.string.button_info_auth);
              //  c.enabled = Utils.isVerified();
                c.icon = R.drawable.ic_priority_high_black_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                  /*      if (Utils.isVerified())
                            Utils.getMainActivity().startActivity(new Intent(Utils.context, NachhilfeboerseActivity.class));
                        else
                            Utils.getMainActivity().showDialog(); */
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
                        Utils.getMainActivity().startActivity(new Intent(Utils.context, SchwarzesBrettActivity.class));
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
                        Utils.getMainActivity().startActivity(new Intent(Utils.context, StimmungsbarometerActivity.class));
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
                            Utils.getMainActivity().startActivity(new Intent(Utils.context, WrapperStundenplanActivity.class));
                        else
                            Utils.getMainActivity().showDialog();
                    }
                };
                break;
            case SUBSTITUTION:
                cards.append(c = new InfoCard(false, type));
                c.title = Utils.getString(R.string.title_subst);
                c.descr = Utils.getString(R.string.summary_info_subst);
                c.buttonDescr = Utils.getString(R.string.coming_soon);
                c.icon = R.drawable.ic_priority_high_black_24dp;
                c.buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Utils.getMainActivity().startActivity(new Intent(Utils.context, WrapperSubstitutionActivity.class));
                    }
                };
                break;
            case WEATHER:
                cards.append(m = new MiscCard(false, type, true));
                m.title = Utils.getString(R.string.title_subst);

                break;
            case NEXT_TEST:
                cards.append(m = new MiscCard(false, type, true));
                m.title = Utils.getString(R.string.title_subst);
                break;

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
        Card ref = cards.getContent();

        boolean quickLayout = Start.pref.getBoolean("pref_key_card_config_quick", false);

        return quickLayout ? 2 : ref.large ? 1 : 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        cards.toIndex(position);
        Card c = cards.getContent();


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
            if (ref.buttonDescr.equals(Utils.getString(R.string.coming_soon))) {
                //  holder.icon.setColorFilter(Color.rgb(0x00,0x91, 0xea));
                holder.icon.setColorFilter(Color.rgb(0xf4, 0x43, 0x36));
                if(Start.pref.getBoolean("pref_key_card_config_quick", false))
                    holder.title.setText(ref.buttonDescr);
            } else
                holder.icon.setColorFilter(null);
            holder.description.setText(ref.descr);
            holder.content.setVisibility(GONE);
            holder.icon.setImageResource(ref.icon);
            if (!ref.enabled)
                holder.icon.setColorFilter(Color.GRAY);
        } else {
            MiscCard ref = (MiscCard) c;
            holder.button.setVisibility(GONE);
            holder.title.setVisibility(GONE);
            holder.description.setVisibility(GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(GONE);

            ImageView weatherIcon = new ImageView(Utils.context);
            weatherIcon.setId(generateViewId());
            weatherIcon.setImageResource(R.drawable.weather_partlycloudy);
            TextView temperature = new TextView(Utils.context);
            temperature.setId(generateViewId());
            TextView humidity = new TextView(Utils.context);

            new WeatherUpdateTask().execute(weatherIcon, temperature, humidity);

            weatherIcon.setColorFilter(Color.rgb(0x00, 0x91, 0xea));

            if(Start.pref.getBoolean("pref_key_card_config_quick", false)) {
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

            }

            holder.content.addView(weatherIcon);
            holder.content.addView(temperature);
            holder.content.addView(humidity);

        }

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final Button button;
        final ImageView icon;
        final RelativeLayout content;
        final CardView wrapper;

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
        private TextView temp, humid;

        @Override
        protected String[] doInBackground(View... params) {

            icon = (ImageView) params[0];
            temp = (TextView) params[1];
            humid = (TextView) params[2];

            try {

                if(!Utils.checkNetwork())
                    return null;

                URL apiURL = new URL("http://moritz.liegmanns.de/getWeatherData.php");
                BufferedReader b = new BufferedReader(new InputStreamReader(apiURL.openConnection().getInputStream()));
                String current;
                StringBuilder json = new StringBuilder();
                while((current = b.readLine()) != null)
                    json.append(current);

                StringBuilder weatherCode = new StringBuilder();
                StringBuilder tempCode = new StringBuilder();
                StringBuilder humidCode = new StringBuilder();

                int indexStart = json.indexOf("\"description\":");
                for(int i = indexStart+15; json.charAt(i) != '"'; i++)
                    weatherCode.append(json.charAt(i));

                indexStart = json.indexOf("\"temp\":");
                for(int i = indexStart+7; json.charAt(i) != ','; i++)
                    tempCode.append(json.charAt(i));

                indexStart = json.indexOf("\"humidity\":");
                for(int i = indexStart+11; json.charAt(i) != ','; i++)
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

            if(strings == null) {
                icon.setImageResource(R.drawable.weather_no_connection_alt);
                icon.setColorFilter(Color.GRAY);
                temp.setText("-");
                temp.setTextColor(Color.GRAY);
                humid.setText(Utils.getString(R.string.snackbar_no_connection_info));
                humid.setTextColor(Color.GRAY);
                return;
            }

            Log.wtf("LeoApp", strings[0]);
            Log.wtf("LeoApp", strings[1]);
            Log.wtf("LeoApp", strings[2]);

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

            double temp = Double.parseDouble(strings[1])-273.15;
            DecimalFormat df = new DecimalFormat("#.#");

            this.temp.setText(df.format(temp)+"\u2103");
            this.humid.setText(strings[2]+"%");

        }
    }

}

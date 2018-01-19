package de.slg.umfragen.activity;

import android.support.v7.app.AppCompatActivity;

public class SavedActivity extends AppCompatActivity {

    //    @Override
    //    protected void onCreate(Bundle savedInstanceState) {
    //        super.onCreate(savedInstanceState);
    //        setContentView(R.layout.activity_saved);
    //
    //    }
    //
    //    private String[] readFromDB() {
    //        SQLiteConnectorUmfragenSpeichern db = new SQLiteConnectorUmfragenSpeichern(Utils.getContext());
    //        SQLiteDatabase dbh = db.getWritableDatabase();
    //
    //        dbh.
    //    }
    //
    //    private static class ListAdapter extends ArrayAdapter<Fach> {
    //        private final Context cont;
    //        private
    //
    //        ListAdapter(Context pContext, Fach[] pUmfragen) {
    //            super(pContext, R.layout.list_item_schulstunde, pFach);
    //            cont = pContext;
    //        }
    //
    //        @NonNull
    //        @Override
    //        public View getView(int position, View v, @NonNull ViewGroup parent) {
    //            if (position < fachAd.length && fachAd[0] != null) {
    //                if (v == null) {
    //                    LayoutInflater layoutInflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //                    v = layoutInflater.inflate(R.layout.list_item_schulstunde, null);
    //                }
    //
    //                TextView tvFach   = v.findViewById(R.id.fach_wt);
    //                TextView tvLehrer = v.findViewById(R.id.lehrer_wt);
    //                TextView tvRaum   = v.findViewById(R.id.raum_wt);
    //                TextView tvStunde = v.findViewById(R.id.stunde_wt);
    //                TextView tvNotiz  = v.findViewById(R.id.notiz);
    //
    //                if (fachAd[position] != null) {
    //                    if (fachAd[position].getName() != null && fachAd[position].getNotiz() != null && fachAd[position].getName().equals("") && !fachAd[position].getNotiz().equals("")) {
    //                        tvNotiz.setText(fachAd[position].getNotiz());
    //                        tvFach.setVisibility(View.INVISIBLE);
    //                        tvLehrer.setVisibility(View.INVISIBLE);
    //                        tvRaum.setVisibility(View.INVISIBLE);
    //                        tvNotiz.setVisibility(View.VISIBLE);
    //                    } else {
    //                        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
    //                            tvFach.setText(fachAd[position].getName() + ' ' + fachAd[position].getKuerzel());
    //                        else
    //                            tvFach.setText(fachAd[position].getName());
    //                    }
    //                    if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
    //                        tvLehrer.setText(fachAd[position].getKlasse());
    //                    } else {
    //                        tvLehrer.setText(fachAd[position].getLehrer());
    //                    }
    //                    tvRaum.setText(fachAd[position].getRaum());
    //                    tvStunde.setText(fachAd[position].getStundenName());
    //                    if (fachAd[position].getSchriftlich() && Utils.getUserPermission() != User.PERMISSION_LEHRER) {
    //                        v.findViewById(R.id.iconSchriftlich).setVisibility(View.VISIBLE);
    //                    }
    //                }
    //            }
    //            return v;
    //        }
    //    }
}

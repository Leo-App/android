package de.slgdev.vertretungsplan.task;




import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import de.slgdev.leoapp.sqlite.SQLiteConnectorVertretungsplan;
import de.slgdev.leoapp.task.general.VoidCallbackTask;
import de.slgdev.leoapp.utility.NetworkPerformance;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.Queue;
import de.slgdev.vertretungsplan.utility.MyAuthenticator;
import de.slgdev.vertretungsplan.utility.VertretungsEvent;

public class ImporterVertretung extends VoidCallbackTask<Void> {

    private URL url1;
    private URL url2;
    private BufferedReader br;
    private String line;
    private String quelltext1;
    private String quelltext2;
    private SQLiteConnectorVertretungsplan database;

    @Override
    protected Void doInBackground(Void... voids) {

        if (Utils.isNetworkAvailable() && (Utils.getNetworkPerformance()== NetworkPerformance.EXCELLENT)) {
            database = new SQLiteConnectorVertretungsplan(Utils.getContext());
            quelltext1 = "";
            quelltext2 = "";
            try {

                Authenticator.setDefault(new MyAuthenticator());
                url1 = new URL("http://slg-aachen.de/vertretungsplan/subst_001.htm");
                url2 = new URL("http://slg-aachen.de/vertretungsplan/subst_002.htm");

                br = new BufferedReader(new InputStreamReader(url1.openStream()));
                while ((line = br.readLine()) != null) {
                    quelltext1 += line;
                }
                br.close();

                br = new BufferedReader(new InputStreamReader(url2.openStream()));
                while ((line = br.readLine()) != null) {
                    quelltext2 += line;
                }
                br.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            database.deleteTable();
            insertData(parsen(quelltext1), 1);
            insertData(parsen(quelltext2), 2);
            database.close();
        }
        return null;
    }


    private Queue<VertretungsEvent> parsen(String htmlCode) {

        Queue<VertretungsEvent> ret = new Queue<VertretungsEvent>();
        if (!htmlCode.equals(""))   {

            int[] reihenfolge = new int[8];
            List<String> titel = Jsoup.parse(htmlCode).body().getElementsByClass("mon_list").first().getElementsByTag("th").eachText();
            for (int i=0; !titel.isEmpty() && i<8; i++) {
                reihenfolge[i] = gibNummerZuSpalte(titel.get(0));
                titel.remove(0);
            }
            boolean moeglicheReihenfolge = true;
            for (int j=0; j<8; j++) {
                if (reihenfolge[j] == -1)
                    moeglicheReihenfolge = false;
            }
                if (!moeglicheReihenfolge) {
                for (int k = 0; k < 8; k++)
                    reihenfolge[k] = k;
            }


            Element table = Jsoup.parse(htmlCode).body().getElementsByClass("mon_list").first();
            List<String> tds = table.getElementsByTag("td").eachText();
            if (tds.size()>1) {
                while (!tds.isEmpty()) {
                    if (!tds.get(0).contains("("))
                        ret.append(new VertretungsEvent(tds.get(reihenfolge[0]), tds.get(reihenfolge[1]), tds.get(reihenfolge[2]), tds.get(reihenfolge[3]), tds.get(reihenfolge[4]), tds.get(reihenfolge[5]), tds.get(reihenfolge[6]), tds.get(reihenfolge[7]).equals("x"), Jsoup.parse(htmlCode).getElementsByClass("mon_title").text()));
                    for (int i = 0; i < 8; i++) {
                        tds.remove(0);
                    }
                }
            }
        }
        return ret;
    }


    private int gibNummerZuSpalte(String spaltenName) {
        int ret=-1;
        switch (spaltenName) {
            case "Klasse(n)": ret=0;
                break;
            case "Stunde": ret=1;
                break;
            case "Vertreter": ret=2;
                break;
            case "Fach": ret=3;
                break;
            case "Raum": ret=4;
                break;
            case "(Lehrer)": ret=5;
                break;
            case "Vertretungs-Text": ret=6;
                break;
            case "Entfall": ret=7;
                break;
            default: ret=-1;
                break;
        }
        return ret;
    }

    private void insertData(Queue<VertretungsEvent> plan, int nr)   {
        while (!plan.isEmpty()) {
            if (nr == 1)
                database.insert(plan.getContent(), "vertretung1");
            else
                database.insert(plan.getContent(), "vertretung2");
            plan.remove();
        }
    }

}

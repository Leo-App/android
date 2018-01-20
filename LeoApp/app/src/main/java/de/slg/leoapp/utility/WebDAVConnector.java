package de.slg.leoapp.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import de.slg.leoapp.utility.datastructure.List;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * WebDAVConnector
 * <p>
 * Eine Readonly Schnittstelle für die WebDAV Ressourcen des pädagogischen Netzes.
 *
 * @author Gianni
 * @version 2017.0311
 * @since 0.5.8
 */

@SuppressWarnings("WeakerAccess")
public class WebDAVConnector {

    private OkHttpClient connection;
    private String       currentDirectory;

    /**
     * Konstruktor.
     *
     * @param name     Benutzername für die RegioIT Basic Authentification
     * @param password Passwort für die RegioIT Basic Authentification
     */
    public WebDAVConnector(final String name, final String password) {

        connection = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Nullable
                    @Override
                    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
                        String credentials = Credentials.basic(name, password);
                        return response.request().newBuilder()
                                .header("Authorization", credentials)
                                .build();
                    }
                })
                .build();

        this.currentDirectory = "";
    }

    //TODO: Lehrer: Heimatverzeichnis, Schüler: PrivatSchueler ... / Meine Gruppen Wenn Benutzername Länger als 6 buchstaben ist, SChülerverzeichnis sonst Lehrerverzeichnis

    /**
     * Gibt eine Liste der Inhalte des aktuellen Verzeichnisses zurück.
     *
     * @return Liste mit Verzeichnisinhalten
     */
    public List<String> getDirContent() {

        try {

            Request request = new Request.Builder()
                    .url(Utils.URL_WEBDAV + currentDirectory)
                    .method("PROPFIND", null)
                    .build();

            List<String> files = new List<>();

            Response response = connection.newCall(request).execute();
            String   result   = response.body().string();

            for (int i = 0; i < result.length(); i++) {
                int newIndex = result.indexOf(Utils.URL_WEBDAV + currentDirectory, i) + (Utils.URL_WEBDAV + currentDirectory).length() + 1;
                if (newIndex > i) {
                    i = newIndex;
                    String toAppend = result.substring(i, result.indexOf('<', i));
                    Utils.logError(toAppend);
                    if (toAppend.contains("/D:href>")) //Temporärer Fix
                        continue;
                    files.append(toAppend);
                }
            }

            return files;
        } catch (IOException e) {
            Utils.logError(e);
            return null;
        }
    }

    /**
     * Wechselt das aktuelle Verzeichnis, startet der Parameter name mit einem Slash / handelt es sich um einen absoluten Pfad.
     * Ist der Parameter null, wird ein Verzeichnis nach oben gewechselt.
     *
     * @param name Verzeichnispfad
     */
    public void changeDirectory(@Nullable String name) {
        name = name.replace(" ", "%20");
        name = name.endsWith("/") ? name.substring(0, name.length() - 2) : name;
        if (name == null) {
            if (currentDirectory.length() <= 1)
                return;
            int index = currentDirectory.lastIndexOf('/');
            currentDirectory = currentDirectory.substring(0, index);
        } else {
            currentDirectory = name.startsWith("/") ? name : (currentDirectory + "/" + name);
        }
    }
}

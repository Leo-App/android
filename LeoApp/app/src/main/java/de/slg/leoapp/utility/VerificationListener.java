package de.slg.leoapp.utility;

import android.support.v4.app.Fragment;

/**
 * VerificationListener.
 * <p>
 * Interface für Klassen, die über einen Verifizierungsvorgang informiert werden müssen.
 *
 * @author Gianni
 * @version 2017.2312
 * @since 0.7.0
 */

public interface VerificationListener {
    void onVerificationProcessed(ResponseCode response, Fragment fragment);

    void onSynchronisationProcessed(ResponseCode response, Fragment fragment);
}

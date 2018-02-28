package de.slgdev.startseite;

import org.junit.Test;

import de.slgdev.leoapp.sync.NewsSynchronizer;
import de.slgdev.leoapp.sync.SurveySynchronizer;
import de.slgdev.leoapp.utility.Utils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class NotificationConditionTest {

    @Test
    public void onValidInternetConnectionSynchronizersShouldReturnTrue() {

        NewsSynchronizer synchronizerN = new NewsSynchronizer();
        assertTrue("NewsSynchronizer does not work properly", synchronizerN.run());
        SurveySynchronizer synchronizerS = new SurveySynchronizer();
        assertTrue("SurveySynchronizer does not work properly", synchronizerS.run());
    }
}
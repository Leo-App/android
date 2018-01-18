package de.slg.it_problem.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.slg.it_problem.activity.ITActivity;
import de.slg.it_problem.utility.FragmentType;
import de.slg.it_problem.utility.Subject;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.Utils;

// TODO abc123$

/**
 * SelectionFragment.
 *
 * Verwaltet die Auswahl der Problembereiche. Kann nach Bedarf erweitert werden, dazu muss jedoch zusätzlich das Layout angepasst
 * und neue {@link Subject Subjects} in der entsprechenden Klasse registriert werden, da sonst eine Exception geworfen wird.
 *
 * @author Gianni
 * @since 0.7.2
 * @version 2017.3112
 */
public class SelectionFragment extends Fragment implements TaskStatusListener {

    private View v;

    /**
     * Instanziiert ein neues SelectionFragment, basierend auf einem boolean Parameter, der angibt ob
     * die Buttons nach Synchronisation oder direkt initialisiert werden.
     *
     * @param sync Findet ein Synchronisationvorgang statt?
     * @return Neue SelectionFragment Instanz
     */
    public static SelectionFragment newInstance(boolean sync) {
        SelectionFragment fragment = new SelectionFragment();

        Bundle b = new Bundle();
        b.putBoolean("sync_start", !sync);

        fragment.setArguments(b);

        Utils.logError("INSTATIATE");

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_it_overview, container, false);

        Utils.logError("CREATEVIEW");

        if (getArguments().getBoolean("sync_start"))
            initButtons();

        return v;
    }

    @Override
    public void taskFinished(Object... params) {
        initButtons();
    }

    private void initButtons() {
        v.findViewById(R.id.button_beamer).setOnClickListener(new ButtonClickListener(Subject.BEAMER));
        v.findViewById(R.id.button_network).setOnClickListener(new ButtonClickListener(Subject.NETWORK));
        v.findViewById(R.id.button_computer).setOnClickListener(new ButtonClickListener(Subject.COMPUTER));
    }

    private class ButtonClickListener implements View.OnClickListener {

        ITActivity reference;
        String subject;

        private ButtonClickListener(String subject) {
            reference = (ITActivity) getActivity();
            this.subject = subject;
        }

        @Override
        public void onClick(View v) {
            reference.resetSession();
            reference.setSubject(subject);

            if (!reference.getCurrentSession().isAvailable()) {
                GraphicUtils.sendToast("Keine Lösungen verfügbar");
                return;
            }

            reference.startFragment(FragmentType.QUESTION);
        }

    }

}

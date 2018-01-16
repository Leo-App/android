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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.fragment_it_overview, container, false);
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

        private ButtonClickListener(String subject) {
            reference = (ITActivity) getActivity();
            reference.setSubject(subject);
        }

        @Override
        public void onClick(View v) {
            if (!reference.getCurrentSession().isAvailable()) {
                GraphicUtils.sendToast("Keine Lösungen verfügbar");
                return;
            }

            reference.startFragment(FragmentType.QUESTION);
        }

    }

}

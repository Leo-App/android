package de.slg.it_problem.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.slg.it_problem.activity.ITActivity;
import de.slg.it_problem.utility.FragmentType;
import de.slg.it_problem.utility.Session;
import de.slg.leoapp.R;

public class QuestionFragment extends Fragment {

    private Session sessionReference;
    private ITActivity activityReference;
    private View viewReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityReference = (ITActivity) getActivity();
        sessionReference = activityReference.getCurrentSession();
        viewReference = inflater.inflate(R.layout.fragment_it_question, container, false);

        initButtons();
        refresh();

        return viewReference;
    }

    private void refresh() {
        if (sessionReference.isAnswer()) {
            activityReference.startFragment(FragmentType.ANSWER);
        } else {
            TextView title = (TextView) viewReference.findViewById(R.id.title);
            TextView content = (TextView) viewReference.findViewById(R.id.content);
            ImageView image = (ImageView) viewReference.findViewById(R.id.image);

            title.setText(sessionReference.getTitle());
            content.setText(sessionReference.getDescription());
            //TODO: Download Image from Server and display ProgressBar
        }

    }

    private void initButtons() {
        viewReference.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionReference.answerYes();
                refresh();
            }
        });

        viewReference.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionReference.answerNo();
                refresh();
            }
        });
    }

}

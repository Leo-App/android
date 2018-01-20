package de.slg.it_problem.activity.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.slg.it_problem.activity.ITActivity;
import de.slg.it_problem.task.ImageSynchronizerTask;
import de.slg.it_problem.utility.FragmentType;
import de.slg.it_problem.utility.Session;
import de.slg.leoapp.R;
import de.slg.leoapp.task.general.TaskStatusListener;

public class AnswerFragment extends Fragment implements TaskStatusListener {

    private Session sessionReference;
    private ITActivity activityReference;
    private View viewReference;

    private ProgressBar progressBar;
    private ImageView image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityReference = (ITActivity) getActivity();
        sessionReference = activityReference.getCurrentSession();
        viewReference = inflater.inflate(R.layout.fragment_it_answer, container, false);

        refresh();
        initFAB();

        return viewReference;
    }

    private void initFAB() {
        viewReference.findViewById(R.id.fab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityReference.startFragment(FragmentType.SELECTION);
            }
        });
    }

    private void refresh() {
        TextView title = viewReference.findViewById(R.id.title);
        TextView content = viewReference.findViewById(R.id.content);

        image = viewReference.findViewById(R.id.image);
        progressBar = viewReference.findViewById(R.id.progressBar2);

        title.setText(sessionReference.getTitle());
        content.setText(sessionReference.getDescription());

        if (sessionReference.getPath() == null)
            progressBar.setVisibility(View.GONE);
        else
            new ImageSynchronizerTask().registerListener(this).execute(sessionReference.getPath());
    }

    @Override
    public void taskFinished(Object... images) {
        if (progressBar != null && image != null) {
            progressBar.setVisibility(View.GONE);
            image.setImageBitmap((Bitmap) images[0]);
        }
    }

}

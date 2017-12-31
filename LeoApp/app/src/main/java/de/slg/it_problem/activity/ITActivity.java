package de.slg.it_problem.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Hashtable;

import de.slg.it_problem.activity.fragment.AnswerFragment;
import de.slg.it_problem.activity.fragment.QuestionFragment;
import de.slg.it_problem.activity.fragment.SelectionFragment;
import de.slg.it_problem.utility.FragmentType;
import de.slg.it_problem.utility.Session;
import de.slg.it_problem.utility.Subject;
import de.slg.it_problem.utility.datastructure.DecisionTree;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.exception.SubjectNotKnownException;
import de.slg.leoapp.view.LeoAppFeatureActivity;

/**
 * ITActivity
 * <p>
 * Verwaltet die Fragments des Frage-Antwort-Vorgangs.
 *
 * @author Gianni
 * @version 2017.2912
 * @since 0.7.2
 */
public class ITActivity extends LeoAppFeatureActivity {

    private Session currentSession;
    private String subject;
    private Hashtable<String, DecisionTree> decisionTreeHashtable;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        decisionTreeHashtable = new Hashtable<>();
        initFragments(b);
    }

    @Override
    protected String getActivityTag() {
        return "IT-ACTIVITY";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_it_wrapper;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_it_problem;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.itsolver;
    }

    private void initFragments(Bundle b) {
        if (b != null)
            return;

        SelectionFragment selectionFragment = new SelectionFragment();
        selectionFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, selectionFragment)
                .commit();
    }

    public void startFragment(FragmentType fragmentType) {

        Fragment newFragment = null;

        switch (fragmentType) {
            case ANSWER:
                newFragment = new AnswerFragment();
                break;
            case QUESTION:
                newFragment = new QuestionFragment();
                break;
            case SELECTION:
                newFragment = new SelectionFragment();
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();

    }

    public void setSubject(String s) throws SubjectNotKnownException {
        if(decisionTreeHashtable.get(s) == null)
            throw new SubjectNotKnownException("Subject must be registered in Subject.java");

        subject = s;
    }

    public Session getCurrentSession() {
        if (currentSession == null)
            currentSession = new Session(subject, decisionTreeHashtable);

        return currentSession;
    }

}

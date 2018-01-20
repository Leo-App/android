package de.slg.it_problem.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.Hashtable;

import de.slg.it_problem.activity.fragment.AnswerFragment;
import de.slg.it_problem.activity.fragment.QuestionFragment;
import de.slg.it_problem.activity.fragment.SelectionFragment;
import de.slg.it_problem.task.SynchronizerDownstreamTask;
import de.slg.it_problem.utility.FragmentType;
import de.slg.it_problem.utility.Session;
import de.slg.it_problem.utility.Subject;
import de.slg.it_problem.utility.datastructure.DecisionTree;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
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
    private Bundle b;

    private SelectionFragment selectionFragment;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Utils.getController().registerITActivity(this);

        this.b = b;
        decisionTreeHashtable = new Hashtable<>();

        initFragments();
        initSync();
    }

    @Override
    protected String getActivityTag() {
        return "IT-ACTIVITY";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wrapper_it;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
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

    @Override
    public void onBackPressed() {
        if (getCurrentSession() != null) {
            currentSession = null;
            resetFragments();
        } else {
            finish();
        }
    }

    private void initFragments() {
        if (b != null)
            return;

        selectionFragment = SelectionFragment.newInstance(true);
        selectionFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, selectionFragment, "CUR")
                .commit();
    }

    private void resetFragments() {
        selectionFragment = SelectionFragment.newInstance(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectionFragment)
                .commit();

        initSync();
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
                newFragment = SelectionFragment.newInstance(false);
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment, "CUR")
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

    public void resetSession() {
        currentSession = null;
    }

    private void initSync() {
        new SynchronizerDownstreamTask(decisionTreeHashtable)
                .addListener(selectionFragment)
                .execute(Subject.BEAMER, Subject.COMPUTER, Subject.NETWORK);
    }
}

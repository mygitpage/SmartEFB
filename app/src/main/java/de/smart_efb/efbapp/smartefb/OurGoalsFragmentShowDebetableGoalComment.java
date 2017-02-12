package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by ich on 22.11.2016.
 */
public class OurGoalsFragmentShowDebetableGoalComment extends Fragment {

    // fragment view
    View viewFragmentShowDebetableGoalComment;

    // fragment context
    Context fragmentShowDebetableGoalCommentContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of debetable goal -> the other are old (look at tab old)
    long currentDateOfDebetableGoal;

    // reference cursorAdapter for the listview
    OurGoalShowDebetableGoalCommentCursorAdapter showDebetableGoalCommentCursorAdapter;

    // DB-Id of debetable goal to comment
    int debetableGoalDbIdToShow = 0;

    // debetable goal number in list view
    int debetableGoalNumberInListView = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowDebetableGoalComment = layoutInflater.inflate(R.layout.fragment_our_goals_show_debetable_goal_comment, null);

        return viewFragmentShowDebetableGoalComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowDebetableGoalCommentContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init and display data from fragment show debetable goal comment only when an debetable goal is choosen
        if (debetableGoalDbIdToShow != 0) {

            // init the fragment now
            initFragmentShowComment();

            // show actual comment set for debetable goal
            displayActualCommentSet();
        }

    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowDebetableGoalCommentContext);

        // init the prefs
        prefs = fragmentShowDebetableGoalCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowDebetableGoalCommentContext.MODE_PRIVATE);
        //get current date of debetable goal
        currentDateOfDebetableGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Einschaetzungen Entwuerfe ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsShowCommentText", "string", fragmentShowDebetableGoalCommentContext.getPackageName())) + " " + debetableGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableShowComment");

    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpDebetableGoalDbIdToComment = 0;

        // call getter-methode getDebetableGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale debetable goal
        tmpDebetableGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getDebetableGoalDbIdFromLink();

        if (tmpDebetableGoalDbIdToComment > 0) {
            debetableGoalDbIdToShow = tmpDebetableGoalDbIdToComment;

            // call getter-methode getDdebetableGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale debetable goal
            debetableGoalNumberInListView = ((ActivityOurGoals)getActivity()).getDebetableGoalNumberInListview();
            if (debetableGoalNumberInListView < 1) debetableGoalNumberInListView = 1; // check borders

        }

    }


    public void displayActualCommentSet () {

        // get the data (all comments for an debetable goal) from DB
        Cursor cursor = myDb.getAllRowsOurGoalsDebetableGoalsComment(debetableGoalDbIdToShow);

        // get the data (the choosen debetable goal) from the DB
        String debetableGoal = "";
        Cursor choosenDebetableGoal = myDb.getDebetableRowOurGoals(debetableGoalDbIdToShow);
        debetableGoal = choosenDebetableGoal.getString(choosenDebetableGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));

        // find the listview
        ListView listView = (ListView) viewFragmentShowDebetableGoalComment.findViewById(R.id.listOurGoalsShowDebetableGoalComment);

        // new dataadapter with custom constructor
        showDebetableGoalCommentCursorAdapter = new OurGoalShowDebetableGoalCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                debetableGoalDbIdToShow,
                debetableGoalNumberInListView,
                debetableGoal);

        // Assign adapter to ListView
        listView.setAdapter(showDebetableGoalCommentCursorAdapter);

    }

}
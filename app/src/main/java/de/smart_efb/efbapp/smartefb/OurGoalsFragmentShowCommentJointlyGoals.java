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
 * Created by ich on 28.10.2016.
 */
public class OurGoalsFragmentShowCommentJointlyGoals extends Fragment {

    // fragment view
    View viewFragmentShowCommentJointlyGoals;

    // fragment context
    Context fragmentShowCommentJointlyGoalsContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of jointly goals -> the other are old (look at tab old)
    long currentDateOfJointlyGoals;

    // reference cursorAdapter for the listview
    OurGoalsShowCommentJointlyGoalsCursorAdapter showCommentJointlyGoalsCursorAdapter;

    // DB-Id of jointly goal to comment
    int jointlyGoalDbIdToShow = 0;

    // arrangement number in list view
    int jointlyGoalNumberInListView = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowCommentJointlyGoals = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_show_comment, null);

        return viewFragmentShowCommentJointlyGoals;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowCommentJointlyGoalsContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init and display data from fragment show comment only when an arrangement is choosen
        if (jointlyGoalDbIdToShow != 0) {

            // init the fragment show comment jointly goals
            initFragmentShowCommentJointlyGoals();

            // show actual comment set for selected jointly goal
            displayActualCommentSet();

        }

    }


    // inits the fragment for use
    private void initFragmentShowCommentJointlyGoals() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentJointlyGoalsContext);

        // init the prefs
        prefs = fragmentShowCommentJointlyGoalsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowCommentJointlyGoalsContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Kommentare Ziel ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsShowComment", "string", fragmentShowCommentJointlyGoalsContext.getPackageName())) + " " + jointlyGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyShowComment");

    }



    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpJointlyGoalDbIdToComment = 0;

        // call getter-methode getJointlyGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale jointly goal
        tmpJointlyGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getJointlyGoalDbIdFromLink();

        if (tmpJointlyGoalDbIdToComment > 0) {
            jointlyGoalDbIdToShow = tmpJointlyGoalDbIdToComment;

            // call getter-methode getJointlyGoalsNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            jointlyGoalNumberInListView = ((ActivityOurGoals)getActivity()).getJointlyGoalNumberInListview();
            if (jointlyGoalNumberInListView < 1) jointlyGoalNumberInListView = 1; // check borders

        }

    }


    public void displayActualCommentSet () {

        // get the data (all comments from an jointly goals) from DB
        Cursor cursor = myDb.getAllRowsOurGoalsJointlyGoalsComment(jointlyGoalDbIdToShow);

        // get the data (the choosen jointly goal) from the DB
        String jointlyGoal = "";
        Cursor choosenJointlyGoal = myDb.getJointlyRowOurGoals(jointlyGoalDbIdToShow);
        jointlyGoal = choosenJointlyGoal.getString(choosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));

        // find the listview
        ListView listView = (ListView) viewFragmentShowCommentJointlyGoals.findViewById(R.id.listOurGoalsShowCommentJointlyGoals);

        // new dataadapter with custom constructor
        showCommentJointlyGoalsCursorAdapter = new OurGoalsShowCommentJointlyGoalsCursorAdapter(
                getActivity(),
                cursor,
                0,
                jointlyGoalDbIdToShow,
                jointlyGoalNumberInListView,
                jointlyGoal);

        // Assign adapter to ListView
        listView.setAdapter(showCommentJointlyGoalsCursorAdapter);

    }



}

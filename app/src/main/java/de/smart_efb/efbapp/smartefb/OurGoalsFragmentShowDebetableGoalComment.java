package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    int debetableServerDbIdToShow = 0;

    // debetable goal number in list view
    int debetableGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the list view for debetable comments
    ListView listViewShowDebetableComment = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowDebetableGoalComment = layoutInflater.inflate(R.layout.fragment_our_goals_show_debetable_goal_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver, filter);

        return viewFragmentShowDebetableGoalComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowDebetableGoalCommentContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init and display data from fragment show debetable goal comment only when an debetable goal is choosen
        if (debetableServerDbIdToShow != 0) {

            // init the fragment now
            initFragmentShowComment();

            // show actual comment set for debetable goal
            displayActualCommentSet();
        }

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver);

    }





    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean refreshView = false;

                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsDebetableNow = intentExtras.getString("OurGoalsDebetableNow","0");
                String tmpExtraOurGoalsDebetableNowComment = intentExtras.getString("OurGoalsDebetableComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareDisable= intentExtras.getString("OurGoalsSettingsDebetableCommentShareDisable","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsDebetableCommentShareEnable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsDebetableCommentCountComment","0");

                if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNowComment != null && tmpExtraOurGoalsDebetableNowComment.equals("1")) {
                    // update debetable comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageCommentDebetableGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNow != null && tmpExtraOurGoalsDebetableNow.equals("1")) {
                    // update debetable goals! -> go back to fragment now goals and show dialog

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("debetable");

                    // go back to fragment debetable goals goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_debetable_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset debetable comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }

                // update the list view with sketch arrangements
                if (refreshView) {
                    updateListView();
                }
            }
        }
    };



    // update the list view with debetable comments
    public void updateListView () {

        if (listViewShowDebetableComment != null) {
            listViewShowDebetableComment.destroyDrawingCache();
            listViewShowDebetableComment.setVisibility(ListView.INVISIBLE);
            listViewShowDebetableComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
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

        // Set correct subtitle in Activity -> "Einschaetzungen strittige Ziele ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsShowCommentText", "string", fragmentShowDebetableGoalCommentContext.getPackageName())) + " " + debetableGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableShowComment");
    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpDebetableGoalDbIdToComment = 0;

        // call getter-methode getDebetableGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale debetable goal
        tmpDebetableGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getDebetableGoalDbIdFromLink();

        if (tmpDebetableGoalDbIdToComment > 0) {
            debetableServerDbIdToShow = tmpDebetableGoalDbIdToComment;

            // call getter-methode getDdebetableGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale debetable goal
            debetableGoalNumberInListView = ((ActivityOurGoals)getActivity()).getDebetableGoalNumberInListview();
            if (debetableGoalNumberInListView < 1) debetableGoalNumberInListView = 1; // check borders

            // call getter-methode isCommentLimitationBorderSet in ActivityOurGoals to get true-> debetable comments are limited, false-> debetable comments are not limited
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("debetableGoals");
        }
    }


    public void displayActualCommentSet () {

        // get the data (all comments for an debetable goal) from DB
        Cursor cursor = myDb.getAllRowsOurGoalsDebetableGoalsComment(debetableServerDbIdToShow);

        // get the data (the choosen debetable goal) from the DB
        Cursor choosenDebetableGoal = myDb.getDebetableRowOurGoals(debetableServerDbIdToShow);

        // find the listview
        listViewShowDebetableComment = (ListView) viewFragmentShowDebetableGoalComment.findViewById(R.id.listOurGoalsShowDebetableGoalComment);

        // new dataadapter with custom constructor
        showDebetableGoalCommentCursorAdapter = new OurGoalShowDebetableGoalCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                debetableServerDbIdToShow,
                debetableGoalNumberInListView,
                commentLimitationBorder,
                choosenDebetableGoal);

        // Assign adapter to ListView
        listViewShowDebetableComment.setAdapter(showDebetableGoalCommentCursorAdapter);
    }

}
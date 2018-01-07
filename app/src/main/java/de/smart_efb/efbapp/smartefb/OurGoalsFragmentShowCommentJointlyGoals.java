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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 28.10.2016.
 */
public class OurGoalsFragmentShowCommentJointlyGoals extends Fragment {

    // fragment view
    View viewFragmentShowCommentJointlyGoals;

    // fragment context
    Context fragmentShowCommentJointlyGoalsContext = null;

    // the listview for the comments
    ListView listViewShowComments = null;

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

    // goal number in list view
    int jointlyGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowCommentJointlyGoals = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_show_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver, filter);

        return viewFragmentShowCommentJointlyGoals; 
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowCommentJointlyGoalsContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init and display data from fragment show comment only when a goal is choosen
        if (jointlyGoalDbIdToShow != 0) {

            // init the fragment show comment jointly goals
            initFragmentShowCommentJointlyGoals();

            // show actual comment set for selected jointly goal
            displayActualCommentSet();
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // inits the fragment for use
    private void initFragmentShowCommentJointlyGoals() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentJointlyGoalsContext);

        // init the prefs
        prefs = fragmentShowCommentJointlyGoalsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowCommentJointlyGoalsContext.MODE_PRIVATE);

        //get current date of goal
        currentDateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        // find the listview
        listViewShowComments = (ListView) viewFragmentShowCommentJointlyGoals.findViewById(R.id.listOurGoalsShowCommentJointlyGoals);

        // Set correct subtitle in Activity -> "Kommentare Ziel ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsShowComment", "string", fragmentShowCommentJointlyGoalsContext.getPackageName())) + " " + jointlyGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyShowComment");
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with goals
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                String tmpExtraOurGoalsJointlyComment = intentExtras.getString("OurGoalsJointlyComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsCommentShareEnable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsCommentShareDisable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsCommentCountComment","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsJointlyComment != null && tmpExtraOurGoalsJointlyComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageCommentJointlyGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {
                    // update jointly goals! -> go back to fragment jointly goals and show dialog

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // go back to fragment jointly goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset jointly comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    updateListView = true;
                }

                // update the list view because data has change?
                if (updateListView) {
                    updateListView();
                }
            }
        }
    };


    // update the list view with now comments
    public void updateListView () {

        if (listViewShowComments != null) {
            listViewShowComments.destroyDrawingCache();
            listViewShowComments.setVisibility(ListView.INVISIBLE);
            listViewShowComments.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
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

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("jointlyGoals");
        }
    }


    public void displayActualCommentSet () {

        // get the data (all comments from an jointly goals) from DB
        Cursor cursorComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(jointlyGoalDbIdToShow);

        // get the data (the choosen jointly goal) from the DB
        Cursor choosenJointlyGoal = myDb.getJointlyRowOurGoals(jointlyGoalDbIdToShow);


        if (cursorComments.getCount() > 0 && choosenJointlyGoal.getCount() > 0 && listViewShowComments != null) {

            // new dataadapter with custom constructor
            showCommentJointlyGoalsCursorAdapter = new OurGoalsShowCommentJointlyGoalsCursorAdapter(
                    getActivity(),
                    cursorComments,
                    0,
                    jointlyGoalDbIdToShow,
                    jointlyGoalNumberInListView,
                    commentLimitationBorder,
                    choosenJointlyGoal);

            // Assign adapter to ListView
            listViewShowComments.setAdapter(showCommentJointlyGoalsCursorAdapter);
        }
    }


}

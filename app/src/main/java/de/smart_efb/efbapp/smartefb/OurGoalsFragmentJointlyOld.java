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
 * Created by ich on 30.11.2016.
 */
public class OurGoalsFragmentJointlyOld extends Fragment {

    // fragment view
    View viewFragmentJointlyOld;

    // fragment context
    Context fragmentJointlyOldContext = null;

    // the fragment
    Fragment fragmentOldThisFragmentContext;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of jointly goals -> the other are old
    long currentDateOfJointlyGoal;

    // block id of current jointly goals
    String currentBlockIdOfJointlyGoals = "";

    // reference cursorAdapter for the listview
    OurGoalsJointlyOldCursorAdapter dataAdapter;

    
    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyOld = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_old, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentOldBrodcastReceiver, filter);

        return viewFragmentJointlyOld;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentJointlyOldContext = getActivity().getApplicationContext();

        fragmentOldThisFragmentContext = this;

        // init the fragment now
        initFragmentJointlyOld();

        // show actual old jointly goal set
        displayOldJointlyGoalSet();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentOldBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourGoalsFragmentOldBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentJointlyOldContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {

                    //update current block id of jointly goals
                    currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

                    // check jointly and debetable goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // go back to fragment jointly goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    refreshView = true;
                }

                if (refreshView) {
                    // refresh fragments view
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragmentOldThisFragmentContext).attach(fragmentOldThisFragmentContext).commit();
                }
            }
        }
    };


    // inits the fragment for use
    private void initFragmentJointlyOld() {

        // init the DB
        myDb = new DBAdapter(fragmentJointlyOldContext);

        // init the prefs
        prefs = fragmentJointlyOldContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentJointlyOldContext.MODE_PRIVATE);

        //get current date of jointly goals
        currentDateOfJointlyGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        //get current block id of jointly goals
        currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");
    }


    public void displayOldJointlyGoalSet () {

        String tmpSubtitle;

        // find the listview
        ListView listView = (ListView) viewFragmentJointlyOld.findViewById(R.id.listOurGoalsJointlyOld);

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false) && listView != null) { // Function showOldJointlyGoals is available!!!!

            // get all old jointly goals from DB
            Cursor cursor = myDb.getAllJointlyRowsOurGoals(currentBlockIdOfJointlyGoals,"notEqualBlockId");

            if (cursor.getCount() > 0) {

                // show the list view for the old jointly goals, hide textview "Function not available" and "nothing there"
                setVisibilityListViewOldJointlyGoals ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "ziele aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleOldJointlyGoals", "string", fragmentJointlyOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfJointlyGoal, "dd.MM.yyyy");
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");

                // new dataadapter
                dataAdapter = new OurGoalsJointlyOldCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);
            }
            else {

                // hide the list view for the old jointly goals, hide textview "Function not available", show textview "nothing there"
                setVisibilityListViewOldJointlyGoals ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Ziele vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleGoalsNothingThere", "string", fragmentJointlyOldContext.getPackageName()));
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");
            }
        }
        else { // Function showOldJointlyGoals is not available

            // show the list view for the old jointly goals, show textview "Function not available" and hide "nothing there"
            setVisibilityListViewOldJointlyGoals ("hide");
            setVisibilityTextViewNothingThere ("hide");
            setVisibilityTextViewFunctionNotAvailable ("show");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentJointlyOldContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");

        }
    }


    private void setVisibilityListViewOldJointlyGoals (String visibility) {

        ListView tmplistView = (ListView) viewFragmentJointlyOld.findViewById(R.id.listOurGoalsJointlyOld);

        if (tmplistView != null) {

            switch (visibility) {

                case "show":
                    tmplistView.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmplistView.setVisibility(View.GONE);
                    break;
            }
        }
    }



    private void setVisibilityTextViewFunctionNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsFunctionNotAvailable);

        if (tmpNotAvailable != null) {

            switch (visibility) {

                case "show":
                    tmpNotAvailable.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNotAvailable.setVisibility(View.GONE);
                    break;
            }
        }
    }


    private void setVisibilityTextViewNothingThere (String visibility) {

        TextView tmpNothingThere = (TextView) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsNothingThere);

        if (tmpNothingThere != null) {

            switch (visibility) {

                case "show":
                    tmpNothingThere.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpNothingThere.setVisibility(View.GONE);
                    break;
            }
        }
    }


}
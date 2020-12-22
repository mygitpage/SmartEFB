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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 12.11.2016.
 */
public class OurGoalsFragmentDebetableGoalsNow extends Fragment {

    // fragment view
    View viewFragmentDebetablGoalNow;

    // fragment context
    Context fragmentDebetableGoalNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the date of debetable goal
    long currentDateOfDebetableGoal;

    // block id of current debetable goals
    String currentBlockIdOfDebetableGoals = "";

    // the list view for the debetable goals
    ListView listViewDebetableGoals;

    // reference cursorAdapter for the listview
    OurGoalsDebetableGoalsNowCursorAdapter dataAdapterListViewOurGoalsDebetableGoalsNow = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentDebetablGoalNow = layoutInflater.inflate(R.layout.fragment_our_goals_debetable_goals_now, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentDebetableBrodcastReceiver, filter);

        return viewFragmentDebetablGoalNow;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentDebetableGoalNowContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentDebetableGoalNow();

        // show actual debetable goal set
        displayDebetableGoalsSet();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentDebetableGoalNowContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentDebetableGoalNowContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentDebetableBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // inits the fragment for use
    private void initFragmentDebetableGoalNow() {

        // init the DB
        myDb = new DBAdapter(fragmentDebetableGoalNowContext);

        // init the prefs
        prefs = fragmentDebetableGoalNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentDebetableGoalNowContext.MODE_PRIVATE);

        //get date of debetable goals
        currentDateOfDebetableGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis());

        //get block id of debetable goals
        currentBlockIdOfDebetableGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfDebetableGoals, "0");

        // find the listview for debetable goals
        listViewDebetableGoals = (ListView) viewFragmentDebetablGoalNow.findViewById(R.id.listOurGoalsDebetableGoalsNow);
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver ourGoalsFragmentDebetableBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurGoalsDebetableNow = intentExtras.getString("OurGoalsDebetableNow","0");
                String tmpExtraOurGoalsDebetableNowComment = intentExtras.getString("OurGoalsDebetableComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsDebetableCommentShareEnable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsDebetableCommentShareDisable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsDebetableCommentCountComment","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentDebetableGoalNowContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNow != null && tmpExtraOurGoalsDebetableNow.equals("1")) {
                    // new jointly goals on smartphone -> update now view

                    //update current block id of jointly goals
                    currentBlockIdOfDebetableGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfDebetableGoals, "0");

                    // check jointly and debetable goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("debetable");

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNowComment != null && tmpExtraOurGoalsDebetableNowComment.equals("1")) {
                    // new debetable comments -> update view -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalNowContext.getString(R.string.toastMessageCommentDebetableGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset debetable comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalNowContext.getString(R.string.toastMessageDebetableGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalNowContext.getString(R.string.toastMessageDebetableGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentDebetableGoalNowContext.getString(R.string.toastMessageDebetableGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send successfull?

                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send not successfull?

                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
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


    // update the list view with debetable goals
    public void updateListView () {

        if (listViewDebetableGoals != null) {
            listViewDebetableGoals.destroyDrawingCache();
            listViewDebetableGoals.setVisibility(ListView.INVISIBLE);
            listViewDebetableGoals.setVisibility(ListView.VISIBLE);

            displayDebetableGoalsSet ();
        }
    }


    // show listView with debetable goals or info: nothing there
    public void displayDebetableGoalsSet () {

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false) && listViewDebetableGoals != null) { // Function showDebetableGoals is available!!!!

            // get the data from db -> all debetable goals
            Cursor cursor = myDb.getAllDebetableRowsOurGoals(currentBlockIdOfDebetableGoals);

            if (cursor.getCount() > 0 && listViewDebetableGoals != null) {

                // set listView visible, textView nothing there and not available gone
                setVisibilityListViewDebetableGoalsNow("show");
                setVisibilityTextViewDebetableGoalsNowNotAvailable("hide");
                setVisibilityTextViewDebetableGoalsNowNothingThere ("hide");

                // Set correct subtitle in Activity -> "Strittige Ziele vom ..."
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsNow", "string", fragmentDebetableGoalNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfDebetableGoal, "dd.MM.yyyy");
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");

                // new dataadapter
                dataAdapterListViewOurGoalsDebetableGoalsNow = new OurGoalsDebetableGoalsNowCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listViewDebetableGoals.setAdapter(dataAdapterListViewOurGoalsDebetableGoalsNow);
            }
            else {

                // set listView and textView not available gone, set textView nothing there visible
                setVisibilityListViewDebetableGoalsNow("hide");
                setVisibilityTextViewDebetableGoalsNowNotAvailable("hide");
                setVisibilityTextViewDebetableGoalsNowNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Ziele vorhanden"
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsDebetableSubtitleGoalsNothingThere", "string", fragmentDebetableGoalNowContext.getPackageName()));
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");
            }
        }
        else {

            // set listView and textView nothing there gone, set textView not available visible
            setVisibilityListViewDebetableGoalsNow("hide");
            setVisibilityTextViewDebetableGoalsNowNotAvailable("show");
            setVisibilityTextViewDebetableGoalsNowNothingThere ("hide");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleFunctionNotAvailable", "string", fragmentDebetableGoalNowContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableNow");
        }
    }


    // set visibility of listViewOurGoals
    private void setVisibilityListViewDebetableGoalsNow (String visibility) {

        if (listViewDebetableGoals != null) {

            switch (visibility) {

                case "show":
                    listViewDebetableGoals.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    listViewDebetableGoals.setVisibility(View.GONE);
                    break;
            }
        }
    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewDebetableGoalsNowNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = (RelativeLayout) viewFragmentDebetablGoalNow.findViewById(R.id.textViewOurGoalsFunctionNotAvailable);

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


    private void setVisibilityTextViewDebetableGoalsNowNothingThere (String visibility) {

        RelativeLayout tmpNothingThere = (RelativeLayout) viewFragmentDebetablGoalNow.findViewById(R.id.textViewOurGoalsNothingThere);

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

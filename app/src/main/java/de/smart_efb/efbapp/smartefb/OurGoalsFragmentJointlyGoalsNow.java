package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ich on 16.10.2016.
 */
public class OurGoalsFragmentJointlyGoalsNow extends Fragment {

    // fragment view
    View viewFragmentJointlyGoalsNow;

    // fragment context
    Context fragmentJointlyGoalsNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;
    
    // the recycler view
    RecyclerView recyclerViewJointlyGoalsNow = null;

    // data array of now goals for recycler view
    ArrayList<ObjectSmartEFBGoals> arrayListGoals;

    // reference arrayListAdapter for the recycler view
    OurGoalsJointlyGoalsRecyclerViewAdapter jointlyGoalsRecylerViewAdapter;
    
    // the current date of jointly goals -> the other are old (look at tab old)
    long currentDateOfJointlyGoals;

    // block id of current jointly goals
    String currentBlockIdOfJointlyGoals = "";
    
    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyGoalsNow = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_now, null);

        // fragment has option menu
        setHasOptionsMenu(true);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentJointlyGoalsNowBrodcastReceiver, filter);

        return viewFragmentJointlyGoalsNow;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentJointlyGoalsNowContext = getActivity().getApplicationContext();

        // init the fragment jointly goals now
        initFragmentJointlyGoalsNow();

        // show actual jointly goals set
        displayActualJointlyGoalsSet();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentJointlyGoalsNowContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentJointlyGoalsNowContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        //de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentJointlyGoalsNowBrodcastReceiver);

        // close db connection
        myDb.close();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_goals_fragment_jointly_now, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_goals_menu_fragment_jointly_now_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_goals_menu_fragment_jointly_now_sort_asc);
        MenuItem registerNoJointlyGoalsInfo = menu.findItem(R.id.our_goals_menu_fragment_jointly_now_no_goals_available);

        if (arrayListGoals != null && arrayListGoals.size() > 0) {

            registerNoJointlyGoalsInfo.setVisible(false);
            if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsList, "descending").equals("descending")) {
                registerItemDesc.setVisible(false);
                registerItemAsc.setVisible(true);
            } else {
                registerItemAsc.setVisible(false);
                registerItemDesc.setVisible(true);
            }
        }
        else {
            registerNoJointlyGoalsInfo.setVisible(true);
            registerItemDesc.setVisible(false);
            registerItemAsc.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.our_goals_menu_fragment_jointly_now_sort_desc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsList, "descending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            case R.id.our_goals_menu_fragment_jointly_now_sort_asc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsList, "ascending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver ourGoalsFragmentJointlyGoalsNowBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with goals
            Boolean updateRecyclerView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                String tmpExtraOurGoalsNowComment = intentExtras.getString("OurGoalsJointlyComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsCommentShareEnable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsCommentShareDisable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsCommentCountComment","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpUpdateEvaluationLink = intentExtras.getString("UpdateJointlyEvaluationLink");
                String tmpMessage = intentExtras.getString("Message");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentJointlyGoalsNowContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {
                    // new jointly goals on smartphone -> update now view

                    //update current block id of jointly goals
                    currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

                    // check jointly and debetable goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNowComment != null && tmpExtraOurGoalsNowComment.equals("1")) {
                    // new comments -> update now view -> show toast and update view
                    String updateMessageCommentNow = fragmentJointlyGoalsNowContext.getString(R.string.toastMessageCommentJointlyGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentJointlyGoalsNowContext.getString(R.string.toastMessageJointlyGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentJointlyGoalsNowContext.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentJointlyGoalsNowContext.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
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
                else if (tmpUpdateEvaluationLink != null && tmpUpdateEvaluationLink.equals("1")) {
                    // evaluationperiod has change -> update view, only when current time is bigger than last start point
                    if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, 0)) {
                        updateRecyclerView = true;
                        // set new start point for evaluation timer in view
                        prefsEditor.putLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis());
                        prefsEditor.apply();
                    }
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    updateRecyclerView = true;

                    // new alarm manager service for start all needed alarms
                    EfbSetAlarmManager efbSetAlarmManager = new EfbSetAlarmManager(context);
                    // start check our goals alarm manager, when function our goals is on
                    if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurGoals, false)) {
                        efbSetAlarmManager.setAlarmManagerForOurGoalsEvaluation();
                    }
                }

                // update the list view because data has change?
                if (updateRecyclerView) {
                    updateRecyclerView();
                }
            }
        }
    };


    // update the recycler view with goals
    public void updateRecyclerView () {

        if (recyclerViewJointlyGoalsNow != null) {
            recyclerViewJointlyGoalsNow.destroyDrawingCache();
            recyclerViewJointlyGoalsNow.setVisibility(ListView.INVISIBLE);
            recyclerViewJointlyGoalsNow.setVisibility(ListView.VISIBLE);

            displayActualJointlyGoalsSet ();
        }
    }


    // inits the fragment for use
    private void initFragmentJointlyGoalsNow() {

        // init the DB
        myDb = new DBAdapter(fragmentJointlyGoalsNowContext);

        // init the prefs
        prefs = fragmentJointlyGoalsNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentJointlyGoalsNowContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        
        //get current date of jointly goals
        currentDateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        //get current block id of jointly goals
        currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

        // ask methode isCommentLimitationBorderSet() in ActivityOurGoals to limitation in comments? true-> yes, linitation; false-> no
        commentLimitationBorder = ((ActivityOurGoals) getActivity()).isCommentLimitationBorderSet("jointlyGoals");

        // new recyler view
        recyclerViewJointlyGoalsNow = viewFragmentJointlyGoalsNow.findViewById(R.id.listOurGoalsJointlyGoalsNow);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentJointlyGoalsNowContext);
        recyclerViewJointlyGoalsNow.setLayoutManager(linearLayoutManager);
        recyclerViewJointlyGoalsNow.setHasFixedSize(true);
    }


    // show listView with current goals or info: nothing there
    public void displayActualJointlyGoalsSet () {

        // get the data (all jointly goals) from DB
        arrayListGoals = myDb.getAllRowsOurGoalsJointlyGoalsArrayList(currentBlockIdOfJointlyGoals, "equalBlockId", prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsList, "descending"));

        if (arrayListGoals.size() > 0 && recyclerViewJointlyGoalsNow != null) {

            // set listView visible and textView hide
            setVisibilityListViewJointlyGoalsNow("show");
            setVisibilityTextViewTextNotAvailable("hide");

            // Set correct subtitle in Activity -> "Gemeinsame Ziele vom ..."
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsNow", "string", fragmentJointlyGoalsNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy");
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNow");

            // set visibility of FAB for this fragment
            // show fab and comment jointly goals only when on and possible!
            if ((prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false) && (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0)) > 0 ) || !commentLimitationBorder) {
                // set fab visibility
                ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("show", "jointlyNow");
                // set fab click listener
                ((ActivityOurGoals) getActivity()).setOurGoalFABClickListener(arrayListGoals, "jointlyNow", "comment_an_jointly_goal");
            }
            else {
                ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("hide", "jointlyNow");
            }

            // new dataadapter
            jointlyGoalsRecylerViewAdapter = new OurGoalsJointlyGoalsRecyclerViewAdapter(
                    getActivity(),
                    arrayListGoals,
                    0);

            // Assign adapter to recycler view
            recyclerViewJointlyGoalsNow.setAdapter(jointlyGoalsRecylerViewAdapter);
        }
        else {

            // set listView hide and textView visible
            setVisibilityListViewJointlyGoalsNow("hide");
            setVisibilityTextViewTextNotAvailable("show");

            // Set correct subtitle in Activity -> "Keine gemeinsamen Ziele vorhanden"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsJointlySubtitleGoalsNothingThere", "string", fragmentJointlyGoalsNowContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNow");

            // set visibility of FAB for this fragment
            ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility ("hide", "jointlyNow");
        }

    }


    // set visibility of listViewOurGoals
    private void setVisibilityListViewJointlyGoalsNow (String visibility) {

        RecyclerView tmpRecyclerView = viewFragmentJointlyGoalsNow.findViewById(R.id.listOurGoalsJointlyGoalsNow);


        if (tmpRecyclerView != null) {

            switch (visibility) {

                case "show":
                    tmpRecyclerView.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    tmpRecyclerView.setVisibility(View.GONE);
                    break;
            }
        }
    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewTextNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = viewFragmentJointlyGoalsNow.findViewById(R.id.textViewJointlyGoalsNowNothingThere);

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


    // geter for border for comments
    public boolean isCommentLimitationBorderSet () {

        // true-> comments are limited; false-> no limit
        return  commentLimitationBorder;
    }


}
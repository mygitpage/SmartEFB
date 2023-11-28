package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    SharedPreferences.Editor prefsEditor;

    // the recycler view
    RecyclerView recyclerViewOldJointlyGoals = null;

    // data array of jointly goals for recycler view
    ArrayList<ObjectSmartEFBGoals> arrayListOldJointlyGoals;

    // reference jointlyGoalsListAdapter for the recycler view
    OurGoalsOldJointlyGoalsRecyclerViewAdapter oldJointlyGoalsRecyclerViewAdapter;

    // the current date of jointly goals -> the other are old
    long currentDateOfJointlyGoal;

    // block id of current jointly goals
    String currentBlockIdOfJointlyGoals = "";


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentJointlyOld = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_old, null);

        // fragment has option menu
        setHasOptionsMenu(true);

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

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentJointlyOldContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentJointlyOldContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentOldBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_goals_fragment_jointly_old, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_goals_menu_fragment_old_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_goals_menu_fragment_old_sort_asc);
        MenuItem registerNoGoalsInfo = menu.findItem(R.id.our_goals_menu_fragment_old_no_goals_available);

        if (arrayListOldJointlyGoals != null && arrayListOldJointlyGoals.size() > 0) {

            registerNoGoalsInfo.setVisible(false);
            if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsOldList, "descending_old").equals("descending_old")) {
                registerItemDesc.setVisible(false);
                registerItemAsc.setVisible(true);
            } else {
                registerItemAsc.setVisible(false);
                registerItemDesc.setVisible(true);
            }
        }
        else {
            registerNoGoalsInfo.setVisible(true);
            registerItemDesc.setVisible(false);
            registerItemAsc.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.our_goals_menu_fragment_old_sort_desc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsOldList, "descending_old");
                prefsEditor.apply();
                refreshFragmentView();
                return true;
            case R.id.our_goals_menu_fragment_old_sort_asc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsOldList, "ascending_old");
                prefsEditor.apply();
                refreshFragmentView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourGoalsFragmentOldBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurGoalsDebetableNow = intentExtras.getString("OurGoalsDebetableNow","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentJointlyOldContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    toast.show();

                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {

                    //update current block id of jointly goals
                    currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

                    // check jointly goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNow != null && tmpExtraOurGoalsDebetableNow.equals("1")) {

                    // check debetable goals update and show dialog jointly and debetable goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("debetable");
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    refreshView = true;
                }

                if (refreshView) {
                    refreshFragmentView ();
                }
            }
        }
    };


    // refresh the fragments view
    private void refreshFragmentView () {
        // refresh fragments view
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(fragmentOldThisFragmentContext).attach(fragmentOldThisFragmentContext).commit();
    }


    // inits the fragment for use
    private void initFragmentJointlyOld() {

        // init the DB
        myDb = new DBAdapter(fragmentJointlyOldContext);

        // init the prefs
        prefs = fragmentJointlyOldContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentJointlyOldContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        
        //get current date of jointly goals
        currentDateOfJointlyGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        //get current block id of jointly goals
        currentBlockIdOfJointlyGoals = prefs.getString(ConstansClassOurGoals.namePrefsCurrentBlockIdOfJointlyGoals, "0");

        // new recycler view
        recyclerViewOldJointlyGoals = viewFragmentJointlyOld.findViewById(R.id.listOurGoalsJointlyOld);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentJointlyOldContext);
        recyclerViewOldJointlyGoals.setLayoutManager(linearLayoutManager);
        recyclerViewOldJointlyGoals.setHasFixedSize(true);
    }


    public void displayOldJointlyGoalSet () {

        String tmpSubtitle;

        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkOldGoals, false) && recyclerViewOldJointlyGoals != null) { // Function showOldJointlyGoals is available!!!!

            // get the data (all jointly old goals) from DB
            arrayListOldJointlyGoals = myDb.getAllRowsOurGoalsJointlyGoalsArrayList(currentBlockIdOfJointlyGoals, "notEqualBlockId", prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsOldList, "descending_old"));

            if (arrayListOldJointlyGoals.size() > 0) {

                // show the list view for the old jointly goals, hide textview "Function not available" and "nothing there"
                setVisibilityRecyclerViewOldJointlyGoals ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "ziele aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleOldJointlyGoals", "string", fragmentJointlyOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfJointlyGoal, "dd.MM.yyyy");
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");
                
                // set visibility of FAB for this fragment
                ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility ("hide", "jointlyOld");
                
                // new recycler view adapter
                oldJointlyGoalsRecyclerViewAdapter = new OurGoalsOldJointlyGoalsRecyclerViewAdapter(
                        getActivity(),
                        arrayListOldJointlyGoals,
                        0);

                // Assign adapter to recycler view
                recyclerViewOldJointlyGoals.setAdapter(oldJointlyGoalsRecyclerViewAdapter);

            }
            else {

                // hide the list view for the old jointly goals, hide textview "Function not available", show textview "nothing there"
                setVisibilityRecyclerViewOldJointlyGoals ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Ziele vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsOldSubtitleGoalsNothingThere", "string", fragmentJointlyOldContext.getPackageName()));
                ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");
            }
        }
        else { // Function showOldJointlyGoals is not available

            // show the list view for the old jointly goals, show textview "Function not available" and hide "nothing there"
            setVisibilityRecyclerViewOldJointlyGoals ("hide");
            setVisibilityTextViewNothingThere ("hide");
            setVisibilityTextViewFunctionNotAvailable ("show");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentJointlyOldContext.getPackageName()));
            ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyOld");

        }
    }


    private void setVisibilityRecyclerViewOldJointlyGoals (String visibility) {

        if (recyclerViewOldJointlyGoals != null) {

            switch (visibility) {

                case "show":
                    recyclerViewOldJointlyGoals.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    recyclerViewOldJointlyGoals.setVisibility(View.GONE);
                    break;
            }
        }
    }



    private void setVisibilityTextViewFunctionNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = (RelativeLayout) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsFunctionNotAvailable);

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

        RelativeLayout tmpNothingThere = (RelativeLayout) viewFragmentJointlyOld.findViewById(R.id.textViewOldJointlyGoalsNothingThere);

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
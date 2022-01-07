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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementFragmentOld extends Fragment {

    // fragment view
    View viewFragmentOld;

    // fragment context
    Context fragmentOldContext = null;

    // the fragment
    Fragment fragmentOldThisFragmentContext;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the recycler view
    RecyclerView recyclerViewOldArrangement = null;

    // data array of sketch arrangements for recycler view
    ArrayList<ObjectSmartEFBArrangement> arrayListOldArrangements;

    // reference arrayListAdapter for the recyler view
    OurArrangementOldArrangementRecyclerViewAdapter oldArrangementRecyclerViewAdapter;

    // reference to the DB
    DBAdapter myDb;

    // the current date of arrangement -> the other are old
    long currentDateOfArrangement;

    // block id of cuurrent arrangements
    String currentBlockIdOfArrangement = "";

    // reference cursorAdapter for the listview
    OurArrangementOldCursorAdapter dataAdapter;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentOld = layoutInflater.inflate(R.layout.fragment_our_arrangement_old, null);

        // fragment has option menu
        setHasOptionsMenu(true);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentOldBrodcastReceiver, filter);

        return viewFragmentOld;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentOldContext = getActivity().getApplicationContext();

        fragmentOldThisFragmentContext = this;

        // init the fragment now
        initFragmentOld();

        // show actual arrangement set
        displayOldArrangementSet();

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentOldContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentOldContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentOldBrodcastReceiver);

        // close db connection
        myDb.close();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_arrangement_fragment_old, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_arrangement_menu_fragment_old_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_arrangement_menu_fragment_old_sort_asc);
        MenuItem registerNoArrangementsInfo = menu.findItem(R.id.our_arrangement_menu_fragment_old_no_arrangement_available);

        if (arrayListOldArrangements != null && arrayListOldArrangements.size() > 0) {

            registerNoArrangementsInfo.setVisible(false);
            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementOldList, "descending").equals("descending")) {
                registerItemDesc.setVisible(false);
                registerItemAsc.setVisible(true);
            } else {
                registerItemAsc.setVisible(false);
                registerItemDesc.setVisible(true);
            }
        }
        else {
            registerNoArrangementsInfo.setVisible(true);
            registerItemDesc.setVisible(false);
            registerItemAsc.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.our_arrangement_menu_fragment_old_sort_desc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementOldList, "descending");
                prefsEditor.apply();
                refreshFragmentView();
                return true;
            case R.id.our_arrangement_menu_fragment_old_sort_asc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementOldList, "ascending");
                prefsEditor.apply();
                refreshFragmentView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver ourArrangementFragmentOldBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                Boolean updateView = false;

                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementNow = intentExtras.getString("OurArrangementNow","0");
                String tmpExtraOurArrangementSketch = intentExtras.getString("OurArrangementSketch","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentOldContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {

                    //check arrangement and now arrangement update and show dialog arrangement and now arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {

                    // check arrangement and sketch arrangement update and show dialog arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("sketch");
                }

                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settigs have change -> refresh view
                    updateView = true;
                }

                if (updateView) {
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
    private void initFragmentOld() {

        // init the DB
        myDb = new DBAdapter(fragmentOldContext);

        // init the prefs
        prefs = fragmentOldContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentOldContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

        // new recyler view
        recyclerViewOldArrangement = viewFragmentOld.findViewById(R.id.listOurArrangementOld);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentOldContext);
        recyclerViewOldArrangement.setLayoutManager(linearLayoutManager);
        recyclerViewOldArrangement.setHasFixedSize(true);
    }


    public void displayOldArrangementSet () {

        String tmpSubtitle;

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false) && recyclerViewOldArrangement != null) { // Function showOldArrangement is available!!!!

            // get the data (all now arrangements) from DB
            arrayListOldArrangements = myDb.getAllRowsOurArrangementNowArrayList(currentBlockIdOfArrangement, "notEqualBlockId", prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementOldList, "descending"));

            if (arrayListOldArrangements.size() > 0) {

                // show the list view for the old arrangements, hide textview "Function not available" and "nothing there"
                setVisibilityListViewOldArrangements ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "Absprachen aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("olderArrangementDateFrom", "string", fragmentOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

                // set visibility of FAB for this fragment
                ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "old");

                // new recycler view adapter
                oldArrangementRecyclerViewAdapter = new OurArrangementOldArrangementRecyclerViewAdapter(
                        getActivity(),
                        arrayListOldArrangements,
                        0);

                // Assign adapter to recycler view
                recyclerViewOldArrangement.setAdapter(oldArrangementRecyclerViewAdapter);

            }
            else {

                // hide the list view for the old arrangements, hide textview "Function not available", show textview "nothing there"
                setVisibilityListViewOldArrangements ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleOldNothingThere", "string", fragmentOldContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

                // set visibility of FAB for this fragment
                ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "old");
            }
        }
        else { // Function showOldArrangement is not available

            // show the list view for the old arrangements, show textview "Function not available" and hide "nothing there"
            setVisibilityListViewOldArrangements ("hide");
            setVisibilityTextViewNothingThere ("hide");
            setVisibilityTextViewFunctionNotAvailable ("show");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentOldContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

            // set visibility of FAB for this fragment
            ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "old");
        }
    }


    private void setVisibilityListViewOldArrangements (String visibility) {

        if (recyclerViewOldArrangement != null) {

            switch (visibility) {

                case "show":
                    recyclerViewOldArrangement.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    recyclerViewOldArrangement.setVisibility(View.GONE);
                    break;
            }
        }
    }


    private void setVisibilityTextViewFunctionNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = (RelativeLayout) viewFragmentOld.findViewById(R.id.textViewOldArrangementFunctionNotAvailable);

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

        RelativeLayout tmpNothingThere = (RelativeLayout) viewFragmentOld.findViewById(R.id.textViewOldArrangementNothingThere);

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

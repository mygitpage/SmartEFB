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
 * Created by ich on 26.06.16.
 */
public class OurArrangementFragmentOld extends Fragment {

    // fragment view
    View viewFragmentOld;

    // fragment context
    Context fragmentOldContext = null;

    // the fragment
    Fragment fragmentOldThisFragmentContext;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old
    long currentDateOfArrangement;

    // block id of cuurrent arrangements
    String currentBlockIdOfArrangement = "";

    // reference cursorAdapter for the listview
    OurArrangementOldCursorAdapter dataAdapter;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentOld = layoutInflater.inflate(R.layout.fragment_our_arrangement_old, null);

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
            Intent startServiceIntent = new Intent(fragmentOldContext, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            fragmentOldContext.startService(startServiceIntent);
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


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
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

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");
    }


    public void displayOldArrangementSet () {

        String tmpSubtitle;

        // find the listview
        ListView listView = (ListView) viewFragmentOld.findViewById(R.id.listOurArrangementOld);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowOldArrangement, false) && listView != null) { // Function showOldArrangement is available!!!!

            // get all old arrangement from DB
            Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement,"notEqualBlockId");

            if (cursor.getCount() > 0) {

                // show the list view for the old arrangements, hide textview "Function not available" and "nothing there"
                setVisibilityListViewOldArrangements ("show");
                setVisibilityTextViewNothingThere ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");

                // Set correct subtitle in Activity -> "Absprachen aelter als..."
                tmpSubtitle = getResources().getString(getResources().getIdentifier("olderArrangementDateFrom", "string", fragmentOldContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");

                // new dataadapter
                dataAdapter = new OurArrangementOldCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);
            }
            else {

                // hide the list view for the old arrangements, hide textview "Function not available", show textview "nothing there"
                setVisibilityListViewOldArrangements ("hide");
                setVisibilityTextViewFunctionNotAvailable ("hide");
                setVisibilityTextViewNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNothingThere", "string", fragmentOldContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "old");
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
        }
    }


    private void setVisibilityListViewOldArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentOld.findViewById(R.id.listOurArrangementOld);

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

        TextView tmpNotAvailable = (TextView) viewFragmentOld.findViewById(R.id.textViewOldArrangementFunctionNotAvailable);

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

        TextView tmpNothingThere = (TextView) viewFragmentOld.findViewById(R.id.textViewOldArrangementNothingThere);

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

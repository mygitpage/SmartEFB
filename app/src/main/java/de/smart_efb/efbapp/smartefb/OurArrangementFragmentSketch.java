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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 09.09.16.
 */
public class OurArrangementFragmentSketch  extends Fragment {

    // fragment view
    View viewFragmentSketch;

    // fragment context
    Context fragmentSketchContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the date of sketch arrangement
    long currentDateOfSketchArrangement;

    // block id of current sketch arrangements
    String currentBlockIdOfSketchArrangement = "";

    // reference cursorAdapter for the listview
    OurArrangementSketchCursorAdapter dataAdapterListViewOurArrangementSketch = null;

    // the list view for the sketch arrangements
    ListView listViewSketchArrangement;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSketch = layoutInflater.inflate(R.layout.fragment_our_arrangement_sketch, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentSketchBrodcastReceiver, filter);

        return viewFragmentSketch;


    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentSketchContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentSketch();

        // show actual arrangement set
        displaySketchArrangementSet();


    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentSketchBrodcastReceiver);

    }



    // inits the fragment for use
    private void initFragmentSketch() {

        // init the DB
        myDb = new DBAdapter(fragmentSketchContext);

        // init the prefs
        prefs = fragmentSketchContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSketchContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get date of sketch arrangement
        currentDateOfSketchArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis());

        //get block id of sketch arrangement
        currentBlockIdOfSketchArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0");

        // find the listview for sketch arrangement
        listViewSketchArrangement = (ListView) viewFragmentSketch.findViewById(R.id.listOurArrangementSketch);

    }




    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourArrangementFragmentSketchBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with arrangements
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order
                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementSketch = intentExtras.getString("OurArrangementSketch","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");

                if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {

                    //update current block id of sketch arrangements
                    currentBlockIdOfSketchArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0");

                    checkUpdateForShowDialog ();

                    updateListView = true;

                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1")) { // send successfull?

                    Toast.makeText(context, intentExtras.getString("Message"), Toast.LENGTH_LONG).show();

                }

            }

            // update the list view with sketch arrangements
            if (updateListView) {
                updateListView();
            }

        }
    };



    // check prefs for update now and sketch arrangement or only sketch arrangements?
    public void checkUpdateForShowDialog () {


        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false) && prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false)) {

            // set signal arrangements and sketch arrangements are update to false; because user is informed by dialog!
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalNowArrangementUpdate, false);
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false);
            prefsEditor.commit();

            // show dialog arrangement and sketch arrangement change
            ((ActivityOurArrangement) getActivity()).alertDialogArrangementChange("currentSketch");

        }
        else if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false)) {
            // set signal sketch arrangements are update to false; because user is informed by dialog!
            prefsEditor.putBoolean(ConstansClassOurArrangement.namePrefsSignalSketchArrangementUpdate, false);
            prefsEditor.commit();

            // show dialog arrangement change
            ((ActivityOurArrangement) getActivity()).alertDialogArrangementChange("sketch");

        }


    }


    // update the list view with sketch arrangements
    public void updateListView () {

        if (listViewSketchArrangement != null) {
            listViewSketchArrangement.destroyDrawingCache();
            listViewSketchArrangement.setVisibility(ListView.INVISIBLE);
            listViewSketchArrangement.setVisibility(ListView.VISIBLE);

            displaySketchArrangementSet ();
        }
    }





    // show listView with sketch arrangements or info: nothing there
    public void displaySketchArrangementSet () {

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false) && listViewSketchArrangement != null) { // Function showSketchArrangement is available!!!!

            // get the data from db -> all sketch arrangements
            Cursor cursor = myDb.getAllRowsSketchOurArrangement(currentBlockIdOfSketchArrangement);

            if (cursor.getCount() > 0) {

                // set listView visible, textView nothing there and not available gone
                setVisibilityListViewSketchArrangements("show");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("hide");

                // Set correct subtitle in Activity -> "Entwuerfe vom ..."
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("sketchArrangementsubtitle", "string", fragmentSketchContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfSketchArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

                // new dataadapter
                dataAdapterListViewOurArrangementSketch = new OurArrangementSketchCursorAdapter(
                        getActivity(),
                        cursor,
                        0);

                // Assign adapter to ListView
                listViewSketchArrangement.setAdapter(dataAdapterListViewOurArrangementSketch);

            } else {

                // set listView and textView not available gone, set textView nothing there visible
                setVisibilityListViewSketchArrangements("hide");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleSketchNothingThere", "string", fragmentSketchContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");
            }

        }
        else {

            // set listView and textView nothing there gone, set textView not available visible
            setVisibilityListViewSketchArrangements("hide");
            setVisibilityTextViewSketchNotAvailable("show");
            setVisibilityTextViewSketchNothingThere ("hide");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentSketchContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

        }
    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewSketchArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentSketch.findViewById(R.id.listOurArrangementSketch);

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


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewSketchNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentSketch.findViewById(R.id.textViewArrangementSketchFunctionNotAvailable);

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


    private void setVisibilityTextViewSketchNothingThere (String visibility) {

        TextView tmpNothingThere = (TextView) viewFragmentSketch.findViewById(R.id.textViewArrangementSketchNothingThere);

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

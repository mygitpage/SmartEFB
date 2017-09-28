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
import android.view.Gravity;
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
                String tmpExtraOurArrangementSketchComment = intentExtras.getString("OurArrangementSketchComment","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementSketchCommentShareEnable = intentExtras.getString("OurArrangementSettingsSketchCommentShareEnable","0");
                String tmpExtraOurArrangementSketchCommentShareDisable = intentExtras.getString("OurArrangementSettingsSketchCommentShareDisable","0");
                String tmpExtraOurArrangementResetSketchCommentCountComment = intentExtras.getString("OurArrangementSettingsSketchCommentCountComment","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpMessage = intentExtras.getString("Message");

                if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {

                    //update current block id of sketch arrangements
                    currentBlockIdOfSketchArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0");

                    // check arrangement and skecth arrangement update and show dialog arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("sketch");

                    updateListView = true;

                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketchComment != null && tmpExtraOurArrangementSketchComment.equals("1")) {
                    // new sketch comments -> update sketch view -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchContext.getString(R.string.toastMessageCommentSketchNewComments);
                    Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetSketchCommentCountComment != null && tmpExtraOurArrangementResetSketchCommentCountComment.equals("1")) {
                    // reset sketch comment counter -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchContext.getString(R.string.toastMessageArrangementResetSketchCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareDisable  != null && tmpExtraOurArrangementSketchCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchContext.getString(R.string.toastMessageArrangementSketchCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareEnable  != null && tmpExtraOurArrangementSketchCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentSketch = fragmentSketchContext.getString(R.string.toastMessageArrangementSketchCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send successfull?
                    // show message send successefull; position center
                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send not successfull?
                    // show message send not successefull; position center
                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
            }

            // update the list view with sketch arrangements
            if (updateListView) {
                updateListView();
            }

        }
    };




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

        if (listViewSketchArrangement != null) {

            switch (visibility) {

                case "show":
                    listViewSketchArrangement.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    listViewSketchArrangement.setVisibility(View.GONE);
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

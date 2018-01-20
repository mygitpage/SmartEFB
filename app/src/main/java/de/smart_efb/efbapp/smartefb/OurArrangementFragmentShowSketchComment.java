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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 06.10.16.
 */
public class OurArrangementFragmentShowSketchComment extends Fragment {

    // fragment view
    View viewFragmentShowSketchComment;

    // fragment context
    Context fragmentShowSketchCommentContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // reference cursorAdapter for the listview
    OurArrangementShowSketchCommentCursorAdapter showSketchCommentCursorAdapter;

    // Server DB-Id of arrangement to comment
    int sketchArrangementServerDbIdToShow = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the list view for sketch comments
    ListView listViewShowSketchComment = null;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_sketch_comment, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentShowSketchCommentBrodcastReceiver, filter);

        return viewFragmentShowSketchComment;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowSketchCommentContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init and display data from fragment show sketch comment only when an arrangement is choosen
        if (sketchArrangementServerDbIdToShow != 0) {

            // init the fragment now
            initFragmentShowComment();

            // show actual comment set for arrangement
            displayActualCommentSet();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {
            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentShowSketchCommentContext, ExchangeServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            // start service
            fragmentShowSketchCommentContext.startService(startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentShowSketchCommentBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeServiceEfb
    private BroadcastReceiver ourArrangementFragmentShowSketchCommentBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update list view;
            Boolean updateListView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {

                String tmpExtraOurArrangement = intentExtras.getString("OurArrangement","0");
                String tmpExtraOurArrangementSketch = intentExtras.getString("OurArrangementSketch","0");
                String tmpExtraOurArrangementSketchComment = intentExtras.getString("OurArrangementSketchComment","0");
                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");
                String tmpExtraOurArrangementSketchCommentShareEnable = intentExtras.getString("OurArrangementSettingsSketchCommentShareEnable","0");
                String tmpExtraOurArrangementSketchCommentShareDisable = intentExtras.getString("OurArrangementSettingsSketchCommentShareDisable","0");
                String tmpExtraOurArrangementResetSketchCommentCountComment = intentExtras.getString("OurArrangementSettingsSketchCommentCountComment","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentShowSketchCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketchComment != null && tmpExtraOurArrangementSketchComment.equals("1")) {
                    // update show sketch comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowSketchCommentContext.getString(R.string.toastMessageCommentSketchNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    //update view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {
                    // update sketch arrangement! -> go back to fragment sketch arrangement and show dialog

                    // check sketch arrangement and sketch arrangement update and show dialog skezch arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("sketch");

                    // go back to fragment sketch arrangement -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurArrangement.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_sketch_arrangement");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetSketchCommentCountComment != null && tmpExtraOurArrangementResetSketchCommentCountComment.equals("1")) {
                    // reset sketch comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowSketchCommentContext.getString(R.string.toastMessageArrangementResetSketchCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    //update view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareDisable  != null && tmpExtraOurArrangementSketchCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentSketch = fragmentShowSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    //update view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementSketchCommentShareEnable  != null && tmpExtraOurArrangementSketchCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentSketch = fragmentShowSketchCommentContext.getString(R.string.toastMessageArrangementSketchCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentSketch, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    //update view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    updateListView = true;
                }

                // update the list view with sketch arrangements
                if (updateListView) {
                    updateListView();
                }
            }
        }
    };


    // update the list view with sketch arrangements
    public void updateListView () {

        if (listViewShowSketchComment != null) {
            listViewShowSketchComment.destroyDrawingCache();
            listViewShowSketchComment.setVisibility(ListView.INVISIBLE);
            listViewShowSketchComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowSketchCommentContext);

        // init the prefs
        prefs = fragmentShowSketchCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowSketchCommentContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Einschaetzungen Entwuerfe ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowSketchCommentText", "string", fragmentShowSketchCommentContext.getPackageName())) + " " + sketchArrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showSketchComment");
    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            sketchArrangementServerDbIdToShow = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
            if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders

            // call getter-methode isCommentLimitationBorderSet in ActivityOurArrangement to get true-> sketch comments are limited, false-> sketch comments are not limited
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("sketch");
        }
    }


    public void displayActualCommentSet () {

        // get the data (all comments from an sketch arrangement) from DB
        Cursor cursor = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementServerDbIdToShow);

        // get the data (the choosen sketch arrangement) from the DB
        Cursor choosenArrangement = myDb.getRowSketchOurArrangement(sketchArrangementServerDbIdToShow);

        // find the listview
        listViewShowSketchComment = (ListView) viewFragmentShowSketchComment.findViewById(R.id.listOurArrangementShowSketchComment);

        // new dataadapter with custom constructor
        showSketchCommentCursorAdapter = new OurArrangementShowSketchCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                sketchArrangementServerDbIdToShow,
                sketchArrangementNumberInListView,
                commentLimitationBorder,
                choosenArrangement);

        // Assign adapter to ListView
        listViewShowSketchComment.setAdapter(showSketchCommentCursorAdapter);
    }

}
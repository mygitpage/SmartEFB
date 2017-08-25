package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 26.06.16.
 */
public class OurArrangementFragmentNow extends Fragment {

    // fragment view
    View viewFragmentNow;

    // fragment context
    Context fragmentNowContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the list view for the arrangements
    ListView listViewArrangements = null;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // block id of current arrangements
    String currentBlockIdOfArrangement = "";

    // reference cursorAdapter for the listview
    OurArrangementNowCursorAdapter dataAdapterListViewOurArrangement = null;

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentNow = layoutInflater.inflate(R.layout.fragment_our_arrangement_now, null);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentNowBrodcastReceiver, filter);

        return viewFragmentNow;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentNowContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentNow();

        // show actual arrangement set
        displayActualArrangementSet();

    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentNowBrodcastReceiver);

    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeServiceEfb
    private BroadcastReceiver ourArrangementFragmentNowBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurArrangementNow = intentExtras.getString("OurArrangementNow","0");
                String tmpExtraOurArrangementNowComment = intentExtras.getString("OurArrangementNowComment","0");

                String tmpExtraOurArrangementSettings = intentExtras.getString("OurArrangementSettings","0");

                String tmpExtraOurArrangementCommentShare = intentExtras.getString("OurArrangementSettingsCommentShare","0");

                String tmpExtraOurArrangementResetCommentCountComment = intentExtras.getString("OurArrangementSettingsCommentCountComment","0");


                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpUpdateEvaluationLink = intentExtras.getString("UpdateEvaluationLink");
                String tmpMessage = intentExtras.getString("Message");


                Log.d("BROADCAST RECEIVER NOW", "In der Funktion -------");

                // returnMap.put("OurArrangementSettingsSketchCommentCountComment", "1");
                // returnMap.put("OurArrangementSettingsSketchCommentShare","1");
                // returnMap.put("OurArrangement","1");
                // returnMap.put("OurArrangementSettings","1");
                // returnMap.put("OurArrangementSettingsCommentCountComment", "1");
                // returnMap.put("OurArrangementSettingsCommentShare","1");



                if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {

                    //update current block id of arrangements
                    currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

                    // check arrangement and skecth arrangement update and show dialog arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowComment != null && tmpExtraOurArrangementNowComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageCommentNowNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetCommentCountComment != null && tmpExtraOurArrangementResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageArrangementResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                   // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShare  != null && tmpExtraOurArrangementCommentShare .equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageArrangementCommentShare);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }





                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send successfull?

                    Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG).show();
                }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send not successfull?

                    Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG).show();
                }
                else if (tmpUpdateEvaluationLink != null && tmpUpdateEvaluationLink.equals("1")) {

                    updateListView = true;

                    // set new start point for evaluation timer in view
                    prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis());
                    prefsEditor.commit();
               }

                // update the list view because data has change?
                if (updateListView) {
                    updateListView();
                }
            }
        }
    };


    // update the list view with arrangements
    public void updateListView () {

        if (listViewArrangements != null) {
            listViewArrangements.destroyDrawingCache();
            listViewArrangements.setVisibility(ListView.INVISIBLE);
            listViewArrangements.setVisibility(ListView.VISIBLE);

            displayActualArrangementSet ();
        }
    }



    // inits the fragment for use
    private void initFragmentNow() {

        // init the DB
        myDb = new DBAdapter(fragmentNowContext);

        // init the prefs
        prefs = fragmentNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentNowContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

        // ask methode isCommentLimitationBorderSet() in ActivityOurArrangement to limitation in comments? true-> yes, linitation; false-> no
        commentLimitationBorder = ((ActivityOurArrangement) getActivity()).isCommentLimitationBorderSet("current");

        // find the listview for the arrangements
        listViewArrangements = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);

    }


    // show listView with current arrangements or info: mothing there
    public void displayActualArrangementSet () {

        Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement, "equalBlockId");

        Log.d("Arrangement NOW","Anzahl:"+cursor.getCount());

        if (cursor.getCount() > 0 && listViewArrangements != null) {

            // set listView visible and textView hide
            setVisibilityListViewNowArrangements("show");
            setVisibilityTextViewNowNotAvailable("hide");

            // Set correct subtitle in Activity -> "Absprachen vom ..."
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", fragmentNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");

            // new dataadapter
            dataAdapterListViewOurArrangement = new OurArrangementNowCursorAdapter(
                    getActivity(),
                    cursor,
                    0);

            // Assign adapter to ListView
            listViewArrangements.setAdapter(dataAdapterListViewOurArrangement);

        }
        else {

            // set listView hide and textView visible
            setVisibilityListViewNowArrangements("hide");
            setVisibilityTextViewNowNotAvailable("show");

            // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNothingThere", "string", fragmentNowContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");

        }

    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewNowArrangements (String visibility) {

        ListView tmplistView = (ListView) viewFragmentNow.findViewById(R.id.listOurArrangementNow);

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
    private void setVisibilityTextViewNowNotAvailable (String visibility) {

        TextView tmpNotAvailable = (TextView) viewFragmentNow.findViewById(R.id.textViewArrangementNowNothingThere);

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

}

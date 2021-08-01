package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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

    // the recycler view
    RecyclerView recyclerViewNowArangement = null;

    // data array of now arrangements for recycler view
    ArrayList<ObjectSmartEFBArrangement> arrayListArrangements;

    // reference cursorAdapter for the recyler view
    OurArrangementNowArrangementRecylerViewAdapter nowArrangementRecylerViewAdapter;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // block id of current arrangements
    String currentBlockIdOfArrangement = "";

    //limitation in count comments true-> yes, there is a border; no, there is no border, wirte infitisly comments
    Boolean commentLimitationBorder;

    // the fab
    FloatingActionButton fabFragmentNowArrangement;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentNow = layoutInflater.inflate(R.layout.fragment_our_arrangement_now, null);

        // fragment has option menu
        setHasOptionsMenu(true);

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

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentNowContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentNowContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentNowBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_arrangement_fragment_now, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_arrangement_menu_fragment_now_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_arrangement_menu_fragment_now_sort_asc);

        if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending").equals("descending")) {
            registerItemDesc.setVisible(false);
            registerItemAsc.setVisible(true);
        }
        else {
            registerItemAsc.setVisible(false);
            registerItemDesc.setVisible(true);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.our_arrangement_menu_fragment_now_sort_desc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending");
                prefsEditor.apply();
                updateListView();
                return true;
            case R.id.our_arrangement_menu_fragment_now_sort_asc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "ascending");
                prefsEditor.apply();
                updateListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from alarmmanager ourArrangement or from ExchangeJobIntentServiceEfb
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
                String tmpExtraOurArrangementCommentShareEnable = intentExtras.getString("OurArrangementSettingsCommentShareEnable","0");
                String tmpExtraOurArrangementCommentShareDisable = intentExtras.getString("OurArrangementSettingsCommentShareDisable","0");
                String tmpExtraOurArrangementResetCommentCountComment = intentExtras.getString("OurArrangementSettingsCommentCountComment","0");
                String tmpSendSuccessefull = intentExtras.getString("SendSuccessfull");
                String tmpSendNotSuccessefull = intentExtras.getString("SendNotSuccessfull");
                String tmpUpdateEvaluationLink = intentExtras.getString("UpdateEvaluationLink");
                String tmpMessage = intentExtras.getString("Message");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentNowContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {
                    // new arrangement on smartphone -> update now view

                    //update current block id of arrangements
                    currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

                    // check arrangement and skecth arrangement update and show dialog arrangement and sketch arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowComment != null && tmpExtraOurArrangementNowComment.equals("1")) {
                    // new comments -> update now view -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageCommentNowNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetCommentCountComment != null && tmpExtraOurArrangementResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageArrangementResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                   // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareDisable  != null && tmpExtraOurArrangementCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageArrangementCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareEnable  != null && tmpExtraOurArrangementCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentNowContext.getString(R.string.toastMessageArrangementCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpSendSuccessefull != null && tmpSendSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send successfull?

                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                 }
                else if (tmpSendNotSuccessefull != null && tmpSendNotSuccessefull.equals("1") && tmpMessage != null && tmpMessage.length() > 0) { // send not successfull?

                    Toast toast = Toast.makeText(context, tmpMessage, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpUpdateEvaluationLink != null && tmpUpdateEvaluationLink.equals("1")) {
                    // evaluationperiod has change -> update view, only when current time is bigger than last start point
                    if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0)) {
                        updateListView = true;
                        // set new start point for evaluation timer in view
                        prefsEditor.putLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis());
                        prefsEditor.apply();
                    }
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    updateListView = true;

                    // new alarm manager service for start all needed alarms
                    EfbSetAlarmManager efbSetAlarmManager = new EfbSetAlarmManager(context);
                    // start check our arrangement alarm manager, when function our arrangement is on
                    if (prefs.getBoolean(ConstansClassMain.namePrefsMainMenueElementId_OurArrangement, false)) {
                        efbSetAlarmManager.setAlarmManagerForOurArrangementEvaluation();
                    }
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

        if (recyclerViewNowArangement != null) {
            recyclerViewNowArangement.destroyDrawingCache();
            recyclerViewNowArangement.setVisibility(ListView.INVISIBLE);
            recyclerViewNowArangement.setVisibility(ListView.VISIBLE);

            displayActualArrangementSet ();
        }
    }


    // inits the fragment for use
    private void initFragmentNow() {

        // init the DB
        myDb = new DBAdapter(fragmentNowContext);

        // call getter-methode getFabViewOurArrangement() in ActivityOurArrangement to get view for fab
        fabFragmentNowArrangement = ((ActivityOurArrangement)getActivity()).getFabViewOurArrangement();

        // init the prefs
        prefs = fragmentNowContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentNowContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        //get current block id of arrangements
        currentBlockIdOfArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfArrangement, "0");

        // ask methode isCommentLimitationBorderSet() in ActivityOurArrangement to limitation in comments? true-> yes, linitation; false-> no
        commentLimitationBorder = ((ActivityOurArrangement) getActivity()).isCommentLimitationBorderSet("current");

        // new recyler view
        recyclerViewNowArangement = viewFragmentNow.findViewById(R.id.listOurArrangementNowArrangement);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentNowContext);
        recyclerViewNowArangement.setLayoutManager(linearLayoutManager);
        recyclerViewNowArangement.setHasFixedSize(true);

        // get the data (all arrangement) from DB
        final Cursor cursor = myDb.getAllRowsCurrentOurArrangement(currentBlockIdOfArrangement, "equalBlockId" , prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending"));

        // show fab and comment arrangement only when on!
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false) && cursor.getCount() > 0) {

            // show fab and set on click listener
            if (fabFragmentNowArrangement != null) {

                fabFragmentNowArrangement.show();

                // add on click listener to fab
                fabFragmentNowArrangement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (cursor.getCount() > 1) {

                            // create popup menu for fab
                            PopupMenu popupFabCommentArrangement = new PopupMenu(((ActivityOurArrangement) getActivity()), view);

                            // inflate popup menu for fab
                            popupFabCommentArrangement.getMenuInflater().inflate(R.menu.popup_efb_our_arrangement_now_arrangement_fab, popupFabCommentArrangement.getMenu());

                            // set on click listener for popup menu item
                            popupFabCommentArrangement.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                @Override
                                public boolean onMenuItemClick(MenuItem item) {

                                    Intent intent = new Intent(fragmentNowContext, ActivityOurArrangement.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("com", "comment_an_arrangement");

                                    switch (item.getItemId()) {
                                        case R.id.arrangement1:
                                            cursor.moveToFirst();
                                            intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                            intent.putExtra("arr_num", 1);

                                            break;
                                        case R.id.arrangement2:
                                            if (cursor.moveToPosition(1)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 2);
                                            }
                                            break;
                                        case R.id.arrangement3:
                                            if (cursor.moveToPosition(2)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 3);
                                            }
                                            break;
                                        case R.id.arrangement4:
                                            if (cursor.moveToPosition(3)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 4);
                                            }
                                            break;

                                        case R.id.arrangement5:
                                            if (cursor.moveToPosition(4)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 5);
                                            }
                                            break;
                                        case R.id.arrangement6:
                                            if (cursor.moveToPosition(5)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 6);
                                            }
                                            break;
                                        case R.id.arrangement7:
                                            if (cursor.moveToPosition(6)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 7);
                                            }
                                            break;

                                        case R.id.arrangement8:
                                            if (cursor.moveToPosition(7)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 8);
                                            }
                                            break;
                                        case R.id.arrangement9:
                                            if (cursor.moveToPosition(8)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 9);
                                            }
                                            break;
                                        case R.id.arrangement10:
                                            if (cursor.moveToPosition(9)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 10);
                                            }
                                            break;

                                        case R.id.arrangement11:
                                            if (cursor.moveToPosition(10)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 11);
                                            }
                                            break;
                                        case R.id.arrangement12:
                                            if (cursor.moveToPosition(11)) {
                                                intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                                                intent.putExtra("arr_num", 12);
                                            }
                                            break;
                                    }

                                    // start comment choosen arrangement
                                    fragmentNowContext.startActivity(intent);

                                    return true;
                                }
                            });

                            // disable menu entry not used
                            int resId;
                            String ressourceArrangementString = "arrangement";
                            for (int t = cursor.getCount() + 1; t <= 12; t++) {

                                resId = getResources().getIdentifier(ressourceArrangementString + t, "id", fragmentNowContext.getPackageName());
                                // set popup menu entrys gone, when not needed
                                popupFabCommentArrangement.getMenu().findItem(resId).setVisible(false);
                            }

                            // show popup menu
                            popupFabCommentArrangement.show();

                        } else {
                            // only one arrangement in db
                            cursor.moveToFirst();
                            Intent intent = new Intent(fragmentNowContext, ActivityOurArrangement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("com", "comment_an_arrangement");
                            intent.putExtra("db_id", cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));
                            intent.putExtra("arr_num", 1);
                            // start comment for this arrangement
                            fragmentNowContext.startActivity(intent);
                        }
                    }
                });
            }
        }

    }


    // show recycler view with current arrangements or info: nothing there
    public void displayActualArrangementSet () {

        // get the data (all now arrangements) from DB
        arrayListArrangements = myDb.getAllRowsOurArrangementNowArrayList(currentBlockIdOfArrangement, "equalBlockId", prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending"));

        if (arrayListArrangements.size() > 0 && recyclerViewNowArangement != null) {

            // set listView visible and textView hide
            setVisibilityListViewNowArrangements("show");
            setVisibilityTextViewNowNotAvailable("hide");

            // Set correct subtitle in Activity -> "Absprachen vom ..."
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("currentArrangementDateFrom", "string", fragmentNowContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfArrangement, "dd.MM.yyyy");
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");

            // new dataadapter
            nowArrangementRecylerViewAdapter = new OurArrangementNowArrangementRecylerViewAdapter (
                    getActivity(),
                    arrayListArrangements,
                    0);

            // Assign adapter to ListView
            recyclerViewNowArangement.setAdapter(nowArrangementRecylerViewAdapter);
        }
        else {

            // set listView hide and textView visible
            setVisibilityListViewNowArrangements("hide");
            setVisibilityTextViewNowNotAvailable("show");

            // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNowNothingThere", "string", fragmentNowContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "now");
        }
    }


    // set visibility of listViewOurArrangement
    private void setVisibilityListViewNowArrangements (String visibility) {

        RecyclerView tmpRecyclerView = viewFragmentNow.findViewById(R.id.listOurArrangementNowArrangement);

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
    private void setVisibilityTextViewNowNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = viewFragmentNow.findViewById(R.id.textViewArrangementNowNothingThere);

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
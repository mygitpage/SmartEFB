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

    // the recycler view
    RecyclerView recyclerViewSketchArrangement = null;

    // data array of sketch arrangements for recycler view
    ArrayList<ObjectSmartEFBArrangement> arrayListSketchArrangements;

    // reference arrayListAdapter for the recyler view
    OurArrangementSketchArrangementRecyclerViewAdapter sketchArrangementRecyclerViewAdapter;

    // the date of sketch arrangement
    long currentDateOfSketchArrangement;

    // block id of current sketch arrangements
    String currentBlockIdOfSketchArrangement = "";

    // true-> sketch comments are limited, false -> sketch comments are not limited
    Boolean sketchCommentLimitationBorder = false;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentSketch = layoutInflater.inflate(R.layout.fragment_our_arrangement_sketch, null);

        // fragment has option menu
        setHasOptionsMenu(true);

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

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentSketchContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentSketchContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentSketchBrodcastReceiver);

        // close db connection
        myDb.close();
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

        // call getter-methode isCommentLimitationBorderSet in ActivityOurArrangement to get true-> sketch comments are limited, false-> sketch comments are not limited
        sketchCommentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("sketch");

        // new recyler view
        recyclerViewSketchArrangement = viewFragmentSketch.findViewById(R.id.listOurArrangementSketchArrangement);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentSketchContext);
        recyclerViewSketchArrangement.setLayoutManager(linearLayoutManager);
        recyclerViewSketchArrangement.setHasFixedSize(true);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_arrangement_fragment_sketch, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_sort_asc);
        MenuItem registerNoArrangementsInfo = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_no_arrangement_available);
        MenuItem registerFunctionOff = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_function_off);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false)) {

            if (arrayListSketchArrangements != null && arrayListSketchArrangements.size() > 0) {

                registerNoArrangementsInfo.setVisible(false);
                registerFunctionOff.setVisible(false);
                if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "descending").equals("descending")) {
                    registerItemDesc.setVisible(false);
                    registerItemAsc.setVisible(true);
                } else {
                    registerItemAsc.setVisible(false);
                    registerItemDesc.setVisible(true);
                }
            } else {
                registerNoArrangementsInfo.setVisible(true);
                registerFunctionOff.setVisible(false);
                registerItemDesc.setVisible(false);
                registerItemAsc.setVisible(false);
            }
        }
        else {
            registerNoArrangementsInfo.setVisible(false);
            registerFunctionOff.setVisible(true);
            registerItemDesc.setVisible(false);
            registerItemAsc.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.our_arrangement_menu_fragment_sketch_sort_desc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "descending");
                prefsEditor.apply();
                updateListView();
                return true;
            case R.id.our_arrangement_menu_fragment_sketch_sort_asc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "ascending");
                prefsEditor.apply();
                updateListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
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
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentSketchContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSketch != null && tmpExtraOurArrangementSketch.equals("1")) {

                    //update current block id of sketch arrangements
                    currentBlockIdOfSketchArrangement = prefs.getString(ConstansClassOurArrangement.namePrefsCurrentBlockIdOfSketchArrangement, "0");

                    // check arrangement and sketch arrangement update and show dialog arrangement and sketch arrangement change
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
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    updateListView = true;
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

        if (recyclerViewSketchArrangement != null) {
            recyclerViewSketchArrangement.destroyDrawingCache();
            recyclerViewSketchArrangement.setVisibility(ListView.INVISIBLE);
            recyclerViewSketchArrangement.setVisibility(ListView.VISIBLE);

            displaySketchArrangementSet ();
        }
    }


    // show listView with sketch arrangements or info: nothing there
    public void displaySketchArrangementSet () {

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false) && recyclerViewSketchArrangement != null) { // Function showSketchArrangement is available!!!!

            // get the data (all now arrangements) from DB
            arrayListSketchArrangements = myDb.getAllRowsOurArrangementSketchArrayList(currentBlockIdOfSketchArrangement, prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchList, "descending"));

            if (arrayListSketchArrangements.size() > 0) {

                // set recycler view visible, textView nothing there and not available gone
                setVisibilityRecyclerViewSketchArrangements("show");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("hide");

                // Set correct subtitle in Activity
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("sketchArrangementsubtitle", "string", fragmentSketchContext.getPackageName())) + " " + EfbHelperClass.timestampToDateFormat(currentDateOfSketchArrangement, "dd.MM.yyyy");
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

                // set visibility of FAB for this fragment

                // show fab and comment sketch arrangement only when on and possible!
                if ((prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowLinkCommentSketchArrangement, false) && (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0)) > 0 ) || !sketchCommentLimitationBorder) {
                    // set fab visibility
                    ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility("show", "sketch");
                    // set fab click listener
                    ((ActivityOurArrangement) getActivity()).setOurArrangementFABClickListener(arrayListSketchArrangements, "sketch", "comment_an_sketch_arrangement");
                }
                else {
                    ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility("hide", "sketch");
                }
                
                // new recycler view adapter
                sketchArrangementRecyclerViewAdapter = new OurArrangementSketchArrangementRecyclerViewAdapter(
                        getActivity(),
                        arrayListSketchArrangements,
                        0);

                // Assign adapter to recycler view
                recyclerViewSketchArrangement.setAdapter(sketchArrangementRecyclerViewAdapter);

            } else {

                // set recycler view and textView not available gone, set textView nothing there visible
                setVisibilityRecyclerViewSketchArrangements("hide");
                setVisibilityTextViewSketchNotAvailable("hide");
                setVisibilityTextViewSketchNothingThere ("show");

                // Set correct subtitle in Activity -> "Keine Absprachen vorhanden"
                String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleSketchNothingThere", "string", fragmentSketchContext.getPackageName()));
                ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

                // set visibility of FAB for this fragment
                ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "sketch");

            }
        }
        else {

            // set listView and textView nothing there gone, set textView not available visible
            setVisibilityRecyclerViewSketchArrangements("hide");
            setVisibilityTextViewSketchNotAvailable("show");
            setVisibilityTextViewSketchNothingThere ("hide");

            // Set correct subtitle in Activity -> "Funktion nicht moeglich"
            String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleNotAvailable", "string", fragmentSketchContext.getPackageName()));
            ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketch");

            // set visibility of FAB for this fragment
            ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility ("hide", "sketch");

        }
    }


    // set visibility of listViewOurArrangement
    private void setVisibilityRecyclerViewSketchArrangements (String visibility) {

        if (recyclerViewSketchArrangement != null) {

            switch (visibility) {

                case "show":
                    recyclerViewSketchArrangement.setVisibility(View.VISIBLE);
                    break;
                case "hide":
                    recyclerViewSketchArrangement.setVisibility(View.GONE);
                    break;
            }
        }
    }


    // set visibility of textView "nothing there"
    private void setVisibilityTextViewSketchNotAvailable (String visibility) {

        RelativeLayout tmpNotAvailable = viewFragmentSketch.findViewById(R.id.textViewArrangementSketchFunctionNotAvailable);

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

        RelativeLayout tmpNothingThere = viewFragmentSketch.findViewById(R.id.textViewArrangementSketchNothingThere);

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

package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ich on 06.10.16.
 */
public class OurArrangementFragmentShowSketchComment extends Fragment {

    // fragment view
    View viewFragmentShowSketchComment;

    // fragment context
    Context fragmentShowSketchCommentContext = null;

    // the recycler view
    RecyclerView recyclerViewShowSketchComment = null;

    // data array of sketch comments for recycler view
    ArrayList<ObjectSmartEFBComment> arrayListSketchComments;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // reference for the recycler view
    OurArrangementShowSketchCommentRecyclerViewAdapter showSketchCommentRecyclerViewAdapter;

    // Server DB-Id of arrangement to comment
    int sketchArrangementServerDbIdToShow = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean sketchCommentLimitationBorder = false;

    // reference to dialog selecting number of sketch comment
    AlertDialog dialogSelectingNumberOfSketchComment;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_sketch_comment, null);

        // fragment has option menu
        setHasOptionsMenu(true);

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
            Intent startServiceIntent = new Intent(fragmentShowSketchCommentContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentShowSketchCommentContext, startServiceIntent);
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_arrangement_fragment_sketch_show_comment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_show_comment_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_arrangement_menu_fragment_sketch_show_comment_sort_asc);

        if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending").equals("descending")) {
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

            case R.id.our_arrangement_menu_fragment_sketch_show_comment_sort_desc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            case R.id.our_arrangement_menu_fragment_sketch_show_comment_sort_asc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "ascending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            case R.id.our_arrangement_menu_fragment_sketch_count_comment_in_recycler_view:
                showDialogForSelectingNumberOfSketchCommentInList();
                updateRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogForSelectingNumberOfSketchCommentInList () {

        LayoutInflater dialogInflater;

        // get alert dialog builder with custom style
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.selectDialogStyle);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogSelecting = dialogInflater.inflate(R.layout.select_dialog_our_arrangement_sketch_number_of_comment, null);

        //set radio button with correct value
        RadioButton tmpRadioButton;
        switch (prefs.getInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementSketchShowComment, 50)) {
            case 5:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfSketchComment5);
                break;
            case 10:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfSketchComment10);
                break;
            case 20:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfSketchComment20);
                break;
            case 50:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfSketchComment50);
                break;
            default:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfSketchCommentAll);
                break;
        }
        tmpRadioButton.setChecked(true);

        // set on click listener
        RadioGroup radioGroupNumberOfSketchComment = dialogSelecting.findViewById(R.id.selectNumberOfSketchComment);
        radioGroupNumberOfSketchComment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int numberOfSketchComment = 0;
                switch (checkedId) {
                    case R.id.numberOfSketchComment5:
                        numberOfSketchComment = 5;
                        break;
                    case R.id.numberOfSketchComment10:
                        numberOfSketchComment = 10;
                        break;
                    case R.id.numberOfSketchComment20:
                        numberOfSketchComment = 20;
                        break;
                    case R.id.numberOfSketchComment50:
                        numberOfSketchComment = 50;
                        break;
                    case R.id.numberOfSketchCommentAll:
                        numberOfSketchComment = 0;
                        break;
                }
                prefsEditor.putInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementSketchShowComment, numberOfSketchComment);
                prefsEditor.apply();

                // close dialog
                dialogSelectingNumberOfSketchComment.dismiss();

                // update recycler view with new number of comment
                updateRecyclerView();
            }
        });

        // build the dialog
        builder.setView(dialogSelecting);

        // Add close button
        builder.setNegativeButton("Schliessen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogSelectingNumberOfSketchComment.cancel();
            }
        });

        // add title
        builder.setTitle(R.string.select_dialog_our_arrangement_sketch_number_of_comment_title);

        // and create
        dialogSelectingNumberOfSketchComment = builder.create();

        // set correct color of close button
        dialogSelectingNumberOfSketchComment.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // change background and text color of button
                Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                // Change negative button text and background color
                negativeButton.setTextColor(ContextCompat.getColor(((ActivityOurArrangement)getActivity()), R.color.white));
                negativeButton.setBackgroundResource(R.drawable.select_dialog_style_custom_negativ_button_background);
            }
        });

        // show dialog
        dialogSelectingNumberOfSketchComment.show();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
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
                String tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView = intentExtras.getString("OurArrangementSketchCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");
                // sort sequence of list view changed
                String tmpSortSequenceChangeSketchList = intentExtras.getString("changeSortSequenceOfListViewSketchComment", "0");

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
                else if (tmpSortSequenceChangeSketchList.equals("1")) {
                    if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending").equals("descending")) {
                        prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "ascending");
                    }
                    else {
                        prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending");
                    }
                    prefsEditor.apply();

                    // list view sort sequence have change -> refresh view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView != null &&  tmpExtraOurArrangementSketchCommentSendInBackgroundRefreshView.equals("1")) {
                    // sketch comment send in background -> refresh view
                    updateListView = true;
                }

                // update the list view with sketch arrangements
                if (updateListView) {
                    updateRecyclerView();
                }
            }
        }
    };


    // update the recycler view with sketch comments
    public void updateRecyclerView() {

        if (recyclerViewShowSketchComment != null) {

            recyclerViewShowSketchComment.destroyDrawingCache();
            recyclerViewShowSketchComment.setVisibility(ListView.INVISIBLE);
            recyclerViewShowSketchComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowSketchCommentContext);

        // init the prefs
        prefs = fragmentShowSketchCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowSketchCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Einschaetzungen Entwuerfe ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowSketchCommentText", "string", fragmentShowSketchCommentContext.getPackageName())) + " " + sketchArrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showSketchComment");

        // new recycler view
        recyclerViewShowSketchComment = viewFragmentShowSketchComment.findViewById(R.id.listOurArrangementShowSketchComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentShowSketchCommentContext);
        recyclerViewShowSketchComment.setLayoutManager(linearLayoutManager);
        recyclerViewShowSketchComment.setHasFixedSize(true);
    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getSketchArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            sketchArrangementServerDbIdToShow = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
            if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders

            // call getter-methode isCommentLimitationBorderSet in ActivityOurArrangement to get true-> sketch comments are limited, false-> sketch comments are not limited
            sketchCommentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("sketch");
        }
    }


    public void displayActualCommentSet () {

        // get the data (all comments from an sketch arrangement) from DB
        arrayListSketchComments = myDb.getAllRowsOurArrangementSketchCommentArrayList(sketchArrangementServerDbIdToShow, prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending"), prefs.getInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementSketchShowComment, 50));

        // get the data for chose sketch arrangement
        ArrayList<ObjectSmartEFBArrangement> arrayListChooseSketchArrangement = myDb.getRowOurArrangementSketchArrayList(sketchArrangementServerDbIdToShow);

        // set visibility of FAB for this fragment
        // show fab and comment arrangement only when on!
        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowSketchArrangement, false) && (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0)) > 0 || !sketchCommentLimitationBorder) {
            // set fab visibility
            ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility("show", "showSketchComment");
            // set fab click listener
            ((ActivityOurArrangement) getActivity()).setOurArrangementFABClickListener(arrayListChooseSketchArrangement, "showSketchComment", "comment_an_sketch_arrangement");
        }
        else {
            ((ActivityOurArrangement) getActivity()).setOurArrangementFABVisibility("hide", "showSketchComment");
        }

        if (arrayListSketchComments.size() > 0 && arrayListChooseSketchArrangement.size() > 0 && recyclerViewShowSketchComment != null) {

            showSketchCommentRecyclerViewAdapter = new OurArrangementShowSketchCommentRecyclerViewAdapter(
                    getActivity(),
                    arrayListSketchComments,
                    sketchArrangementServerDbIdToShow,
                    sketchArrangementNumberInListView,
                    sketchCommentLimitationBorder,
                    arrayListChooseSketchArrangement);

            // Assign adapter to Recycler View
            recyclerViewShowSketchComment.setAdapter(showSketchCommentRecyclerViewAdapter);

        }
    }

}
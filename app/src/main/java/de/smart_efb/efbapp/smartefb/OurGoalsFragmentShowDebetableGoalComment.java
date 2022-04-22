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
 * Created by ich on 22.11.2016.
 */
public class OurGoalsFragmentShowDebetableGoalComment extends Fragment {

    // fragment view
    View viewFragmentShowDebetableGoalComment;

    // fragment context
    Context fragmentShowDebetableGoalCommentContext = null;

    // the recycler view
    RecyclerView recyclerViewShowDebetableComment = null;

    // data array of debetable comments for recycler view
    ArrayList<ObjectSmartEFBGoalsComment> arrayListDebetableComments;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the current date of debetable goal -> the other are old (look at tab old)
    long currentDateOfDebetableGoal;

    // reference for the recycler view
    OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter showDebetableCommentRecyclerViewAdapter;

    // DB-Id of debetable goal to comment
    int debetableServerDbIdToShow = 0;

    // debetable goal number in list view
    int debetableGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // reference to dialog selecting number of debetable comment
    AlertDialog dialogSelectingNumberOfDebetableComment;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowDebetableGoalComment = layoutInflater.inflate(R.layout.fragment_our_goals_show_debetable_goal_comment, null);

        // fragment has option menu
        setHasOptionsMenu(true);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver, filter);

        return viewFragmentShowDebetableGoalComment;
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowDebetableGoalCommentContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init and display data from fragment show debetable goal comment only when an debetable goal is choosen
        if (debetableServerDbIdToShow != 0) {

            // init the fragment now
            initFragmentShowComment();

            // show actual comment set for debetable goal
            displayActualCommentSet();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentShowDebetableGoalCommentContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentShowDebetableGoalCommentContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver);

        // close db connection
        myDb.close();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_goals_fragment_debetable_show_comment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_goals_menu_fragment_debetable_show_comment_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_goals_menu_fragment_debetable_show_comment_sort_asc);

        if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending").equals("descending")) {
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

            case R.id.our_goals_menu_fragment_debetable_show_comment_sort_desc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            case R.id.our_goals_menu_fragment_debetable_show_comment_sort_asc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "ascending");
                prefsEditor.apply();
                updateRecyclerView();
                return true;
            case R.id.our_goals_menu_fragment_debetable_count_comment_in_recycler_view:
                showDialogForSelectingNumberOfDebetableCommentInList();
                updateRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogForSelectingNumberOfDebetableCommentInList () {

        LayoutInflater dialogInflater;

        // get alert dialog builder with custom style
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.selectDialogStyle);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogSelecting = dialogInflater.inflate(R.layout.select_dialog_our_goals_debetable_number_of_comment, null);

        //set radio button with correct value
        RadioButton tmpRadioButton;
        switch (prefs.getInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsDebetableGoalsShowComment, 50)) {
            case 5:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfDebetableComment5);
                break;
            case 10:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfDebetableComment10);
                break;
            case 20:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfDebetableComment20);
                break;
            case 50:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfDebetableComment50);
                break;
            default:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfDebetableCommentAll);
                break;
        }
        tmpRadioButton.setChecked(true);

        // set on click listener
        RadioGroup radioGroupNumberOfDebetableComment = dialogSelecting.findViewById(R.id.selectNumberOfDebetableComment);
        radioGroupNumberOfDebetableComment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int numberOfDebetableComment = 0;
                switch (checkedId) {
                    case R.id.numberOfDebetableComment5:
                        numberOfDebetableComment = 5;
                        break;
                    case R.id.numberOfDebetableComment10:
                        numberOfDebetableComment = 10;
                        break;
                    case R.id.numberOfDebetableComment20:
                        numberOfDebetableComment = 20;
                        break;
                    case R.id.numberOfDebetableComment50:
                        numberOfDebetableComment = 50;
                        break;
                    case R.id.numberOfDebetableCommentAll:
                        numberOfDebetableComment = 0;
                        break;
                }
                prefsEditor.putInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsDebetableGoalsShowComment, numberOfDebetableComment);
                prefsEditor.apply();

                // close dialog
                dialogSelectingNumberOfDebetableComment.dismiss();

                // update recycler view with new number of comment
                updateRecyclerView();
            }
        });

        // build the dialog
        builder.setView(dialogSelecting);

        // Add close button
        builder.setNegativeButton("Schliessen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogSelectingNumberOfDebetableComment.cancel();
            }
        });

        // add title
        builder.setTitle(R.string.select_dialog_our_goals_debetable_number_of_comment_title);

        // and create
        dialogSelectingNumberOfDebetableComment = builder.create();

        // set correct color of close button
        dialogSelectingNumberOfDebetableComment.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // change background and text color of button
                Button negativeButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                // Change negative button text and background color
                negativeButton.setTextColor(ContextCompat.getColor(((ActivityOurGoals)getActivity()), R.color.white));
                negativeButton.setBackgroundResource(R.drawable.select_dialog_style_custom_negativ_button_background);
            }
        });

        // show dialog
        dialogSelectingNumberOfDebetableComment.show();
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver ourGoalsFragmentShowCommentDebetableGoalsBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurGoalsDebetableNow = intentExtras.getString("OurGoalsDebetableNow","0");
                String tmpExtraOurGoalsDebetableNowComment = intentExtras.getString("OurGoalsDebetableComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareDisable= intentExtras.getString("OurGoalsSettingsDebetableCommentShareDisable","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsDebetableCommentShareEnable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsDebetableCommentCountComment","0");
                String tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView = intentExtras.getString("OurGoalsDebetableCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");
                // sort sequence of list view changed
                String tmpSortSequenceChange = intentExtras.getString("changeSortSequenceOfListViewDebetableComment", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentShowDebetableGoalCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }

                if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNowComment != null && tmpExtraOurGoalsDebetableNowComment.equals("1")) {
                    // update debetable comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageCommentDebetableGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsDebetableNow != null && tmpExtraOurGoalsDebetableNow.equals("1")) {

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("debetable");

                    // go back to fragment debetable goals goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_debetable_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset debetable comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable.equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable.equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowDebetableGoalCommentContext.getString(R.string.toastMessageDebetableGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // refresh fragments view
                    refreshView = true;
                }
                else if (tmpSortSequenceChange.equals("1")) {
                    if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending").equals("descending")) {
                        prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "ascending");
                    }
                    else {
                        prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending");
                    }
                    prefsEditor.apply();

                    // list view sort sequence have change -> refresh view
                    refreshView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    refreshView = true;
                }
                else if (tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView != null &&  tmpExtraOurGoalsDebetableCommentSendInBackgroundRefreshView.equals("1")) {

                    // jointly comment send in background -> refresh view
                    refreshView = true;
                }

                // update the list view with debetable goals
                if (refreshView) {
                    updateRecyclerView();
                }
            }
        }
    };



    // update the list view with debetable comments
    public void updateRecyclerView() {

        if (recyclerViewShowDebetableComment != null) {

            recyclerViewShowDebetableComment.destroyDrawingCache();
            recyclerViewShowDebetableComment.setVisibility(ListView.INVISIBLE);
            recyclerViewShowDebetableComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowDebetableGoalCommentContext);

        // init the prefs
        prefs = fragmentShowDebetableGoalCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowDebetableGoalCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of debetable goal
        currentDateOfDebetableGoal = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Einschaetzungen strittige Ziele ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGoalsShowCommentText", "string", fragmentShowDebetableGoalCommentContext.getPackageName())) + " " + debetableGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableShowComment");

        // new recycler view
        recyclerViewShowDebetableComment = viewFragmentShowDebetableGoalComment.findViewById(R.id.listOurGoalsShowDebetableComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentShowDebetableGoalCommentContext);
        recyclerViewShowDebetableComment.setLayoutManager(linearLayoutManager);
        recyclerViewShowDebetableComment.setHasFixedSize(true);
    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpDebetableGoalDbIdToComment = 0;

        // call getter-methode getDebetableGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale debetable goal
        tmpDebetableGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getDebetableGoalDbIdFromLink();

        if (tmpDebetableGoalDbIdToComment > 0) {
            debetableServerDbIdToShow = tmpDebetableGoalDbIdToComment;

            // call getter-methode getDdebetableGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale debetable goal
            debetableGoalNumberInListView = ((ActivityOurGoals)getActivity()).getDebetableGoalNumberInListview();
            if (debetableGoalNumberInListView < 1) debetableGoalNumberInListView = 1; // check borders

            // call getter-methode isCommentLimitationBorderSet in ActivityOurGoals to get true-> debetable comments are limited, false-> debetable comments are not limited
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("debetableGoals");
        }
    }


    public void displayActualCommentSet () {

        // get the data (all comments from an debetable goals) from DB
        arrayListDebetableComments = myDb.getAllRowsOurGoalsDebetableGoalsCommentArrayList (debetableServerDbIdToShow, prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending"), prefs.getInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsDebetableGoalsShowComment, 50));

        // get the data for chose debetable goals
        ArrayList<ObjectSmartEFBGoals> arrayListChoseDebetableGoals = myDb.getRowOurGoalsDebetableGoalsArrayList(debetableServerDbIdToShow);

        // set visibility of FAB for this fragment
        // show fab and comment debetable goals only when on!
        if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkDebetableGoals, false) && (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0)) > 0 || !commentLimitationBorder) {
            // set fab visibility
            ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("show", "debetableShowComment");
            // set fab click listener
            ((ActivityOurGoals) getActivity()).setOurGoalFABClickListener(arrayListChoseDebetableGoals, "debetableShowComment", "comment_an_debetable_goal");
        }
        else {
            ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("hide", "debetableShowComment");
        }


            
        
        
        
        











/*

        // get the data (all comments for an debetable goal) from DB
        Cursor cursor = myDb.getAllRowsOurGoalsDebetableGoalsComment(debetableServerDbIdToShow, prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending"));

        // get the data (the choosen debetable goal) from the DB
        Cursor choosenDebetableGoal = myDb.getDebetableRowOurGoals(debetableServerDbIdToShow);

*/






        if (arrayListDebetableComments.size() > 0 && arrayListChoseDebetableGoals.size() > 0 && recyclerViewShowDebetableComment != null) {

            showDebetableCommentRecyclerViewAdapter = new OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter(
                    getActivity(),
                    arrayListDebetableComments,
                    debetableServerDbIdToShow,
                    debetableGoalNumberInListView,
                    commentLimitationBorder,
                    arrayListChoseDebetableGoals);

            // Assign adapter to Recycler View
            recyclerViewShowDebetableComment.setAdapter(showDebetableCommentRecyclerViewAdapter);

        }
    }

}
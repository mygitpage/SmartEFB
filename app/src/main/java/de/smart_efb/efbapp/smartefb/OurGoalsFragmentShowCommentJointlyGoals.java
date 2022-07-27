package de.smart_efb.efbapp.smartefb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
 * Created by ich on 28.10.2016.
 */
public class OurGoalsFragmentShowCommentJointlyGoals extends Fragment {

    // fragment view
    View viewFragmentShowCommentJointlyGoals;

    // fragment context
    Context fragmentShowCommentJointlyGoalsContext = null;

    // the recycler view
    RecyclerView recyclerViewShowComment = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the current date of jointly goals -> the other are old (look at tab old)
    long currentDateOfJointlyGoals;

    // reference for the recycler view adapter
    OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter showJointlyGoalCommentRecyclerViewAdapter;

    // data array of comments for recycler view
    ArrayList<ObjectSmartEFBGoalsComment> arrayListJointlyComments;

    // DB-Id of jointly goal to comment
    int jointlyGoalDbIdToShow = 0;

    // goal number in list view
    int jointlyGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // reference to dialog selecting number of comment
    AlertDialog dialogSelectingNumberOfComment;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowCommentJointlyGoals = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_show_comment, null);

        // fragment has option menu
        setHasOptionsMenu(true);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver, filter);

        return viewFragmentShowCommentJointlyGoals; 
    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowCommentJointlyGoalsContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init and display data from fragment show comment only when a goal is choosen
        if (jointlyGoalDbIdToShow != 0) {

            // init the fragment show comment jointly goals
            initFragmentShowCommentJointlyGoals();

            // show actual comment set for selected jointly goal
            displayActualCommentSet();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentShowCommentJointlyGoalsContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentShowCommentJointlyGoalsContext, startServiceIntent);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_goals_fragment_jointly_show_comment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_goals_menu_fragment_jointly_show_comment_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_goals_menu_fragment_jointly_show_comment_sort_asc);

        if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending").equals("descending")) {
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

            case R.id.our_goals_menu_fragment_jointly_show_comment_sort_desc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending");
                prefsEditor.apply();
                updateRecyclerView();

                return true;
            case R.id.our_goals_menu_fragment_jointly_show_comment_sort_asc:
                prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "ascending");
                prefsEditor.apply();
                updateRecyclerView();

                return true;
            case R.id.our_goals_menu_fragment_jointly_count_comment_in_recycler_view:
                showDialogForSelectingNumberOfCommentInList();
                updateRecyclerView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogForSelectingNumberOfCommentInList () {

        LayoutInflater dialogInflater;

        // get alert dialog builder with custom style
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.selectDialogStyle);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogSelecting = dialogInflater.inflate(R.layout.select_dialog_our_goals_jointly_number_of_comment, null);

        //set radio button with correct value
        RadioButton tmpRadioButton;
        switch (prefs.getInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsJointlyGoalsShowComment, 50)) {
            case 5:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfComment5);
                break;
            case 10:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfComment10);
                break;
            case 20:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfComment20);
                break;
            case 50:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfComment50);
                break;
            default:
                tmpRadioButton = dialogSelecting.findViewById(R.id.numberOfCommentAll);
                break;
        }
        tmpRadioButton.setChecked(true);

        // set on click listener
        RadioGroup radioGroupNumberOfComment = dialogSelecting.findViewById(R.id.selectNumberOfComment);
        radioGroupNumberOfComment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int numberOfComment = 0;
                switch (checkedId) {
                    case R.id.numberOfComment5:
                        numberOfComment = 5;
                        break;
                    case R.id.numberOfComment10:
                        numberOfComment = 10;
                        break;
                    case R.id.numberOfComment20:
                        numberOfComment = 20;
                        break;
                    case R.id.numberOfComment50:
                        numberOfComment = 50;
                        break;
                    case R.id.numberOfCommentAll:
                        numberOfComment = 0;
                        break;
                }
                prefsEditor.putInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsJointlyGoalsShowComment, numberOfComment);
                prefsEditor.apply();

                // close dialog
                dialogSelectingNumberOfComment.dismiss();

                // update recycler view with new number of comment
                updateRecyclerView();
            }
        });

        // build the dialog
        builder.setView(dialogSelecting);

        // Add close button
        builder.setNegativeButton("Schliessen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogSelectingNumberOfComment.cancel();
            }
        });

        // add title
        builder.setTitle(R.string.select_dialog_our_goals_jointly_number_of_comment_title);

        // and create
        dialogSelectingNumberOfComment = builder.create();

        // set correct color of close button
        dialogSelectingNumberOfComment.setOnShowListener(new DialogInterface.OnShowListener() {
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
        dialogSelectingNumberOfComment.show();
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    // inits the fragment for use
    private void initFragmentShowCommentJointlyGoals() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentJointlyGoalsContext);

        // init the prefs
        prefs = fragmentShowCommentJointlyGoalsContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowCommentJointlyGoalsContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of goal
        currentDateOfJointlyGoals = prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis());

        // new recycler view
        recyclerViewShowComment = viewFragmentShowCommentJointlyGoals.findViewById(R.id.listOurGoalsShowCommentJointlyGoals);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentShowCommentJointlyGoalsContext);
        recyclerViewShowComment.setLayoutManager(linearLayoutManager);
        recyclerViewShowComment.setHasFixedSize(true);

        // Set correct subtitle in Activity -> "Kommentare Ziel ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsShowComment", "string", fragmentShowCommentJointlyGoalsContext.getPackageName())) + " " + jointlyGoalNumberInListView;
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyShowComment");
    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private final BroadcastReceiver ourGoalsFragmentShowCommentJointlyGoalsBrodcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Extras from intent that holds data
            Bundle intentExtras = null;

            // true-> update the list view with goals
            Boolean updateRecyclerView = false;

            // check for intent extras
            intentExtras = intent.getExtras();
            if (intentExtras != null) {
                // check intent order

                String tmpExtraOurGoals = intentExtras.getString("OurGoals","0");
                String tmpExtraOurGoalsNow = intentExtras.getString("OurGoalsJointlyNow","0");
                String tmpExtraOurGoalsJointlyComment = intentExtras.getString("OurGoalsJointlyComment","0");
                String tmpExtraOurGoalsSettings = intentExtras.getString("OurGoalsSettings","0");
                String tmpExtraOurGoalsCommentShareEnable = intentExtras.getString("OurGoalsSettingsCommentShareEnable","0");
                String tmpExtraOurGoalsCommentShareDisable = intentExtras.getString("OurGoalsSettingsCommentShareDisable","0");
                String tmpExtraOurGoalsResetCommentCountComment = intentExtras.getString("OurGoalsSettingsCommentCountComment","0");
                String tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView = intentExtras.getString("OurGoalsJointlyCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");
                // sort sequence of list view changed
                String tmpSortSequenceChange = intentExtras.getString("changeSortSequenceOfListViewJointlyComment", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsJointlyComment != null && tmpExtraOurGoalsJointlyComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageCommentJointlyGoalsNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsNow != null && tmpExtraOurGoalsNow.equals("1")) {
                    // update jointly goals! -> go back to fragment jointly goals and show dialog

                    // check goals and goals update and show dialog goals change
                    ((ActivityOurGoals) getActivity()).checkUpdateForShowDialog ("jointly");

                    // go back to fragment jointly goals -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurGoals.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsResetCommentCountComment != null && tmpExtraOurGoalsResetCommentCountComment.equals("1")) {
                    // reset jointly comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareDisable  != null && tmpExtraOurGoalsCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1") && tmpExtraOurGoalsCommentShareEnable  != null && tmpExtraOurGoalsCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentJointlyGoalsContext.getString(R.string.toastMessageJointlyGoalsCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateRecyclerView = true;
                }
                else if (tmpSortSequenceChange.equals("1")) {
                    if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending").equals("descending")) {
                        prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "ascending");
                    }
                    else {
                        prefsEditor.putString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending");
                    }
                    prefsEditor.apply();

                    // list view sort sequence have change -> refresh view
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoals != null && tmpExtraOurGoals.equals("1") && tmpExtraOurGoalsSettings != null && tmpExtraOurGoalsSettings.equals("1")) {

                    // goal settings change
                    updateRecyclerView = true;
                }
                else if (tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView != null &&  tmpExtraOurGoalsJointlyCommentSendInBackgroundRefreshView.equals("1")) {

                    // jointly comment send in background -> refresh view
                    updateRecyclerView = true;
                }

                // update the list view because data has change?
                if (updateRecyclerView) {
                    updateRecyclerView();
                }
            }
        }
    };


    // update the list view with now comments
    public void updateRecyclerView () {

        if (recyclerViewShowComment != null) {
            recyclerViewShowComment.destroyDrawingCache();
            recyclerViewShowComment.setVisibility(ListView.INVISIBLE);
            recyclerViewShowComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpJointlyGoalDbIdToComment = 0;

        // call getter-methode getJointlyGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale jointly goal
        tmpJointlyGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getJointlyGoalDbIdFromLink();

        if (tmpJointlyGoalDbIdToComment > 0) {
            jointlyGoalDbIdToShow = tmpJointlyGoalDbIdToComment;

            // call getter-methode getJointlyGoalsNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            jointlyGoalNumberInListView = ((ActivityOurGoals)getActivity()).getJointlyGoalNumberInListview();
            if (jointlyGoalNumberInListView < 1) jointlyGoalNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("jointlyGoals");
        }
    }


    // build the view for the comments
    public void displayActualCommentSet () {

        // get the data (all comments from a goal) from DB
        arrayListJointlyComments = myDb.getAllRowsOurGoalsJointlyGoalsCommentArrayList(jointlyGoalDbIdToShow, prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending"), prefs.getInt(ConstansClassOurGoals.namePrefsNumberOfCommentForOurGoalsJointlyGoalsShowComment, 50));

        // get the data for chose goal
        ArrayList<ObjectSmartEFBGoals> arrayListChooseGoal = myDb.getRowOurGoalsJointlyArrayList (jointlyGoalDbIdToShow);

        // set visibility of FAB for this fragment
        // show fab and comment goal only when on and possible!
        if ((prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false) && (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0)) > 0 ) || !commentLimitationBorder) {
            // set fab visibility
            ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("show", "jointlyShowComment");
            // set fab click listener
            ((ActivityOurGoals) getActivity()).setOurGoalFABClickListener(arrayListChooseGoal, "jointlyShowComment", "comment_an_jointly_goal");
        }
        else {
            ((ActivityOurGoals) getActivity()).setOurGoalFABVisibility("hide", "jointlyShowComment");
        }

        if (arrayListJointlyComments.size() > 0 && arrayListChooseGoal.size() > 0 && recyclerViewShowComment != null) {

            showJointlyGoalCommentRecyclerViewAdapter = new OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter(
                    getActivity(),
                    arrayListJointlyComments,
                    jointlyGoalDbIdToShow,
                    jointlyGoalNumberInListView,
                    commentLimitationBorder,
                    arrayListChooseGoal);

            // Assign adapter to Recycler View
            recyclerViewShowComment.setAdapter(showJointlyGoalCommentRecyclerViewAdapter);

        }
    }

}

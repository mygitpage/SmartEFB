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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by ich on 22.07.16.
 */
public class OurArrangementFragmentShowComment extends Fragment {

    // fragment view
    View viewFragmentShowComment;

    // fragment context
    Context fragmentShowCommentContext = null;

    // the linear layout manager
    LinearLayoutManager linearLayoutManager;

    // the recycler view
    RecyclerView recyclerViewShowComment = null;

    // data array of comments for recycler view
    ArrayList<ObjectSmartEFBComment> arrayListComments;

    // the fab
    FloatingActionButton fabFragmentShowComment;

    // hide/ show fab on scroll?
    Boolean switchHideShowOfFab = true;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // reference cursorAdapter for the recyler view
    OurArrangementShowCommentRecylerViewAdapter showCommentRecylerViewAdapter;

    // Server DB-Id of arrangement to comment
    int arrangementServerDbIdToShow = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // reference to dialog selecting number of comment
    AlertDialog dialogSelectingNumberOfComment;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_comment, null);

        // fragment has option menu
        setHasOptionsMenu(true);

        // register broadcast receiver and intent filter for action ACTIVITY_STATUS_UPDATE
        IntentFilter filter = new IntentFilter("ACTIVITY_STATUS_UPDATE");
        getActivity().getApplicationContext().registerReceiver(ourArrangementFragmentShowCommentBrodcastReceiver, filter);

        return viewFragmentShowComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowCommentContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init and display data from fragment show comment only when an arrangement is choosen
        if (arrangementServerDbIdToShow != 0) {

            // init the fragment now
            initFragmentShowComment();

            // show actual comment set for arrangement
            displayActualCommentSet();
        }

        // first ask to server for new data, when case is not closed!
        if (!prefs.getBoolean(ConstansClassSettings.namePrefsCaseClose, false)) {

            // send intent to service to start the service
            Intent startServiceIntent = new Intent(fragmentShowCommentContext, ExchangeJobIntentServiceEfb.class);
            // set command = "ask new data" on server
            startServiceIntent.putExtra("com", "ask_new_data");
            startServiceIntent.putExtra("dbid",0L);
            startServiceIntent.putExtra("receiverBroadcast","");
            // start service
            ExchangeJobIntentServiceEfb.enqueueWork(fragmentShowCommentContext, startServiceIntent);
        }
    }


    // fragment is destroyed
    public void onDestroyView() {
        super.onDestroyView();

        // de-register broadcast receiver
        getActivity().getApplicationContext().unregisterReceiver(ourArrangementFragmentShowCommentBrodcastReceiver);

        // close db connection
        myDb.close();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.menu_efb_our_arrangement_fragment_now_show_comment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        MenuItem registerItemDesc = menu.findItem(R.id.our_arrangement_menu_fragment_now_show_comment_sort_desc);
        MenuItem registerItemAsc = menu.findItem(R.id.our_arrangement_menu_fragment_now_show_comment_sort_asc);

        if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
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

            case R.id.our_arrangement_menu_fragment_now_show_comment_sort_desc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending");
                prefsEditor.apply();
                updateListView();
                return true;
            case R.id.our_arrangement_menu_fragment_now_show_comment_sort_asc:
                prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "ascending");
                prefsEditor.apply();
                updateListView();
                return true;
            case R.id.our_arrangement_menu_fragment_now_count_comment_in_recycler_view:
                showDialogForSelectingNumberOfCommentInList();
                updateListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showDialogForSelectingNumberOfCommentInList () {

        LayoutInflater dialogInflater;

        // get alert dialog builder with custom style
        AlertDialog.Builder builder = new AlertDialog.Builder(((ActivityOurArrangement)getActivity()), R.style.selectDialogStyle);

        // Get the layout inflater
        dialogInflater = (LayoutInflater) ((ActivityOurArrangement)getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate and get the view
        View dialogSelecting = dialogInflater.inflate(R.layout.select_dialog_our_arrangement_now_number_of_comment, null);

        //set radio button with correct value
        RadioButton tmpRadioButton;
        switch (prefs.getInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementNowShowComment, 50)) {
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
                prefsEditor.putInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementNowShowComment, numberOfComment);
                prefsEditor.apply();

                // close dialog
                dialogSelectingNumberOfComment.dismiss();

                // update recycler view with new number of comment
                updateListView ();
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
        builder.setTitle(R.string.select_dialog_our_arrangement_now_number_of_comment_title);

        // and create
        dialogSelectingNumberOfComment = builder.create();

        // set correct color of close button
        dialogSelectingNumberOfComment.setOnShowListener(new DialogInterface.OnShowListener() {
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
        dialogSelectingNumberOfComment.show();
    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentContext);

        // init the prefs
        prefs = fragmentShowCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Kommentare Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowCommentText", "string", fragmentShowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showComment");

        // new recyler view
        recyclerViewShowComment = viewFragmentShowComment.findViewById(R.id.listOurArrangementShowComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentShowCommentContext);
        recyclerViewShowComment.setLayoutManager(linearLayoutManager);
        recyclerViewShowComment.setHasFixedSize(true);

        // show fab and set on click listener
        if (fabFragmentShowComment != null) {

            fabFragmentShowComment.show();

            // add on click listener to fab
            fabFragmentShowComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(fragmentShowCommentContext, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("com", "comment_an_arrangement");
                    intent.putExtra("db_id", arrangementServerDbIdToShow);
                    intent.putExtra("arr_num", arrangementNumberInListView);
                    fragmentShowCommentContext.startActivity(intent);
                }
            });
        }

    }


    // Broadcast receiver for action ACTIVITY_STATUS_UPDATE -> comes from ExchangeJobIntentServiceEfb
    private BroadcastReceiver ourArrangementFragmentShowCommentBrodcastReceiver = new BroadcastReceiver() {

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
                String tmpExtraOurArrangementCommentSendInBackgroundRefreshView = intentExtras.getString("OurArrangementCommentSendInBackgroundRefreshView","0");
                // case is close
                String tmpSettings = intentExtras.getString("Settings", "0");
                String tmpCaseClose = intentExtras.getString("Case_close", "0");
                // sort sequence of list view changed
                String tmpSortSequenceChange = intentExtras.getString("changeSortSequenceOfListView", "0");

                if (tmpSettings != null && tmpSettings.equals("1") && tmpCaseClose != null && tmpCaseClose.equals("1")) {
                    // case close! -> show toast
                    String textCaseClose = fragmentShowCommentContext.getString(R.string.toastCaseClose);
                    Toast toast = Toast.makeText(context, textCaseClose, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if (v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                } else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNowComment != null && tmpExtraOurArrangementNowComment.equals("1")) {
                    // update now comment view -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentContext.getString(R.string.toastMessageCommentNowNewComments);
                    Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG).show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementNow != null && tmpExtraOurArrangementNow.equals("1")) {

                    // check arrangement and now arrangement update and show dialog arrangement and now arrangement change
                    ((ActivityOurArrangement) getActivity()).checkUpdateForShowDialog ("now");

                    // go back to fragment now arrangement -> this is my mother!
                    Intent backIntent = new Intent(getActivity(), ActivityOurArrangement.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    backIntent.putExtra("com","show_arrangement_now");
                    getActivity().startActivity(backIntent);
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementResetCommentCountComment != null && tmpExtraOurArrangementResetCommentCountComment.equals("1")) {
                    // reset now comment counter -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentContext.getString(R.string.toastMessageArrangementResetCommentCountComment);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareDisable  != null && tmpExtraOurArrangementCommentShareDisable .equals("1")) {
                    // sharing is disable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentContext.getString(R.string.toastMessageArrangementCommentShareDisable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1") && tmpExtraOurArrangementCommentShareEnable  != null && tmpExtraOurArrangementCommentShareEnable .equals("1")) {
                    // sharing is enable -> show toast and update view
                    String updateMessageCommentNow = fragmentShowCommentContext.getString(R.string.toastMessageArrangementCommentShareEnable);
                    Toast toast = Toast.makeText(context, updateMessageCommentNow, Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.show();

                    // update the view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangement != null && tmpExtraOurArrangement.equals("1") && tmpExtraOurArrangementSettings != null && tmpExtraOurArrangementSettings.equals("1")) {
                    // arrangement settings have change -> refresh view
                    updateListView = true;
                }
                else if (tmpSortSequenceChange.equals("1")) {
                    if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                        prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "ascending");
                    }
                    else {
                        prefsEditor.putString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending");
                    }
                    prefsEditor.apply();

                    // list view sort sequence have change -> refresh view
                    updateListView = true;
                }
                else if (tmpExtraOurArrangementCommentSendInBackgroundRefreshView != null &&  tmpExtraOurArrangementCommentSendInBackgroundRefreshView.equals("1")) {

                    // comment send in background -> refresh view
                    updateListView = true;
                }

                // update the list view because data has change?
                if (updateListView) {
                    updateListView();
                }

            }
        }
    };


    // update the list view with now comments
    public void updateListView () {

        if (recyclerViewShowComment != null) {

            recyclerViewShowComment.destroyDrawingCache();
            recyclerViewShowComment.setVisibility(ListView.INVISIBLE);
            recyclerViewShowComment.setVisibility(ListView.VISIBLE);

            displayActualCommentSet ();
        }
    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment;

        // call getter-methode getFabViewOurArrangement() in ActivityOurArrangement to get view for fab
        fabFragmentShowComment = ((ActivityOurArrangement)getActivity()).getFabViewOurArrangement();

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            arrangementServerDbIdToShow = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // call getter-methode isCommentLimitationBorderSet in ActivityOurArrangement to get true-> comments are limited, false-> comments are not limited
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }
    }


    // build the view for the comments
    public void displayActualCommentSet () {

        // get the data (all comments from an arrangement) from DB
        arrayListComments = myDb.getAllRowsOurArrangementCommentArrayList(arrangementServerDbIdToShow, prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending"), prefs.getInt(ConstansClassOurArrangement.namePrefsNumberOfCommentForOurArrangementNowShowComment, 50));

        // get the data (the choosen arrangement) from the DB
        Cursor choosenArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToShow);

        if (arrayListComments.size() > 0 && choosenArrangement.getCount() > 0 && recyclerViewShowComment != null) {

            showCommentRecylerViewAdapter = new OurArrangementShowCommentRecylerViewAdapter(
                    getActivity(),
                    arrayListComments,
                    arrangementServerDbIdToShow,
                    arrangementNumberInListView,
                    commentLimitationBorder,
                    choosenArrangement);

            // Assign adapter to Recycler View
            recyclerViewShowComment.setAdapter(showCommentRecylerViewAdapter);





        }
    }

}
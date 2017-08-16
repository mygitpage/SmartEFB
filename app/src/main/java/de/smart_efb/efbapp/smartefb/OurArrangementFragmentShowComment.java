package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by ich on 22.07.16.
 */
public class OurArrangementFragmentShowComment extends Fragment {

    // fragment view
    View viewFragmentShowComment;

    // fragment context
    Context fragmentShowCommentContext = null;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the settings
    SharedPreferences prefs;

    // the current date of arrangement -> the other are old (look at tab old)
    long currentDateOfArrangement;

    // reference cursorAdapter for the listview
    OurArrangementShowCommentCursorAdapter showCommentCursorAdapter;

    // Server DB-Id of arrangement to comment
    int arrangementServerDbIdToShow = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_comment, null);

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

    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentContext);

        // init the prefs
        prefs = fragmentShowCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowCommentContext.MODE_PRIVATE);

        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Kommentare Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowCommentText", "string", fragmentShowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showComment");

    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

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




    public void displayActualCommentSet () {

        // get the data (all comments from an arrangement) from DB
        Cursor cursorComments = myDb.getAllRowsOurArrangementComment(arrangementServerDbIdToShow);

        // get the data (the choosen arrangement) from the DB
        Cursor choosenArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToShow);

        // find the listview
        ListView listView = (ListView) viewFragmentShowComment.findViewById(R.id.listOurArrangementShowComment);

        // new dataadapter with custom constructor for show comments now
        showCommentCursorAdapter = new OurArrangementShowCommentCursorAdapter(
                getActivity(),
                cursorComments,
                0,
                arrangementServerDbIdToShow,
                arrangementNumberInListView,
                commentLimitationBorder,
                choosenArrangement);

        // Assign adapter to ListView
        listView.setAdapter(showCommentCursorAdapter);

    }

}










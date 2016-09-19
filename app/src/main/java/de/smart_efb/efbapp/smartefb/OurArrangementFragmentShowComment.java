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

    // DB-Id of arrangement to comment
    int arrangementDbIdToShow = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_comment, null);

        return viewFragmentShowComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowCommentContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentShowComment();

        // show actual comment set for arrangement
        displayActualCommentSet();

    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowCommentContext);

        // init the prefs
        prefs = fragmentShowCommentContext.getSharedPreferences("smartEfbSettings", fragmentShowCommentContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        arrangementDbIdToShow = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();
        if (arrangementDbIdToShow < 0) arrangementDbIdToShow = 0; // check borders
        // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
        arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
        if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

        // Set correct subtitle in Activity -> "Kommentare Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowCommentText", "string", fragmentShowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showComment");

    }


    public void displayActualCommentSet () {

        // get the data (all comments from an arrangement) from DB
        Cursor cursor = myDb.getAllRowsOurArrangementComment(arrangementDbIdToShow);

        // get the data (the choosen arrangement) from the DB
        String arrangement = "";
        Cursor choosenArrangement = myDb.getRowOurArrangement(arrangementDbIdToShow);
        arrangement = choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));

        // find the listview
        ListView listView = (ListView) viewFragmentShowComment.findViewById(R.id.listOurArrangementShowComment);

        // new dataadapter with custom constructor
        showCommentCursorAdapter = new OurArrangementShowCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                arrangementDbIdToShow,
                arrangementNumberInListView,
                arrangement);

        // Assign adapter to ListView
        listView.setAdapter(showCommentCursorAdapter);

    }

}










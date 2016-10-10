package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    // DB-Id of arrangement to comment
    int arrangementDbIdToShow = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_sketch_comment, null);

        return viewFragmentShowSketchComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentShowSketchCommentContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentShowComment();

        // show actual comment set for arrangement
        displayActualCommentSet();

    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowSketchCommentContext);

        // init the prefs
        prefs = fragmentShowSketchCommentContext.getSharedPreferences("smartEfbSettings", fragmentShowSketchCommentContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong("currentDateOfArrangement", System.currentTimeMillis());

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        arrangementDbIdToShow = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();
        if (arrangementDbIdToShow < 0) arrangementDbIdToShow = 0; // check borders
        // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
        arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
        if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

        // Set correct subtitle in Activity -> "Einschaetzungen Entwuerfe ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowSketchCommentText", "string", fragmentShowSketchCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showSketchComment");

    }


    public void displayActualCommentSet () {

        // get the data (all comments from an sketch arrangement) from DB
        Cursor cursor = myDb.getAllRowsOurArrangementSketchComment(arrangementDbIdToShow);

        // get the data (the choosen arrangement) from the DB
        String arrangement = "";
        Cursor choosenArrangement = myDb.getRowSketchOurArrangement(arrangementDbIdToShow);
        arrangement = choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));

        // find the listview
        ListView listView = (ListView) viewFragmentShowSketchComment.findViewById(R.id.listOurArrangementShowSketchComment);

        // new dataadapter with custom constructor
        showSketchCommentCursorAdapter = new OurArrangementShowSketchCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                arrangementDbIdToShow,
                arrangementNumberInListView,
                arrangement);

        // Assign adapter to ListView
        listView.setAdapter(showSketchCommentCursorAdapter);

    }

}
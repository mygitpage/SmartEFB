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

    // Server DB-Id of arrangement to comment
    int sketchArrangementServerDbIdToShow = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentShowSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_show_sketch_comment, null);

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

    }


    // inits the fragment for use
    private void initFragmentShowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentShowSketchCommentContext);

        // init the prefs
        prefs = fragmentShowSketchCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentShowSketchCommentContext.MODE_PRIVATE);
        //get current date of arrangement
        currentDateOfArrangement = prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis());

        // Set correct subtitle in Activity -> "Einschaetzungen Entwuerfe ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentShowSketchCommentText", "string", fragmentShowSketchCommentContext.getPackageName())) + " " + sketchArrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "showSketchComment");

    }


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            sketchArrangementServerDbIdToShow = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
            if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders

        }

    }


    public void displayActualCommentSet () {

        // get the data (all comments from an sketch arrangement) from DB
        Cursor cursor = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementServerDbIdToShow);

        // get the data (the choosen sketch arrangement) from the DB
        String arrangement = "";
        Cursor choosenArrangement = myDb.getRowSketchOurArrangement(sketchArrangementServerDbIdToShow);
        arrangement = choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));

        // find the listview
        ListView listView = (ListView) viewFragmentShowSketchComment.findViewById(R.id.listOurArrangementShowSketchComment);

        // new dataadapter with custom constructor
        showSketchCommentCursorAdapter = new OurArrangementShowSketchCommentCursorAdapter(
                getActivity(),
                cursor,
                0,
                sketchArrangementServerDbIdToShow,
                sketchArrangementNumberInListView,
                arrangement);

        // Assign adapter to ListView
        listView.setAdapter(showSketchCommentCursorAdapter);

    }

}
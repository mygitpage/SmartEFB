package de.smart_efb.efbapp.smartefb;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 01.07.16.
 */
public class OurArrangementFragmentNowComment extends Fragment {

    // fragment view
    View viewFragmentNowComment;

    // fragment context
    Context fragmentNowCommentContext = null;



    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // DB-Id of arrangement to comment
    int arrangementIdToComment = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;

    // cursor for all comments to the choosen arrangement
    Cursor cursorArrangementAllComments;




    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        viewFragmentNowComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_now_comment, null);

        return viewFragmentNowComment;

    }




    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentNowCommentContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentNowComment();

    }



    // inits the fragment for use
    private void initFragmentNowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentNowCommentContext);

        // init the prefs
        prefs = fragmentNowCommentContext.getSharedPreferences("smartEfbSettings", fragmentNowCommentContext.MODE_PRIVATE);

        arrangementIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementIdFromLink();
        if (arrangementIdToComment < 0) arrangementIdToComment = 0; // check borders



        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementIdToComment);

        // get all comments for choosen arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementIdToComment);


        // build the view
        //textview for the comment intro
        TextView textCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.arrangementCommentIntro));

        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentNowComment.findViewById(R.id.choosenArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);


        // textview intro for the history of comments
        TextView textCommentHistoryIntro = (TextView) viewFragmentNowComment.findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementAllComments.getColumnCount() > 0) {
            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText));
            Toast.makeText(fragmentNowCommentContext, "Anzahl Kommentare: " + cursorArrangementAllComments.getColumnCount(), Toast.LENGTH_SHORT).show();
        } else {
            textCommentHistoryIntro.setVisibility(View.INVISIBLE);
        }


        // End build the view





    }



}

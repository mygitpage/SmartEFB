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
import android.widget.LinearLayout;
import android.widget.ListView;
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
    int arrangementDbIdToComment = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

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

        arrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();
        if (arrangementDbIdToComment < 0) arrangementDbIdToComment = 0; // check borders

        arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
        if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementDbIdToComment);

        // get all comments for choosen arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementDbIdToComment);


        // build the view
        //textview for the comment intro
        TextView textCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.arrangementCommentIntro) + " " + arrangementNumberInListView + ":");

        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentNowComment.findViewById(R.id.choosenArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);


        // textview intro for the history of comments
        TextView textCommentHistoryIntro = (TextView) viewFragmentNowComment.findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementAllComments.getCount() > 0) {

            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText)+ " " + arrangementNumberInListView + ":");
            Toast.makeText(fragmentNowCommentContext, cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT)), Toast.LENGTH_SHORT).show();

        } else {

            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentNowComment.findViewById(R.id.commentHistoryContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);

        }


        // comment textfield -> insert new comment
        final EditText txtInputArrangementComment = (EditText) viewFragmentNowComment.findViewById(R.id.inputArrangementComment);
        // button send comment
        Button buttonSendArrangementComment = (Button) viewFragmentNowComment.findViewById(R.id.buttonSendArrangementComment);

        // onClick listener send arrangement comment
        buttonSendArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (txtInputArrangementComment.getText().toString().length() > 1) {

                    // insert comment in DB
                    long newID = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString("userName", "John Doe"), System.currentTimeMillis() , arrangementDbIdToComment);

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentNowCommentContext, fragmentNowCommentContext.getResources().getString(R.string.commentSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // Change fragment to Show Arrangement Now
                    ((ActivityOurArrangement) getActivity()).executeIntentCommand("show_arrangement_now");

                } else {
                    // Toast "Comment to short"
                    Toast.makeText(fragmentNowCommentContext, fragmentNowCommentContext.getResources().getString(R.string.commentToShort), Toast.LENGTH_SHORT).show();
                }

            }
        });


        // button abbort
        Button buttonAbbortArrangementComment = (Button) viewFragmentNowComment.findViewById(R.id.buttonAbortComment);
        // onClick listener button abbort
        buttonAbbortArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Abbort and change fragment to Show Arrangement Now
                ((ActivityOurArrangement)getActivity()).executeIntentCommand("show_arrangement_now");

            }
        });

        // End build the view


    }


    public void displayActualCommentSet () {


        // find the listview for comment history
        ListView commentHistorylistView = (ListView) viewFragmentNowComment.findViewById(R.id.listCommentHistory);


        // new dataadapter
        OurArrangementCommentHistoryCursorAdapter commentHistoryDataAdapter = new OurArrangementCommentHistoryCursorAdapter(
                getActivity(),
                cursorArrangementAllComments,
                0);

        // Assign adapter to ListView
        commentHistorylistView.setAdapter(commentHistoryDataAdapter);


    }






}

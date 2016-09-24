package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 22.09.16.
 */
public class OurArrangementFragmentSketchComment extends Fragment {

    // fragment view
    View viewFragmentSketchComment;

    // fragment context
    Context fragmentSketchCommentContext = null;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of arrangement to comment
    int arrangementDbIdToComment = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;

    // cursor for the choosen sketch arrangement
    Cursor cursorChoosenSketchArrangement;

    // cursor for all comments to the choosen arrangement
    Cursor cursorArrangementAllComments;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentSketchComment = layoutInflater.inflate(R.layout.fragment_our_arrangement_sketch_comment, null);

        return viewFragmentSketchComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentSketchCommentContext = getActivity().getApplicationContext();

        // init the fragment now
        initFragmentSketchComment();

    }


    // inits the fragment for use
    private void initFragmentSketchComment() {

        // init the DB
        myDb = new DBAdapter(fragmentSketchCommentContext);

        // init the prefs
        prefs = fragmentSketchCommentContext.getSharedPreferences("smartEfbSettings", fragmentSketchCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();


        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        arrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();
        if (arrangementDbIdToComment < 0) arrangementDbIdToComment = 0; // check borders
        // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
        sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
        if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders



        // get choosen arrangement
        cursorChoosenSketchArrangement = myDb.getRowSketchOurArrangement(arrangementDbIdToComment);

        // get all comments for choosen arrangement
        //cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementDbIdToComment);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."


        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("subtitleFragmentSketchCommentText", "string", fragmentSketchCommentContext.getPackageName())), sketchArrangementNumberInListView);




        //String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentSketchCommentText", "string", fragmentSketchCommentContext.getPackageName())) + " " + sketchArrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketchComment");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.sketchArrangementCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showSketchArrangementIntroText) + " " + sketchArrangementNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);


        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.ilink_comment")
                .authority("www.smart-efb.de")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_sketch_arrangement");
        TextView linkShowCommentBackLink = (TextView) viewFragmentSketchComment.findViewById(R.id.arrangementShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+fragmentSketchCommentContext.getResources().getString(fragmentSketchCommentContext.getResources().getIdentifier("ourArrangementBackLinkToSketchArrangement", "string", fragmentSketchCommentContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the sketch arrangement
        TextView textViewArrangement = (TextView) viewFragmentSketchComment.findViewById(R.id.choosenSketchArrangement);
        String arrangement = cursorChoosenSketchArrangement.getString(cursorChoosenSketchArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        //textview for the comment intro
        TextView textCommentIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.sketchArrangementCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.sketchArrangementCommentIntro) + " " + sketchArrangementNumberInListView);


        // textview intro for the history of comments
        /*
        TextView textCommentHistoryIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementAllComments.getCount() > 0) { // show comments for arrangement when count comments > 0
            // show intro for comments
            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText)+ " " + arrangementNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentSketchComment.findViewById(R.id.commentHistoryContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);
        }
        */
        // comment textfield -> insert new comment
        final EditText txtInputSketchArrangementComment = (EditText) viewFragmentSketchComment.findViewById(R.id.inputSketchArrangementComment);
        // button send comment
        Button buttonSendSketchArrangementComment = (Button) viewFragmentSketchComment.findViewById(R.id.buttonSendSketchArrangementComment);

        // onClick listener send arrangement comment
        buttonSendSketchArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (txtInputSketchArrangementComment.getText().toString().length() > 1) {

                    // insert comment in DB
                    //long newID = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString("userName", "John Doe"), System.currentTimeMillis() , arrangementDbIdToComment, true, prefs.getLong("currentDateOfArrangement", System.currentTimeMillis()));

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentSketchCommentContext, fragmentSketchCommentContext.getResources().getString(R.string.commentSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // increment comment count
                    /*
                    int countCommentSum = prefs.getInt("commentOurArrangementCountComment",0) + 1;
                    prefsEditor.putInt("commentOurArrangementCountComment", countCommentSum);
                    prefsEditor.commit();
                    */
                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_sketch_arrangement");
                    getActivity().startActivity(intent);

                } else {
                    // Toast "Comment to short"
                    Toast.makeText(fragmentSketchCommentContext, fragmentSketchCommentContext.getResources().getString(R.string.commentToShort), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // button abbort
        Button buttonAbbortArrangementComment = (Button) viewFragmentSketchComment.findViewById(R.id.buttonAbortSketchComment);
        // onClick listener button abbort
        buttonAbbortArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_sketch_arrangement");
                getActivity().startActivity(intent);

            }
        });

        // End build the view

    }


    public void addActualCommentSetToView () {

        LinearLayout commentHolderLayout = (LinearLayout) viewFragmentSketchComment.findViewById(R.id.commentSketchHolder);

        cursorArrangementAllComments.moveToFirst();

        do {

            int actualCursorNumber = cursorArrangementAllComments.getPosition()+1;

            // Linear Layout holds comment text and linear layout with author,date and new entry text
            LinearLayout l_inner_layout = new LinearLayout(fragmentSketchCommentContext);
            l_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l_inner_layout.setOrientation(LinearLayout.VERTICAL);

            //add textView for comment text
            TextView txtViewCommentText = new TextView (fragmentSketchCommentContext);
            txtViewCommentText.setText(cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT)));
            txtViewCommentText.setId(actualCursorNumber);
            txtViewCommentText.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color));
            txtViewCommentText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentText.setTextSize(16);
            txtViewCommentText.setGravity(Gravity.LEFT);
            txtViewCommentText.setPadding(15,0,0,0);

            // Linear Layout holds author, date and text new entry
            LinearLayout aadn_inner_layout = new LinearLayout(fragmentSketchCommentContext);
            aadn_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            aadn_inner_layout.setOrientation(LinearLayout.HORIZONTAL);

            // check if comment new entry
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                //add textView for text new entry
                TextView txtViewCommentNewEntry = new TextView (fragmentSketchCommentContext);
                txtViewCommentNewEntry.setText(this.getResources().getString(R.string.newEntryText));
                txtViewCommentNewEntry.setId(actualCursorNumber);
                txtViewCommentNewEntry.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_accent_color));
                txtViewCommentNewEntry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                txtViewCommentNewEntry.setTextSize(14);
                txtViewCommentNewEntry.setGravity(Gravity.LEFT);
                txtViewCommentNewEntry.setPadding(15,0,0,0);

                // add new entry text to linear layout
                aadn_inner_layout.addView (txtViewCommentNewEntry);

                // delet status new entry in db
                myDb.deleteStatusNewEntryOurArrangementComment(cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            //add textView for comment author and date
            TextView txtViewCommentAuthorAndDate = new TextView (fragmentSketchCommentContext);
            long writeTime = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
            String authorAndDate = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
            txtViewCommentAuthorAndDate.setText(authorAndDate);
            txtViewCommentAuthorAndDate.setId(actualCursorNumber);
            txtViewCommentAuthorAndDate.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color));
            txtViewCommentAuthorAndDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentAuthorAndDate.setTextSize(14);
            txtViewCommentAuthorAndDate.setGravity(Gravity.RIGHT);
            txtViewCommentAuthorAndDate.setPadding(0,0,0,55);

            aadn_inner_layout.addView (txtViewCommentAuthorAndDate);

            // add elements to inner linear layout
            l_inner_layout.addView (txtViewCommentText);
            l_inner_layout.addView (aadn_inner_layout);

            // add inner layout to comment holder (linear layout in xml-file)
            commentHolderLayout.addView(l_inner_layout);

        } while (cursorArrangementAllComments.moveToNext());



        // Linear Layout holds author, date and text new entry
        LinearLayout btnBack_inner_layout = new LinearLayout(fragmentSketchCommentContext);
        btnBack_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBack_inner_layout.setOrientation(LinearLayout.HORIZONTAL);
        btnBack_inner_layout.setGravity(Gravity.CENTER);


        // create back button (to arrangement)
        Button btnBackToArrangement = new Button (fragmentSketchCommentContext);
        btnBackToArrangement.setText(this.getResources().getString(R.string.btnAbortShowComment));
        btnBackToArrangement.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color_white));
        btnBackToArrangement.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToArrangement.setTextSize(14);
        btnBackToArrangement.setGravity(Gravity.CENTER);
        btnBackToArrangement.setBackgroundColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.bg_btn_join));
        btnBackToArrangement.setPadding(10,10,10,10);
        btnBackToArrangement.setTop(25);
        btnBackToArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_sketch_arrangement");
                getActivity().startActivity(intent);

            }
        });

        // add elements to inner linear layout
        btnBack_inner_layout.addView (btnBackToArrangement);

        // add back button to comment holder (linear layout in xml-file)
        commentHolderLayout.addView(btnBack_inner_layout);

    }

}
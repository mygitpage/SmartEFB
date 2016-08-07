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
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
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

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

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

        layoutInflaterForFragment = layoutInflater;

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

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        arrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();
        if (arrangementDbIdToComment < 0) arrangementDbIdToComment = 0; // check borders
        // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
        arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
        if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementDbIdToComment);

        // get all comments for choosen arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementDbIdToComment);

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showArrangementIntroText) + " " + arrangementNumberInListView);


        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.ilink_comment")
                .authority("www.smart-efb.de")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_arrangement_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementBackLinkToArrangement", "string", fragmentNowCommentContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentNowComment.findViewById(R.id.choosenArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);

        //textview for the comment intro
        TextView textCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.arrangementCommentIntro) + " " + arrangementNumberInListView);


        // textview intro for the history of comments
        TextView textCommentHistoryIntro = (TextView) viewFragmentNowComment.findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementAllComments.getCount() > 0) { // show comments for arrangement when count comments > 0
            // show intro for comments
            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText)+ " " + arrangementNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
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
                    long newID = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString("userName", "John Doe"), System.currentTimeMillis() , arrangementDbIdToComment, true);

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentNowCommentContext, fragmentNowCommentContext.getResources().getString(R.string.commentSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_arrangement_now");
                    getActivity().startActivity(intent);

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

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_arrangement_now");
                getActivity().startActivity(intent);

            }
        });

        // End build the view

    }


    public void addActualCommentSetToView () {

        LinearLayout commentHolderLayout = (LinearLayout) viewFragmentNowComment.findViewById(R.id.commentHolder);

        cursorArrangementAllComments.moveToFirst();

        do {

            int actualCursorNumber = cursorArrangementAllComments.getPosition()+1;

            // Linear Layout holds comment text and linear layout with author,date and new entry text
            LinearLayout l_inner_layout = new LinearLayout(fragmentNowCommentContext);
            l_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l_inner_layout.setOrientation(LinearLayout.VERTICAL);

            //add textView for comment text
            TextView txtViewCommentText = new TextView (fragmentNowCommentContext);
            txtViewCommentText.setText(cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT)));
            txtViewCommentText.setId(actualCursorNumber);
            txtViewCommentText.setTextColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.text_color));
            txtViewCommentText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentText.setTextSize(16);
            txtViewCommentText.setGravity(Gravity.LEFT);
            txtViewCommentText.setPadding(15,0,0,0);

            // Linear Layout holds author, date and text new entry
            LinearLayout aadn_inner_layout = new LinearLayout(fragmentNowCommentContext);
            aadn_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            aadn_inner_layout.setOrientation(LinearLayout.HORIZONTAL);

            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                //add textView for text new entry
                TextView txtViewCommentNewEntry = new TextView (fragmentNowCommentContext);
                txtViewCommentNewEntry.setText(this.getResources().getString(R.string.newEntryText));
                txtViewCommentNewEntry.setId(actualCursorNumber);
                txtViewCommentNewEntry.setTextColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.text_accent_color));
                txtViewCommentNewEntry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                txtViewCommentNewEntry.setTextSize(14);
                txtViewCommentNewEntry.setGravity(Gravity.LEFT);
                txtViewCommentNewEntry.setPadding(15,0,0,0);

                // add new entry text to linear layout
                aadn_inner_layout.addView (txtViewCommentNewEntry);
            }

            //add textView for comment author and date
            TextView txtViewCommentAuthorAndDate = new TextView (fragmentNowCommentContext);
            long writeTime = cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
            String authorAndDate = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
            txtViewCommentAuthorAndDate.setText(authorAndDate);
            txtViewCommentAuthorAndDate.setId(actualCursorNumber);
            txtViewCommentAuthorAndDate.setTextColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.text_color));
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
        LinearLayout btnBack_inner_layout = new LinearLayout(fragmentNowCommentContext);
        btnBack_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBack_inner_layout.setOrientation(LinearLayout.HORIZONTAL);
        btnBack_inner_layout.setGravity(Gravity.CENTER);


        // create back button (to arrangement)
        Button btnBackToArrangement = new Button (fragmentNowCommentContext);
        btnBackToArrangement.setText(this.getResources().getString(R.string.btnAbortShowComment));
        btnBackToArrangement.setTextColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.text_color_white));
        btnBackToArrangement.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToArrangement.setTextSize(14);
        btnBackToArrangement.setGravity(Gravity.CENTER);
        btnBackToArrangement.setBackgroundColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.bg_btn_join));
        btnBackToArrangement.setPadding(10,10,10,10);
        btnBackToArrangement.setTop(25);
        btnBackToArrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_arrangement_now");
                getActivity().startActivity(intent);

            }
        });

        // add elements to inner linear layout
        btnBack_inner_layout.addView (btnBackToArrangement);

        // add back button to comment holder (linear layout in xml-file)
        commentHolderLayout.addView(btnBack_inner_layout);

    }

}

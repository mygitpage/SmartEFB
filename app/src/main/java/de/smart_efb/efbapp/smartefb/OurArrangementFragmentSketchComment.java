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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 22.09.16.
 */
public class OurArrangementFragmentSketchComment extends Fragment {

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateSketchCommentScalesLevel = new String [countScalesLevel];

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
    int sketchArrangementDbIdToComment = 0;

    // arrangement number in list view
    int sketchArrangementNumberInListView = 0;

    // cursor for the choosen sketch arrangement
    Cursor cursorChoosenSketchArrangement;

    // cursor for all comments to the choosen sketch arrangement
    Cursor cursorSketchArrangementAllComments;

    //number of radio buttons in struct question
    static final int numberOfRadioButtonsStructQuestion = 5;

    // result of struct question (1-5)
    int structQuestionResultSketchComment = 0;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


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

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment now only when an arrangement is choosen
        if (sketchArrangementDbIdToComment != 0) {

            // init the fragment now
            initFragmentSketchComment();
        }

    }


    // inits the fragment for use
    private void initFragmentSketchComment() {

        // init the DB
        myDb = new DBAdapter(fragmentSketchCommentContext);

        // init the prefs
        prefs = fragmentSketchCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentSketchCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init array for text description of scales levels
        evaluateSketchCommentScalesLevel = getResources().getStringArray(R.array.evaluateSketchCommentScalesLevel);

        // get choosen arrangement
        cursorChoosenSketchArrangement = myDb.getRowSketchOurArrangement(sketchArrangementDbIdToComment);

        // get all comments for choosen sketch arrangement
        cursorSketchArrangementAllComments = myDb.getAllRowsOurArrangementSketchComment(sketchArrangementDbIdToComment);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("subtitleFragmentSketchCommentText", "string", fragmentSketchCommentContext.getPackageName())), sketchArrangementNumberInListView);
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "sketchComment");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.sketchArrangementCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showSketchArrangementIntroText) + " " + sketchArrangementNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);


        // generate back link "zurueck zu allen Entwuerfen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourarrangement")
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

        // textview intro for the history of comments
        TextView textCommentSketchHistoryIntro = (TextView) viewFragmentSketchComment.findViewById(R.id.commentSketchHistoryIntro);
        if (cursorSketchArrangementAllComments.getCount() > 0) { // show comments for arrangement when count comments > 0
            // show intro for comments
            textCommentSketchHistoryIntro.setText(this.getResources().getString(R.string.commentSketchHistoryIntroText)+ " " + sketchArrangementNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentSketchComment.findViewById(R.id.commentSketchHistoryContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);
        }

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;


        for (int numberOfButtons=0; numberOfButtons < numberOfRadioButtonsStructQuestion; numberOfButtons++) {
            tmpRessourceName ="structQuestionOne_Answer" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentSketchCommentContext.getPackageName());

                tmpRadioButtonQuestion = (RadioButton) viewFragmentSketchComment.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new sketchCommentRadioButtonListener(numberOfButtons));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = (TextView) viewFragmentSketchComment.findViewById(R.id.infoSketchCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral;
        // build text element max sketch comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextSketchCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) > 1 && commentLimitationBorder){
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextSketchCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentUnlimitedText);
        }

        // build text element count sketch comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextSketchCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0));
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral);





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

        // button send comment
        Button buttonSendSketchArrangementComment = (Button) viewFragmentSketchComment.findViewById(R.id.buttonSendSketchArrangementComment);

        // onClick listener send arrangement comment
        buttonSendSketchArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean sketchCommentNoError = true;
                TextView tmpErrorTextView;

                // check result struct question
                tmpErrorTextView = (TextView) viewFragmentSketchComment.findViewById(R.id.errorStructQuestionForCommentSketchArrangement);
                if ( structQuestionResultSketchComment == 0 && tmpErrorTextView != null) {
                    sketchCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);

                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);

                }

                // comment textfield -> insert new comment
                tmpErrorTextView = (TextView) viewFragmentSketchComment.findViewById(R.id.errorFreeQuestionForCommentSketchArrangement);
                EditText txtInputSketchArrangementComment = (EditText) viewFragmentSketchComment.findViewById(R.id.inputSketchArrangementComment);
                if (txtInputSketchArrangementComment.getText().toString().length() < 3 && tmpErrorTextView != null) {
                    sketchCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }

                // check for errors?
                if (sketchCommentNoError) {

                    // insert comment in DB
                    long newId = myDb.insertRowOurArrangementSketchComment(txtInputSketchArrangementComment.getText().toString(), structQuestionResultSketchComment, 0, 0, prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "John Doe"), System.currentTimeMillis(), sketchArrangementDbIdToComment, true, prefs.getLong("currentDateOfSketchArrangement", System.currentTimeMillis()), 0);

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentSketchCommentContext, fragmentSketchCommentContext.getResources().getString(R.string.sketchCommentSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // increment sketch comment count
                    int countSketchCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) + 1;
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, countSketchCommentSum);
                    prefsEditor.commit();

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_sketch_arrangement");
                    getActivity().startActivity(intent);

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


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmpArrangementDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmpArrangementDbIdToComment = ((ActivityOurArrangement)getActivity()).getSketchArrangementDbIdFromLink();

        if (tmpArrangementDbIdToComment > 0) {
            sketchArrangementDbIdToComment = tmpArrangementDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            sketchArrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getSketchArrangementNumberInListview();
            if (sketchArrangementNumberInListView < 1) sketchArrangementNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }

    }


    public void addActualCommentSetToView () {

        LinearLayout commentHolderLayout = (LinearLayout) viewFragmentSketchComment.findViewById(R.id.commentSketchHolder);

        cursorSketchArrangementAllComments.moveToFirst();

        do {

            int actualCursorNumber = cursorSketchArrangementAllComments.getPosition()+1;

            // Linear Layout holds comment text and linear layout with author,date and new entry text
            LinearLayout l_inner_layout = new LinearLayout(fragmentSketchCommentContext);
            l_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l_inner_layout.setOrientation(LinearLayout.VERTICAL);

            //add textView for actual result struct question
            TextView txtViewSketchCommentResultStructQuestion = new TextView (fragmentSketchCommentContext);
            String actualResultStructQuestion = getResources().getString(R.string.textSketchCommentActualResultStructQuestion);
            actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1))-1]);
            txtViewSketchCommentResultStructQuestion.setText(actualResultStructQuestion);
            txtViewSketchCommentResultStructQuestion.setId(actualCursorNumber);
            txtViewSketchCommentResultStructQuestion.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color));
            txtViewSketchCommentResultStructQuestion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewSketchCommentResultStructQuestion.setTextSize(12);
            txtViewSketchCommentResultStructQuestion.setGravity(Gravity.LEFT);
            txtViewSketchCommentResultStructQuestion.setPadding(15,0,0,0);

            //add textView for comment text
            TextView txtViewCommentText = new TextView (fragmentSketchCommentContext);
            txtViewCommentText.setText(cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT)));
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
            if (cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
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

                // delete status new entry in db
                myDb.deleteStatusNewEntryOurArrangementSketchComment(cursorSketchArrangementAllComments.getInt(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            //add textView for comment author and date
            TextView txtViewCommentAuthorAndDate = new TextView (fragmentSketchCommentContext);
            long writeTime = cursorSketchArrangementAllComments.getLong(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME));
            String authorAndDate = cursorSketchArrangementAllComments.getString(cursorSketchArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
            txtViewCommentAuthorAndDate.setText(authorAndDate);
            txtViewCommentAuthorAndDate.setId(actualCursorNumber);
            txtViewCommentAuthorAndDate.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color));
            txtViewCommentAuthorAndDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentAuthorAndDate.setTextSize(14);
            txtViewCommentAuthorAndDate.setGravity(Gravity.RIGHT);
            txtViewCommentAuthorAndDate.setPadding(0,0,0,55);

            aadn_inner_layout.addView (txtViewCommentAuthorAndDate);

            // add elements to inner linear layout
            l_inner_layout.addView (txtViewSketchCommentResultStructQuestion);
            l_inner_layout.addView (txtViewCommentText);
            l_inner_layout.addView (aadn_inner_layout);

            // add inner layout to comment holder (linear layout in xml-file)
            commentHolderLayout.addView(l_inner_layout);

        } while (cursorSketchArrangementAllComments.moveToNext());

        // Linear Layout holds author, date and text new entry
        LinearLayout btnBack_inner_layout = new LinearLayout(fragmentSketchCommentContext);
        btnBack_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBack_inner_layout.setOrientation(LinearLayout.HORIZONTAL);
        btnBack_inner_layout.setGravity(Gravity.CENTER);

        // create back button (to arrangement)
        Button btnBackToArrangement = new Button (fragmentSketchCommentContext);
        btnBackToArrangement.setText(this.getResources().getString(R.string.ourArrangementBackLinkToSketchArrangement));
        btnBackToArrangement.setTextColor(ContextCompat.getColor(fragmentSketchCommentContext, R.color.text_color_white));
        btnBackToArrangement.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToArrangement.setTextSize(14);
        btnBackToArrangement.setGravity(Gravity.CENTER);
        btnBackToArrangement.setBackground(ContextCompat.getDrawable(fragmentSketchCommentContext,R.drawable.app_button_style));
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


    //
    // onClickListener for radioButtons in fragment layout evaluate
    //
    public class sketchCommentRadioButtonListener implements View.OnClickListener {

        int radioButtonNumber;

        public sketchCommentRadioButtonListener (int number) {

            this.radioButtonNumber = number;

        }

        @Override
        public void onClick(View v) {

            int tmpResultQuestion;

            // check button number and get result
            switch (radioButtonNumber) {

                case 0: // ever
                    tmpResultQuestion = 1;
                    break;
                case 1:
                    tmpResultQuestion = 2;
                    break;
                case 2:
                    tmpResultQuestion = 3;
                    break;
                case 3:
                    tmpResultQuestion = 4;
                    break;
                case 4: // radioButton never
                    tmpResultQuestion = 5;
                    break;
                default:
                    tmpResultQuestion = 0;
                    break;
            }

            structQuestionResultSketchComment = tmpResultQuestion;

        }

    }







}

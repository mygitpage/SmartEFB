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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 14.11.2016.
 */
public class OurGoalsFragmentDebetableGoalsComment extends Fragment {

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] debetableGoalsCommentScalesLevel = new String [countScalesLevel];

    // fragment view
    View viewFragmentDebetableGoalsComment;

    // fragment context
    Context fragmentDebetableGoalsContext = null;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment debetable goals
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of debetable goal to comment
    int debetableGoalsDbIdToComment = 0;

    // debetable goal number in list view
    int debetableGoalNumberInListView = 0;

    // cursor for the choosen debetable goal
    Cursor cursorChoosenDebetableGoals;

    // cursor for all comments to the choosen debetable goal
    Cursor cursorDebetableGoalAllComments;

    //number of radio buttons in struct question
    static final int numberOfRadioButtonsStructQuestion = 5;

    // result of struct question (1-5)
    int structQuestionResultDebetableGoalComment = 0;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentDebetableGoalsComment = layoutInflater.inflate(R.layout.fragment_our_goals_debetable_goals_comment, null);

        return viewFragmentDebetableGoalsComment;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {


        super.onViewCreated(view, saveInstanceState);

        fragmentDebetableGoalsContext = getActivity().getApplicationContext();

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment only when an debetable goal is choosen
        if (debetableGoalsDbIdToComment != 0) {

            // init the fragment now
            initFragmentDebetableGoalComment();
        }

    }


    // inits the fragment for use
    private void initFragmentDebetableGoalComment() {

        // init the DB
        myDb = new DBAdapter(fragmentDebetableGoalsContext);

        // init the prefs
        prefs = fragmentDebetableGoalsContext.getSharedPreferences("smartEfbSettings", fragmentDebetableGoalsContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // init array for text description of scales levels
        debetableGoalsCommentScalesLevel = getResources().getStringArray(R.array.debetableGoalsCommentScalesLevel);

        // get choosen debetable goal
        cursorChoosenDebetableGoals = myDb.getDebetableRowOurGoals(debetableGoalsDbIdToComment);

        // get all comments for choosen debetable goal
        cursorDebetableGoalAllComments = myDb.getAllRowsOurGoalsDebetableGoalsComment(debetableGoalsDbIdToComment);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = String.format(getResources().getString(getResources().getIdentifier("ourGoalsSubtitleDebetableGOalsComment", "string", fragmentDebetableGoalsContext.getPackageName())), debetableGoalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "debetableComment");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.debetableGoalCommentNumberIntro);
        String tmpCommentNumberIntro = this.getResources().getString(R.string.showDebetableGoalsIntroText) + " " + debetableGoalNumberInListView;
        textCommentNumberIntro.setText(tmpCommentNumberIntro);


        // generate back link "zurueck zu strittigen Zielen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourgoals")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_debetable_goals_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.debetableGoalShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+fragmentDebetableGoalsContext.getResources().getString(fragmentDebetableGoalsContext.getResources().getIdentifier("ourGoalsBackLinkToDebetableGoals", "string", fragmentDebetableGoalsContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the debetable goal
        TextView textViewGoal = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.choosenDebetableGoal);
        String debetableGoal = cursorChoosenDebetableGoals.getString(cursorChoosenDebetableGoals.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewGoal.setText(debetableGoal);

        // textview intro for the history of comments
        TextView textCommentDebetableGoalHistoryIntro = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.commentDebetableGoalHistoryIntro);
        if (cursorDebetableGoalAllComments.getCount() > 0) { // show comments for debetable goals when count comments > 0
            // show intro for comments
            textCommentDebetableGoalHistoryIntro.setText(this.getResources().getString(R.string.commentDebetableGoalHistoryIntroText)+ " " + debetableGoalNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentDebetableGoalsComment.findViewById(R.id.commentDebetableGoalsHistoryContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);
        }

        // set onClickListener for radio button in radio group question 1-4
        String tmpRessourceName ="";
        RadioButton tmpRadioButtonQuestion;


        for (int numberOfButtons=0; numberOfButtons < numberOfRadioButtonsStructQuestion; numberOfButtons++) {
            tmpRessourceName ="structQuestionOne_Answer" + (numberOfButtons+1);
            try {
                int resourceId = this.getResources().getIdentifier(tmpRessourceName, "id", fragmentDebetableGoalsContext.getPackageName());

                tmpRadioButtonQuestion = (RadioButton) viewFragmentDebetableGoalsComment.findViewById(resourceId);
                tmpRadioButtonQuestion.setOnClickListener(new debetableGoalCommentRadioButtonListener(numberOfButtons));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.infoDebetableGoalCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral;
        // build text element max debetable goal comment
        if (prefs.getInt("commentDebetableGoalsOurGoalsMaxComment", 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextDebetableGoalCommentMaxSingular), prefs.getInt("commentDebetableGoalsOurGoalsMaxComment", 0));
        }
        else if (prefs.getInt("commentDebetableGoalsOurGoalsMaxComment", 0) > 1 && commentLimitationBorder){
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextDebetableGoalCommentMaxPlural), prefs.getInt("commentDebetableGoalsOurGoalsMaxComment", 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentUnlimitedText);
        }

        // build text element count debetable goal comment count
        if (prefs.getInt("commentDebetableGoalsOurGoalsCountComment", 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountZero);
        }
        else if (prefs.getInt("commentDebetableGoalsOurGoalsCountComment", 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextDebetableGoalCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt("commentDebetableGoalsOurGoalsCountComment", 0));
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral);





        // textview intro for the history of comments
        /*
        TextView textCommentHistoryIntro = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.commentHistoryIntro);
        if (cursorArrangementAllComments.getCount() > 0) { // show comments for arrangement when count comments > 0
            // show intro for comments
            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryIntroText)+ " " + arrangementNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentDebetableGoalsComment.findViewById(R.id.commentHistoryContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);
        }
        */

        // button send comment
        Button buttonSendDebetableCommentComment = (Button) viewFragmentDebetableGoalsComment.findViewById(R.id.buttonSendDebetableGoalComment);

        // onClick listener send debetable goal comment
        buttonSendDebetableCommentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Boolean debetableGoalCommentNoError = true;
                TextView tmpErrorTextView;

                // check result struct question
                tmpErrorTextView = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.errorStructQuestionForCommentDebetableGoal);
                if ( structQuestionResultDebetableGoalComment == 0 && tmpErrorTextView != null) {
                    debetableGoalCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);

                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);

                }

                // comment textfield -> insert new comment
                tmpErrorTextView = (TextView) viewFragmentDebetableGoalsComment.findViewById(R.id.errorFreeQuestionForCommentDebetableGoal);
                EditText txtInputDebetableCommentComment = (EditText) viewFragmentDebetableGoalsComment.findViewById(R.id.inputDebetableGoalComment);
                if (txtInputDebetableCommentComment.getText().toString().length() < 3 && tmpErrorTextView != null) {
                    debetableGoalCommentNoError = false;
                    tmpErrorTextView.setVisibility(View.VISIBLE);
                } else if (tmpErrorTextView != null) {
                    tmpErrorTextView.setVisibility(View.GONE);
                }



                if (debetableGoalCommentNoError) {

                    // insert comment in DB
                    long newId = myDb.insertRowOurGoalsDebetableGoalsComment(txtInputDebetableCommentComment.getText().toString(), structQuestionResultDebetableGoalComment, 0, 0, prefs.getString("userName", "John Doe"), System.currentTimeMillis(), debetableGoalsDbIdToComment, true, prefs.getLong("currentDateOfDebetableGoals", System.currentTimeMillis()));

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentDebetableGoalsContext, fragmentDebetableGoalsContext.getResources().getString(R.string.debetableGoalCommentSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // increment debetable goal comment count
                    int countDebetableGoalsCommentSum = prefs.getInt("commentDebetableGoalsOurGoalsCountComment",0) + 1;
                    prefsEditor.putInt("commentDebetableGoalsOurGoalsCountComment", countDebetableGoalsCommentSum);
                    prefsEditor.commit();

                    // build intent to get back to OurGoalsFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_debetable_goals_now");
                    getActivity().startActivity(intent);

                }


            }
        });

        // button abbort
        Button buttonAbbortDebetableGoalComment = (Button) viewFragmentDebetableGoalsComment.findViewById(R.id.buttonAbortDebetableGoalComment);
        // onClick listener button abbort
        buttonAbbortDebetableGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_debetable_goals_now");
                getActivity().startActivity(intent);

            }
        });

        // End build the view

    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpDebetableGoalsDbIdToComment = 0;

        // call getter-methode getDebetableGoalsDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale debetable goal
        tmpDebetableGoalsDbIdToComment = ((ActivityOurGoals)getActivity()).getDebetableGoalDbIdFromLink();

        if (tmpDebetableGoalsDbIdToComment > 0) {
            debetableGoalsDbIdToComment = tmpDebetableGoalsDbIdToComment;

            // call getter-methode getDebetableGoalsNumberInListview() in ActivityOurGoals to get listView-number for the actuale debetable goal
            debetableGoalNumberInListView = ((ActivityOurGoals)getActivity()).getDebetableGoalNumberInListview();
            if (debetableGoalNumberInListView < 1) debetableGoalNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("debetableGoals");
        }

    }


    public void addActualCommentSetToView () {

        LinearLayout commentHolderLayout = (LinearLayout) viewFragmentDebetableGoalsComment.findViewById(R.id.commentDebetableGoalHolder);

        cursorDebetableGoalAllComments.moveToFirst();

        do {

            int actualCursorNumber = cursorDebetableGoalAllComments.getPosition()+1;

            // Linear Layout holds comment text and linear layout with author,date and new entry text
            LinearLayout l_inner_layout = new LinearLayout(fragmentDebetableGoalsContext);
            l_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l_inner_layout.setOrientation(LinearLayout.VERTICAL);

            //add textView for actual result struct question
            TextView txtViewDebetableGoalCommentResultStructQuestion = new TextView (fragmentDebetableGoalsContext);
            String actualResultStructQuestion = getResources().getString(R.string.textDebetableGoalCommentActualResultStructQuestion);
            actualResultStructQuestion = String.format(actualResultStructQuestion, debetableGoalsCommentScalesLevel[cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_RESULT_QUESTION1))-1]);
            txtViewDebetableGoalCommentResultStructQuestion.setText(actualResultStructQuestion);
            txtViewDebetableGoalCommentResultStructQuestion.setId(actualCursorNumber);
            txtViewDebetableGoalCommentResultStructQuestion.setTextColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.text_color));
            txtViewDebetableGoalCommentResultStructQuestion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewDebetableGoalCommentResultStructQuestion.setTextSize(12);
            txtViewDebetableGoalCommentResultStructQuestion.setGravity(Gravity.LEFT);
            txtViewDebetableGoalCommentResultStructQuestion.setPadding(15,0,0,0);

            //add textView for comment text
            TextView txtViewCommentText = new TextView (fragmentDebetableGoalsContext);
            txtViewCommentText.setText(cursorDebetableGoalAllComments.getString(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_COMMENT)));
            txtViewCommentText.setId(actualCursorNumber);
            txtViewCommentText.setTextColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.text_color));
            txtViewCommentText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentText.setTextSize(16);
            txtViewCommentText.setGravity(Gravity.LEFT);
            txtViewCommentText.setPadding(15,0,0,0);

            // Linear Layout holds author, date and text new entry
            LinearLayout aadn_inner_layout = new LinearLayout(fragmentDebetableGoalsContext);
            aadn_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            aadn_inner_layout.setOrientation(LinearLayout.HORIZONTAL);

            // check if comment new entry
            if (cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
                //add textView for text new entry
                TextView txtViewCommentNewEntry = new TextView (fragmentDebetableGoalsContext);
                txtViewCommentNewEntry.setText(this.getResources().getString(R.string.newEntryText));
                txtViewCommentNewEntry.setId(actualCursorNumber);
                txtViewCommentNewEntry.setTextColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.text_accent_color));
                txtViewCommentNewEntry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                txtViewCommentNewEntry.setTextSize(14);
                txtViewCommentNewEntry.setGravity(Gravity.LEFT);
                txtViewCommentNewEntry.setPadding(15,0,0,0);

                // add new entry text to linear layout
                aadn_inner_layout.addView (txtViewCommentNewEntry);

                // delete status new entry in db
                myDb.deleteStatusNewEntryOurGoalsDebetableGoalsComment(cursorDebetableGoalAllComments.getInt(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            //add textView for comment author and date
            TextView txtViewCommentAuthorAndDate = new TextView (fragmentDebetableGoalsContext);
            long writeTime = cursorDebetableGoalAllComments.getLong(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_WRITE_TIME));
            String authorAndDate = cursorDebetableGoalAllComments.getString(cursorDebetableGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_DEBETABLE_GOALS_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
            txtViewCommentAuthorAndDate.setText(authorAndDate);
            txtViewCommentAuthorAndDate.setId(actualCursorNumber);
            txtViewCommentAuthorAndDate.setTextColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.text_color));
            txtViewCommentAuthorAndDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentAuthorAndDate.setTextSize(14);
            txtViewCommentAuthorAndDate.setGravity(Gravity.RIGHT);
            txtViewCommentAuthorAndDate.setPadding(0,0,0,55);

            aadn_inner_layout.addView (txtViewCommentAuthorAndDate);

            // add elements to inner linear layout
            l_inner_layout.addView (txtViewDebetableGoalCommentResultStructQuestion);
            l_inner_layout.addView (txtViewCommentText);
            l_inner_layout.addView (aadn_inner_layout);

            // add inner layout to comment holder (linear layout in xml-file)
            commentHolderLayout.addView(l_inner_layout);

        } while (cursorDebetableGoalAllComments.moveToNext());



        // Linear Layout holds author, date and text new entry
        LinearLayout btnBack_inner_layout = new LinearLayout(fragmentDebetableGoalsContext);
        btnBack_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBack_inner_layout.setOrientation(LinearLayout.HORIZONTAL);
        btnBack_inner_layout.setGravity(Gravity.CENTER);


        // create back button (to debetable goals)
        Button btnBackToDebetableGoals = new Button (fragmentDebetableGoalsContext);
        btnBackToDebetableGoals.setText(this.getResources().getString(R.string.ourGoalsBackLinkToDebetableGoals));
        btnBackToDebetableGoals.setTextColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.text_color_white));
        btnBackToDebetableGoals.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToDebetableGoals.setTextSize(14);
        btnBackToDebetableGoals.setGravity(Gravity.CENTER);
        btnBackToDebetableGoals.setBackgroundColor(ContextCompat.getColor(fragmentDebetableGoalsContext, R.color.bg_btn_join));
        btnBackToDebetableGoals.setPadding(10,10,10,10);
        btnBackToDebetableGoals.setTop(25);
        btnBackToDebetableGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_debetable_goals_now");
                getActivity().startActivity(intent);

            }
        });

        // add elements to inner linear layout
        btnBack_inner_layout.addView (btnBackToDebetableGoals);

        // add back button to comment holder (linear layout in xml-file)
        commentHolderLayout.addView(btnBack_inner_layout);

    }





    //
    // onClickListener for radioButtons in fragment layout debetable goal comment
    //
    public class debetableGoalCommentRadioButtonListener implements View.OnClickListener {

        int radioButtonNumber;

        public debetableGoalCommentRadioButtonListener (int number) {

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

            structQuestionResultDebetableGoalComment = tmpResultQuestion;

        }

    }


}

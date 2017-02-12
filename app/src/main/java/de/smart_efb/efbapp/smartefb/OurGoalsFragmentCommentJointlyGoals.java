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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 24.10.2016.
 */
public class OurGoalsFragmentCommentJointlyGoals extends Fragment {

    // fragment view
    View viewFragmentCommentJointlyGoals;

    // fragment context
    Context fragmentCommentContextJointlyGoals = null;

    // layout inflater for fragment
    LayoutInflater layoutInflaterForFragment;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment jointly goals
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // DB-Id of jointly goal to comment
    int goalDbIdToComment = 0;

    // jointly goal number in list view
    int goalNumberInListView = 0;

    // cursor for the choosen jointly goal
    Cursor cursorChoosenGoal;

    // cursor for all comments to the choosen jointly goal
    Cursor cursorGoalAllComments;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup container, Bundle saveInstanceState) {

        layoutInflaterForFragment = layoutInflater;

        viewFragmentCommentJointlyGoals = layoutInflater.inflate(R.layout.fragment_our_goals_jointly_goals_comment, null);

        return viewFragmentCommentJointlyGoals;

    }


    @Override
    public void onViewCreated (View view, @Nullable Bundle saveInstanceState) {

        super.onViewCreated(view, saveInstanceState);

        fragmentCommentContextJointlyGoals = getActivity().getApplicationContext();

        // call getter function in ActivityOurGoals
        callGetterFunctionInSuper();

        // init the fragment jointly goals comment only when an goal is choosen
        if (goalDbIdToComment != 0) {
            initFragmentCommentJointlyGoals();
        }
    }


    // inits the fragment for use
    private void initFragmentCommentJointlyGoals() {

        // init the DB
        myDb = new DBAdapter(fragmentCommentContextJointlyGoals);

        // init the prefs
        prefs = fragmentCommentContextJointlyGoals.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentCommentContextJointlyGoals.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen jointly goal
        cursorChoosenGoal = myDb.getJointlyRowOurGoals(goalDbIdToComment);

        // get all comments for choosen jointly goals
        cursorGoalAllComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(goalDbIdToComment);

        // Set correct subtitle in Activity -> "Ziel ... kommentieren"
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("ourGoalsSubtitleJointlyGoalsComment", "string", fragmentCommentContextJointlyGoals.getPackageName()));
        tmpSubtitle = String.format(tmpSubtitle, goalNumberInListView);
        ((ActivityOurGoals) getActivity()).setOurGoalsToolbarSubtitle (tmpSubtitle, "jointlyNowComment");

        // build the view
        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.goalCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showJointlyGoalCommentIntroText) + " " + goalNumberInListView);


        // generate back link "zurueck zu gemeinsamen zielen"
        Uri.Builder commentLinkBuilder = new Uri.Builder();
        commentLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourgoals")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_jointly_goals_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.goalShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+viewFragmentCommentJointlyGoals.getResources().getString(fragmentCommentContextJointlyGoals.getResources().getIdentifier("ourGoalsBackLinkToJointlyGoals", "string", fragmentCommentContextJointlyGoals.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());


        // textview for the choosen jointly goal
        TextView textViewArrangement = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.choosenJointlyGoal);
        String arrangement = cursorChoosenGoal.getString(cursorChoosenGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL));
        textViewArrangement.setText(arrangement);

        //textview for the comment intro
        TextView textCommentIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.jointlyGoalCommentIntro);
        textCommentIntro.setText(this.getResources().getString(R.string.jointlyGoalCommentIntro) + " " + goalNumberInListView);


        // textview intro for the history of comments for jointly goals
        TextView textCommentHistoryIntro = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.commentHistoryJointlyGoalsIntro);
        if (cursorGoalAllComments.getCount() > 0) { // show comments for goal when count comments > 0
            // show intro for comments
            textCommentHistoryIntro.setText(this.getResources().getString(R.string.commentHistoryJointlyGoalsIntroText)+ " " + goalNumberInListView);
            // show comments
            addActualCommentSetToView ();

        } else { // else show nothing
            LinearLayout comentHistoryLinearLayoutContainer = (LinearLayout) viewFragmentCommentJointlyGoals.findViewById(R.id.commentHistoryJointlyGoalsContainer);
            comentHistoryLinearLayoutContainer.setVisibility(View.INVISIBLE);
        }

        // textview for max comments and count comments
        TextView textViewMaxAndCount = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.infoJointlyGoalsCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral;
        // build text element max sketch comment
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextJointlyGoalsCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextJointlyGoalsCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
        }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentUnlimitedText);
        }

        // build text element count current comment
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextJointlyGoalsCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0));
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral);


        // comment textfield -> insert new comment
        final EditText txtInputJointlyGoalComment = (EditText) viewFragmentCommentJointlyGoals.findViewById(R.id.inputJointlyGoalComment);
        // button send comment
        Button buttonSendJointlyGoalComment = (Button) viewFragmentCommentJointlyGoals.findViewById(R.id.buttonSendJointlyGoalComment);

        // onClick listener send jointly goal comment
        buttonSendJointlyGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (txtInputJointlyGoalComment.getText().toString().length() > 3) {

                    // insert comment in DB
                    long newID = myDb.insertRowOurGoalJointlyGoalComment(txtInputJointlyGoalComment.getText().toString(), prefs.getString("userName", "John Doe"), System.currentTimeMillis() , goalDbIdToComment, true, prefs.getLong("currentDateOfJointlyGoals", System.currentTimeMillis()));

                    // Toast "Comment sucsessfull send"
                    Toast.makeText(fragmentCommentContextJointlyGoals, viewFragmentCommentJointlyGoals.getResources().getString(R.string.commentJointlyGoalSuccsesfulySend), Toast.LENGTH_SHORT).show();

                    // increment comment count
                    int countCommentSum = prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0) + 1;
                    prefsEditor.putInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, countCommentSum);
                    prefsEditor.commit();

                    // build intent to get back to OurGoalsFragmentJointlyGoals
                    Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_jointly_goals_now");
                    getActivity().startActivity(intent);

                } else {

                    TextView tmpErrorTextView = (TextView) viewFragmentCommentJointlyGoals.findViewById(R.id.errorInputJointlyGoalComment);
                    tmpErrorTextView.setVisibility(View.VISIBLE);

                }

            }
        });

        // button abbort
        Button buttonAbbortJointlyGoalComment = (Button) viewFragmentCommentJointlyGoals.findViewById(R.id.buttonAbortJointlyGoalComment);
        // onClick listener button abbort
        buttonAbbortJointlyGoalComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
                getActivity().startActivity(intent);

            }
        });

        // End build the view

    }


    // call getter Functions in ActivityOurGoals for some data
    private void callGetterFunctionInSuper () {

        int tmpJointlyGoalDbIdToComment = 0;

        // call getter-methode getJointlyGoalDbIdFromLink() in ActivityOurGoals to get DB ID for the actuale goal
        tmpJointlyGoalDbIdToComment = ((ActivityOurGoals)getActivity()).getJointlyGoalDbIdFromLink();

        if (tmpJointlyGoalDbIdToComment > 0) {
            goalDbIdToComment = tmpJointlyGoalDbIdToComment;

            // call getter-methode getJointlyGoalNumberInListview() in ActivityOurGoals to get listView-number for the actuale jointly goal
            goalNumberInListView = ((ActivityOurGoals)getActivity()).getJointlyGoalNumberInListview();
            if (goalNumberInListView < 1) goalNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurGoals)getActivity()).isCommentLimitationBorderSet("jointlyGoals");
        }

    }


    public void addActualCommentSetToView () {

        LinearLayout commentHolderLayout = (LinearLayout) viewFragmentCommentJointlyGoals.findViewById(R.id.commentJointlyGoalsHolder);

        cursorGoalAllComments.moveToFirst();

        do {

            int actualCursorNumber = cursorGoalAllComments.getPosition()+1;

            // Linear Layout holds comment text and linear layout with author,date and new entry text
            LinearLayout l_inner_layout = new LinearLayout(fragmentCommentContextJointlyGoals);
            l_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            l_inner_layout.setOrientation(LinearLayout.VERTICAL);

            //add textView for comment text
            TextView txtViewCommentText = new TextView (fragmentCommentContextJointlyGoals);
            txtViewCommentText.setText(cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT)));
            txtViewCommentText.setId(actualCursorNumber);
            txtViewCommentText.setTextColor(ContextCompat.getColor(fragmentCommentContextJointlyGoals, R.color.text_color));
            txtViewCommentText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            txtViewCommentText.setTextSize(16);
            txtViewCommentText.setGravity(Gravity.LEFT);
            txtViewCommentText.setPadding(15,0,0,0);

            // Linear Layout holds author, date and text new entry
            LinearLayout aadn_inner_layout = new LinearLayout(fragmentCommentContextJointlyGoals);
            aadn_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            aadn_inner_layout.setOrientation(LinearLayout.HORIZONTAL);

            // check if comment new entry
            if (cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                //add textView for text new entry
                TextView txtViewCommentNewEntry = new TextView (fragmentCommentContextJointlyGoals);
                txtViewCommentNewEntry.setText(this.getResources().getString(R.string.newEntryText));
                txtViewCommentNewEntry.setId(actualCursorNumber);
                txtViewCommentNewEntry.setTextColor(ContextCompat.getColor(fragmentCommentContextJointlyGoals, R.color.text_accent_color));
                txtViewCommentNewEntry.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                txtViewCommentNewEntry.setTextSize(14);
                txtViewCommentNewEntry.setGravity(Gravity.LEFT);
                txtViewCommentNewEntry.setPadding(15,0,0,0);

                // add new entry text to linear layout
                aadn_inner_layout.addView (txtViewCommentNewEntry);

                // delet status new entry in db
                myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(cursorGoalAllComments.getInt(cursorGoalAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
            }

            //add textView for comment author and date
            TextView txtViewCommentAuthorAndDate = new TextView (fragmentCommentContextJointlyGoals);
            long writeTime = cursorGoalAllComments.getLong(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
            String authorAndDate = cursorGoalAllComments.getString(cursorGoalAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME)) + ", " + EfbHelperClass.timestampToDateFormat(writeTime, "dd.MM.yyyy - HH:mm");
            txtViewCommentAuthorAndDate.setText(authorAndDate);
            txtViewCommentAuthorAndDate.setId(actualCursorNumber);
            txtViewCommentAuthorAndDate.setTextColor(ContextCompat.getColor(fragmentCommentContextJointlyGoals, R.color.text_color));
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

        } while (cursorGoalAllComments.moveToNext());



        // Linear Layout holds author, date and text new entry
        LinearLayout btnBack_inner_layout = new LinearLayout(fragmentCommentContextJointlyGoals);
        btnBack_inner_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBack_inner_layout.setOrientation(LinearLayout.HORIZONTAL);
        btnBack_inner_layout.setGravity(Gravity.CENTER);


        // create back button (to jointly goals)
        Button btnBackToJointlyGoals = new Button (fragmentCommentContextJointlyGoals);
        btnBackToJointlyGoals.setText(this.getResources().getString(R.string.btnAbortJointlyGoalComment));
        btnBackToJointlyGoals.setTextColor(ContextCompat.getColor(fragmentCommentContextJointlyGoals, R.color.text_color_white));
        btnBackToJointlyGoals.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToJointlyGoals.setTextSize(14);
        btnBackToJointlyGoals.setGravity(Gravity.CENTER);
        btnBackToJointlyGoals.setBackground(ContextCompat.getDrawable(fragmentCommentContextJointlyGoals,R.drawable.app_button_style));
        btnBackToJointlyGoals.setPadding(10,10,10,10);
        btnBackToJointlyGoals.setTop(25);
        btnBackToJointlyGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityOurGoals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("com","show_jointly_goals_now");
                getActivity().startActivity(intent);

            }
        });

        // add elements to inner linear layout
        btnBack_inner_layout.addView (btnBackToJointlyGoals);

        // add back button to comment holder (linear layout in xml-file)
        commentHolderLayout.addView(btnBack_inner_layout);

    }
}

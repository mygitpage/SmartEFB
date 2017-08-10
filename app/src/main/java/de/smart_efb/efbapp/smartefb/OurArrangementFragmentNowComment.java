package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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

    // dialog for information no more messages possible
    AlertDialog alertDialogNoMoreMessages;

    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    // Server DB-Id of arrangement to comment (the server id of arrangement is anchor)
    int arrangementServerDbIdToComment = 0;

    // arrangement number in list view
    int arrangementNumberInListView = 0;

    // cursor for the choosen arrangement
    Cursor cursorChoosenArrangement;

    // cursor for all comments to the choosen arrangement
    Cursor cursorArrangementAllComments;

    // comment limitation true-> yes, there is a border; no -> unlimited comments
    Boolean commentLimitationBorder;


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

        // call getter function in ActivityOurArrangment
        callGetterFunctionInSuper();

        // init the fragment now only when an arrangement is choosen
        if (arrangementServerDbIdToComment != 0) {
            initFragmentNowComment();
        }
    }


    // inits the fragment for use
    private void initFragmentNowComment() {

        // init the DB
        myDb = new DBAdapter(fragmentNowCommentContext);

        // init the prefs
        prefs = fragmentNowCommentContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, fragmentNowCommentContext.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        // get choosen arrangement
        cursorChoosenArrangement = myDb.getRowOurArrangement(arrangementServerDbIdToComment);

        // get all comments for choosen arrangement
        cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementServerDbIdToComment);

        // Set correct subtitle in Activity -> "Kommentieren Absprache ..."
        String tmpSubtitle = getResources().getString(getResources().getIdentifier("subtitleFragmentNowCommentText", "string", fragmentNowCommentContext.getPackageName())) + " " + arrangementNumberInListView;
        ((ActivityOurArrangement) getActivity()).setOurArrangementToolbarSubtitle (tmpSubtitle, "nowComment");

        // build the view

        //textview for the comment intro
        TextView textCommentNumberIntro = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementCommentNumberIntro);
        textCommentNumberIntro.setText(this.getResources().getString(R.string.showArrangementIntroText) + " " + arrangementNumberInListView);

        // textview for the author of arrangement
        TextView tmpTextViewAuthorNameText = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorName);
        String tmpTextAuthorNameText = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementAuthorNameText), cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)));
        tmpTextViewAuthorNameText.setText(tmpTextAuthorNameText);

        // textview for the arrangement
        TextView textViewArrangement = (TextView) viewFragmentNowComment.findViewById(R.id.choosenArrangement);
        String arrangement = cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(arrangement);


        // generate back link "zurueck zu allen Absprachen"
        Uri.Builder backArrangementLinkBuilder = new Uri.Builder();
        backArrangementLinkBuilder.scheme("smart.efb.deeplink")
                .authority("linkin")
                .path("ourarrangement")
                .appendQueryParameter("db_id", "0")
                .appendQueryParameter("arr_num", "0")
                .appendQueryParameter("com", "show_arrangement_now");
        TextView linkShowCommentBackLink = (TextView) viewFragmentNowComment.findViewById(R.id.arrangementShowCommentBackLinkNow);
        linkShowCommentBackLink.setText(Html.fromHtml("<a href=\"" + backArrangementLinkBuilder.build().toString() + "\">"+fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementBackLinkToArrangementFromComment", "string", fragmentNowCommentContext.getPackageName()))+"</a>"));
        linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

        // some comments for arrangement available?
        if (cursorArrangementAllComments.getCount() > 0) {

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentText));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // check if comment entry new?
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {

                TextView newEntryOfComment = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentNewInfoText);
                String txtNewEntryOfComment = fragmentNowCommentContext.getResources().getString(R.string.newEntryText);
                newEntryOfComment.setText(txtNewEntryOfComment);
                myDb.deleteStatusNewEntryOurArrangementComment(cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));

            }

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            String tmpAuthorName = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME));
            String commentDate = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME)), "dd.MM.yyyy");;
            String commentTime = EfbHelperClass.timestampToDateFormat(cursorArrangementAllComments.getLong(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME)), "HH:mm");;
            String tmpTextAuthorNameLastActualComment = String.format(fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
            tmpTextViewAuthorNameLastActualComment.setText(tmpTextAuthorNameLastActualComment);


            // textview for the author of last actual comment
            if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 0) {
                TextView tmpTextViewSendInfoLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textSendInfoLastActualComment);
                String tmpTextSendInfoLastActualComment = fragmentNowCommentContext.getResources().getString(R.string.ourArrangementCommentSendInfo);
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);


            }


            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            String tmpCommentText = cursorArrangementAllComments.getString(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
            tmpTextViewCommentText.setText(tmpCommentText);



            TextView tmpTextViewLInkToShowAllComment = (TextView) viewFragmentNowComment.findViewById(R.id.commentLInkToShowAllComments);

            // more than one comment available?
            if (cursorArrangementAllComments.getCount() > 1) {

                // generate link to show all comments and set
                Uri.Builder commentLinkBuilder = new Uri.Builder();
                commentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", Integer.toString(arrangementServerDbIdToComment))
                        .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                        .appendQueryParameter("com", "show_comment_for_arrangement");

                if (cursorArrangementAllComments.getCount() == 2) {
                    String tmpLinkStringShowAllComments = String.format(fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsSingular", "string", fragmentNowCommentContext.getPackageName())),cursorArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                else {
                    String tmpLinkStringShowAllComments = String.format(fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsPlural", "string", fragmentNowCommentContext.getPackageName())),cursorArrangementAllComments.getCount()-1);
                    tmpTextViewLInkToShowAllComment.setText(Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkStringShowAllComments + "</a>"));
                }
                tmpTextViewLInkToShowAllComment.setMovementMethod(LinkMovementMethod.getInstance());

            }
            else {
                // no comment anymore
                String tmpLinkStringShowAllComments = fragmentNowCommentContext.getResources().getString(fragmentNowCommentContext.getResources().getIdentifier("ourArrangementCommentLinkToShowAllCommentsNotAvailable", "string", fragmentNowCommentContext.getPackageName()));
                tmpTextViewLInkToShowAllComment.setText(tmpLinkStringShowAllComments);
            }

        }
        else { // no comments

            //textview for the last actual comment intro
            TextView textLastActualCommentIntro = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentInfoText);
            textLastActualCommentIntro.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabel));

            // position one for comment cursor
            cursorArrangementAllComments.moveToFirst();

            // textview for the author of last actual comment
            TextView tmpTextViewAuthorNameLastActualComment = (TextView) viewFragmentNowComment.findViewById(R.id.textAuthorNameLastActualComment);
            tmpTextViewAuthorNameLastActualComment.setText(this.getResources().getString(R.string.lastActualCommentTextNoCommentAvailabelFirstAuthor));

            // textview for the comment text
            TextView tmpTextViewCommentText = (TextView) viewFragmentNowComment.findViewById(R.id.lastActualCommentText);
            tmpTextViewCommentText.setVisibility(View.GONE);

        }


        // textview for max comments, count comments and max letters
        TextView textViewMaxAndCount = (TextView) viewFragmentNowComment.findViewById(R.id.infoNowCommentMaxAndCount);
        String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
        // build text element max comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) == 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextNowCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) > 1 && commentLimitationBorder) {
            tmpInfoTextMaxSingluarPluaral = String.format(this.getResources().getString(R.string.infoTextNowCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
         }
        else {
            tmpInfoTextMaxSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentUnlimitedText);
        }

        // build text element count comment
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 0) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountZero);
        }
        else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 1) {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountSingular);
        }
        else {
            tmpInfoTextCountSingluarPluaral = this.getResources().getString(R.string.infoTextNowCommentCountPlural);
        }
        tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0));

        // generate text comment max letters
        tmpInfoTextCommentMaxLetters =  this.getResources().getString(R.string.infoTextNowCommentCommentMaxLetters);
        tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 0));

        // show info text
        textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters);


        // get max letters for edit text comment
        final int tmpMaxLength = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 10);

        // get textView to count input letters and init it
        final TextView textViewCountLettersCommentEditText = (TextView) viewFragmentNowComment.findViewById(R.id.countLettersCommentEditText);
        String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
        tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, "0", tmpMaxLength);
        textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);

        // comment textfield -> set hint text
        final EditText txtInputArrangementComment = (EditText) viewFragmentNowComment.findViewById(R.id.inputArrangementComment);

        // set text watcher to count letters in comment field
        final TextWatcher txtInputArrangementCommentTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
                String tmpInfoTextCountLetters =  getResources().getString(R.string.infoTextCountLettersForComment);
                tmpInfoTextCountLetters = String.format(tmpInfoTextCountLetters, String.valueOf(s.length()), tmpMaxLength);
                textViewCountLettersCommentEditText.setText(tmpInfoTextCountLetters);
            }
            public void afterTextChanged(Editable s) {
            }
        };

        // set text watcher to count input letters
        txtInputArrangementComment.addTextChangedListener(txtInputArrangementCommentTextWatcher);

        // set input filter max length for comment field
        txtInputArrangementComment.setFilters(new InputFilter[] {new InputFilter.LengthFilter(tmpMaxLength)});

        // get button send comment
        Button buttonSendArrangementComment = (Button) viewFragmentNowComment.findViewById(R.id.buttonSendArrangementComment);

        // set onClick listener send arrangement comment
        buttonSendArrangementComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtInputArrangementComment.getText().toString().length() > 3) {

                    // insert comment in DB
                    Long tmpDbId = myDb.insertRowOurArrangementComment(txtInputArrangementComment.getText().toString(), prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), System.currentTimeMillis(), 0, cursorChoosenArrangement.getString(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_BLOCK_ID)), true, prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), 0, cursorChoosenArrangement.getInt(cursorChoosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)));

                    // increment comment count
                    int countCommentSum = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) + 1;
                    prefsEditor.putInt(ConstansClassOurArrangement.namePrefsCommentCountComment, countCommentSum);
                    prefsEditor.commit();

                    // send intent to service to start the service and send comment to server!
                    Intent startServiceIntent = new Intent(fragmentNowCommentContext, ExchangeServiceEfb.class);
                    startServiceIntent.putExtra("com","send_now_comment_arrangement");
                    startServiceIntent.putExtra("dbid",tmpDbId);
                    fragmentNowCommentContext.startService(startServiceIntent);

                    // build intent to get back to OurArrangementFragmentNow
                    Intent intent = new Intent(getActivity(), ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com", "show_arrangement_now");
                    getActivity().startActivity(intent);

                } else {

                    TextView tmpErrorTextView = (TextView) viewFragmentNowComment.findViewById(R.id.errorInputArrangementComment);
                    tmpErrorTextView.setVisibility(View.VISIBLE);
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


    // call getter Functions in ActivityOurArrangement for some data
    private void callGetterFunctionInSuper () {

        int tmparrangementServerDbIdToComment = 0;

        // call getter-methode getArrangementDbIdFromLink() in ActivityOurArrangement to get DB ID for the actuale arrangement
        tmparrangementServerDbIdToComment = ((ActivityOurArrangement)getActivity()).getArrangementDbIdFromLink();

        if (tmparrangementServerDbIdToComment > 0) {
            arrangementServerDbIdToComment = tmparrangementServerDbIdToComment;

            // call getter-methode getArrangementNumberInListview() in ActivityOurArrangement to get listView-number for the actuale arrangement
            arrangementNumberInListView = ((ActivityOurArrangement)getActivity()).getArrangementNumberInListview();
            if (arrangementNumberInListView < 1) arrangementNumberInListView = 1; // check borders

            // check for comment limitations
            commentLimitationBorder = ((ActivityOurArrangement)getActivity()).isCommentLimitationBorderSet("current");
        }

    }

    /*
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

            // check if comment new entry
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

                // delete status new entry in db
                myDb.deleteStatusNewEntryOurArrangementComment(cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.KEY_ROWID)));
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
        btnBackToArrangement.setTextColor(ContextCompat.getColor(fragmentNowCommentContext, R.color.text_color));
        btnBackToArrangement.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnBackToArrangement.setTextSize(14);
        btnBackToArrangement.setGravity(Gravity.CENTER);
        btnBackToArrangement.setBackground(ContextCompat.getDrawable(fragmentNowCommentContext,R.drawable.app_button_style));
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
    */



}

package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 06.10.16.
 */
public class OurArrangementShowSketchCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateSketchCommentScalesLevel = new String [countScalesLevel];

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the chossen arrangement to show the sketch comments
    Cursor choosenArrangement;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // reference to the DB
    private DBAdapter myDb;

    // count headline number for sketch comments
    int countCommentHeadlineNumber = 0;


    // own constructor!!!
    public OurArrangementShowSketchCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, Boolean commentsLimitation, Cursor arrangement) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for arrangement
        arrangementDbIdToShow = dbId;

        // set arrangement number in list view
        arrangementNumberInListView = numberInLIst;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choosen arrangement
        choosenArrangement = arrangement;

        // init the DB
        myDb = new DBAdapter(context);

        // init array for text description of scales levels
        evaluateSketchCommentScalesLevel = context.getResources().getStringArray(R.array.evaluateSketchCommentScalesLevel);

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor position first?
        if (cursor.isFirst()) {

            // set text arrangement intro
            TextView textViewShowSketchArrangementIntro = (TextView) view.findViewById(R.id.arrangementShowSketchArrangementIntro);
            String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showSketchArrangementIntroText)+ " " + arrangementNumberInListView;
            textViewShowSketchArrangementIntro.setText(txtArrangementIntro);

            // textview for the author of sketch arrangement
            TextView tmpTextViewAuthorNameText = (TextView) view.findViewById(R.id.textAuthorName);
            String tmpTextAuthorNameText = String.format(view.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

            // make link back to show sketch arrangement
            Uri.Builder backLinkBuilder = new Uri.Builder();
            backLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_sketch_arrangement");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.arrangementShowSketchCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + backLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementBackLinkToSketchArrangement", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

            // check, sharing sketch comments enable?
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 0) {
                TextView textSketchCommentSharingIsDisable = (TextView) view.findViewById(R.id.sketchCommentSharingIsDisable);
                textSketchCommentSharingIsDisable.setVisibility (View.VISIBLE);
            }

            // show choosen arrangement
            TextView textViewShowChoosenArrangement = (TextView) view.findViewById(R.id.choosenSketchArrangement);
            textViewShowChoosenArrangement.setText(choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT)));
        }

        // generate onclicklistener for Button "zurueck zu den entwuerfen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den Entwuerfen"
            Button buttonBackToArrangement = (Button) view.findViewById(R.id.buttonAbortShowSketchComment);

            // onClick listener abbort button
            buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_sketch_arrangement");
                    contextForActivity.startActivity(intent);
                }
            });

            // generate link for sort sequence change
            TextView textViewChangeSortSequence = (TextView) view.findViewById(R.id.linkToChangeSortSequenceOfCommentList);
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(arrangementDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                    .appendQueryParameter("eval_next", Boolean.toString(false))
                    .appendQueryParameter("com", "change_sort_sequence_arrangement_sketch_comment");

            String tmpLinkTextChangeSortSequence;
            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending").equals("descending")) {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementSketchCommentDescending", "string", context.getPackageName()));
            }
            else {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementSketchCommentAscending", "string", context.getPackageName()));
            }
            Spanned tmpSortLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextChangeSortSequence + "</a>");
            textViewChangeSortSequence.setText(tmpSortLink);
            textViewChangeSortSequence.setMovementMethod(LinkMovementMethod.getInstance());
            

            // textview for max comments and count comments
            TextView textViewMaxAndCount = (TextView) view.findViewById(R.id.infoSketchCommentMaxAndCount);
            String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextSketchCommentMaxLetters;
            // build text element max sketch comment
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) == 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextSketchCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) > 1 && commentLimitationBorder){
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextSketchCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
            }
            else {
                tmpInfoTextMaxSingluarPluaral = context.getString(R.string.infoTextSketchCommentUnlimitedText);
            }

            // build text element count sketch comment
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 0) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextSketchCommentCountZero);
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 1) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextSketchCommentCountSingular);
            }
            else {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextSketchCommentCountPlural);
            }
            tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0));

            // build text element delay time
            String tmpInfoTextDelaytimeSingluarPluaral = "";
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 0) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextSketchCommentDelaytimeNoDelay);
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 1) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextSketchCommentDelaytimeSingular);
            }
            else {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextSketchCommentDelaytimePlural);
                tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0));

            }

            // generate text comment max letters
            tmpInfoTextSketchCommentMaxLetters =  context.getString(R.string.infoTextSketchCommentMaxLetters);
            tmpInfoTextSketchCommentMaxLetters = String.format(tmpInfoTextSketchCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters, 0));

            // show info text
            textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextSketchCommentMaxLetters + " " + tmpInfoTextDelaytimeSingluarPluaral);
        }
    }


    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        final Context context = mContext;

        // set row id of comment from db for timer update
        final Long rowIdForUpdate = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_first, parent, false);
            countCommentHeadlineNumber = 1;
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_firstandlast, parent, false);
            countCommentHeadlineNumber++;
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment_last, parent, false);
            countCommentHeadlineNumber++;
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_sketch_comment, parent, false);
            countCommentHeadlineNumber++;
        }

        // set sketch comment information to the view
        // set sketch comment headline
        TextView textViewShowActualSketchCommentHeadline = (TextView) inflatedView.findViewById(R.id.actualSketchCommentInfoText);
        String actualCommentHeadline = context.getResources().getString(R.string.showSketchCommentHeadlineWithNumber) + " " + countCommentHeadlineNumber;
        if (cursor.isFirst()) { // set text newest comment
            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending").equals("descending")) {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showSketchCommentHeadlineWithNumberExtraNewest);
            }
            else {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showSketchCommentHeadlineWithNumberExtraOldest);
            }
        }
        textViewShowActualSketchCommentHeadline.setText(actualCommentHeadline);

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) inflatedView.findViewById(R.id.actualSketchCommentNewInfoText);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);

            // delet status new entry in db
            myDb.deleteStatusNewEntryOurArrangementSketchComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameLastActualComment = (TextView) inflatedView.findViewById(R.id.textAuthorNameActualSketchComment);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_AUTHOR_NAME));
        if (tmpAuthorName.equals(prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"))) {
            tmpAuthorName = context.getResources().getString(R.string.ourArrangementShowSketchCommentPersonalAuthorName);
        }
        String commentDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");;
        String commentTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME)), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = "";
        tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourArrangementShowSketchCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        if (cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourArrangementShowSketchCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!

        tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

        // textview for status 0 of the last actual comment
        final TextView tmpTextViewSendInfoLastActualSketchComment = (TextView) inflatedView.findViewById(R.id.textSendInfoActualSketchComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 0) {

            String tmpTextSendInfoLastActualSketchComment = context.getResources().getString(R.string.ourArrangementShowSketchCommentSendInfo);
            tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
            tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualSketchComment);

        } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_STATUS)) == 1) {
            // textview for status 1 of the last actual comment

            // check, sharing of sketch comments enable?
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 1) {
                // check system time is in past or future?
                Long writeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME)); // write time is from sever
                Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) * 60000; // make milliseconds from miutes
                Long maxTimerTime = writeTimeComment+delayTime;
                if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!) {
                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Long localeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_LOCAL_TIME));
                    Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                    // set textview visible
                    tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);

                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // gernate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowSketchCommentSendDelayInfo);
                                String tmpTime = String.format(FORMAT,
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                // put count down to string
                                String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                // and show
                                tmpTextViewSendInfoLastActualSketchComment.setText(tmpCountdownTimerString);
                            }

                            public void onFinish() {
                                // count down is over -> show
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                                tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }.start();

                    } else {
                        // no count down anymore -> show send successfull
                        String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else {
                    // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                    tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                    String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                    tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                    myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }
            else { // sharing of sketch comments is disable! -> show text
                String tmpTextSendInfoLastActualSketchComment = "";
                tmpTextViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShareChangeTime, 0) < cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_WRITE_TIME))) {
                    // show send successfull, but no sharing
                    tmpTextSendInfoLastActualSketchComment = context.getResources().getString(R.string.ourArrangementCommentSendInfoSharingDisable);
                }
                else {
                    // show send successfull
                    tmpTextSendInfoLastActualSketchComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                }
                tmpTextViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualSketchComment);
                myDb.updateTimerStatusOurArrangementSketchComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
            }
        }

        // show actual result struct question only when result > 0
        TextView textViewShowResultStructQuestion = (TextView) inflatedView.findViewById(R.id.assessementValueForSketchComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) > 0) {
            String actualResultStructQuestion = context.getResources().getString(R.string.textOurArrangementShowSketchCommentActualResultStructQuestion);
            actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_RESULT_QUESTION1)) - 1]);
            textViewShowResultStructQuestion.setText(Html.fromHtml(actualResultStructQuestion));
        } else { // result is =0; comes from server/ coach
            String actualResultStructQuestionFromCoach = context.getResources().getString(R.string.textOurArrangementShowSketchCommentActualResultStructQuestionFromCoach);
            textViewShowResultStructQuestion.setText(actualResultStructQuestionFromCoach);
        }

        // show actual sketch comment
        TextView textViewShowActualComment = (TextView) inflatedView.findViewById(R.id.listActualTextSketchComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_SKETCH_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // make link to comment an sketch arrangement only when comments possible
        TextView linkToCommentAnArrangement = (TextView) inflatedView.findViewById(R.id.linkToCommentAnSketchArrangement);
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment,0) || !commentLimitationBorder) {

            // generate difference text for comment anymore
            String tmpNumberSketchCommentsPossible;
            int tmpDifferenceComments = prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0);
            if (commentLimitationBorder) {
                if (tmpDifferenceComments > 0) {
                    if (tmpDifferenceComments > 1) { //plural comments
                        tmpNumberSketchCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfSketchCommentsPossiblePlural), tmpDifferenceComments);
                    } else { // singular comments
                        tmpNumberSketchCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfSketchCommentsPossibleSingular), tmpDifferenceComments);
                    }
                }
                else {
                    tmpNumberSketchCommentsPossible = context.getString(R.string.infoTextNumberOfSketchCommentsPossibleNoMore);
                }
            }
            else {
                tmpNumberSketchCommentsPossible = context.getString(R.string.infoTextNumberOfSketchCommentsPossibleNoBorder);
            }

            Uri.Builder sketchCommentLinkBuilder = new Uri.Builder();
            sketchCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(arrangementDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                    .appendQueryParameter("com", "comment_an_sketch_arrangement");
            String tmpLinkTextForCommentAnArrangement = String.format(context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowSketchCommentLinkToCommentAnArrangement", "string", context.getPackageName())), arrangementNumberInListView);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + sketchCommentLinkBuilder.build().toString() + "\">" + tmpLinkTextForCommentAnArrangement + " " + tmpNumberSketchCommentsPossible + "</a>");
            linkToCommentAnArrangement.setText(tmpBackLink);
            linkToCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            linkToCommentAnArrangement.setVisibility(View.GONE);
        }

        return inflatedView;
    }


    // Turn off view recycling in listview, because there are different views (first, normal, last)
    // getViewTypeCount(), getItemViewType
    @Override
    public int getViewTypeCount () {

        return getCount();
    }

    @Override
    public int getItemViewType (int position) {

        return position;
    }


}

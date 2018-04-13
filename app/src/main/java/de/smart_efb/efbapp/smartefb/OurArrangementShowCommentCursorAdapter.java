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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 23.07.16.
 */
public class OurArrangementShowCommentCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the chossen arrangement to show the comments
    Cursor choosenArrangement;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // count headline number for comments
    int countCommentHeadlineNumber = 0;


    // own constructor!!!
    public OurArrangementShowCommentCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInList, Boolean commentsLimitation, Cursor arrangement) {

        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for arrangement
        arrangementDbIdToShow = dbId;

        // set arrangement number in list view
        arrangementNumberInListView = numberInList;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choosen arrangement
        choosenArrangement = arrangement;

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor position first?
        if (cursor.isFirst()) {

            // set text arrangement intro
            TextView textViewShowArrangementIntro = (TextView) view.findViewById(R.id.arrangementShowArrangementIntro);
            String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showArrangementIntroText)+ " " + arrangementNumberInListView;
            textViewShowArrangementIntro.setText(txtArrangementIntro);

            // textview for the author of arrangement
            TextView tmpTextViewAuthorNameText = (TextView) view.findViewById(R.id.textAuthorName);
            String tmpTextAuthorNameText = String.format(view.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

            // make link back to show arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_arrangement_now");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.arrangementShowCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementBackLinkToArrangementFromShowComment", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

            // show choosen arrangement
            TextView textViewShowChoosenArrangement = (TextView) view.findViewById(R.id.choosenArrangement);
            textViewShowChoosenArrangement.setText(choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT)));

            // show hint sharing is disable
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 0) {
                TextView textCommentSharingIsDisable = (TextView) view.findViewById(R.id.commentSharingIsDisable);
                textCommentSharingIsDisable.setVisibility (View.VISIBLE);
            }
        }

        // generate onclicklistener for Button "zurueck zu den Absprachen"
        if (cursor.isLast()) {

            // button abbort "zurueck zu den Absprachen"
            Button buttonBackToArrangement = (Button) view.findViewById(R.id.buttonAbortShowComment);

            // onClick listener abbort button
            buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_arrangement_now");
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
                    .appendQueryParameter("com", "change_sort_sequence_arrangement_comment");

            String tmpLinkTextChangeSortSequence;
            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementCommentDescending", "string", context.getPackageName()));
            }
            else {
                tmpLinkTextChangeSortSequence = context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementCommentAscending", "string", context.getPackageName()));
            }
            Spanned tmpSortLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextChangeSortSequence + "</a>");
            textViewChangeSortSequence.setText(tmpSortLink);
            textViewChangeSortSequence.setMovementMethod(LinkMovementMethod.getInstance());

            // textview for max comments, count comments and max letters
            TextView textViewMaxAndCount = (TextView) view.findViewById(R.id.infoShowCommentMaxAndCount);
            String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
            // build text element max comment
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) == 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextNowCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) > 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextNowCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
            }
            else {
                tmpInfoTextMaxSingluarPluaral = context.getString(R.string.infoTextNowCommentUnlimitedText);
            }

            // build text element count comment
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 0) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextNowCommentCountZero);
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 1) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextNowCommentCountSingular);
            }
            else {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextNowCommentCountPlural);
            }
            tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0));

            // build text element delay time
            String tmpInfoTextDelaytimeSingluarPluaral = "";
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 0) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextNowCommentDelaytimeNoDelay);
            }
            else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 1) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextNowCommentDelaytimeSingular);
            }
            else {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextNowCommentDelaytimePlural);
                tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0));
            }

            // generate text comment max letters
            tmpInfoTextCommentMaxLetters =  context.getString(R.string.infoTextNowCommentCommentMaxLettersAndDelaytime);
            tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 0));

            // show info text
            textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);
        }
    }


    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        // init the DB
        final DBAdapter myDb = new DBAdapter(mContext);

        View inflatedView;

        final Context context = mContext;

        // set row id of comment from db for timer update
        final Long rowIdForUpdate = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_first, parent, false);
            countCommentHeadlineNumber = 1;
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_firstandlast, parent, false);
            countCommentHeadlineNumber++;
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment_last, parent, false);
            countCommentHeadlineNumber++;
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_show_comment, parent, false);
            countCommentHeadlineNumber++;
        }

        // set comment information to the view
        // set comment headline
        TextView textViewShowActualCommentHeadline = (TextView) inflatedView.findViewById(R.id.actualCommentInfoText);
        String actualCommentHeadline = context.getResources().getString(R.string.showCommentHeadlineWithNumber) + " " + countCommentHeadlineNumber;
        if (cursor.isFirst()) { // set text newest comment
            if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showCommentHeadlineWithNumberExtraNewest);
            }
            else {
                actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showCommentHeadlineWithNumberExtraOldest);
            }
        }
        textViewShowActualCommentHeadline.setText(actualCommentHeadline);

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) inflatedView.findViewById(R.id.actualCommentNewInfoText);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);

            // delet status new entry in db
            myDb.deleteStatusNewEntryOurArrangementComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameLastActualComment = (TextView) inflatedView.findViewById(R.id.textAuthorNameActualComment);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_AUTHOR_NAME));
        if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
            tmpAuthorName = context.getResources().getString(R.string.ourArrangementShowCommentPersonalAuthorName);
        }
        String commentDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "dd.MM.yyyy");;
        String commentTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME)), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = "";
        tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        if (cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 4) {tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
        tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

        // textview for status 0 of the last actual comment
        final TextView tmpTextViewSendInfoLastActualComment = (TextView) inflatedView.findViewById(R.id.textSendInfoActualComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 0) {

            String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendInfo);
            tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
            tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

        } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_STATUS)) == 1) {
            // textview for status 1 of the last actual comment

            // check, sharing of comments enable?
            if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 1) {
                Long writeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME));
                Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                Long maxTimerTime = writeTimeComment+delayTime;
                if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_TIMER_STATUS)) == 0) { // check system time is in past and timer status is run!
                    // set textview visible
                    tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                    // calculate run time for timer in MILLISECONDS!!!
                    Long nowTime = System.currentTimeMillis();
                    Long localeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_LOCAL_TIME));
                    Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);
                    // start the timer with the calculated milliseconds
                    if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                        new CountDownTimer(runTimeForTimer, 1000) {
                            public void onTick(long millisUntilFinished) {
                                // gernate count down timer
                                String FORMAT = "%02d:%02d:%02d";
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendDelayInfo);
                                String tmpTime = String.format(FORMAT,
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                // put count down to string
                                String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                // and show
                                tmpTextViewSendInfoLastActualComment.setText(tmpCountdownTimerString);
                            }

                            public void onFinish() {
                                // count down is over -> show
                                String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }.start();

                    } else {
                        // no count down anymore -> show send successfull
                        String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                        tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }
                else { // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                    tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                    String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                    myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                }
            }
            else { // sharing of comments is disable! -> show text

                String tmpTextSendInfoLastActualComment = "";
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementCommentShareChangeTime, 0) < cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_WRITE_TIME))) {
                    // show send successfull, but no sharing
                    tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementCommentSendInfoSharingDisable);
                }
                else {
                    // show send successfull
                    tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                }
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
            }
        }

        // show actual comment
        TextView textViewShowActualComment = (TextView) inflatedView.findViewById(R.id.listActualTextComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // generate difference text for comment anymore
        String tmpNumberCommentsPossible;
        int tmpDifferenceComments = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0);
        if (commentLimitationBorder) {
            if (tmpDifferenceComments > 0) {
                if (tmpDifferenceComments > 1) { //plural comments
                    tmpNumberCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfCommentsPossiblePlural), tmpDifferenceComments);
                } else { // singular comments
                    tmpNumberCommentsPossible = String.format(context.getString(R.string.infoTextNumberOfCommentsPossibleSingular), tmpDifferenceComments);
                }
            }
            else {
                tmpNumberCommentsPossible = context.getString(R.string.infoTextNumberOfCommentsPossibleNoMore);
            }
        }
        else {
            tmpNumberCommentsPossible = context.getString(R.string.infoTextNumberOfCommentsPossibleNoBorder);
        }

        // make link to comment an sketch arrangement only when comments possible
        TextView linkToCommentAnArrangement = (TextView) inflatedView.findViewById(R.id.linkToCommentAnArrangement);
        if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) || !commentLimitationBorder) {
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(arrangementDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                    .appendQueryParameter("com", "comment_an_arrangement");
                        String tmpLinkTextForCommentAnArrangement = String.format(context.getResources().getString(context.getResources().getIdentifier("ourArrangementShowCommentLinkToCommentAnArrangement", "string", context.getPackageName())), arrangementNumberInListView);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextForCommentAnArrangement + " " + tmpNumberCommentsPossible + "</a>");
            linkToCommentAnArrangement.setText(tmpBackLink);
            linkToCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            linkToCommentAnArrangement.setVisibility(View.GONE);
        }

        // close DB connection
        myDb.close();

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









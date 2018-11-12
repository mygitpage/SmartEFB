package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

/**
 * Created by ich on 30.05.16.
 */

public class OurArrangementNowCursorAdapter extends CursorAdapter {

    // hold layoutInflater
    private LayoutInflater cursorInflater;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];


    // Default constructor
    public OurArrangementNowCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor first?
        if (cursor.isFirst() ) {
            TextView numberOfArrangement = (TextView) view.findViewById(R.id.ourArrangementIntroText);
            String txtArrangementNumber = context.getResources().getString(R.string.ourArrangementIntroText) + " " + EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy");
            numberOfArrangement.setText(txtArrangementNumber);
        }

        // is cursor last?
        if (cursor.isLast() ) { // listview for last element -> set gap to bottom of display

            if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) { // show info of evaluation period when activated
                // set info text evaluation period
                TextView textViewEvaluationPeriodGapToTop = (TextView) view.findViewById(R.id.borderBetweenLastElementAndEvaluationInfo);
                textViewEvaluationPeriodGapToTop.setVisibility(View.VISIBLE);
                TextView textViewEvaluationPeriod = (TextView) view.findViewById(R.id.infoEvaluationTimePeriod);
                textViewEvaluationPeriod.setVisibility(View.VISIBLE);
                // make time and date variables
                String tmpBeginEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                String tmpBeginEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                String tmpEndEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                String tmpEndEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                int tmpEvaluationPeriodActive = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, 3600) / 3600; // make hours from seconds
                int tmpEvaluationPeriodPassiv = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 3600) / 3600; // make hours from seconds

                String textEvaluationPeriod = "";
                if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv < 2) {
                    // 0 or 1 hour for active and passiv time
                    textEvaluationPeriod = String.format(context.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive);
                }
                else if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv >= 2) {
                    // 0 or 1 hour for active and more than one hour for passiv time
                    textEvaluationPeriod = String.format(context.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                }
                else if (tmpEvaluationPeriodActive >= 2 && tmpEvaluationPeriodPassiv < 2) {
                    // more than one hour for active  and 0 or 1 hour for passiv time
                    textEvaluationPeriod = String.format(context.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                }
                else {
                    // more than one hour for active and passiv time
                    textEvaluationPeriod = String.format(context.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                }
                textViewEvaluationPeriod.setText(textEvaluationPeriod);
            }
            else { // show gap to bottom of display
                TextView tmpGapToBottom = (TextView) view.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
                tmpGapToBottom.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // init the DB
        DBAdapter myDb = new DBAdapter(context);

        View inflatedView;

        // link to show comments
        Spanned showCommentsLinkTmp = null;
        // link to comment an arrangement
        Spanned showCommentArrangementLinkTmp = null;

        // text for new comment entry
        String tmpTextNewEntryComment = "";

        if (cursor.isFirst() ) { // listview for first element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now_first, parent, false);
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_arrangement_now, parent, false);
        }

        // put arrangement number
        TextView numberOfArrangement = (TextView) inflatedView.findViewById(R.id.listArrangementNumberText);
        String txtArrangementNumber = context.getResources().getString(R.string.showArrangementIntroText)+ " " + Integer.toString(cursor.getPosition()+1);
        numberOfArrangement.setText(txtArrangementNumber);

        // put author name
        TextView tmpTextViewAuthorNameText = (TextView) inflatedView.findViewById(R.id.listTextAuthorName);
        String tmpTextAuthorNameText = String.format(context.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
        tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

        // check if arrangement entry new?
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfArrangement = (TextView) inflatedView.findViewById(R.id.listArrangementNewArrangementText);
            String txtnewEntryOfArrangement = context.getResources().getString(R.string.newEntryText);
            newEntryOfArrangement.setText(txtnewEntryOfArrangement);
            myDb.deleteStatusNewEntryOurArrangement(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)));
        }

        // put arrangement text
        TextView textViewArrangement = (TextView) inflatedView.findViewById(R.id.listTextArrangement);
        String title = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT));
        textViewArrangement.setText(title);

        // generate link for evaluate an arrangement
        final TextView linkEvaluateAnArrangement = (TextView) inflatedView.findViewById(R.id.linkToEvaluateAnArrangement);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) { // evaluation on/off?
            // evaluation timezone expired?

            // get start time and end time for evaluation
            Long startEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsStartDateEvaluationInMills, System.currentTimeMillis());
            Long endEvaluationDate = prefs.getLong(ConstansClassOurArrangement.namePrefsEndDateEvaluationInMills, System.currentTimeMillis());

            // check if current time between borders start- and end evaluation period
            if (System.currentTimeMillis() < endEvaluationDate && System.currentTimeMillis() > startEvaluationDate ) {
                // check if current time bigger than last start point
                if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, 0)) {

                    // make link to evaluate arrangement, when evaluation is possible for this arrangement
                    if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_EVALUATE_POSSIBLE)) == 1) {
                        final Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                        evaluateLinkBuilder.scheme("smart.efb.deeplink")
                                .authority("linkin")
                                .path("ourarrangement")
                                .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                                .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition() + 1))
                                .appendQueryParameter("com", "evaluate_an_arrangement");

                        final String tmpLinkTextForEvaluationActive = context.getResources().getString(context.getResources().getIdentifier("ourArrangementEvaluateStringNextPassivPeriod", "string", context.getPackageName()));
                        final String tmpTextEvaluationModulSwitchOnOff = context.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOff);

                        // show time until next evaluation period
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Integer pausePeriod = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluateActiveTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                        Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis()));
                        // start the timer with the calculated milliseconds
                        if (runTimeForTimer > 0 && runTimeForTimer <= pausePeriod) {
                            new CountDownTimer(runTimeForTimer, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    // gernate count down timer
                                    String FORMAT = "%02d:%02d:%02d";
                                    String tmpTime = String.format(FORMAT,
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                    // put count down to string
                                    String tmpCountdownTimerString = String.format(tmpLinkTextForEvaluationActive, tmpTime);
                                    // generate link for output
                                    Spanned tmpCountdownTimerLink = Html.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + tmpCountdownTimerString + "</a>");

                                    // and set to textview
                                    linkEvaluateAnArrangement.setText(tmpCountdownTimerLink);
                                    linkEvaluateAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());
                                }

                                public void onFinish() {
                                    // change text to evaluation modul will switch off!
                                    linkEvaluateAnArrangement.setText(tmpTextEvaluationModulSwitchOnOff);
                                }
                            }.start();
                        } else { // error in timer -> hide timer text
                            linkEvaluateAnArrangement.setVisibility(View.GONE);
                        }

                    } else { // link is not possible, pause period, so do it with text

                        final String tmpTextNextEvaluationPeriod = context.getResources().getString(R.string.ourArrangementEvaluateStringNextActivePeriod);
                        final String tmpTextEvaluationModulSwitchOnOff = context.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOn);
                        final String tmpTextEvaluationArrangementAlreadyEvaluated = context.getResources().getString(R.string.ourArrangementEvaluateTextArrangementAlreadyEvaluated);

                        // show time until next evaluation period
                        // calculate run time for timer in MILLISECONDS!!!
                        Long nowTime = System.currentTimeMillis();
                        Integer pausePeriod = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                        Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis()));
                        Long endPointEval = runTimeForTimer + nowTime;
                        Long startPointEval = endPointEval - pausePeriod;

                        if (cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_LAST_EVAL_TIME)) < startPointEval) {

                            // start the timer with the calculated milliseconds
                            if (runTimeForTimer > 0 && runTimeForTimer <= pausePeriod) {
                                new CountDownTimer(runTimeForTimer, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // generate count down timer
                                        String FORMAT = "%02d:%02d:%02d";
                                        String tmpTime = String.format(FORMAT,
                                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                        // put count down to string
                                        Spanned tmpCountdownTimerString = Html.fromHtml(String.format(tmpTextNextEvaluationPeriod, tmpTime));

                                        // and set to textview
                                        linkEvaluateAnArrangement.setText(tmpCountdownTimerString);
                                        linkEvaluateAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());
                                    }

                                    public void onFinish() {
                                        // change text to evaluation modul will switch on!
                                        linkEvaluateAnArrangement.setText(tmpTextEvaluationModulSwitchOnOff);
                                    }
                                }.start();
                            } else { // error in timer -> hide timer text
                                linkEvaluateAnArrangement.setVisibility(View.GONE);
                            }
                        } else {
                            // change text to evaluation
                            linkEvaluateAnArrangement.setText(tmpTextEvaluationArrangementAlreadyEvaluated);
                        }
                    }
                }
                else { // error current time is not bigger than last start point -> show evaluation modul will be switched on
                    String tmpEvaluationErrorModulSwitchedOn = context.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOn);
                    linkEvaluateAnArrangement.setText(tmpEvaluationErrorModulSwitchedOn);
                }
            }
            else { // evaluation time expired!
                String tmpEvaluationPeriodExpired = context.getResources().getString(R.string.ourArrangementEvaluatePeriodExpired);
                linkEvaluateAnArrangement.setText(tmpEvaluationPeriodExpired);
            }
        }
        else { // evaluation not possible/ deactivated
            linkEvaluateAnArrangement.setVisibility(View.GONE);
        }

        // Show link for comment an arrangement and to show all comments for an arrangement
        TextView linkCommentAnArrangement = (TextView) inflatedView.findViewById(R.id.linkCommentAnArrangement);
        TextView linkShowCommentOfArrangement = (TextView) inflatedView.findViewById(R.id.linkToShowCommentsOfArrangements);

        if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false)) {

            // get from DB  all comments for choosen arrangement (getCount)
            Cursor cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID)), "descending");
            // generate the number of comments to show
            String tmpCountComments;
            int tmpIntCountComments = cursorArrangementAllComments.getCount();
            if (cursorArrangementAllComments.getCount() > 10) {
                tmpCountComments = numberCountForComments[11];

            }
            else {
                tmpCountComments = numberCountForComments[cursorArrangementAllComments.getCount()];
            }

            // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
            if (cursorArrangementAllComments.getCount() > 0) {
                cursorArrangementAllComments.moveToFirst();
                if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                    tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(context, R.color.text_accent_color) + "'>"+ context.getResources().getString(R.string.newEntryText) + "</font>";
                }
            }

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

            // make link to comment arrangement
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "comment_an_arrangement");

            // make link to show comment for arrangement
            Uri.Builder showCommentLinkBuilder = new Uri.Builder();
            showCommentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourarrangement")
                    .appendQueryParameter("db_id", Integer.toString(cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_SERVER_ID))))
                    .appendQueryParameter("arr_num", Integer.toString(cursor.getPosition()+1))
                    .appendQueryParameter("com", "show_comment_for_arrangement");


            if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) || !commentLimitationBorder) {
                showCommentArrangementLinkTmp = Html.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName())) + " " + tmpNumberCommentsPossible + "</a>");
            }
            else {
                showCommentArrangementLinkTmp = Html.fromHtml(context.getResources().getString(context.getResources().getIdentifier("ourArrangementCommentString", "string", context.getPackageName()))+ " " + tmpNumberCommentsPossible);
            }
            linkCommentAnArrangement.setText(showCommentArrangementLinkTmp);
            linkCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());


            if (tmpIntCountComments == 0) {
                showCommentsLinkTmp = Html.fromHtml(tmpCountComments);
            }
            else {
                showCommentsLinkTmp = Html.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment);
            }
            linkShowCommentOfArrangement.setText(showCommentsLinkTmp);
            linkShowCommentOfArrangement.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else { // comment and show comment are deactivated -> hide them
            linkCommentAnArrangement.setVisibility(View.GONE);
            linkShowCommentOfArrangement.setVisibility(View.GONE);
        }

        // close DB connection
        myDb.close();

        return inflatedView;

    }


    // Turn off view recycling in listview, because there are different views (first, normal)
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

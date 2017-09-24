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
 * Created by ich on 28.10.2016.
 */
public class OurGoalsShowCommentJointlyGoalsCursorAdapter extends CursorAdapter {


    private LayoutInflater cursorInflater;

    final Context contextForActivity;

    // DB-Id of jointly goal
    int jointlyGoalDbIdToShow = 0;

    // jointly goal number in list view of fragment show jointly goal now
    int jointlyGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // cursor for the choosen goal
    Cursor choosenJointlyGoal;

    // reference to the DB
    private DBAdapter myDb;

    // shared prefs for the comment jointly goals
    SharedPreferences prefs;

    // count headline number for comments
    int countCommentHeadlineNumber = 0;


    // own constructor!!!
    public OurGoalsShowCommentJointlyGoalsCursorAdapter(Context context, Cursor cursor, int flags, int dbId, int numberInLIst, Boolean commentsLimitation, Cursor goal) {

        super(context, cursor, flags);
        
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for jointly goal
        jointlyGoalDbIdToShow = dbId;

        // set jointly goal number in list view
        jointlyGoalNumberInListView = numberInLIst;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choosen jointly goal text
        choosenJointlyGoal = goal;

        // init the DB
        myDb = new DBAdapter(context);

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // is cursor position first?
        if (cursor.isFirst()) {

            // set text intro "Ziel ..."
            TextView textViewShowGoalIntro = (TextView) view.findViewById(R.id.goalsShowJointlyGoalIntro);
            String txtGoalIntro = contextForActivity.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + jointlyGoalNumberInListView;
            textViewShowGoalIntro.setText(txtGoalIntro);

            // textview for the author of goals
            TextView tmpTextViewAuthorNameText = (TextView) view.findViewById(R.id.textAuthorName);
            String tmpTextAuthorNameText = String.format(view.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), choosenJointlyGoal.getString(choosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
            tmpTextViewAuthorNameText.setText(Html.fromHtml(tmpTextAuthorNameText));

            // make link back to show jointly goals
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", "0")
                    .appendQueryParameter("arr_num", "0")
                    .appendQueryParameter("com", "show_jointly_goals_now");
            TextView linkShowCommentBackLink = (TextView) view.findViewById(R.id.goalShowCommentBackLink);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">"+context.getResources().getString(context.getResources().getIdentifier("ourGoalsBackLinkToJointlyGoals", "string", context.getPackageName()))+"</a>");
            linkShowCommentBackLink.setText(tmpBackLink);
            linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

            // show choosen goal
            TextView textViewShowChoosenGoal = (TextView) view.findViewById(R.id.choosenGoal);
            textViewShowChoosenGoal.setText(choosenJointlyGoal.getString(choosenJointlyGoal.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_KEY_GOAL)));

            // show hint sharing is disable
            if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 0) {
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

                    Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("com","show_jointly_goals_now");
                    contextForActivity.startActivity(intent); 

                }
            });


            // textview for max comments, count comments and max letters
            TextView textViewMaxAndCount = (TextView) view.findViewById(R.id.infoShowCommentMaxAndCount);
            String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
            // build text element max comment
            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) == 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextJointlyGoalsCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) > 1 && commentLimitationBorder) {
                tmpInfoTextMaxSingluarPluaral = String.format(context.getString(R.string.infoTextJointlyGoalsCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
            }
            else {
                tmpInfoTextMaxSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentUnlimitedText);
            }

            // build text element count comment
            if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 0) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentCountZero);
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 1) {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentCountSingular);
            }
            else {
                tmpInfoTextCountSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentCountPlural);
            }
            tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0));

            // build text element delay time
            String tmpInfoTextDelaytimeSingluarPluaral = "";
            if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 0) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentDelaytimeNoDelay);
            }
            else if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 1) {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentDelaytimeSingular);
            }
            else {
                tmpInfoTextDelaytimeSingluarPluaral = context.getString(R.string.infoTextJointlyGoalsCommentDelaytimePlural);
                tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0));

            }

            // generate text comment max letters
            tmpInfoTextCommentMaxLetters =  context.getString(R.string.infoTextJointlyGoalsCommentCommentMaxLettersAndDelaytime);
            tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, 0));

            // show info text
            textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);

        }
    }


    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        final Context context = mContext;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_first, parent, false);
            countCommentHeadlineNumber = 1;
        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_firstandlast, parent, false);
            countCommentHeadlineNumber++;
        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_last, parent, false);
            countCommentHeadlineNumber++;
        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_our_goals_show_comment_jointly_goal_normal, parent, false);
            countCommentHeadlineNumber++;
        }

        // set comment information to the view
        // set comment headline
        TextView textViewShowActualCommentHeadline = (TextView) inflatedView.findViewById(R.id.actualCommentInfoText);
        String actualCommentHeadline = context.getResources().getString(R.string.showJointlyGoalsCommentHeadlineWithNumber) + " " + countCommentHeadlineNumber;
        if (cursor.isFirst()) { // set text newest comment
            actualCommentHeadline = actualCommentHeadline + " " + context.getResources().getString(R.string.showJointlyGoalsCommentHeadlineWithNumberExtraNewest);
        }
        textViewShowActualCommentHeadline.setText(actualCommentHeadline);

        // check if jointly goal comment entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_NEW_ENTRY)) == 1) {
            TextView newEntryOfJointlyGoalComment = (TextView) inflatedView.findViewById(R.id.actualCommentNewInfoText);
            String txtnewEntryOfJointlyGoalComment = context.getResources().getString(R.string.newEntryText);
            newEntryOfJointlyGoalComment.setText(txtnewEntryOfJointlyGoalComment);

            // delet status new entry in db
            myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(cursor.getInt(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }

        // textview for the author and date
        TextView tmpTextViewAuthorNameLastActualComment = (TextView) inflatedView.findViewById(R.id.textAuthorNameActualComment);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_AUTHOR_NAME));
        if (tmpAuthorName.equals(prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"))) {
            tmpAuthorName = context.getResources().getString(R.string.ourJointlyGoalsShowCommentPersonalAuthorName);
        }
        String commentDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME)), "dd.MM.yyyy");;
        String commentTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME)), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = String.format(context.getResources().getString(R.string.ourJointlyGoalsShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        tmpTextViewAuthorNameLastActualComment.setText(Html.fromHtml(tmpTextAuthorNameLastActualComment));

        // textview for status 0 of the last actual comment
        final TextView tmpTextViewSendInfoLastActualComment = (TextView) inflatedView.findViewById(R.id.textSendInfoActualComment);
        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 0) {

            String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsShowCommentSendInfo);
            tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
            tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

        } else if (cursor.getInt(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_STATUS)) == 1) {
            // textview for status 1 of the last actual comment


            // check, sharing of comments enable?
            if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 1) {

                // set textview visible
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                // calculate run time for timer in MILLISECONDS!!!
                Long nowTime = System.currentTimeMillis();
                Long writeTimeComment = cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME));
                Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) * 60000; // make milliseconds from miutes
                Long runTimeForTimer = delayTime - (nowTime - writeTimeComment);
                // start the timer with the calculated milliseconds
                if (runTimeForTimer > 0) {
                    new CountDownTimer(runTimeForTimer, 1000) {
                        public void onTick(long millisUntilFinished) {
                            // gernate count down timer
                            String FORMAT = "%02d:%02d:%02d";
                            String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsShowCommentSendDelayInfo);
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
                            String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                            tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        }
                    }.start();

                } else {
                    // no count down anymore -> show send successfull
                    String tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                    tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                }
            }
            else { // sharing of comments is disable! -> show text

                String tmpTextSendInfoLastActualComment = "";
                tmpTextViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                if (prefs.getLong(ConstansClassOurGoals.namePrefsJointlyCommentShareChangeTime, 0) < cursor.getLong(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_WRITE_TIME))) {
                    // show send successfull, but no sharing
                    tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsCommentSendInfoSharingDisable);
                }
                else {
                    // show send successfull
                    tmpTextSendInfoLastActualComment = context.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                }
                tmpTextViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
            }

        }

        // show actual comment
        TextView textViewShowActualComment = (TextView) inflatedView.findViewById(R.id.listActualTextComment);
        String actualComment = cursor.getString(cursor.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_GOALS_COMMENT_KEY_COMMENT));
        textViewShowActualComment.setText(actualComment);

        // generate difference text for comment anymore
        String tmpNumberCommentsPossible;
        int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0);
        if (commentLimitationBorder) {
            if (tmpDifferenceComments > 0) {
                if (tmpDifferenceComments > 1) { //plural comments
                    tmpNumberCommentsPossible = String.format(context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossiblePlural), tmpDifferenceComments);
                } else { // singular comments
                    tmpNumberCommentsPossible = String.format(context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleSingular), tmpDifferenceComments);
                }
            }
            else {
                tmpNumberCommentsPossible = context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoMore);
            }
        }
        else {
            tmpNumberCommentsPossible = context.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoBorder);
        }

        // make link to comment a jointly goal only when comments possible
        TextView linkToCommentAGoal = (TextView) inflatedView.findViewById(R.id.linkToCommentAGoal);
        if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment,0) || !commentLimitationBorder) {
            Uri.Builder commentLinkBuilder = new Uri.Builder();
            commentLinkBuilder.scheme("smart.efb.deeplink")
                    .authority("linkin")
                    .path("ourgoals")
                    .appendQueryParameter("db_id", Integer.toString(jointlyGoalDbIdToShow))
                    .appendQueryParameter("arr_num", Integer.toString(jointlyGoalNumberInListView))
                    .appendQueryParameter("com", "comment_an_jointly_goal");
            String tmpLinkTextForCommentAGoal = String.format(context.getResources().getString(context.getResources().getIdentifier("ourJointlyGoalsShowCommentLinkToCommentAGoal", "string", context.getPackageName())), jointlyGoalNumberInListView);
            Spanned tmpBackLink = Html.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextForCommentAGoal + " " + tmpNumberCommentsPossible + "</a>");
            linkToCommentAGoal.setText(tmpBackLink);
            linkToCommentAGoal.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            linkToCommentAGoal.setVisibility(View.GONE);
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

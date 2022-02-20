package de.smart_efb.efbapp.smartefb;

import android.content.Context;
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
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class OurGoalsJointlyGoalsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    final Context contextForActivity;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for goals (12 numbers!)
    private String[] numberCountForComments = new String [12];

    // Array list for jointly goals
    ArrayList<ObjectSmartEFBGoals> jointlyGoalsListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurGoalsJointlyGoalsRecyclerViewAdapter(Context context, ArrayList<ObjectSmartEFBGoals> goalsList, int flags) {

        // context of activity OurGoals
        contextForActivity = context;

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourGoalsCountComments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurGoals)context).isCommentLimitationBorderSet("jointlyGoals");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of goals
        jointlyGoalsListForRecyclerView = goalsList;

        // init view has item and footer
        STATE = FOOTER;

    }


    // inner class for footer element
    private class OurGoalsJointlyGoalsFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewBorderBetweenElementAndFooterNothingElse, textViewBorderBetweenElementAndFooter, textViewInfoTextForEvaluationTimePeriod;

        OurGoalsJointlyGoalsFooterViewHolder(View footerView) {

            super (footerView);

            textViewBorderBetweenElementAndFooterNothingElse = footerView.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
            textViewBorderBetweenElementAndFooter = footerView.findViewById(R.id.borderBetweenLastElementAndEvaluationInfo);
            textViewInfoTextForEvaluationTimePeriod = footerView.findViewById(R.id.infoEvaluationTimePeriod);
        }
    }


    // inner class for item element
    private class OurGoalsJointlyGoalsItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNumberOfGoal, textViewNewSignalForGoal, textViewAuthorNameForGoal;
        public TextView textViewJointlyGoalText, textViewLinkForCommentAnJointlyGoal, textViewLinkForShowCommentOfOneJointlyGoal, textViewLinkForEvaluationOfAnJointlyGoal;

        OurGoalsJointlyGoalsItemViewHolder(View itemView) {

            super(itemView);

            textViewNumberOfGoal = itemView.findViewById(R.id.listGoalsNumberText);
            textViewNewSignalForGoal = itemView.findViewById(R.id.listGoalsNewGoalText);
            textViewAuthorNameForGoal = itemView.findViewById(R.id.listTextAuthorName);
            textViewJointlyGoalText = itemView.findViewById(R.id.listTextGoal);
            textViewLinkForCommentAnJointlyGoal = itemView.findViewById(R.id.linkCommentAnJointlyGoal);
            textViewLinkForShowCommentOfOneJointlyGoal = itemView.findViewById(R.id.linkToShowCommentsOfJointlyGoals);
            textViewLinkForEvaluationOfAnJointlyGoal = itemView.findViewById(R.id.linkToEvaluateAnGoal);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_jointly_goals_recyclerview_footer, parent, false));
        }

        return new OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_jointly_goals_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (jointlyGoalsListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (jointlyGoalsListForRecyclerView.size()) ) {
                    return FOOTER;
                }
                return ITEM;
            default:
                return ITEM;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();

        // init the DB
        final DBAdapter myDb = new DBAdapter(contextForActivity);

        switch (viewType) {
            case ITEM:

                final ObjectSmartEFBGoals goalsElement = jointlyGoalsListForRecyclerView.get(position);

                // cast holder
                OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsItemViewHolder itemView = (OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsItemViewHolder) holder;

                // put goals number
                int goalsElementPositionNumber = 0;
                if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfJointlyGoalsList, "descending").equals("descending")) {
                    goalsElementPositionNumber = jointlyGoalsListForRecyclerView.size() - goalsElement.getPositionNumber() + 1;
                }
                else {
                    goalsElementPositionNumber = goalsElement.getPositionNumber();
                }
                String txtGoalsNumber = contextForActivity.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + goalsElementPositionNumber;
                itemView.textViewNumberOfGoal .setText(txtGoalsNumber);

                // put author name
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), goalsElement.getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
                itemView.textViewAuthorNameForGoal.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // check if goal entry new?
                if (goalsElement.getNewEntry() == 1) {
                    itemView.textViewNewSignalForGoal.setVisibility(View.VISIBLE);
                    myDb.deleteStatusNewEntryOurGoals(goalsElement.getRowID());
                }

                // put goal text
                itemView.textViewJointlyGoalText.setText(goalsElement.getGoalText());

                // generate link for evaluate a goal
                final TextView linkEvaluateAGoal = itemView.textViewLinkForEvaluationOfAnJointlyGoal;

                if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) { // evaluation on/off?
                    // evaluation timezone expired?

                    // get start time and end time for evaluation
                    Long startEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());
                    Long endEvaluationDate = prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis());

                    // check if current time between borders start- and end evaluation period
                    if (System.currentTimeMillis() < endEvaluationDate && System.currentTimeMillis() > startEvaluationDate ) {
                        // check if current time bigger than last start point
                        if (System.currentTimeMillis() >= prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, 0)) {

                            // make link to evaluate goal, when evaluation is possible for this goal
                            if (goalsElement.getEvaluatePossible() == 1) {
                                final Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                                evaluateLinkBuilder.scheme("smart.efb.deeplink")
                                        .authority("linkin")
                                        .path("ourgoals")
                                        .appendQueryParameter("db_id", Integer.toString(goalsElement.getServerIdGoal()))
                                        .appendQueryParameter("arr_num", Integer.toString(goalsElementPositionNumber))
                                        .appendQueryParameter("com", "evaluate_an_jointly_goal");

                                final String tmpLinkTextForEvaluationActive = contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourGoalsEvaluateStringNextPassivPeriod", "string", contextForActivity.getPackageName()));
                                final String tmpTextEvaluationModulSwitchOnOff = contextForActivity.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextEvaluationModulSwitchOff);

                                // show time until next evaluation period
                                // calculate run time for timer in MILLISECONDS!!!
                                Long nowTime = System.currentTimeMillis();
                                Integer pausePeriod = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                                Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis()));
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
                                            Spanned tmpCountdownTimerLink = HtmlCompat.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + tmpCountdownTimerString + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);

                                            // and set to textview
                                            linkEvaluateAGoal.setText(tmpCountdownTimerLink);
                                            linkEvaluateAGoal.setMovementMethod(LinkMovementMethod.getInstance());
                                        }

                                        public void onFinish() {
                                            // change text to evaluation modul will switch off!
                                            linkEvaluateAGoal.setText(tmpTextEvaluationModulSwitchOnOff);
                                        }
                                    }.start();
                                } else { // error in timer -> hide timer text
                                    linkEvaluateAGoal.setVisibility(View.GONE);
                                }

                            } else { // link is not possible, pause period, so do it with text

                                final String tmpTextNextEvaluationPeriod = contextForActivity.getResources().getString(R.string.ourGoalsEvaluateStringNextActivePeriod);
                                final String tmpTextEvaluationModulSwitchOnOff = contextForActivity.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextEvaluationModulSwitchOn);
                                final String tmpTextEvaluationJointlyGoalAlreadyEvaluated = contextForActivity.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextGoalAlreadyEvaluated);

                                // show time until next evaluation period
                                // calculate run time for timer in MILLISECONDS!!!
                                Long nowTime = System.currentTimeMillis();
                                Integer pausePeriod = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                                Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurGoals.namePrefsStartPointJointlyGoalsEvaluationPeriodInMills, System.currentTimeMillis()));
                                Long endPointEval = runTimeForTimer + nowTime;
                                Long startPointEval = endPointEval - pausePeriod;

                                if (goalsElement.getLastEvalTime() < startPointEval) {

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
                                                linkEvaluateAGoal.setText(tmpCountdownTimerString);
                                                linkEvaluateAGoal.setMovementMethod(LinkMovementMethod.getInstance());
                                            }

                                            public void onFinish() {
                                                // change text to evaluation modul will switch on!
                                                linkEvaluateAGoal.setText(tmpTextEvaluationModulSwitchOnOff);
                                            }
                                        }.start();
                                    } else { // error in timer -> hide timer text
                                        linkEvaluateAGoal.setVisibility(View.GONE);
                                    }
                                } else {
                                    // change text to evaluation
                                    linkEvaluateAGoal.setText(tmpTextEvaluationJointlyGoalAlreadyEvaluated);
                                }
                            }
                        }
                        else { // error current time is not bigger than last start point -> show evaluation modul will be switched on
                            String tmpEvaluationErrorModulSwitchedOn = contextForActivity.getResources().getString(R.string.evaluateJointlyGoalEvaluateTextEvaluationModulSwitchOn);
                            linkEvaluateAGoal.setText(tmpEvaluationErrorModulSwitchedOn);
                        }
                    }
                    else { // evaluation time expired!
                        String tmpEvaluationPeriodExpired = contextForActivity.getResources().getString(R.string.evaluateJointlyGoalEvaluatePeriodExpired);
                        linkEvaluateAGoal.setText(tmpEvaluationPeriodExpired);
                    }
                }
                else { // evaluation not possible/ deactivated
                    linkEvaluateAGoal.setVisibility(View.GONE);
                }

                // generate link to comment a goal
                if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkCommentJointlyGoals, false)) {

                    // get from DB  all comments for choosen jointly goal (getCount)
                    Cursor cursorJointlyGoalAllComments = myDb.getAllRowsOurGoalsJointlyGoalsComment(goalsElement.getServerIdGoal(), "descending", 0);
                    // generate the number of comments to show
                    String tmpCountComments;
                    int tmpIntCountComments = cursorJointlyGoalAllComments.getCount();
                    if (cursorJointlyGoalAllComments.getCount() > 10) {
                        tmpCountComments = numberCountForComments[11];
                    }
                    else {
                        tmpCountComments = numberCountForComments[cursorJointlyGoalAllComments.getCount()];
                    }

                    // check comments for new entry, the cursor is sorted DESC, so first element is newest!!! new entry is markt by == 1
                    String tmpTextNewEntryComment = "";
                    if (cursorJointlyGoalAllComments.getCount() > 0) {
                        cursorJointlyGoalAllComments.moveToFirst();
                        if (cursorJointlyGoalAllComments.getInt(cursorJointlyGoalAllComments.getColumnIndex(DBAdapter.OUR_GOALS_JOINTLY_DEBETABLE_GOALS_NEW_ENTRY)) == 1) {
                            tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(contextForActivity, R.color.text_accent_color) + "'>"+ contextForActivity.getResources().getString(R.string.newEntryText) + "</font>";
                        }
                    }

                    // generate difference text for comment anymore
                    String tmpNumberCommentsPossible;
                    int tmpDifferenceComments = prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0);
                    if (commentLimitationBorder) {
                        if (tmpDifferenceComments > 0) {
                            if (tmpDifferenceComments > 1) { //plural comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossiblePlural), tmpDifferenceComments);
                            } else { // singular comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleSingular), tmpDifferenceComments);
                            }
                        }
                        else {
                            tmpNumberCommentsPossible = contextForActivity.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoMore);
                        }
                    }
                    else {
                        tmpNumberCommentsPossible = contextForActivity.getString(R.string.ourGoalsInfoTextNumberOfCommentsPossibleNoBorder);
                    }

                    // make link to comment a goal
                    Uri.Builder commentLinkBuilder = new Uri.Builder();
                    commentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourgoals")
                            .appendQueryParameter("db_id", Integer.toString(goalsElement.getServerIdGoal()))
                            .appendQueryParameter("arr_num", Integer.toString(goalsElementPositionNumber))
                            .appendQueryParameter("com", "comment_an_jointly_goal");

                    // make link to show comment for goal
                    Uri.Builder showCommentLinkBuilder = new Uri.Builder();
                    showCommentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourgoals")
                            .appendQueryParameter("db_id", Integer.toString(goalsElement.getServerIdGoal()))
                            .appendQueryParameter("arr_num", Integer.toString(goalsElementPositionNumber))
                            .appendQueryParameter("com", "show_comment_for_jointly_goal");

                    Spanned showCommentJointlyGoalLinkTmp;
                    if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment,0) < prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment,0) || !commentLimitationBorder) {
                        showCommentJointlyGoalLinkTmp = HtmlCompat.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">" + contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourGoalsCommentString", "string", contextForActivity.getPackageName())) + " " + tmpNumberCommentsPossible + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentJointlyGoalLinkTmp = HtmlCompat.fromHtml(contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourGoalsCommentString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForCommentAnJointlyGoal.setText(showCommentJointlyGoalLinkTmp);
                    itemView.textViewLinkForCommentAnJointlyGoal.setMovementMethod(LinkMovementMethod.getInstance());

                    Spanned showCommentsLinkTmp;
                    if (tmpIntCountComments == 0) {
                        showCommentsLinkTmp = HtmlCompat.fromHtml(tmpCountComments, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentsLinkTmp = HtmlCompat.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForShowCommentOfOneJointlyGoal.setText(showCommentsLinkTmp);
                    itemView.textViewLinkForShowCommentOfOneJointlyGoal.setMovementMethod(LinkMovementMethod.getInstance());
                }
                else { // comment and show comment are deactivated -> hide them
                    itemView.textViewLinkForShowCommentOfOneJointlyGoal.setVisibility(View.GONE);
                    itemView.textViewLinkForCommentAnJointlyGoal.setVisibility(View.GONE);
                }

                break;
            case HEADER:

                break;
            case FOOTER:

                // cast holder
                OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsFooterViewHolder footerView = (OurGoalsJointlyGoalsRecyclerViewAdapter.OurGoalsJointlyGoalsFooterViewHolder) holder;

                // show and generate info text evaluation period
                if (prefs.getBoolean(ConstansClassOurGoals.namePrefsShowLinkEvaluateJointlyGoals, false)) { // show info of evaluation period when activated
                    // set info text evaluation period visible
                    footerView.textViewBorderBetweenElementAndFooter.setVisibility(View.VISIBLE);
                    footerView.textViewInfoTextForEvaluationTimePeriod.setVisibility(View.VISIBLE);

                    // make time and date variables
                    String tmpBeginEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                    String tmpBeginEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsStartDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                    String tmpEndEvaluationDate = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "dd.MM.yyyy");
                    String tmpEndEvaluatioTime = EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsEndDateJointlyGoalsEvaluationInMills, System.currentTimeMillis()), "HH:mm");
                    int tmpEvaluationPeriodActive = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsActiveTimeInSeconds, 3600) / 3600; // make hours from seconds
                    int tmpEvaluationPeriodPassiv = prefs.getInt(ConstansClassOurGoals.namePrefsEvaluateJointlyGoalsPauseTimeInSeconds, 3600) / 3600; // make hours from seconds

                    String textEvaluationPeriod = "";
                    if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv < 2) {
                        // 0 or 1 hour for active and passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodSingularSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive);
                    }
                    else if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv >= 2) {
                        // 0 or 1 hour for active and more than one hour for passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodSingularPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                    }
                    else if (tmpEvaluationPeriodActive >= 2 && tmpEvaluationPeriodPassiv < 2) {
                        // more than one hour for active  and 0 or 1 hour for passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodPluralSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                    }
                    else {
                        // more than one hour for active and passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.evaluateJointlyGoalInfoEvaluationPeriodPluralPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                    }
                    footerView.textViewInfoTextForEvaluationTimePeriod.setText(textEvaluationPeriod);
                }
                else { // show gap to bottom of display
                    footerView.textViewBorderBetweenElementAndFooterNothingElse.setVisibility(View.VISIBLE);
                }

                break;

            default:
                break;
        }

        // close DB connection
        myDb.close();

    }


    @Override
    public int getItemCount() {
        switch (STATE) {
            case BOTH:
                // if both, return 2 additional for header & footer Resource
                return jointlyGoalsListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return jointlyGoalsListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return jointlyGoalsListForRecyclerView.size() + 1;
        }
    }


}

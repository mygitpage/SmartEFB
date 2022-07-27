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

public class OurArrangementNowArrangementRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    final Context contextForActivity;

    //limitation in count comments true-> yes, there is a border; no, there is no border
    Boolean commentLimitationBorder;

    // for prefs
    SharedPreferences prefs;

    // number for count comments for arrangement (12 numbers!)
    private String[] numberCountForComments = new String [12];

    // Array list for now arrangements
    ArrayList<ObjectSmartEFBArrangement> nowArrangementListForRecyclerView;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurArrangementNowArrangementRecyclerViewAdapter(Context context, ArrayList<ObjectSmartEFBArrangement> arrangementList, int flags) {

        // context of activity OurArrangement
        contextForActivity = context;

        // init array for count comments
        numberCountForComments = context.getResources().getStringArray(R.array.ourArrangementCountComments);

        //limitation in count comments true-> yes, there is a border; no, there is no border
        commentLimitationBorder = ((ActivityOurArrangement)context).isCommentLimitationBorderSet("current");

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // copy array list of arrangements
        nowArrangementListForRecyclerView = arrangementList;

        // init view has item and footer
        STATE = FOOTER;

    }


    // inner class for footer element
    private class OurArrangementNowArrangementFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewBorderBetweenElementAndFooterNothingElse, textViewBorderBetweenElementAndFooter, textViewInfoTextForEvaluationTimePeriod;

        OurArrangementNowArrangementFooterViewHolder(View footerView) {

            super (footerView);
            
            textViewBorderBetweenElementAndFooterNothingElse = footerView.findViewById(R.id.borderToBottomOfDisplayWhenNeeded);
            textViewBorderBetweenElementAndFooter = footerView.findViewById(R.id.borderBetweenLastElementAndEvaluationInfo);
            textViewInfoTextForEvaluationTimePeriod = footerView.findViewById(R.id.infoEvaluationTimePeriod);
         }
    }
    
    
    // inner class for item element
    private class OurArrangementNowArrangementItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNumberOfArrangement, textViewNewSignalForArrangement, textViewAuthorNameForArrangement;
        public TextView textViewArrangementText, textViewLinkForCommentAnArrangement, textViewLinkForShowCommentOfOneArrangement, textViewLinkForEvaluationOfAnArrangement;

        OurArrangementNowArrangementItemViewHolder(View itemView) {

            super(itemView);

            textViewNumberOfArrangement = itemView.findViewById(R.id.listArrangementNumberText);
            textViewNewSignalForArrangement = itemView.findViewById(R.id.listArrangementNewArrangementText);
            textViewAuthorNameForArrangement = itemView.findViewById(R.id.listTextAuthorName);
            textViewArrangementText = itemView.findViewById(R.id.listTextArrangement);
            textViewLinkForCommentAnArrangement = itemView.findViewById(R.id.linkCommentAnArrangement);
            textViewLinkForShowCommentOfOneArrangement = itemView.findViewById(R.id.linkToShowCommentsOfArrangements);
            textViewLinkForEvaluationOfAnArrangement = itemView.findViewById(R.id.linkToEvaluateAnArrangement);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_now_arrangement_recyclerview_footer, parent, false));
        }
        
        return new OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_now_arrangement_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (nowArrangementListForRecyclerView.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                if (position >= (nowArrangementListForRecyclerView.size()) ) {
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

                final ObjectSmartEFBArrangement arrangementElement = nowArrangementListForRecyclerView.get(position);

                // cast holder
                OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementItemViewHolder itemView = (OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementItemViewHolder) holder;

                // put arrangement number
                int arrangementElementPositionNumber = 0;
                if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementNowList, "descending").equals("descending")) {
                    arrangementElementPositionNumber = nowArrangementListForRecyclerView.size() - arrangementElement.getPositionNumber() + 1;
                }
                else {
                    arrangementElementPositionNumber = arrangementElement.getPositionNumber();
                }
                String txtArrangementNumber = contextForActivity.getResources().getString(R.string.showArrangementIntroText)+ " " + arrangementElementPositionNumber;
                itemView.textViewNumberOfArrangement .setText(txtArrangementNumber);

                // put author name
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), arrangementElement.getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
                itemView.textViewAuthorNameForArrangement.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // check if arrangement entry new?
               if (arrangementElement.getNewEntry() == 1) {
                    itemView.textViewNewSignalForArrangement.setVisibility(View.VISIBLE);
                    myDb.deleteStatusNewEntryOurArrangement(arrangementElement.getRowID());
                }

                // put arrangement text
                itemView.textViewArrangementText.setText(arrangementElement.getArrangementText());

                // generate link for evaluate an arrangement
                final TextView linkEvaluateAnArrangement = itemView.textViewLinkForEvaluationOfAnArrangement;

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
                            if (arrangementElement.getEvaluatePossible() == 1) {
                                final Uri.Builder evaluateLinkBuilder = new Uri.Builder();
                                evaluateLinkBuilder.scheme("smart.efb.deeplink")
                                        .authority("linkin")
                                        .path("ourarrangement")
                                        .appendQueryParameter("db_id", Integer.toString(arrangementElement.getServerIdArrangement()))
                                        .appendQueryParameter("arr_num", Integer.toString(arrangementElementPositionNumber))
                                        .appendQueryParameter("com", "evaluate_an_arrangement");

                                final String tmpLinkTextForEvaluationActive = contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementEvaluateStringNextPassivPeriod", "string", contextForActivity.getPackageName()));
                                final String tmpTextEvaluationModulSwitchOnOff = contextForActivity.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOff);

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
                                            Spanned tmpCountdownTimerLink = HtmlCompat.fromHtml("<a href=\"" + evaluateLinkBuilder.build().toString() + "\">" + tmpCountdownTimerString + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);

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

                                final String tmpTextNextEvaluationPeriod = contextForActivity.getResources().getString(R.string.ourArrangementEvaluateStringNextActivePeriod);
                                final String tmpTextEvaluationModulSwitchOnOff = contextForActivity.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOn);
                                final String tmpTextEvaluationArrangementAlreadyEvaluated = contextForActivity.getResources().getString(R.string.ourArrangementEvaluateTextArrangementAlreadyEvaluated);

                                // show time until next evaluation period
                                // calculate run time for timer in MILLISECONDS!!!
                                Long nowTime = System.currentTimeMillis();
                                Integer pausePeriod = prefs.getInt(ConstansClassOurArrangement.namePrefsEvaluatePauseTimeInSeconds, 0) * 1000; // make milliseconds from seconds
                                Long runTimeForTimer = pausePeriod - (nowTime - prefs.getLong(ConstansClassOurArrangement.namePrefsStartPointEvaluationPeriodInMills, System.currentTimeMillis()));
                                Long endPointEval = runTimeForTimer + nowTime;
                                Long startPointEval = endPointEval - pausePeriod;

                                if (arrangementElement.getLastEvalTime() < startPointEval) {

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
                                                Spanned tmpCountdownTimerString = HtmlCompat.fromHtml(String.format(tmpTextNextEvaluationPeriod, tmpTime), HtmlCompat.FROM_HTML_MODE_LEGACY);

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
                            String tmpEvaluationErrorModulSwitchedOn = contextForActivity.getResources().getString(R.string.ourArrangementEvaluateTextEvaluationModulSwitchOn);
                            linkEvaluateAnArrangement.setText(tmpEvaluationErrorModulSwitchedOn);
                        }
                    }
                    else { // evaluation time expired!
                        String tmpEvaluationPeriodExpired = contextForActivity.getResources().getString(R.string.ourArrangementEvaluatePeriodExpired);
                        linkEvaluateAnArrangement.setText(tmpEvaluationPeriodExpired);
                    }
                }
                else { // evaluation not possible/ deactivated
                    linkEvaluateAnArrangement.setVisibility(View.GONE);
                }

                // generate link to comment an arrangement
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowArrangementComment, false)) {

                    // get from DB  all comments for choosen arrangement (getCount)
                    Cursor cursorArrangementAllComments = myDb.getAllRowsOurArrangementComment(arrangementElement.getServerIdArrangement(), "descending", 0);
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
                    String tmpTextNewEntryComment = "";
                    if (cursorArrangementAllComments.getCount() > 0) {
                        cursorArrangementAllComments.moveToFirst();
                        if (cursorArrangementAllComments.getInt(cursorArrangementAllComments.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_COMMENT_KEY_NEW_ENTRY)) == 1) {
                            tmpTextNewEntryComment = "<font color='"+ ContextCompat.getColor(contextForActivity, R.color.text_accent_color) + "'>"+ contextForActivity.getResources().getString(R.string.newEntryText) + "</font>";
                        }
                    }

                    // generate difference text for comment anymore
                    String tmpNumberCommentsPossible;
                    int tmpDifferenceComments = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0);
                    if (commentLimitationBorder) {
                        if (tmpDifferenceComments > 0) {
                            if (tmpDifferenceComments > 1) { //plural comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfCommentsPossiblePlural), tmpDifferenceComments);
                            } else { // singular comments
                                tmpNumberCommentsPossible = String.format(contextForActivity.getString(R.string.infoTextNumberOfCommentsPossibleSingular), tmpDifferenceComments);
                            }
                        }
                        else {
                            tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfCommentsPossibleNoMore);
                        }
                    }
                    else {
                        tmpNumberCommentsPossible = contextForActivity.getString(R.string.infoTextNumberOfCommentsPossibleNoBorder);
                    }

                    // make link to comment arrangement
                    Uri.Builder commentLinkBuilder = new Uri.Builder();
                    commentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourarrangement")
                            .appendQueryParameter("db_id", Integer.toString(arrangementElement.getServerIdArrangement()))
                            .appendQueryParameter("arr_num", Integer.toString(arrangementElementPositionNumber))
                            .appendQueryParameter("com", "comment_an_arrangement");

                    // make link to show comment for arrangement
                    Uri.Builder showCommentLinkBuilder = new Uri.Builder();
                    showCommentLinkBuilder.scheme("smart.efb.deeplink")
                            .authority("linkin")
                            .path("ourarrangement")
                            .appendQueryParameter("db_id", Integer.toString(arrangementElement.getServerIdArrangement()))
                            .appendQueryParameter("arr_num", Integer.toString(arrangementElementPositionNumber))
                            .appendQueryParameter("com", "show_comment_for_arrangement");

                    Spanned showCommentArrangementLinkTmp;
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment,0) < prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment,0) || !commentLimitationBorder) {
                        showCommentArrangementLinkTmp = HtmlCompat.fromHtml(" <a href=\"" + commentLinkBuilder.build().toString() + "\">" + contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementCommentString", "string", contextForActivity.getPackageName())) + " " + tmpNumberCommentsPossible + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentArrangementLinkTmp = HtmlCompat.fromHtml(contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementCommentString", "string", contextForActivity.getPackageName()))+ " " + tmpNumberCommentsPossible, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForCommentAnArrangement.setText(showCommentArrangementLinkTmp);
                    itemView.textViewLinkForCommentAnArrangement.setMovementMethod(LinkMovementMethod.getInstance());

                    Spanned showCommentsLinkTmp;
                    if (tmpIntCountComments == 0) {
                        showCommentsLinkTmp = HtmlCompat.fromHtml(tmpCountComments, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    else {
                        showCommentsLinkTmp = HtmlCompat.fromHtml("<a href=\"" + showCommentLinkBuilder.build().toString() + "\">" + tmpCountComments + "</a> " + tmpTextNewEntryComment, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    }
                    itemView.textViewLinkForShowCommentOfOneArrangement.setText(showCommentsLinkTmp);
                    itemView.textViewLinkForShowCommentOfOneArrangement.setMovementMethod(LinkMovementMethod.getInstance());
                }
                else { // comment and show comment are deactivated -> hide them
                    itemView.textViewLinkForShowCommentOfOneArrangement.setVisibility(View.GONE);
                    itemView.textViewLinkForCommentAnArrangement.setVisibility(View.GONE);
                }

                break;
            case HEADER:

                break;
            case FOOTER:

                // cast holder
                OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementFooterViewHolder footerView = (OurArrangementNowArrangementRecyclerViewAdapter.OurArrangementNowArrangementFooterViewHolder) holder;

                // show and generate info text evaluation period
                if (prefs.getBoolean(ConstansClassOurArrangement.namePrefsShowEvaluateArrangement, false)) { // show info of evaluation period when activated
                    // set info text evaluation period visible
                    footerView.textViewBorderBetweenElementAndFooter.setVisibility(View.VISIBLE);
                    footerView.textViewInfoTextForEvaluationTimePeriod.setVisibility(View.VISIBLE);

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
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive);
                    }
                    else if (tmpEvaluationPeriodActive < 2 && tmpEvaluationPeriodPassiv >= 2) {
                        // 0 or 1 hour for active and more than one hour for passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodSingularPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                    }
                    else if (tmpEvaluationPeriodActive >= 2 && tmpEvaluationPeriodPassiv < 2) {
                        // more than one hour for active  and 0 or 1 hour for passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralSingular), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
                    }
                    else {
                        // more than one hour for active and passiv time
                        textEvaluationPeriod = String.format(contextForActivity.getResources().getString(R.string.ourArrangementEvaluationInfoEvaluationPeriodPluralPlural), tmpBeginEvaluationDate, tmpBeginEvaluatioTime, tmpEndEvaluationDate, tmpEndEvaluatioTime, tmpEvaluationPeriodActive, tmpEvaluationPeriodPassiv);
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
                return nowArrangementListForRecyclerView.size() + 2;
            case ITEM:
                // if just items, return just the length
                return nowArrangementListForRecyclerView.size();
            default:
                // if either or, return 1 additional
                return nowArrangementListForRecyclerView.size() + 1;
        }
    }

}

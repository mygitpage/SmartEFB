package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;



public class OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // Array List of jointly goal comment
    ArrayList<ObjectSmartEFBGoalsComment> ourGoalsShowJointlyCommentList;

    // DB-Id of jointly goal
    int goalsDbIdToShow = 0;

    // goal number in list view of fragment show jointly goal
    int jointlyGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the chose goal to show the comments
    ArrayList<ObjectSmartEFBGoals> choseGoal;

    // shared prefs for the comment jointly goal
    SharedPreferences prefs;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBGoalsComment> commentList, int dbId, int numberInList, Boolean commentsLimitation, ArrayList<ObjectSmartEFBGoals> goals) {

        // Array List of goals comment
        ourGoalsShowJointlyCommentList = commentList;

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for goals
        goalsDbIdToShow = dbId;

        // set goal number in list view
        jointlyGoalNumberInListView = numberInList;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set chose goal
        choseGoal = goals;

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

        // init view has items and footer
        STATE = FOOTER;

    }


    // inner class for footer element
    private class OurGoalsShowCommentFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowGoalIntro, textViewAuthorNameText, textViewMaxAndCount, textViewShowChoseGoal;
        public Button buttonFooterBackToGoal, buttonCommentThisGoal;

        OurGoalsShowCommentFooterViewHolder(View footerView) {

            super(footerView);
            textViewMaxAndCount = footerView.findViewById(R.id.infoShowCommentMaxAndCount);
            buttonCommentThisGoal = footerView.findViewById(R.id.buttonCommentThisGoal);
            textViewShowGoalIntro = footerView.findViewById(R.id.goalsShowGoalIntro);
            textViewAuthorNameText = footerView.findViewById(R.id.textAuthorName);
            buttonFooterBackToGoal = footerView.findViewById(R.id.buttonFooterBackToGoal);
            textViewShowChoseGoal = footerView.findViewById(R.id.choseGoal);

        }
    }


    // inner class for item element
    private class OurGoalsShowCommentItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowActualCommentHeadline, newEntryOfGoal, textViewAuthorNameLastActualComment;
        public TextView textViewSendInfoLastActualComment, textViewShowActualComment;

        OurGoalsShowCommentItemViewHolder(View ItemView) {

            super (ItemView);
            textViewShowActualCommentHeadline = ItemView.findViewById(R.id.actualCommentInfoText);
            newEntryOfGoal = ItemView.findViewById(R.id.actualCommentNewInfoText);
            textViewAuthorNameLastActualComment = ItemView.findViewById(R.id.textAuthorNameActualComment);
            textViewSendInfoLastActualComment = ItemView.findViewById(R.id.textSendInfoActualComment);
            textViewShowActualComment = ItemView.findViewById(R.id.listActualTextComment);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_show_jointly_comment_recyclerview_footer, parent, false));
        }
        return new OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_show_jointly_comment_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (ourGoalsShowJointlyCommentList.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:

                if (position >= (ourGoalsShowJointlyCommentList.size()) ) {
                    return FOOTER;
                }
                return ITEM;

            default:
                return ITEM;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int actualPosition = position;
        int viewType = holder.getItemViewType();

        // init the DB
        final DBAdapter myDb = new DBAdapter(contextForActivity);

        switch (viewType) {
            case ITEM:

                final ObjectSmartEFBGoalsComment commentElement = ourGoalsShowJointlyCommentList.get(position);

                // cast holder
                final OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentItemViewHolder itemView = (OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentItemViewHolder) holder;

                // set jointly comment headline
                String actualCommentHeadline = contextForActivity.getResources().getString(R.string.showJointlyGoalsCommentHeadlineWithNumber) + " " + (actualPosition+1);
                if (actualPosition == 0) { // set text newest/oldest comment
                    if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsJointlyCommentList, "descending").equals("descending")) {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showJointlyGoalsCommentHeadlineWithNumberExtraNewest);
                    }
                    else {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showJointlyGoalsCommentHeadlineWithNumberExtraOldest);
                    }
                }
                itemView.textViewShowActualCommentHeadline.setText(actualCommentHeadline);

                // check if goal entry new?
                if (commentElement.getNewEntry() == 1) {
                    String txtnewEntryOfGoal = contextForActivity.getResources().getString(R.string.newEntryText);
                    itemView.newEntryOfGoal.setVisibility(View.VISIBLE);
                    itemView.newEntryOfGoal.setText(txtnewEntryOfGoal);

                    // delete status new entry in db
                    myDb.deleteStatusNewEntryOurGoalsJointlyGoalComment(commentElement.getRowID());
                }

                // set author name and date/time
                String tmpAuthorName = commentElement.getAuthorName();
                if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                    tmpAuthorName = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentPersonalAuthorName);
                }
                String commentDate = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "dd.MM.yyyy");
                String commentTime = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "HH:mm");
                String tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
                if (commentElement.getStatus() == 4) {tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
                itemView.textViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set row id of comment from db for timer update (cast from int to long because db function needs long value)
                final Long rowIdForUpdate = Long.valueOf((commentElement.getRowID()).longValue());

                // status 0 of the last actual comment
                if (commentElement.getStatus() == 0) {

                    String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendInfo);
                    itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                    itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

                } else if (commentElement.getStatus() == 1) { // textview for status 1 of the last actual comment

                    // check, sharing of comments enable and timer for comment possible, not finish?
                    if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentShare, 0) == 1) {
                        // check system time is in past or future?
                        Long writeTimeComment = commentElement.getCommentTime(); // write time is from sever
                        Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                        Long maxTimerTime = writeTimeComment+delayTime;
                        if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && commentElement.getTimerStatus() == 0) { // check system time is in past and timer status is run!
                            // calculate run time for timer in MILLISECONDS!!!
                            Long nowTime = System.currentTimeMillis();
                            Long localeTimeComment = commentElement.getLocalTime();
                            Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                            // set textview visible
                            itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);

                            // start the timer with the calculated milliseconds
                            if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                                new CountDownTimer(runTimeForTimer, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // gernate count down timer
                                        String FORMAT = "%02d:%02d:%02d";
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendDelayInfo);
                                        String tmpTime = String.format(FORMAT,
                                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                        // put count down to string
                                        String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                        // and show
                                        itemView.textViewSendInfoLastActualComment.setText(tmpCountdownTimerString);
                                    }

                                    public void onFinish() {
                                        // count down is over -> show send successfull
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                                        itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                        myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                    }
                                }.start();

                            } else {
                                // no count down anymore -> show send successfull
                                String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                                itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }
                        else {
                            // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                            itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                            String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                            itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else { // sharing of comments is disable! -> show text
                        String tmpTextSendInfoLastActualComment;
                        itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                        if (prefs.getLong(ConstansClassOurGoals.namePrefsJointlyCommentShareChangeTime, 0) < commentElement.getCommentTime()) {
                            // show send successfull, but no sharing
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsCommentSendInfoSharingDisable);
                        }
                        else {
                            // show send successfull
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourJointlyGoalsShowCommentSendSuccsessfullInfo);
                        }
                        itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurGoalsJointlyComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }

                // show actual comment
                itemView.textViewShowActualComment.setText(commentElement.getCommentText());
                break;
            case HEADER:

                break;
            case FOOTER:

                // type cast footerView
                OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentFooterViewHolder footerView = (OurGoalsShowCommentJointlyGoalsRecyclerViewAdapter.OurGoalsShowCommentFooterViewHolder) holder;

                // set button text "comment goal X"
                String tmpButtonTextCommentGoalX = String.format(contextForActivity.getResources().getString(R.string.ourGoalsShowCommentCommentThisGoal), jointlyGoalNumberInListView);
                footerView.buttonCommentThisGoal.setText(HtmlCompat.fromHtml(tmpButtonTextCommentGoalX, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set text jointly goal intro
                String txtGoalIntro = contextForActivity.getResources().getString(R.string.showJointlyGoalTextNumber)+ " " + jointlyGoalNumberInListView;
                footerView.textViewShowGoalIntro.setText(txtGoalIntro);

                // textview for the author of goal
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourGoalsAuthorNameTextWithDate), choseGoal.get(0).getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfJointlyGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
                footerView.textViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // show chose goal
                footerView.textViewShowChoseGoal.setText(choseGoal.get(0).getGoalText());

                // set click listener for button "comment this goal" only when possible
                if ((prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0)) > 0 || !commentLimitationBorder) { // check comment possible?
                    // onClick listener comment this goal
                    footerView.buttonCommentThisGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "comment_an_jointly_goal");
                            intent.putExtra("db_id", goalsDbIdToShow);
                            intent.putExtra("arr_num", jointlyGoalNumberInListView);
                            contextForActivity.startActivity(intent);
                        }
                    });
                }
                else {
                    footerView.buttonCommentThisGoal.setVisibility((View.GONE));
                }

                // onClick listener back button to jointly goals
                footerView.buttonFooterBackToGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_jointly_goals_now");
                        contextForActivity.startActivity(intent);
                    }
                });


                String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
                // build text element max comment
                if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) == 1 && commentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextJointlyGoalsCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0) > 1 && commentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextJointlyGoalsCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyComment, 0));
                }
                else {
                    tmpInfoTextMaxSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentUnlimitedText);
                }

                // build text element count comment
                if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 0) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentCountZero);
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0) == 1) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentCountSingular);
                }
                else {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentCountPlural);
                }
                tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountJointlyComment, 0));

                // build text element delay time
                String tmpInfoTextDelaytimeSingluarPluaral = "";
                if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 0) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentDelaytimeNoDelay);
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0) == 1) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentDelaytimeSingular);
                }
                else {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextJointlyGoalsCommentDelaytimePlural);
                    tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsJointlyCommentDelaytime, 0));
                }

                // generate text comment max letters
                tmpInfoTextCommentMaxLetters =  contextForActivity.getString(R.string.infoTextJointlyGoalsCommentCommentMaxLettersAndDelaytime);
                tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountJointlyLetters, 0));

                // show info text
                footerView.textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " +tmpInfoTextDelaytimeSingluarPluaral);

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
                return ourGoalsShowJointlyCommentList.size() + 2;
            case ITEM:
                // if just items, return just the length
                return ourGoalsShowJointlyCommentList.size();
            default:
                // if either or, return 1 additional
                return ourGoalsShowJointlyCommentList.size() + 1;
        }
    }
    
    
}

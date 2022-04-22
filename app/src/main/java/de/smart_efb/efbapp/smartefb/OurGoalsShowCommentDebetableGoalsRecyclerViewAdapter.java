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

public class OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateDebetableGoalCommentScalesLevel = new String [countScalesLevel];
    
    // Array List of jointly goal comment
    ArrayList<ObjectSmartEFBGoalsComment> ourGoalsShowDebetableCommentList;

    // DB-Id of jointly goal
    int goalsDbIdToShow = 0;

    // goal number in list view of fragment show debetable goal
    int debetableGoalNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean debetableCommentLimitationBorder = false;

    // the chose goal to show the comments
    ArrayList<ObjectSmartEFBGoals> choseDebetableGoal;

    // shared prefs for the comment jointly goal
    SharedPreferences prefs;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;

    
    // own constructor!!!
    public OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter (Context context, ArrayList<ObjectSmartEFBGoalsComment> commentList, int dbId, int numberInList, Boolean commentsLimitation, ArrayList<ObjectSmartEFBGoals> goals) {

        // Array List of goals comment
        ourGoalsShowDebetableCommentList = commentList;

        // context of activity OurGoals
        contextForActivity = context;

        // set db id for goals
        goalsDbIdToShow = dbId;

        // set goal number in list view
        debetableGoalNumberInListView = numberInList;

        // set comments limitation
        debetableCommentLimitationBorder = commentsLimitation;

        // set chose goal
        choseDebetableGoal = goals;

        // init array for text description of scales levels
        evaluateDebetableGoalCommentScalesLevel = context.getResources().getStringArray(R.array.debetableGoalsCommentScalesLevel);

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

        // init view has items and footer
        STATE = FOOTER;

    }




    // inner class for footer element
    private class OurGoalsShowDebetableCommentFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowDebetableGoalIntro, textViewAuthorNameText, textViewMaxAndCount, textViewShowChoseDebetableGoal;
        public Button buttonFooterBackToDebetableGoal, buttonCommentThisDebetableGoal;

        OurGoalsShowDebetableCommentFooterViewHolder(View footerView) {

            super(footerView);
            textViewMaxAndCount = footerView.findViewById(R.id.infoDebetableCommentMaxAndCount);
            buttonCommentThisDebetableGoal = footerView.findViewById(R.id.buttonCommentThisDebetableGoal);
            textViewShowDebetableGoalIntro = footerView.findViewById(R.id.goalShowDebetableGoalIntro);
            textViewAuthorNameText = footerView.findViewById(R.id.textAuthorName);
            buttonFooterBackToDebetableGoal = footerView.findViewById(R.id.buttonFooterBackToDebetableGoal);
            textViewShowChoseDebetableGoal = footerView.findViewById(R.id.choseDebetableGoal);
        }
    }


    // inner class for item element
    private class OurGoalsShowDebetableCommentItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowActualDebetableCommentHeadline, newEntryOfDebetableComment, textViewAuthorNameLastActualDebetableComment;
        public TextView textViewSendInfoLastActualDebetableComment, textViewShowActualDebetableComment, textViewShowActualDebetableCommentAssessementValue;

        OurGoalsShowDebetableCommentItemViewHolder(View ItemView) {

            super (ItemView);
            textViewShowActualDebetableCommentHeadline = ItemView.findViewById(R.id.actualDebetableCommentInfoText);
            newEntryOfDebetableComment = ItemView.findViewById(R.id.actualDebetableCommentNewInfoText);
            textViewAuthorNameLastActualDebetableComment = ItemView.findViewById(R.id.textAuthorNameActualDebetableComment);
            textViewSendInfoLastActualDebetableComment = ItemView.findViewById(R.id.textSendInfoActualSketchComment);
            textViewShowActualDebetableComment = ItemView.findViewById(R.id.listActualTextDebetableComment);
            textViewShowActualDebetableCommentAssessementValue = ItemView.findViewById(R.id.assessementValueForDebetableComment);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_show_debetable_comment_recyclerview_footer, parent, false));
        }
        return new OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_goals_show_debetable_comment_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footer Resource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (ourGoalsShowDebetableCommentList.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:

                if (position >= (ourGoalsShowDebetableCommentList.size()) ) {
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

                final ObjectSmartEFBGoalsComment debetableCommentElement = ourGoalsShowDebetableCommentList.get(position);

                // cast holder
                final OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentItemViewHolder itemView = (OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentItemViewHolder) holder;

                // set debetable comment headline
                String actualDebetableCommentHeadline = contextForActivity.getResources().getString(R.string.showDebetableCommentHeadlineWithNumber) + " " + (actualPosition+1);
                if (actualPosition == 0) { // set text newest/oldest comment
                    if (prefs.getString(ConstansClassOurGoals.namePrefsSortSequenceOfGoalsDebetableCommentList, "descending").equals("descending")) {
                        actualDebetableCommentHeadline = actualDebetableCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showDebetableCommentHeadlineWithNumberExtraNewest);
                    }
                    else {
                        actualDebetableCommentHeadline = actualDebetableCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showDebetableCommentHeadlineWithNumberExtraOldest);
                    }
                }
                itemView.textViewShowActualDebetableCommentHeadline.setText(actualDebetableCommentHeadline);

                // check if comment entry new?
                if (debetableCommentElement.getNewEntry() == 1) {
                    String txtNewEntryOfCommentOfDebetableGoal = contextForActivity.getResources().getString(R.string.newEntryTextOurGoal);
                    itemView.newEntryOfDebetableComment.setVisibility(View.VISIBLE);
                    itemView.newEntryOfDebetableComment.setText(txtNewEntryOfCommentOfDebetableGoal);

                    // delete status new entry in db
                    myDb.deleteStatusNewEntryOurGoalsDebetableGoalsComment(debetableCommentElement.getRowID());
                }

                // set author name and date/time
                String tmpAuthorName = debetableCommentElement.getAuthorName();
                if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                    tmpAuthorName = contextForActivity.getResources().getString(R.string.ourGoalsDebetableCommentPersonalAuthorName);
                }
                String commentDate = EfbHelperClass.timestampToDateFormat(debetableCommentElement.getLocalTime(), "dd.MM.yyyy");
                String commentTime = EfbHelperClass.timestampToDateFormat(debetableCommentElement.getLocalTime(), "HH:mm");
                String tmpTextAuthorNameLastActualDebetableComment = String.format(contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
                if (debetableCommentElement.getStatus() == 4) {tmpTextAuthorNameLastActualDebetableComment = String.format(contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
                itemView.textViewAuthorNameLastActualDebetableComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualDebetableComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set row id of comment from db for timer update (cast from int to long because db function needs long value)
                final Long rowIdForUpdate = Long.valueOf((debetableCommentElement.getRowID()).longValue());

                // status 0 of the last actual comment
                if (debetableCommentElement.getStatus() == 0) {

                    String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendInfo);
                    itemView.textViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                    itemView.textViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);

                } else if (debetableCommentElement.getStatus() == 1) { // textview for status 1 of the last actual comment

                    // check, sharing of comments enable and timer for comment possible, not finish?
                    if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentShare, 0) == 1) {
                        // check system time is in past or future?
                        Long writeTimeComment = debetableCommentElement.getCommentTime(); // write time is from sever
                        Integer delayTime = prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                        Long maxTimerTime = writeTimeComment+delayTime;
                        if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && debetableCommentElement.getTimerStatus() == 0) { // check system time is in past and timer status is run!
                            // calculate run time for timer in MILLISECONDS!!!
                            Long nowTime = System.currentTimeMillis();
                            Long localeTimeComment = debetableCommentElement.getLocalTime();
                            Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                            // set textview visible
                            itemView.textViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);

                            // start the timer with the calculated milliseconds
                            if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                                new CountDownTimer(runTimeForTimer, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // gernate count down timer
                                        String FORMAT = "%02d:%02d:%02d";
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendDelayInfo);
                                        String tmpTime = String.format(FORMAT,
                                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                        // put count down to string
                                        String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                        // and show
                                        itemView.textViewSendInfoLastActualDebetableComment.setText(tmpCountdownTimerString);
                                    }

                                    public void onFinish() {
                                        // count down is over -> show send successfull
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                                        itemView.textViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                                        myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                    }
                                }.start();

                            } else {
                                // no count down anymore -> show send successfull
                                String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                                itemView.textViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }
                        else {
                            // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                            itemView.textViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                            String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                            itemView.textViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else { // sharing of comments is disable! -> show text
                        String tmpTextSendInfoLastActualComment;
                        itemView.textViewSendInfoLastActualDebetableComment.setVisibility(View.VISIBLE);
                        if (prefs.getLong(ConstansClassOurGoals.namePrefsDebetableCommentShareChangeTime, 0) < debetableCommentElement.getCommentTime()) {
                            // show send successfull, but no sharing
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendInfoSharingDisable);
                        }
                        else {
                            // show send successfull
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourGoalsShowDebetableCommentSendSuccsessfullInfo);
                        }
                        itemView.textViewSendInfoLastActualDebetableComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurGoalsDebetableComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }

                // show actual result struct question only when result > 0
                if (debetableCommentElement.getCommentResultStructQuestion1() > 0) {
                    String actualResultStructQuestion = contextForActivity.getResources().getString(R.string.textOurGoalsShowDebetableCommentActualResultStructQuestion);
                    actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateDebetableGoalCommentScalesLevel[debetableCommentElement.getCommentResultStructQuestion1() - 1]);
                    itemView.textViewShowActualDebetableCommentAssessementValue.setText(HtmlCompat.fromHtml(actualResultStructQuestion, HtmlCompat.FROM_HTML_MODE_LEGACY));
                } else { // result is =0; comes from server/ coach
                    String actualResultStructQuestionFromCoach = contextForActivity.getResources().getString(R.string.textOurGoalsShowDebetableCommentActualResultStructQuestionFromCoach);
                    itemView.textViewShowActualDebetableCommentAssessementValue.setText(actualResultStructQuestionFromCoach);
                }

                // show actual debetable comment
                itemView.textViewShowActualDebetableComment.setText(debetableCommentElement.getCommentText());

                break;
            case HEADER:

                break;
            case FOOTER:

                // type cast footerView
                OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentFooterViewHolder footerView = (OurGoalsShowCommentDebetableGoalsRecyclerViewAdapter.OurGoalsShowDebetableCommentFooterViewHolder) holder;

                // set button text "comment debetable goal X"
                String tmpButtonTextCommentDebetableGoalX = String.format(contextForActivity.getResources().getString(R.string.ourGoalsShowCommentCommentThisDebetableGoal), debetableGoalNumberInListView);
                footerView.buttonCommentThisDebetableGoal.setText(HtmlCompat.fromHtml(tmpButtonTextCommentDebetableGoalX, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set click listener for button "comment this debetable goal" only when possible
                if ((prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) - prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0)) > 0 || !debetableCommentLimitationBorder) { // check comment possible?
                    // onClick listener comment this sketch arrangement
                    footerView.buttonCommentThisDebetableGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "comment_an_debetable_goal");
                            intent.putExtra("db_id", goalsDbIdToShow);
                            intent.putExtra("arr_num", debetableGoalNumberInListView);
                            contextForActivity.startActivity(intent);
                        }
                    });
                }
                else {
                    footerView.buttonCommentThisDebetableGoal.setVisibility(View.GONE);
                }

                // set text debetable goal intro
                String txtDebetableGoalIntro = contextForActivity.getResources().getString(R.string.showDebetableGoalsIntroText)+ " " + debetableGoalNumberInListView;
                footerView.textViewShowDebetableGoalIntro.setText(txtDebetableGoalIntro);

                // textview for the author of sketch arrangement
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourGoalsDebetableGoalsAuthorNameTextWithDate), choseDebetableGoal.get(0).getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurGoals.namePrefsCurrentDateOfDebetableGoals, System.currentTimeMillis()), "dd.MM.yyyy"));
                footerView.textViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // show chose debetable goal
                footerView.textViewShowChoseDebetableGoal.setText(choseDebetableGoal.get(0).getGoalText());

                // onClick listener back button to debetable goals
                footerView.buttonFooterBackToDebetableGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurGoals.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_debetable_goals_now");
                        contextForActivity.startActivity(intent);
                    }
                });

                String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
                // build text element max debetable goal comment
                if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) == 1 && debetableCommentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextDebetableGoalCommentMaxSingular), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0) > 1 && debetableCommentLimitationBorder){
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextDebetableGoalCommentMaxPlural), prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableComment, 0));
                }
                else {
                    tmpInfoTextMaxSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableGoalCommentUnlimitedText);
                }

                // build text element count debetable goal comment count
                if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 0) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableGoalCommentCountZero);
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0) == 1) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableGoalCommentCountSingular);
                }
                else {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableGoalCommentCountPlural);
                }
                tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsCommentCountDebetableComment, 0));

                // build text element delay time
                String tmpInfoTextDelaytimeSingluarPluaral = "";
                if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 0) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableCommentDelaytimeNoDelay);
                }
                else if (prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0) == 1) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableCommentDelaytimeSingular);
                }
                else {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextDebetableCommentDelaytimePlural);
                    tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurGoals.namePrefsDebetableCommentDelaytime, 0));
                }

                // generate text comment max letters
                tmpInfoTextCommentMaxLetters =  contextForActivity.getString(R.string.infoTextDebetableGoalCommentMaxLetters);
                tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurGoals.namePrefsCommentMaxCountDebetableLetters, 0));

                // show info text
                footerView.textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral+tmpInfoTextCountSingluarPluaral+tmpInfoTextCommentMaxLetters + " " + tmpInfoTextDelaytimeSingluarPluaral);

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
                return ourGoalsShowDebetableCommentList.size() + 2;
            case ITEM:
                // if just items, return just the length
                return ourGoalsShowDebetableCommentList.size();
            default:
                // if either or, return 1 additional
                return ourGoalsShowDebetableCommentList.size() + 1;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    












}

package de.smart_efb.efbapp.smartefb;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OurArrangementShowSketchCommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // Array List of sketch arrangement comment
    ArrayList<ObjectSmartEFBComment> ourArrangementShowSketchCommentList;

    // DB-Id of arrangement
    int sketchArrangementDbIdToShow = 0;

    // sketch arrangement number in list view of fragment show sketch arrangement
    int sketchArrangementNumberInListView = 0;

    // true-> comments are limited, false -> sketch comments are not limited
    Boolean sketchCommentLimitationBorder = false;

    // count Array-elements for text description of scales levels
    final static int countScalesLevel = 5;

    // Array for text description of scales levels
    private String[] evaluateSketchCommentScalesLevel = new String [countScalesLevel];

    // the choose sketch arrangement to show the comments
    ArrayList<ObjectSmartEFBArrangement> chooseSketchArrangement;

    // shared prefs for the sketch comment arrangement
    SharedPreferences prefs;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurArrangementShowSketchCommentRecyclerViewAdapter(Context context, ArrayList<ObjectSmartEFBComment> commentList, int dbId, int numberInList, Boolean commentsLimitation, ArrayList<ObjectSmartEFBArrangement> arrangement) {

        // Array List of sketch arrangement comment
        ourArrangementShowSketchCommentList = commentList;

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for sketch arrangement
        sketchArrangementDbIdToShow = dbId;

        // set sketch arrangement number in list view
        sketchArrangementNumberInListView = numberInList;

        // set sketch comments limitation
        sketchCommentLimitationBorder = commentsLimitation;

        // set choose arrangement
        chooseSketchArrangement = arrangement;

        // init array for text description of scales levels
        evaluateSketchCommentScalesLevel = context.getResources().getStringArray(R.array.evaluateSketchCommentScalesLevel);

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

        // init view has items and footer
        STATE = FOOTER;

    }


    // inner class for footer element
    private class OurArrangementShowSketchCommentFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowSketchArrangementIntro, textViewAuthorNameText, textViewMaxAndCount, textViewShowChooseSketchArrangement;
        public Button buttonFooterBackToSketchArrangement, buttonCommentThisSketchArrangement;

        OurArrangementShowSketchCommentFooterViewHolder(View footerView) {

            super(footerView);
            textViewMaxAndCount = footerView.findViewById(R.id.infoSketchCommentMaxAndCount);
            buttonCommentThisSketchArrangement = footerView.findViewById(R.id.buttonCommentThisSketchArrangement);
            textViewShowSketchArrangementIntro = footerView.findViewById(R.id.arrangementShowSketchArrangementIntro);
            textViewAuthorNameText = footerView.findViewById(R.id.textAuthorName);
            buttonFooterBackToSketchArrangement = footerView.findViewById(R.id.buttonFooterBackToSketchArrangement);
            textViewShowChooseSketchArrangement = footerView.findViewById(R.id.chooseSketchArrangement);
        }
    }


    // inner class for item element
    private class OurArrangementShowSketchCommentItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowActualSketchCommentHeadline, newEntryOfSketchComment, textViewAuthorNameLastActualSketchComment;
        public TextView textViewSendInfoLastActualSketchComment, textViewShowActualSketchComment, textViewShowActualSketchCommentAssessementValue;

        OurArrangementShowSketchCommentItemViewHolder(View ItemView) {

            super (ItemView);
            textViewShowActualSketchCommentHeadline = ItemView.findViewById(R.id.actualSketchCommentInfoText);
            newEntryOfSketchComment = ItemView.findViewById(R.id.actualSketchCommentNewInfoText);
            textViewAuthorNameLastActualSketchComment = ItemView.findViewById(R.id.textAuthorNameActualSketchComment);
            textViewSendInfoLastActualSketchComment = ItemView.findViewById(R.id.textSendInfoActualSketchComment);
            textViewShowActualSketchComment = ItemView.findViewById(R.id.listActualTextSketchComment);
            textViewShowActualSketchCommentAssessementValue = ItemView.findViewById(R.id.assessementValueForSketchComment);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_sketch_comment_recyclerview_footer, parent, false));
        }
        return new OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_sketch_comment_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footer Resource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (ourArrangementShowSketchCommentList.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:

                if (position >= (ourArrangementShowSketchCommentList.size()) ) {
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

                final ObjectSmartEFBComment sketchCommentElement = ourArrangementShowSketchCommentList.get(position);

                // cast holder
                final OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentItemViewHolder itemView = (OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentItemViewHolder) holder;

                // set sketch comment headline
                String actualSketchCommentHeadline = contextForActivity.getResources().getString(R.string.showSketchCommentHeadlineWithNumber) + " " + (actualPosition+1);
                if (actualPosition == 0) { // set text newest/oldest comment
                    if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementSketchCommentList, "descending").equals("descending")) {
                        actualSketchCommentHeadline = actualSketchCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showSketchCommentHeadlineWithNumberExtraNewest);
                    }
                    else {
                        actualSketchCommentHeadline = actualSketchCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showSketchCommentHeadlineWithNumberExtraOldest);
                    }
                }
                itemView.textViewShowActualSketchCommentHeadline.setText(actualSketchCommentHeadline);

                // check if arrangement entry new?
                if (sketchCommentElement.getNewEntry() == 1) {
                    String txtNewEntryOfCommentOfSketchArrangement = contextForActivity.getResources().getString(R.string.newEntryText);
                    itemView.newEntryOfSketchComment.setVisibility(View.VISIBLE);
                    itemView.newEntryOfSketchComment.setText(txtNewEntryOfCommentOfSketchArrangement);

                    // delete status new entry in db
                    myDb.deleteStatusNewEntryOurArrangementSketchComment(sketchCommentElement.getRowID());
                }

                // set author name and date/time
                String tmpAuthorName = sketchCommentElement.getAuthorName();
                if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                    tmpAuthorName = contextForActivity.getResources().getString(R.string.ourArrangementSketchCommentPersonalAuthorName);
                }
                String commentDate = EfbHelperClass.timestampToDateFormat(sketchCommentElement.getLocalTime(), "dd.MM.yyyy");
                String commentTime = EfbHelperClass.timestampToDateFormat(sketchCommentElement.getLocalTime(), "HH:mm");
                String tmpTextAuthorNameLastActualSketchComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
                if (sketchCommentElement.getStatus() == 4) {tmpTextAuthorNameLastActualSketchComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementSketchCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
                itemView.textViewAuthorNameLastActualSketchComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualSketchComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

                
                
                
                
                // ++++++++++++++++++++++++++++++++
                // set row id of comment from db for timer update (cast from int to long because db function needs long value)
                final Long rowIdForUpdate = Long.valueOf((sketchCommentElement.getRowID()).longValue());

                // status 0 of the last actual comment
                if (sketchCommentElement.getStatus() == 0) {

                    String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendInfo);
                    itemView.textViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                    itemView.textViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);

                } else if (sketchCommentElement.getStatus() == 1) { // textview for status 1 of the last actual comment

                    // check, sharing of comments enable and timer for comment possible, not finish?
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShare, 0) == 1) {
                        // check system time is in past or future?
                        Long writeTimeComment = sketchCommentElement.getCommentTime(); // write time is from sever
                        Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) * 60000; // make milliseconds from minutes
                        Long maxTimerTime = writeTimeComment+delayTime;
                        if ( maxTimerTime > prefs.getLong(ConstansClassMain.namePrefsLastContactTimeToServerInMills, 0) && sketchCommentElement.getTimerStatus() == 0) { // check system time is in past and timer status is run!
                            // calculate run time for timer in MILLISECONDS!!!
                            Long nowTime = System.currentTimeMillis();
                            Long localeTimeComment = sketchCommentElement.getLocalTime();
                            Long runTimeForTimer = delayTime - (nowTime - localeTimeComment);

                            // set textview visible
                            itemView.textViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);

                            // start the timer with the calculated milliseconds
                            if (runTimeForTimer > 0 && runTimeForTimer <= delayTime) {
                                new CountDownTimer(runTimeForTimer, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        // gernate count down timer
                                        String FORMAT = "%02d:%02d:%02d";
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendDelayInfo);
                                        String tmpTime = String.format(FORMAT,
                                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                                        // put count down to string
                                        String tmpCountdownTimerString = String.format(tmpTextSendInfoLastActualComment, tmpTime);
                                        // and show
                                        itemView.textViewSendInfoLastActualSketchComment.setText(tmpCountdownTimerString);
                                    }

                                    public void onFinish() {
                                        // count down is over -> show send successfull
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                                        itemView.textViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                    }
                                }.start();

                            } else {
                                // no count down anymore -> show send successfull
                                String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                                itemView.textViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }
                        else {
                            // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                            itemView.textViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                            String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                            itemView.textViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else { // sharing of comments is disable! -> show text
                        String tmpTextSendInfoLastActualComment;
                        itemView.textViewSendInfoLastActualSketchComment.setVisibility(View.VISIBLE);
                        if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementSketchCommentShareChangeTime, 0) < sketchCommentElement.getCommentTime()) {
                            // show send successfull, but no sharing
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementSketchCommentSendInfoSharingDisable);
                        }
                        else {
                            // show send successfull
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentSendSuccsessfullInfo);
                        }
                        itemView.textViewSendInfoLastActualSketchComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }

                // show actual result struct question only when result > 0
                if (sketchCommentElement.getCommentResultStructQuestion1() > 0) {
                    String actualResultStructQuestion = contextForActivity.getResources().getString(R.string.textOurArrangementShowSketchCommentActualResultStructQuestion);
                    actualResultStructQuestion = String.format(actualResultStructQuestion, evaluateSketchCommentScalesLevel[sketchCommentElement.getCommentResultStructQuestion1() - 1]);
                    itemView.textViewShowActualSketchCommentAssessementValue.setText(HtmlCompat.fromHtml(actualResultStructQuestion, HtmlCompat.FROM_HTML_MODE_LEGACY));
                } else { // result is =0; comes from server/ coach
                    String actualResultStructQuestionFromCoach = contextForActivity.getResources().getString(R.string.textOurArrangementShowSketchCommentActualResultStructQuestionFromCoach);
                    itemView.textViewShowActualSketchCommentAssessementValue.setText(actualResultStructQuestionFromCoach);
                }

                // show actual sketch comment
                itemView.textViewShowActualSketchComment.setText(sketchCommentElement.getCommentText());

                break;
            case HEADER:

                break;
            case FOOTER:

                // type cast footerView
                OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentFooterViewHolder footerView = (OurArrangementShowSketchCommentRecyclerViewAdapter.OurArrangementShowSketchCommentFooterViewHolder) holder;

                // set button text "comment sketch arrangement X"
                String tmpButtonTextCommentSketchArrangementX = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowSketchCommentCommentThisSketchArrangement), sketchArrangementNumberInListView);
                footerView.buttonCommentThisSketchArrangement.setText(HtmlCompat.fromHtml(tmpButtonTextCommentSketchArrangementX, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set click listener for button "comment this sketch arrangement" only when possible
                if ((prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0)) > 0 || !sketchCommentLimitationBorder) { // check comment possible?
                    // onClick listener comment this sketch arrangement
                    footerView.buttonCommentThisSketchArrangement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "comment_an_sketch_arrangement");
                            intent.putExtra("db_id", sketchArrangementDbIdToShow);
                            intent.putExtra("arr_num", sketchArrangementNumberInListView);
                            contextForActivity.startActivity(intent);
                        }
                    });
                }
                else {
                    footerView.buttonCommentThisSketchArrangement.setVisibility(View.GONE);
                }

                // set text sketch arrangement intro
                String txtSketchArrangementIntro = contextForActivity.getResources().getString(R.string.showSketchArrangementIntroText)+ " " + sketchArrangementNumberInListView;
                footerView.textViewShowSketchArrangementIntro.setText(txtSketchArrangementIntro);

                // textview for the author of sketch arrangement
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), chooseSketchArrangement.get(0).getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfSketchArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
                footerView.textViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // show choose sketch arrangement
                footerView.textViewShowChooseSketchArrangement.setText(chooseSketchArrangement.get(0).getArrangementText());

                // onClick listener back button to now arrangement
                footerView.buttonFooterBackToSketchArrangement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_sketch_arrangement");
                        contextForActivity.startActivity(intent);
                    }
                });

                String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextSketchCommentMaxLetters;
                // build text element max sketch comment
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) == 1 && sketchCommentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextSketchCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0) > 1 && sketchCommentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextSketchCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchComment, 0));
                }
                else {
                    tmpInfoTextMaxSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentUnlimitedText);
                }

                // build text element count sketch comment
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 0) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentCountZero);
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0) == 1) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentCountSingular);
                }
                else {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentCountPlural);
                }
                tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentCountComment, 0));

                // build text element delay time
                String tmpInfoTextDelaytimeSingluarPluaral;
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 0) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentDelaytimeNoDelay);
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0) == 1) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentDelaytimeSingular);
                }
                else {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextSketchCommentDelaytimePlural);
                    tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsSketchCommentDelaytime, 0));
                }

                // generate text sketch comment max letters
                tmpInfoTextSketchCommentMaxLetters =  contextForActivity.getString(R.string.infoTextSketchCommentMaxLetters);
                tmpInfoTextSketchCommentMaxLetters = String.format(tmpInfoTextSketchCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsMaxSketchCommentLetters, 0));

                // show info text
                footerView.textViewMaxAndCount.setText(tmpInfoTextMaxSingluarPluaral + tmpInfoTextCountSingluarPluaral + tmpInfoTextSketchCommentMaxLetters + " " + tmpInfoTextDelaytimeSingluarPluaral);

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
                return ourArrangementShowSketchCommentList.size() + 2;
            case ITEM:
                // if just items, return just the length
                return ourArrangementShowSketchCommentList.size();
            default:
                // if either or, return 1 additional
                return ourArrangementShowSketchCommentList.size() + 1;
        }
    }


}

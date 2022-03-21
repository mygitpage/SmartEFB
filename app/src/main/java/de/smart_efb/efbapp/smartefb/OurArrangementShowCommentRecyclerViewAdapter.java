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


// Look at:
// https://ekeitho.medium.com/dressing-up-recyclerview-with-a-hat-and-shoes-923d6a2fe450
// Auf GitHub: https://github.com/ekeitho/RV-header-footer/tree/master/app/src/main/java/com/ekeitho/headerfooterview
// for recycler view with header and footer

// Look at:
// datei auswahlmenue
// https://o7planning.org/12725/create-a-simple-file-chooser-in-android


public class OurArrangementShowCommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // Array List of arrangement comment
    ArrayList<ObjectSmartEFBComment> ourArrangementShowCommentList;

    // DB-Id of arrangement
    int arrangementDbIdToShow = 0;

    // arrangement number in list view of fragment show arrangement now
    int arrangementNumberInListView = 0;

    // true-> comments are limited, false -> comments are not limited
    Boolean commentLimitationBorder = false;

    // the chosse arrangement to show the comments
    ArrayList<ObjectSmartEFBArrangement> chooseArrangement;

    // shared prefs for the comment arrangement
    SharedPreferences prefs;

    // own reycler view elements
    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;
    private int STATE;


    // own constructor!!!
    public OurArrangementShowCommentRecyclerViewAdapter(Context context, ArrayList<ObjectSmartEFBComment> commentList, int dbId, int numberInList, Boolean commentsLimitation, ArrayList<ObjectSmartEFBArrangement> arrangement) {

        // Array List of arrangement comment
        ourArrangementShowCommentList = commentList;

        // context of activity OurArrangement
        contextForActivity = context;

        // set db id for arrangement
        arrangementDbIdToShow = dbId;

        // set arrangement number in list view
        arrangementNumberInListView = numberInList;

        // set comments limitation
        commentLimitationBorder = commentsLimitation;

        // set choose arrangement
        chooseArrangement = arrangement;

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

        // init view has items and footer
        STATE = FOOTER;

    }


    // inner class for footer element
    private class OurArrangementShowCommentFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowArrangementIntro, textViewAuthorNameText, textViewMaxAndCount, textViewShowChooseArrangement;
        public Button buttonFooterBackToArrangement, buttonCommentThisArrangement;

        OurArrangementShowCommentFooterViewHolder(View footerView) {
            
            super(footerView);
            textViewMaxAndCount = footerView.findViewById(R.id.infoShowCommentMaxAndCount);
            buttonCommentThisArrangement = footerView.findViewById(R.id.buttonCommentThisArrangement);
            textViewShowArrangementIntro = footerView.findViewById(R.id.arrangementShowArrangementIntro);
            textViewAuthorNameText = footerView.findViewById(R.id.textAuthorName);
            buttonFooterBackToArrangement = footerView.findViewById(R.id.buttonFooterBackToArrangement);
            textViewShowChooseArrangement = footerView.findViewById(R.id.chooseArrangement);

        }
    }


    // inner class for item element
    private class OurArrangementShowCommentItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowActualCommentHeadline, newEntryOfArrangement, textViewAuthorNameLastActualComment;
        public TextView textViewSendInfoLastActualComment, textViewShowActualComment;

        OurArrangementShowCommentItemViewHolder(View ItemView) {

            super (ItemView);
            textViewShowActualCommentHeadline = ItemView.findViewById(R.id.actualCommentInfoText);
            newEntryOfArrangement = ItemView.findViewById(R.id.actualCommentNewInfoText);
            textViewAuthorNameLastActualComment = ItemView.findViewById(R.id.textAuthorNameActualComment);
            textViewSendInfoLastActualComment = ItemView.findViewById(R.id.textSendInfoActualComment);
            textViewShowActualComment = ItemView.findViewById(R.id.listActualTextComment);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == FOOTER) {
            return new OurArrangementShowCommentFooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_comment_recyclerview_footer, parent, false));
        }
        return new OurArrangementShowCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_comment_recyclerview_item, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        // if there is both footerResource and header
        switch (STATE) {
            case BOTH:
                if (position == 0) {
                    return HEADER;
                }
                else if (position >= (ourArrangementShowCommentList.size()+1) ) {
                    return FOOTER;
                }
                return ITEM;
            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:

                if (position >= (ourArrangementShowCommentList.size()) ) {
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

                final ObjectSmartEFBComment commentElement = ourArrangementShowCommentList.get(position);

                // cast holder
                final OurArrangementShowCommentItemViewHolder itemView = (OurArrangementShowCommentItemViewHolder) holder;

                // set comment headline
                String actualCommentHeadline = contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumber) + " " + (actualPosition+1);
                if (actualPosition == 0) { // set text newest/oldest comment
                    if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumberExtraNewest);
                    }
                    else {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumberExtraOldest);
                    }
                }
                itemView.textViewShowActualCommentHeadline.setText(actualCommentHeadline);

                // check if arrangement entry new?
                if (commentElement.getNewEntry() == 1) {
                    String txtnewEntryOfArrangement = contextForActivity.getResources().getString(R.string.newEntryText);
                    itemView.newEntryOfArrangement.setVisibility(View.VISIBLE);
                    itemView.newEntryOfArrangement.setText(txtnewEntryOfArrangement);

                    // delete status new entry in db
                    myDb.deleteStatusNewEntryOurArrangementComment(commentElement.getRowID());
                }

                // set author name and date/time
                String tmpAuthorName = commentElement.getAuthorName();
                if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                    tmpAuthorName = contextForActivity.getResources().getString(R.string.ourArrangementShowCommentPersonalAuthorName);
                }
                String commentDate = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "dd.MM.yyyy");
                String commentTime = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "HH:mm");
                String tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
                if (commentElement.getStatus() == 4) {tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
                itemView.textViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set row id of comment from db for timer update (cast from int to long because db function needs long value)
                final Long rowIdForUpdate = Long.valueOf((commentElement.getRowID()).longValue());

                // status 0 of the last actual comment
                if (commentElement.getStatus() == 0) {

                    String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendInfo);
                    itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                    itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);

                } else if (commentElement.getStatus() == 1) { // textview for status 1 of the last actual comment

                    // check, sharing of comments enable and timer for comment possible, not finish?
                    if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 1) {
                        // check system time is in past or future?
                        Long writeTimeComment = commentElement.getCommentTime(); // write time is from sever
                        Integer delayTime = prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) * 60000; // make milliseconds from minutes
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
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendDelayInfo);
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
                                        String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                                        itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                                    }
                                }.start();

                            } else {
                                // no count down anymore -> show send successfull
                                String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                                itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                                myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                            }
                        }
                        else {
                            // system time is in past or timer status is stop! -> Show Text: Comment send successfull!
                            itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                            String tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendSuccsessfullInfo);
                            itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                            myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                        }
                    }
                    else { // sharing of comments is disable! -> show text
                        String tmpTextSendInfoLastActualComment;
                        itemView.textViewSendInfoLastActualComment.setVisibility(View.VISIBLE);
                        if (prefs.getLong(ConstansClassOurArrangement.namePrefsArrangementCommentShareChangeTime, 0) < commentElement.getCommentTime()) {
                            // show send successfull, but no sharing
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementCommentSendInfoSharingDisable);
                        }
                        else {
                            // show send successfull
                            tmpTextSendInfoLastActualComment = contextForActivity.getResources().getString(R.string.ourArrangementShowCommentSendSuccsessfullInfo);
                        }
                        itemView.textViewSendInfoLastActualComment.setText(tmpTextSendInfoLastActualComment);
                        myDb.updateTimerStatusOurArrangementComment(rowIdForUpdate, 1); // timer status: 0= timer can run; 1=timer finish!
                    }
                }

                // show actual comment
                itemView.textViewShowActualComment.setText(commentElement.getCommentText());

                break;
            case HEADER:

                break;
            case FOOTER:

                // type cast footerView
                OurArrangementShowCommentFooterViewHolder footerView = (OurArrangementShowCommentFooterViewHolder) holder;

                // set button text "comment arrangement X"
                String tmpButtonTextCommentArrangementX = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentCommentThisArrangement), arrangementNumberInListView);
                footerView.buttonCommentThisArrangement.setText(HtmlCompat.fromHtml(tmpButtonTextCommentArrangementX, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // set text arrangement intro
                String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showArrangementIntroText)+ " " + arrangementNumberInListView;
                footerView.textViewShowArrangementIntro.setText(txtArrangementIntro);

                // textview for the author of arrangement
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), chooseArrangement.get(0).getAuthorName(), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
                footerView.textViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // show choose arrangement
                footerView.textViewShowChooseArrangement.setText(chooseArrangement.get(0).getArrangementText());

                // set click listener for button "comment this arrangement" only when possible
                if ((prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) - prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0)) > 0 || !commentLimitationBorder) { // check comment possible?
                    // onClick listener comment this arrangement
                    footerView.buttonCommentThisArrangement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("com", "comment_an_arrangement");
                            intent.putExtra("db_id", arrangementDbIdToShow);
                            intent.putExtra("arr_num", arrangementNumberInListView);
                            contextForActivity.startActivity(intent);
                        }
                    });
                }
                else {
                    footerView.buttonCommentThisArrangement.setVisibility((View.GONE));
                }

                // onClick listener back button to now arrangement
                footerView.buttonFooterBackToArrangement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_arrangement_now");
                        contextForActivity.startActivity(intent);
                    }
                });


                String tmpInfoTextMaxSingluarPluaral, tmpInfoTextCountSingluarPluaral, tmpInfoTextCommentMaxLetters;
                // build text element max comment
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) == 1 && commentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextNowCommentMaxSingular), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0) > 1 && commentLimitationBorder) {
                    tmpInfoTextMaxSingluarPluaral = String.format(contextForActivity.getString(R.string.infoTextNowCommentMaxPlural), prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxComment, 0));
                }
                else {
                    tmpInfoTextMaxSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentUnlimitedText);
                }

                // build text element count comment
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 0) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentCountZero);
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0) == 1) {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentCountSingular);
                }
                else {
                    tmpInfoTextCountSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentCountPlural);
                }
                tmpInfoTextCountSingluarPluaral = String.format(tmpInfoTextCountSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentCountComment, 0));

                // build text element delay time
                String tmpInfoTextDelaytimeSingluarPluaral;
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 0) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentDelaytimeNoDelay);
                }
                else if (prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0) == 1) {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentDelaytimeSingular);
                }
                else {
                    tmpInfoTextDelaytimeSingluarPluaral = contextForActivity.getString(R.string.infoTextNowCommentDelaytimePlural);
                    tmpInfoTextDelaytimeSingluarPluaral = String.format(tmpInfoTextDelaytimeSingluarPluaral, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentDelaytime, 0));
                }

                // generate text comment max letters
                tmpInfoTextCommentMaxLetters =  contextForActivity.getString(R.string.infoTextNowCommentCommentMaxLettersAndDelaytime);
                tmpInfoTextCommentMaxLetters = String.format(tmpInfoTextCommentMaxLetters, prefs.getInt(ConstansClassOurArrangement.namePrefsCommentMaxLetters, 0));

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
                return ourArrangementShowCommentList.size() + 2;
            case ITEM:
                // if just items, return just the length
                return ourArrangementShowCommentList.size();
            default:
                // if either or, return 1 additional
                return ourArrangementShowCommentList.size() + 1;
        }
    }

}
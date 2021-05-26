package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;



// Look at:
// https://ekeitho.medium.com/dressing-up-recyclerview-with-a-hat-and-shoes-923d6a2fe450
// Auf GitHub: https://github.com/ekeitho/RV-header-footer/tree/master/app/src/main/java/com/ekeitho/headerfooterview
// for recycler view with header and footer

// Look at:
// datei auswahlmenue
// https://o7planning.org/12725/create-a-simple-file-chooser-in-android


public class OurArrangementShowCommentRecylerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final Context contextForActivity;

    // Array List of arrangement comment
    ArrayList<ObjectSmartEFBComment> ourArrangementShowCommentList;

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





    final static int HEADER = 0;
    final static int ITEM = 1;
    final static int FOOTER = 2;
    final static int BOTH = 3;


    private int STATE;




    // own constructor!!!
    public OurArrangementShowCommentRecylerViewAdapter (Context context, ArrayList<ObjectSmartEFBComment> commentList, int flags, int dbId, int numberInList, Boolean commentsLimitation, Cursor arrangement) {

        //cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        // set choosen arrangement
        choosenArrangement = arrangement;

        // init the prefs
        prefs = contextForActivity.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, contextForActivity.MODE_PRIVATE);

        // init view has header, items and footer
        STATE = 3;


    }





    private class OurArrangementShowCommentHeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowArrangementIntro, tmpTextViewAuthorNameText, linkShowCommentBackLink;
        public TextView textViewShowChoosenArrangement, textCommentSharingIsDisable;
        public Button buttonHeaderBackToArrangement;


        OurArrangementShowCommentHeaderViewHolder(View headerView) {

            super(headerView);

            textViewShowArrangementIntro = (TextView) headerView.findViewById(R.id.arrangementShowArrangementIntro);
            tmpTextViewAuthorNameText = (TextView) headerView.findViewById(R.id.textAuthorName);
            //linkShowCommentBackLink = (TextView) headerView.findViewById(R.id.arrangementShowCommentBackLink);
            buttonHeaderBackToArrangement = (Button) headerView.findViewById(R.id.buttonHeaderBackToArrangement);
            textViewShowChoosenArrangement = (TextView) headerView.findViewById(R.id.choosenArrangement);
            textCommentSharingIsDisable = (TextView) headerView.findViewById(R.id.commentSharingIsDisable);

        }

    }


    private class OurArrangementShowCommentFooterViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewChangeSortSequence, textViewMaxAndCount;
        public Button buttonBackToArrangement;
        
        OurArrangementShowCommentFooterViewHolder(View footerView) {
            
            super(footerView);

            buttonBackToArrangement = (Button) footerView.findViewById(R.id.buttonAbortShowComment);

            textViewChangeSortSequence = (TextView) footerView.findViewById(R.id.linkToChangeSortSequenceOfCommentList);
            textViewMaxAndCount = (TextView) footerView.findViewById(R.id.infoShowCommentMaxAndCount);
            
            
        }

    }


    private class OurArrangementShowCommentItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewShowActualCommentHeadline, newEntryOfArrangement, tmpTextViewAuthorNameLastActualComment;
        public TextView tmpTextViewSendInfoLastActualComment, textViewShowActualComment, linkToCommentAnArrangement;

        OurArrangementShowCommentItemViewHolder(View ItemView) {

            super (ItemView);

            textViewShowActualCommentHeadline = (TextView) ItemView.findViewById(R.id.actualCommentInfoText);
            newEntryOfArrangement = (TextView) ItemView.findViewById(R.id.actualCommentNewInfoText);
            tmpTextViewAuthorNameLastActualComment = (TextView) ItemView.findViewById(R.id.textAuthorNameActualComment);
            tmpTextViewSendInfoLastActualComment = (TextView) ItemView.findViewById(R.id.textSendInfoActualComment);
            textViewShowActualComment = (TextView) ItemView.findViewById(R.id.listActualTextComment);
            linkToCommentAnArrangement = (TextView) ItemView.findViewById(R.id.linkToCommentAnArrangement);






        }

    }









    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HEADER) {
            return new OurArrangementShowCommentHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_our_arrangement_show_comment_recyclerview_header, parent, false));
        }
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


                Log.d("Get Item View Type --->", "BOTH mit Laenge:" + ourArrangementShowCommentList.size() + " ##### Position: "+ position);


                if (position == 0) {
                    Log.d("Get Item View Type --->", "Gewaehlt -> HEADER");
                    return HEADER;

                } else if (position >= (ourArrangementShowCommentList.size()+1) ) {
                    Log.d("Get Item View Type --->", "Gewaehlt -> FOOTER");
                    return FOOTER;

                }
                Log.d("Get Item View Type --->", "Gewaehlt -> ITEM");
                return ITEM;

            case HEADER:
                return position == 0 ? HEADER : ITEM;

            case FOOTER:
                return position == ourArrangementShowCommentList.size() ? FOOTER : ITEM;

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



        Uri.Builder commentLinkBuilder;

        switch (viewType) {
            case ITEM:


                Log.d("OnBind", "In ITEM mit Position: " + position +" +++++++++++++++++++++++++++");

                // ++++++++++++++++++++++++++++++++++++
                final ObjectSmartEFBComment commentElement = ourArrangementShowCommentList.get(position-1);



                OurArrangementShowCommentItemViewHolder itemView = (OurArrangementShowCommentItemViewHolder) holder;


                // set comment headline
                String actualCommentHeadline = contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumber) + " " + actualPosition;
                if (actualPosition == 1) { // set text newest/oldest comment
                    if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumberExtraNewest);
                    }
                    else {
                        actualCommentHeadline = actualCommentHeadline + " " + contextForActivity.getResources().getString(R.string.showCommentHeadlineWithNumberExtraOldest);
                    }
                }
                itemView.textViewShowActualCommentHeadline.setText(actualCommentHeadline);


                // check if arrangement entry new?

                Log.d("ITEM VISIBLE ------------------>", "Value: " + commentElement.getNewEntry());

                if (commentElement.getNewEntry() == 1) {




                    String txtnewEntryOfArrangement = contextForActivity.getResources().getString(R.string.newEntryText);
                    itemView.newEntryOfArrangement.setVisibility(View.VISIBLE);
                    itemView.newEntryOfArrangement.setText(txtnewEntryOfArrangement);

                    // delete status new entry in db
                    //myDb.deleteStatusNewEntryOurArrangementComment(commentElement.getRowID());
                }



                // set author name and date/time
                String tmpAuthorName = commentElement.getAuthorName();
                if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
                    tmpAuthorName = contextForActivity.getResources().getString(R.string.ourArrangementShowCommentPersonalAuthorName);
                }

                String commentDate = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "dd.MM.yyyy");
                String commentTime = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "HH:mm");;
                String tmpTextAuthorNameLastActualComment = "";
                tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
                if (commentElement.getStatus() == 4) {tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!
                itemView.tmpTextViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // show actual comment
                itemView.textViewShowActualComment.setText(commentElement.getCommentText());




                // ++++++++++++++++++++++++++++++++++++++

                break;
            case HEADER:

                Log.d("OnBind", "In HEADER +++++++++++++++++++++++++++");

                OurArrangementShowCommentHeaderViewHolder headerView = (OurArrangementShowCommentHeaderViewHolder) holder;

                // set text arrangement intro
                String txtArrangementIntro = contextForActivity.getResources().getString(R.string.showArrangementIntroText)+ " " + arrangementNumberInListView;
                headerView.textViewShowArrangementIntro.setText(txtArrangementIntro);

                // textview for the author of arrangement
                String tmpTextAuthorNameText = String.format(contextForActivity.getResources().getString(R.string.ourArrangementAuthorNameTextWithDate), choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_AUTHOR_NAME)), EfbHelperClass.timestampToDateFormat(prefs.getLong(ConstansClassOurArrangement.namePrefsCurrentDateOfArrangement, System.currentTimeMillis()), "dd.MM.yyyy"));
                headerView.tmpTextViewAuthorNameText.setText(HtmlCompat.fromHtml(tmpTextAuthorNameText, HtmlCompat.FROM_HTML_MODE_LEGACY));

                // make link back to show arrangement
                commentLinkBuilder = new Uri.Builder();
                commentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", "0")
                        .appendQueryParameter("arr_num", "0")
                        .appendQueryParameter("com", "show_arrangement_now");

                Spanned tmpBackLink = HtmlCompat.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementBackLinkToArrangementFromShowComment", "string", contextForActivity.getPackageName()))+"</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
                //headerView.linkShowCommentBackLink.setText(tmpBackLink);
                //headerView.linkShowCommentBackLink.setMovementMethod(LinkMovementMethod.getInstance());

                // show choosen arrangement
                headerView.textViewShowChoosenArrangement.setText(choosenArrangement.getString(choosenArrangement.getColumnIndex(DBAdapter.OUR_ARRANGEMENT_KEY_ARRANGEMENT)));

                // show hint sharing is disable
                if (prefs.getInt(ConstansClassOurArrangement.namePrefsArrangementCommentShare, 0) == 0) {
                    headerView.textCommentSharingIsDisable.setVisibility (View.VISIBLE);
                }

                // onClick listener back button
                headerView.buttonHeaderBackToArrangement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_arrangement_now");
                        contextForActivity.startActivity(intent);
                    }
                });





                break;
            case FOOTER:

                // type cast footerView
                OurArrangementShowCommentFooterViewHolder footerView = (OurArrangementShowCommentFooterViewHolder) holder;

                // onClick listener abbort button
                footerView.buttonBackToArrangement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(contextForActivity, ActivityOurArrangement.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com","show_arrangement_now");
                        contextForActivity.startActivity(intent);
                    }
                });

                // generate link for sort sequence change
                
                commentLinkBuilder = new Uri.Builder();
                commentLinkBuilder.scheme("smart.efb.deeplink")
                        .authority("linkin")
                        .path("ourarrangement")
                        .appendQueryParameter("db_id", Integer.toString(arrangementDbIdToShow))
                        .appendQueryParameter("arr_num", Integer.toString(arrangementNumberInListView))
                        .appendQueryParameter("eval_next", Boolean.toString(false))
                        .appendQueryParameter("com", "change_sort_sequence_arrangement_comment");

                String tmpLinkTextChangeSortSequence;
                if (prefs.getString(ConstansClassOurArrangement.namePrefsSortSequenceOfArrangementCommentList, "descending").equals("descending")) {
                    tmpLinkTextChangeSortSequence = contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementCommentDescending", "string", contextForActivity.getPackageName()));
                }
                else {
                    tmpLinkTextChangeSortSequence = contextForActivity.getResources().getString(contextForActivity.getResources().getIdentifier("ourArrangementShowCommentLinkChangeSortSequenceOfArrangementCommentAscending", "string", contextForActivity.getPackageName()));
                }
                Spanned tmpSortLink = HtmlCompat.fromHtml("<a href=\"" + commentLinkBuilder.build().toString() + "\">" + tmpLinkTextChangeSortSequence + "</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);

                footerView.textViewChangeSortSequence.setText(tmpSortLink);
                footerView.textViewChangeSortSequence.setMovementMethod(LinkMovementMethod.getInstance());


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
                String tmpInfoTextDelaytimeSingluarPluaral = "";
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



/*
    @Override
    public void onBindViewHolder (OurArrangementShowCommentViewHolder holder, int position) {


        final ObjectSmartEFBComment commentElement = ourArrangementShowCommentList.get(position);




        // set author name and date/time
        String tmpAuthorName = commentElement.getAuthorName();
        if (tmpAuthorName.equals(prefs.getString(ConstansClassSettings.namePrefsClientName, "Unbekannt"))) {
            tmpAuthorName = contextForActivity.getResources().getString(R.string.ourArrangementShowCommentPersonalAuthorName);
        }

        String commentDate = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "dd.MM.yyyy");
        String commentTime = EfbHelperClass.timestampToDateFormat(commentElement.getLocalTime(), "HH:mm");;
        String tmpTextAuthorNameLastActualComment = "";
        tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDate), tmpAuthorName, commentDate, commentTime);
        if (commentElement.getStatus() == 4) {tmpTextAuthorNameLastActualComment = String.format(contextForActivity.getResources().getString(R.string.ourArrangementShowCommentAuthorNameWithDateExternal), tmpAuthorName, commentDate, commentTime);} // comment from external-> show not text: locale smartphone time!!!

        holder.tmpTextViewAuthorNameLastActualComment.setText(HtmlCompat.fromHtml(tmpTextAuthorNameLastActualComment, HtmlCompat.FROM_HTML_MODE_LEGACY));




    }
*/







    @Override
    public int getItemCount() {
        switch (STATE) {
            case BOTH:
                // if both, return 2 additional for header & footerResource
                Log.d("++++++ Get Item Count ->", "BOTH +2 ["+(ourArrangementShowCommentList.size()+2)+"]");

                return ourArrangementShowCommentList.size() + 2;
            case ITEM:
                // if just items, return just the length
                Log.d("++++++ Get Item Count ->", "Item size");

                return ourArrangementShowCommentList.size();
            default:
                // if either or, return 1 additional
                Log.d("++++++ Get Item Count ->", "Default +1");
                return ourArrangementShowCommentList.size() + 1;
        }
    }


}

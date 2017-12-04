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
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 22.11.2017.
 */

public class MeetingSuggestionOverviewCursorAdapter extends CursorAdapter {




    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingSuggestionOverviewCursorAdapterContext;

    // reference to the DB
    DBAdapter myDb;

    // for prefs
    SharedPreferences prefs;

    // max number of simultaneous meeting checkboxes
    static final int maxSimultaneousMeetingCheckBoxes = ConstansClassMeeting.maxNumbersOfSuggestion;

    // int array for checkbox values (DbId)
    int [] checkBoxSuggestionsValues = new int [maxSimultaneousMeetingCheckBoxes+1];

    // array of meeting places names (only 3 possible-> 0=nothing; 1=Werder(Havel); 2=Bad Belzig)
    String  meetingPlaceNames[] = new String[3];

    // number of min votes for suggestion
    int minNumberOfVotes = 1;
    
    // init view elements in array
    int [] linearLayoutSuggestionHolder = new int[] {R.id.listSuggestionLine1, R.id.listSuggestionLine2, R.id.listSuggestionLine3, R.id.listSuggestionLine4, R.id.listSuggestionLine5, R.id.listSuggestionLine6 };
    int[] textViewSuggestionDateAndTime = new int[] {R.id.listActualDateAndTimeSuggestion1, R.id.listActualDateAndTimeSuggestion2, R.id.listActualDateAndTimeSuggestion3, R.id.listActualDateAndTimeSuggestion4, R.id.listActualDateAndTimeSuggestion5, R.id.listActualDateAndTimeSuggestion6 };
    int[] textViewSuggestionPlace = new int[] {R.id.listActualPlaceSuggestion1, R.id.listActualPlaceSuggestion2, R.id.listActualPlaceSuggestion3, R.id.listActualPlaceSuggestion4, R.id.listActualPlaceSuggestion5, R.id.listActualPlaceSuggestion6 };
    String [] nameDbColumNamePlace = new String[ConstansClassMeeting.maxNumbersOfSuggestion];
    String [] nameDbColumNameDate = new String[ConstansClassMeeting.maxNumbersOfSuggestion];
    int [] checkboxVoteSuggestion = new int[] {R.id.suggestionCheck1, R.id.suggestionCheck2, R.id.suggestionCheck3, R.id.suggestionCheck4, R.id.suggestionCheck5, R.id.suggestionCheck6 };


    // count list view elements vote for
    int countListViewElementVote = 0;

    // number of check box in view
    int countNumberOfCheckBoxObjects = 0;



    // Own constructor
    public MeetingSuggestionOverviewCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        meetingSuggestionOverviewCursorAdapterContext = context;

        // init the DB
        myDb = new DBAdapter(context);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // open sharedPrefs
        prefs = context.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, context.MODE_PRIVATE);

        // init meeting plce array
        meetingPlaceNames = context.getResources().getStringArray(R.array.placesNameForMeetingArray);

        // init checkbox value array
        for (int i=0; i<maxSimultaneousMeetingCheckBoxes+1; i++) {
            checkBoxSuggestionsValues[i] = -1;
        }

        // init db colum names for use in view
        nameDbColumNameDate[0] = DBAdapter.MEETING_SUGGESTION_KEY_DATE1;
        nameDbColumNameDate[1] = DBAdapter.MEETING_SUGGESTION_KEY_DATE2;
        nameDbColumNameDate[2] = DBAdapter.MEETING_SUGGESTION_KEY_DATE3;
        nameDbColumNameDate[3] = DBAdapter.MEETING_SUGGESTION_KEY_DATE4;
        nameDbColumNameDate[4] = DBAdapter.MEETING_SUGGESTION_KEY_DATE5;
        nameDbColumNameDate[5] = DBAdapter.MEETING_SUGGESTION_KEY_DATE6;

        nameDbColumNamePlace[0] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE1;
        nameDbColumNamePlace[1] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE2;
        nameDbColumNamePlace[2] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE3;
        nameDbColumNamePlace[3] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE4;
        nameDbColumNamePlace[4] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE5;
        nameDbColumNamePlace[5] = DBAdapter.MEETING_SUGGESTION_KEY_PLACE6;

    }




    @Override
    public void bindView(View mView, final Context context, Cursor mCursor) {

        final View view = mView;

        final Cursor cursor = mCursor;

        if (cursor.isFirst() ) { // listview for last element

            // find send button "verbindich senden"
            Button tmpSendButton = (Button) view.findViewById(R.id.buttonSendSuggestionToCoach);

            // onClick listener make meeting
            tmpSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (countListViewElementVote >= minNumberOfVotes) { // too little suggestions?

                        // translate vote results from check boxes to variables for update db
                        int tmpResultVote1 = 0;
                        if (checkBoxSuggestionsValues[1] > -1) {tmpResultVote1 = 1;}
                        int tmpResultVote2 = 0;
                        if (checkBoxSuggestionsValues[2] > -1) {tmpResultVote2 = 1;}
                        int tmpResultVote3 = 0;
                        if (checkBoxSuggestionsValues[3] > -1) {tmpResultVote3 = 1;}
                        int tmpResultVote4 = 0;
                        if (checkBoxSuggestionsValues[4] > -1) {tmpResultVote4 = 1;}
                        int tmpResultVote5 = 0;
                        if (checkBoxSuggestionsValues[5] > -1) {tmpResultVote5 = 1;}
                        int tmpResultVote6 = 0;
                        if (checkBoxSuggestionsValues[6] > -1) {tmpResultVote6 = 1;}


                        Log.d("SugCursorAdapter->>", "Tesult 1:"+tmpResultVote1);
                        Log.d("SugCursorAdapter->>", "Tesult 2:"+tmpResultVote2);
                        Log.d("SugCursorAdapter->>", "Tesult 3:"+tmpResultVote3);

                        String tmpClientCommentSuggestionText = "";


                        // canceled time
                        Long tmpVoteDate = System.currentTimeMillis();

                        // canceled status
                        int tmpStatus = 0; // not send to server

                        // insert  in DB
                        Long clientVoteDbId = cursor.getLong(cursor.getColumnIndex(DBAdapter.KEY_ROWID));

                        Log.d("Cursor Adapter -->", "VOTE ID:"+clientVoteDbId);

                        if (myDb.updateSuggestionVoteAndCommentByClient(tmpResultVote1, tmpResultVote2, tmpResultVote3, tmpResultVote4, tmpResultVote5, tmpResultVote6, clientVoteDbId, tmpVoteDate, prefs.getString(ConstansClassConnectBook.namePrefsConnectBookUserName, "Unbekannt"), tmpClientCommentSuggestionText, tmpStatus)) {
                            Log.d("DB RETURN", "Erfolgreich eingetragen!");
                        }

                        // set successfull message in parent activity
                        //String tmpSuccessfullMessage = getResources().getString(getResources().getIdentifier("toastMessageMeetingCanceledMeetingByClientSuccessfullSend", "string", fragmentClientCanceledMeetingContext.getPackageName()));
                        //((ActivityMeeting) getActivity()).setSuccessefullMessageForSending (tmpSuccessfullMessage);


                        // send intent to service to start the service and send canceled meeting to server!
                        Intent startServiceIntent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ExchangeServiceEfb.class);
                        startServiceIntent.putExtra("com","send_meeting_data");
                        startServiceIntent.putExtra("dbid",clientVoteDbId);
                        meetingSuggestionOverviewCursorAdapterContext.startService(startServiceIntent);

                        // build intent to go back to suggestionOverview
                        Intent intent = new Intent(meetingSuggestionOverviewCursorAdapterContext, ActivityMeeting.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("com", "suggestion_overview");
                        meetingSuggestionOverviewCursorAdapterContext.startActivity(intent);








                    }
                    else { // error too little suggestions!

                        // show error message in view
                        TextView textViewTooLittleSuggestionChoosen = (TextView) view.findViewById(R.id.suggestionErrorToFewSuggestionsChoosen);
                        textViewTooLittleSuggestionChoosen.setVisibility(View.VISIBLE);

                        // show error toast
                        //Toast.makeText(meetingFindMeetingCursorAdapterContext, meetingFindMeetingCursorAdapterContext.getResources().getString(R.string.errorCountTooLittleCheckBoexesCheckedText), Toast.LENGTH_SHORT).show();
                    }

                }


            });

        }










    }



    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        final Context context = mContext;

        if (cursor.isFirst() && cursor.getCount() > 1) { // listview for first element, when cursor has more then one element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_overview_first, parent, false);

        }
        else if (cursor.isFirst() && cursor.getCount() == 1) { // listview for first element, when cursor has only one element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_overview_firstandlast, parent, false);

        }
        else if (cursor.isLast()) { // listview for last element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_overview_last, parent, false);

        }
        else { // listview for "normal" element
            inflatedView = cursorInflater.inflate(R.layout.list_meeting_suggestion_overview_normal, parent, false);

        }

        // set title and infotext of suggestion
        // title
        TextView tmpTextViewTitleForSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionTitleAndNumber);
        String tmpTitleForSuggestion = "";
        // info text
        TextView tmpTextViewAuthorNameForSuggestion = (TextView) inflatedView.findViewById(R.id.suggestionAuthorAndDate);
        String tmpAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_AUTHOR));
        String meetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "dd.MM.yyyy");;
        String meetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CREATION_TIME)), "HH:mm");;

        if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED)) == 1) {

            // set info text with canceled info
            String tmpCanceledAuthorName = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_AUTHOR));
            String meetingCanceledDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "dd.MM.yyyy");
            String meetingCanceledTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CANCELED_TIME)), "HH:mm");
            String tmpTextAuthorNameSuggestionWithCancele = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDateWithCancele), tmpAuthorName, meetingDate, meetingTime, tmpCanceledAuthorName, meetingCanceledDate, meetingCanceledTime);
            tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameSuggestionWithCancele));

            // set title "suggestion canceled"
            tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewCanceledSuggestionTitle);
            tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);

        }
        else {

            //check if suggestions votes!
            if (cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE1)) > 0 || cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE2)) > 0 || cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE3)) > 0 || cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE4)) > 0 || cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE5)) > 0 || cursor.getInt(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_VOTE6)) > 0) {



            }
            else {

                // set info text
                String tmpTextAuthorNameMeeting = String.format(context.getResources().getString(R.string.meetingOverviewSuggestionAuthorAndDate), tmpAuthorName, meetingDate, meetingTime);
                tmpTextViewAuthorNameForSuggestion.setText(Html.fromHtml(tmpTextAuthorNameMeeting));

                // set title "actual suggestion"
                tmpTitleForSuggestion = context.getResources().getString(R.string.suggestionOverviewActualSuggestionTitle);
                tmpTextViewTitleForSuggestion.setText(tmpTitleForSuggestion);
            }

        }


        // check if suggestion entry new?
        if (cursor.getInt(cursor.getColumnIndex(myDb.MEETING_SUGGESTION_MEETING_KEY_NEW_METT_SUGGEST)) == 1) {
            TextView newEntryOfMeeting = (TextView) inflatedView.findViewById(R.id.meetingNewSuggestionText);
            String txtNewEntryOfMeeting = context.getResources().getString(R.string.newEntryText);
            newEntryOfMeeting.setText(txtNewEntryOfMeeting);

            // delete status new entry in db
            myDb.deleteStatusNewEntryMeetingAndSuggestion(cursor.getLong(cursor.getColumnIndex(myDb.KEY_ROWID)));
        }


        // set "maxNumbersOfSuggestion" suggestion to view
        for (int t=1; t<ConstansClassMeeting.maxNumbersOfSuggestion; t++) {

            if (linearLayoutSuggestionHolder[t] > 0 && cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])) > 0) { // check LayoutHolder and date of suggestion > 0
                // linear layout for suggetion
                LinearLayout tmpSuggestionHolder = (LinearLayout) inflatedView.findViewById(linearLayoutSuggestionHolder[t-1]);
                tmpSuggestionHolder.setVisibility(View.VISIBLE);

                // text view for date
                TextView tmpTextViewMeetingDateAndTime = (TextView) inflatedView.findViewById(textViewSuggestionDateAndTime[t-1]);
                String tmpMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])), "dd.MM.yyyy");;
                String tmpMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(nameDbColumNameDate[t-1])), "HH:mm");;
                String tmpDateAndTimeForSuggestion = String.format(context.getResources().getString(R.string.meetingOverviewSuggestiondateAndTimeText), tmpMeetingDate, tmpMeetingTime);
                tmpTextViewMeetingDateAndTime.setText(tmpDateAndTimeForSuggestion);

                // textview for meeting place
                TextView tmpTextViewMeetingPlace = (TextView) inflatedView.findViewById(textViewSuggestionPlace[t-1]);
                String tmpMeetingPlace = meetingPlaceNames[cursor.getInt(cursor.getColumnIndex(nameDbColumNamePlace[t-1]))];
                tmpTextViewMeetingPlace.setText(tmpMeetingPlace);


                // set on click listener for checkboxes to vote
                CheckBox tmpSuggestionCheckBox = (CheckBox) inflatedView.findViewById(checkboxVoteSuggestion[t-1]);
                tmpSuggestionCheckBox.setOnClickListener(new onClickListenerCheckBoxSuggestionVote(t));



            }

        }

        // set hint text for suggestion
        String tmpSuggestionHintText = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_COACH_HINT_TEXT));
        if (tmpSuggestionHintText.length() > 0) {
            TextView tmpTextViewSuggestionHintText = (TextView) inflatedView.findViewById(R.id.suggestionHintText);
            tmpTextViewSuggestionHintText.setText(tmpSuggestionHintText);
            tmpTextViewSuggestionHintText.setVisibility(View.VISIBLE);
        }


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








    //
    // onClickListener for vote for timezone
    //
    public class onClickListenerCheckBoxSuggestionVote implements View.OnClickListener {

        int listPosition = 0;

        public onClickListenerCheckBoxSuggestionVote (int tmpListPosition) {

            this.listPosition = tmpListPosition;

            countNumberOfCheckBoxObjects++;

        }


        @Override
        public void onClick(View v) {

            // Is the checkBox checked?
            boolean checked = ((CheckBox) v).isChecked();

            if (checked) {
                checkBoxSuggestionsValues[listPosition] = 1;
                countListViewElementVote++; //inc count checkboxes

                Log.d("OnClickCheckBoy-->","ListP:"+listPosition+" +Array:"+checkBoxSuggestionsValues[listPosition]);


            }
            else {
                checkBoxSuggestionsValues[listPosition] = -1;
                countListViewElementVote--; //dec count checkboxes

                Log.d("OnClickCheckBoy-->","NICHT SETZEN!");

            }

        }
    }












}

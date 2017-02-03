package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ich on 09.01.2017.
 */
public class MeetingFindMeetingCursorAdapter extends CursorAdapter {


    // hold layoutInflater
    private LayoutInflater cursorInflater;

    // context for cursor adapter
    Context meetingFindMeetingCursorAdapterContext;

    // meeting suggestions author
    String meetingSuggestionsAuthor = "";

    // deadline for responding of meeting suggestions
    long meetingSuggestionsResponeseDeadline = 0;

    // max number of simultaneous meeting checkboxes
    static final int maxSimultaneousMeetingCheckBoxes = 20;

    // int array for checkbox values (DbId)
    int [] checkBoxMeetingSuggestionsValues = new int [maxSimultaneousMeetingCheckBoxes];

    // min number of checkboxes to check
    static final int minNumberCheckBoxesToCheck = 1;

    // count checked checkBoxes
    int countCheckBoxChecked = 0;

    // reference for textView for error suggestion choosen
    TextView textViewTooLittleSuggestionChoosen;

    // reference to the DB
    DBAdapter myDb;

    // count list view elements
    int countListViewElements = 0;


    // Own constructor
    public MeetingFindMeetingCursorAdapter(Context context, Cursor cursor, int flags, String tmpAuthorMeetingSuggestion, long tmpResponeseDeadline) {

        super(context, cursor, flags);

        meetingFindMeetingCursorAdapterContext = context;

        meetingSuggestionsAuthor = tmpAuthorMeetingSuggestion;

        meetingSuggestionsResponeseDeadline = tmpResponeseDeadline;

        // init the DB
        myDb = new DBAdapter(context);

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // init checkbox value array
        for (int i=0; i<maxSimultaneousMeetingCheckBoxes; i++) {

            checkBoxMeetingSuggestionsValues[i] = -1;
        }

    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // find textView for error viewing
        textViewTooLittleSuggestionChoosen = (TextView) view.findViewById(R.id.errorCountTooLittleCheckBoexesChecked);

        // count list view elements for send button listener
        countListViewElements = cursor.getCount();


        if (cursor.isFirst() ) { // listview for first element

            // show meeting intro text
            String txtFindFirstMeetingIntro  = context.getResources().getString(R.string.findMeetingIntroTextNoMeetingSoFar);
            TextView textViewNextMeetingIntroText = (TextView) view.findViewById(R.id.nextMeetingIntroText);
            textViewNextMeetingIntroText.setText(txtFindFirstMeetingIntro);

            // set movement methode for explain text find first + meeting telephone link
            TextView tmpTextFindFirstMeetingExplainText = (TextView) view.findViewById(R.id.showExplainTextForFindFirstMeeting);
            tmpTextFindFirstMeetingExplainText.setVisibility(View.VISIBLE);
            tmpTextFindFirstMeetingExplainText.setMovementMethod(LinkMovementMethod.getInstance());

            // set author of suggestion text
            TextView tmpTextFindMeetingAuthorSuggestionMeeting = (TextView) view.findViewById(R.id.showExplainTextAuthorSuggestionMeeting);
            String tmpExplainTextMeetingFind = context.getResources().getString(R.string.findMeetingAuthorSuggestionMeeting);
            tmpExplainTextMeetingFind = String.format(tmpExplainTextMeetingFind, meetingSuggestionsAuthor);
            tmpTextFindMeetingAuthorSuggestionMeeting.setText(tmpExplainTextMeetingFind);
            tmpTextFindMeetingAuthorSuggestionMeeting.setVisibility(View.VISIBLE);

            // show response deadline for meeting suggestions
            TextView tmpResponseDeadline = (TextView) view.findViewById(R.id.showResponseDeadlineForMeetingSuggestions);
            String tmpTextResponseDeadline = context.getResources().getString(R.string.textResponseDeadlineForMeetingSuggestions);
            tmpTextResponseDeadline = String.format(tmpTextResponseDeadline, EfbHelperClass.timestampToDateFormat(meetingSuggestionsResponeseDeadline, "dd.MM.yyyy"));
            tmpResponseDeadline.setText(tmpTextResponseDeadline);
            tmpResponseDeadline.setVisibility(View.VISIBLE);

        }


        // put date text
        TextView tmpDateMeetingSuggestion = (TextView) view.findViewById(R.id.listActualDateMeetingSuggestion);
        String tmpTextMeetingDate = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_DATE_TIME)), "dd.MM.yyyy");
        tmpDateMeetingSuggestion.setText(tmpTextMeetingDate);

        // put time text
        TextView tmpTimeMeetingSuggestion = (TextView) view.findViewById(R.id.listActualTimeMeetingSuggestion);
        String tmpTextMeetingTime = EfbHelperClass.timestampToDateFormat(cursor.getLong(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_DATE_TIME)), "HH:mm");
        String tmpTextMeetingTimeAdditionalWordClock = context.getResources().getString(R.string.showClockWordAdditionalText);
        tmpTimeMeetingSuggestion.setText(", " + tmpTextMeetingTime +" " + tmpTextMeetingTimeAdditionalWordClock );

        // put place text
        TextView tmpPlaceMeetingSuggestion = (TextView) view.findViewById(R.id.listActualPlaceMeetingSuggestion);
        String tmpTextMeetingPlace = cursor.getString(cursor.getColumnIndex(DBAdapter.MEETING_FIND_MEETING_KEY_MEETING_PLACE));
        tmpPlaceMeetingSuggestion.setText(tmpTextMeetingPlace);


        CheckBox tmpMeetingCheckBox = (CheckBox) view.findViewById(R.id.meetingCheck);
        tmpMeetingCheckBox.setOnClickListener(new onClickListenerMeetingSuggestion(cursor.getInt(cursor.getColumnIndex(DBAdapter.KEY_ROWID)), cursor.getPosition()));




        if (cursor.isLast() ) { // listview for last element

            // find send button "verbindich senden"
            Button tmpSendButton = (Button) view.findViewById(R.id.buttonSendSuggestionToCoach);

            // onClick listener make meeting
            tmpSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                        if (countCheckBoxChecked >= minNumberCheckBoxesToCheck) { // too little suggestions?


                            for (int i=0; i<=countListViewElements; i++) {

                                if (checkBoxMeetingSuggestionsValues[i] >= 0) {
                                    myDb.setUnsetStatusApprovalMeetingFindMeeting(checkBoxMeetingSuggestionsValues[i],true);

                                }
                                else {
                                    myDb.setUnsetStatusApprovalMeetingFindMeeting(checkBoxMeetingSuggestionsValues[i],false);

                                }

                            }

                           Toast.makeText(meetingFindMeetingCursorAdapterContext, meetingFindMeetingCursorAdapterContext.getResources().getString(R.string.textMeetingFindMeetingSuggestionSuccesfullSend), Toast.LENGTH_SHORT).show();

                            // TODO ->
                            //
                            // Netzwerk status pruefen
                            // Terminanfrage senden
                            // Ergebnis anzeigen

                            // send intent back to activity meeting
                            Intent intent = new Intent(meetingFindMeetingCursorAdapterContext, ActivityMeeting.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("com", "find_meeting");
                            intent.putExtra("update", true);
                            intent.putExtra("pop_stack", false);
                            intent.putExtra("met_status", 6); // TODO Meeting Status anpassen 6 = erstemal einen Terminvorschlag gewählt; 7= wiederholtemal einen Terminvorschlag gewählt
                            meetingFindMeetingCursorAdapterContext.startActivity(intent);


                        }
                        else { // error too little suggestions!

                            // show error message in view
                            textViewTooLittleSuggestionChoosen.setVisibility(View.VISIBLE);

                            // show error toast
                            Toast.makeText(meetingFindMeetingCursorAdapterContext, meetingFindMeetingCursorAdapterContext.getResources().getString(R.string.errorCountTooLittleCheckBoexesCheckedText), Toast.LENGTH_SHORT).show();
                        }

                }


            });

        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View inflatedView;

        if (cursor.isLast() ) { // listview for last element

            inflatedView = cursorInflater.inflate(R.layout.list_find_meeting_last_suggestion, parent, false);
        }
        else if (cursor.isFirst() ) { // listview for first element

            inflatedView = cursorInflater.inflate(R.layout.list_find_meeting_first_suggestion, parent, false);
        }
        else { // listview for normal element
            inflatedView = cursorInflater.inflate(R.layout.list_find_meeting_suggestion, parent, false);
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
    // onClickListener for checkboxes to choose timezone
    //
    public class onClickListenerMeetingSuggestion implements View.OnClickListener {

        int suggestionDbId = 0;
        int listPosition = 0;

        public onClickListenerMeetingSuggestion (int tmpSuggestionDbId, int tmpListPosition) {

            this.suggestionDbId = tmpSuggestionDbId;
            this.listPosition = tmpListPosition;

        }


        @Override
        public void onClick(View v) {

            // Is the checkBox checked?
            boolean checked = ((CheckBox) v).isChecked();

            if (checked) {
                checkBoxMeetingSuggestionsValues[listPosition] = suggestionDbId;
                countCheckBoxChecked++; //inc count checkboxes

            }
            else {
                checkBoxMeetingSuggestionsValues[listPosition] = -1;
                countCheckBoxChecked--; //dec count checkboxes

            }

        }
    }






}

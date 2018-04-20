package de.smart_efb.efbapp.smartefb;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Arrays;


/**
 * Created by ich on 24.01.2018.
 */

public class AlarmReceiverMeeting extends BroadcastReceiver {


    // reference to the DB
    DBAdapter myDb;

    // shared prefs for the comment goals
    SharedPreferences prefs;

    // array for names of meeting places
    String  meetingPlaceNames[] = new String[3];

    // the context
    Context mContext;



    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        
        Long noWakeUpPointSetValue = 9999999900000L;

        Long [] nextWakeUp = new Long [4]; // array to hold the next timestamps for wake up time of alarm manager; will be sorted ascending

        Long delta_15min_upperLimit = 17L * 60L * 1000L; // 17 min in mills
        Long delta_120min_upperLimit = 122L * 60L * 1000L; // 122 min in mills
        Long delta_1440min_upperLimit = 24L * 60L * 60L * 1000L + (2L * 60L * 1000L); // 1442 (24h) min in mills
        
        Intent  mainActivityIntent;
        
        String meetingTextIn15Min = ""; // 15 minutes
        String meetingTextIn120Min = ""; // 2 hours
        String meetingTextIn1440Min = ""; // 24 hours
        
        String suggestionEndTextIn15Min = ""; // suggestion coach end in 15 minutes
        String suggestionEndTextIn120Min = ""; // suggestion coach end in 120 minutes
        String suggestionEndTextIn1440Min = ""; // suggestion coach end in 1440 minutes (24h)

        String clientSuggestionEndTextIn15Min = ""; // client suggestion end in 15 minutes
        String clientSuggestionEndTextIn120Min = ""; // client suggestion end in 120 minutes
        String clientSuggestionEndTextIn1440Min = ""; // client suggestion end in 1440 minutes (24h)

        String clientSuggestionStartTextIn15Min = ""; // client suggestion start in 15 minutes
        String clientSuggestionStartTextIn120Min = ""; // client suggestion start in 120 minutes
        String clientSuggestionStartTextIn1440Min = ""; // client suggestion start in 1440 minutes (24h)
        
        String lineFeed = "";
        
        // init array for meeting places
        meetingPlaceNames = mContext.getResources().getStringArray(R.array.placesNameForMeetingArray);

        // init the DB
        myDb = new DBAdapter(mContext);

        // open sharedPrefs
        prefs = mContext.getSharedPreferences(ConstansClassMain.namePrefsMainNamePrefs, mContext.MODE_PRIVATE);

        // get notification manager
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

        // get alarm tone
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // new notification builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        // set basic things to all notifications
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notification_large_appicon));
        mBuilder.setSmallIcon(R.drawable.notification_smile);
        mBuilder.setAutoCancel(true);

        // needed for back stack -> start main activity after pressing back
        mainActivityIntent = new Intent(mContext, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // set now time for db request
        Long nowTime = System.currentTimeMillis();

        // check notification for meeting on?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_RememberMeeting, true)) {

            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // check meeting in 15 minutes
            Cursor rememberMeeting_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_15min", nowTime);
            if (rememberMeeting_15min != null && rememberMeeting_15min.getCount() > 0) {
                rememberMeeting_15min.moveToFirst();
                do {
                    String tmpMeetingNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextRemember15Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_15min.getInt(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
                    meetingTextIn15Min += tmpMeetingNotificationText15 + lineFeed;
                    lineFeed = "\n";
                    // update meeting remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 15); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberMeeting_15min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check meeting in 120 minutes
            Cursor rememberMeeting_120min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_120min", nowTime);
            if (rememberMeeting_120min != null && rememberMeeting_120min.getCount() > 0) {
                rememberMeeting_120min.moveToFirst();
                do {
                    String tmpMeetingNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextRemember120Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_120min.getLong(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_120min.getLong(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_120min.getInt(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
                    meetingTextIn120Min += tmpMeetingNotificationText120 + lineFeed;
                    lineFeed = "\n";
                    // update meeting remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberMeeting_120min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 10); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberMeeting_120min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check meeting in 1440 minutes; 24hours!
            Cursor rememberMeeting_1440min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_1440min", nowTime);
            if (rememberMeeting_1440min != null && rememberMeeting_1440min.getCount() > 0) {
                rememberMeeting_1440min.moveToFirst();
                do {
                    String tmpMeetingNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextRemember1440Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_1440min.getLong(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_1440min.getLong(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_1440min.getInt(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
                    meetingTextIn1440Min += tmpMeetingNotificationText1440 + lineFeed;
                    lineFeed = "\n";
                    // update meeting remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberMeeting_1440min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 5); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberMeeting_1440min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check next wakeup for alarm receiver meeting
            Cursor rememberMeeting_nextWakeUp = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_next_wakeup", nowTime);
            if (rememberMeeting_nextWakeUp != null && rememberMeeting_nextWakeUp.getCount() > 0) {
                rememberMeeting_nextWakeUp.moveToFirst();
                nextWakeUp[0] = rememberMeeting_nextWakeUp.getLong(rememberMeeting_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1));
                Log.d("AlarmMeeting-->", "WakeUp0 :"+nextWakeUp[0]);
            }
            else { // no wake up point in suggestion -> set wake up point in far future!
                nextWakeUp[0] = noWakeUpPointSetValue;
                Log.d("AlarmMeeting-->", "WakeUp0 ELSE:"+nextWakeUp[0]);
            }

            // notification meeting in 15 minutes
            if (meetingTextIn15Min.length() > 0) {
                notificationMeetingIn15Minutes(meetingTextIn15Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }

            // notification meeting in 120 minutes
            if (meetingTextIn120Min.length() > 0) {
                notificationMeetingIn120Minutes(meetingTextIn120Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }

            // notification meeting in 1440 minutes; 24 hours
            if (meetingTextIn1440Min.length() > 0) {
                notificationMeetingIn1440Minutes(meetingTextIn1440Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
        }

        // check notification for suggestion and client suggestion on?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationVisualSignal_RememberSuggestion, true)) {

            // check suggestion from coach end time in 15 minutes
            Cursor rememberSuggestionEnd_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_suggestion_end_15min", nowTime);
            if (rememberSuggestionEnd_15min != null && rememberSuggestionEnd_15min.getCount() > 0) {
                rememberSuggestionEnd_15min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextSuggestionEndIn15Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_15min.getLong(rememberSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_15min.getLong(rememberSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
                    suggestionEndTextIn15Min += tmpSuggestionEndNotificationText15 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberSuggestionEnd_15min.getLong(rememberSuggestionEnd_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 15); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberSuggestionEnd_15min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check suggestion from coach end time in 120 minutes
            Cursor rememberSuggestionEnd_120min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_suggestion_end_120min", nowTime);
            if (rememberSuggestionEnd_120min != null && rememberSuggestionEnd_120min.getCount() > 0) {
                rememberSuggestionEnd_120min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextSuggestionEndIn120Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_120min.getLong(rememberSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_120min.getLong(rememberSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
                    suggestionEndTextIn120Min += tmpSuggestionEndNotificationText120 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberSuggestionEnd_120min.getLong(rememberSuggestionEnd_120min.getColumnIndex(DBAdapter.KEY_ROWID)), 10); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberSuggestionEnd_120min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check suggestion from coach end time in 1440 minutes
            Cursor rememberSuggestionEnd_1440min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_suggestion_end_1440min", nowTime);
            if (rememberSuggestionEnd_1440min != null && rememberSuggestionEnd_1440min.getCount() > 0) {
                rememberSuggestionEnd_1440min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextSuggestionEndIn1440Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_1440min.getLong(rememberSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_1440min.getLong(rememberSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
                    suggestionEndTextIn1440Min += tmpSuggestionEndNotificationText1440 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberSuggestionEnd_1440min.getLong(rememberSuggestionEnd_1440min.getColumnIndex(DBAdapter.KEY_ROWID)), 5); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberSuggestionEnd_1440min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check next wakeup for alarm receiver coach suggestion
            Cursor rememberSuggestion_nextWakeUp = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_suggestion_next_wakeup", nowTime);
            if (rememberSuggestion_nextWakeUp != null && rememberSuggestion_nextWakeUp.getCount() > 0) {
                rememberSuggestion_nextWakeUp.moveToFirst();
                nextWakeUp[1] = rememberSuggestion_nextWakeUp.getLong(rememberSuggestion_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME));
                Log.d("AlarmMeeting-->", "WakeUp1 :"+nextWakeUp[1]);
            }
            else { // no wake up point in suggestion -> set wake up point in far future!
                nextWakeUp[1] = noWakeUpPointSetValue;
                Log.d("AlarmMeeting-->", "WakeUp1 ELSE:"+nextWakeUp[1]);
            }
            
            // notification coach suggestion ends in 15 minutes
            if (suggestionEndTextIn15Min.length() > 0) {
                notificationCoachSuggestionEndIn15Minutes(suggestionEndTextIn15Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification coach suggestion ends in 120 minutes
            if (suggestionEndTextIn120Min.length() > 0) {
                notificationCoachSuggestionEndIn120Minutes(suggestionEndTextIn120Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification coach suggestion ends in 1440 minutes
            if (suggestionEndTextIn1440Min.length() > 0) {
                notificationCoachSuggestionEndIn1440Minutes(suggestionEndTextIn1440Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }

            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // check client suggestion start time in 15 minutes
            Cursor rememberClientSuggestionStart_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_start_15min", nowTime);
            if (rememberClientSuggestionStart_15min != null && rememberClientSuggestionStart_15min.getCount() > 0) {
                rememberClientSuggestionStart_15min.moveToFirst();
                do {
                    String tmpClientSuggestionStartNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionStartIn15Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"));
                    clientSuggestionStartTextIn15Min += tmpClientSuggestionStartNotificationText15 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 15); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberClientSuggestionStart_15min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check client suggestion start time in 120 minutes
            Cursor rememberClientSuggestionStart_120min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_start_120min", nowTime);
            if (rememberClientSuggestionStart_120min != null && rememberClientSuggestionStart_120min.getCount() > 0) {
                rememberClientSuggestionStart_120min.moveToFirst();
                do {
                    String tmpClientSuggestionStartNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionStartIn120Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_120min.getLong(rememberClientSuggestionStart_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_120min.getLong(rememberClientSuggestionStart_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_120min.getLong(rememberClientSuggestionStart_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_120min.getLong(rememberClientSuggestionStart_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"));
                    clientSuggestionStartTextIn120Min += tmpClientSuggestionStartNotificationText120 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionStart_120min.getLong(rememberClientSuggestionStart_120min.getColumnIndex(DBAdapter.KEY_ROWID)), 10); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberClientSuggestionStart_120min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check client suggestion start time in 1440 minutes
            Cursor rememberClientSuggestionStart_1440min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_start_1440min", nowTime);
            if (rememberClientSuggestionStart_1440min != null && rememberClientSuggestionStart_1440min.getCount() > 0) {
                rememberClientSuggestionStart_1440min.moveToFirst();
                do {
                    String tmpClientSuggestionStartNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionStartIn1440Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_1440min.getLong(rememberClientSuggestionStart_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_1440min.getLong(rememberClientSuggestionStart_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_1440min.getLong(rememberClientSuggestionStart_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_1440min.getLong(rememberClientSuggestionStart_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"));
                    clientSuggestionStartTextIn1440Min += tmpClientSuggestionStartNotificationText1440 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionStart_1440min.getLong(rememberClientSuggestionStart_1440min.getColumnIndex(DBAdapter.KEY_ROWID)), 10); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberClientSuggestionStart_1440min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check next wakeup for alarm receiver client start suggestion
            Cursor rememberClientSuggestionStart_nextWakeUp = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_next_wakeup", nowTime);
            if (rememberClientSuggestionStart_nextWakeUp != null && rememberClientSuggestionStart_nextWakeUp.getCount() > 0) {
                rememberClientSuggestionStart_nextWakeUp.moveToFirst();
                nextWakeUp[2] = rememberClientSuggestionStart_nextWakeUp.getLong(rememberClientSuggestionStart_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE));
                Log.d("AlarmMeeting-->", "WakeUp2 :"+nextWakeUp[2]);
            }
            else { // no wake up point in client start suggestion -> set wake up point in far future!
                nextWakeUp[2] = noWakeUpPointSetValue;
                Log.d("AlarmMeeting-->", "WakeUp2 ELSE :"+nextWakeUp[2]);
            }

            // notification client start suggestion in 15 minutes
            if (clientSuggestionStartTextIn15Min.length() > 0) {
                notificationClientStartSuggestionIn15Minutes(clientSuggestionStartTextIn15Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification client start suggestion in 120 minutes
            if (clientSuggestionStartTextIn120Min.length() > 0) {
                notificationClientStartSuggestionIn120Minutes(clientSuggestionStartTextIn120Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification client start suggestion in 1440 minutes
            if (clientSuggestionStartTextIn1440Min.length() > 0) {
                notificationClientStartSuggestionIn1440Minutes(clientSuggestionStartTextIn1440Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }

            // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            // check client suggestion end time in 15 minutes
            Cursor rememberClientSuggestionEnd_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_end_15min", nowTime);
            if (rememberClientSuggestionEnd_15min != null && rememberClientSuggestionEnd_15min.getCount() > 0) {
                rememberClientSuggestionEnd_15min.moveToFirst();
                do {
                    String tmpClientSuggestionEndNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionEndIn15Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_15min.getLong(rememberClientSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_15min.getLong(rememberClientSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_15min.getLong(rememberClientSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_15min.getLong(rememberClientSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm") );
                    clientSuggestionEndTextIn15Min += tmpClientSuggestionEndNotificationText15 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionEnd_15min.getLong(rememberClientSuggestionEnd_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 30); // 0 = no remember so far; 20 = remember 24 h; 25 = remember 2 hours; 30 = remember 15 minutes (for end client suggestion
                } while (rememberClientSuggestionEnd_15min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check client suggestion end time in 120 minutes
            Cursor rememberClientSuggestionEnd_120min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_end_120min", nowTime);
            if (rememberClientSuggestionEnd_120min != null && rememberClientSuggestionEnd_120min.getCount() > 0) {
                rememberClientSuggestionEnd_120min.moveToFirst();
                do {
                    String tmpClientSuggestionEndNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionEndIn120Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_120min.getLong(rememberClientSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_120min.getLong(rememberClientSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_120min.getLong(rememberClientSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_120min.getLong(rememberClientSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm") );
                    clientSuggestionEndTextIn120Min += tmpClientSuggestionEndNotificationText120 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionEnd_120min.getLong(rememberClientSuggestionEnd_120min.getColumnIndex(DBAdapter.KEY_ROWID)), 25); // 0 = no remember so far; 20 = remember 24 h; 25 = remember 2 hours; 30 = remember 15 minutes (for end client suggestion
                } while (rememberClientSuggestionEnd_120min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check client suggestion end time in 1440 minutes
            Cursor rememberClientSuggestionEnd_1440min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_end_1440min", nowTime);
            if (rememberClientSuggestionEnd_1440min != null && rememberClientSuggestionEnd_1440min.getCount() > 0) {
                rememberClientSuggestionEnd_1440min.moveToFirst();
                do {
                    String tmpClientSuggestionEndNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverNotificationSubTextClientSuggestionEndIn1440Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_1440min.getLong(rememberClientSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_1440min.getLong(rememberClientSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_1440min.getLong(rememberClientSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionEnd_1440min.getLong(rememberClientSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm") );
                    clientSuggestionEndTextIn1440Min += tmpClientSuggestionEndNotificationText1440 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionEnd_1440min.getLong(rememberClientSuggestionEnd_1440min.getColumnIndex(DBAdapter.KEY_ROWID)), 20); // 0 = no remember so far; 20 = remember 24 h; 25 = remember 2 hours; 30 = remember 15 minutes (for end client suggestion
                } while (rememberClientSuggestionEnd_1440min.moveToNext());
                // reset line feed
                lineFeed = "";
            }

            // check next wakeup for alarm receiver client end suggestion
            Cursor rememberClientSuggestionEnd_nextWakeUp = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_end_wakeup", nowTime);
            if (rememberClientSuggestionEnd_nextWakeUp != null && rememberClientSuggestionEnd_nextWakeUp.getCount() > 0) {
                rememberClientSuggestionEnd_nextWakeUp.moveToFirst();
                nextWakeUp[3] = rememberClientSuggestionEnd_nextWakeUp.getLong(rememberClientSuggestionEnd_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE));
                Log.d("AlarmMeeting-->", "WakeUp3 :"+nextWakeUp[3]);
            }
            else { // no wake up point in client end suggestion -> set wake up point in far future!
                nextWakeUp[3] = noWakeUpPointSetValue;
                Log.d("AlarmMeeting-->", "WakeUp3 ELSE :"+nextWakeUp[3]);
            }

            // notification client end suggestion in 15 minutes
            if (clientSuggestionEndTextIn15Min.length() > 0) {
                notificationClientEndSuggestionIn15Minutes(clientSuggestionEndTextIn15Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification client end suggestion in 120 minutes
            if (clientSuggestionEndTextIn120Min.length() > 0) {
                notificationClientEndSuggestionIn120Minutes(clientSuggestionEndTextIn120Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
            // notification client end suggestion in 1440 minutes
            if (clientSuggestionEndTextIn1440Min.length() > 0) {
                notificationClientEndSuggestionIn1440Minutes(clientSuggestionEndTextIn1440Min, mainActivityIntent, mBuilder, alarmSound, mNotificationManager);
            }
        }

        // sort array with next wake up time for alarm manager ascending
        Arrays.sort(nextWakeUp);

        Log.d("AlarmMeeting-->", "After Sort 0:"+nextWakeUp[0]);
        Log.d("AlarmMeeting-->", "After Sort 1:"+nextWakeUp[1]);
        Log.d("AlarmMeeting-->", "After Sort 2:"+nextWakeUp[2]);
        Log.d("AlarmMeeting-->", "After Sort 3:"+nextWakeUp[3]);

        // set next wake up time for remember meeting/ suggestion
        if (nextWakeUp[0] > 0L && nextWakeUp[0] < noWakeUpPointSetValue) {


            Log.d("AlarmMeeting:", "SET ALARM MANAGER");

            PendingIntent pendingIntentRememberMeeting;

            Long startRememberMeeting = 0L;
            Long currentSystemTime = System.currentTimeMillis();

            if ((nextWakeUp[0]-delta_1440min_upperLimit) > currentSystemTime) {
                startRememberMeeting = nextWakeUp[0] - delta_1440min_upperLimit + 1000; // next start point for meeting/ suggestion remember function
                Log.d("AlarmMeeting:", "ALARM in 24h");
            }
            else  if ((nextWakeUp[0]-delta_120min_upperLimit) > currentSystemTime) {
                startRememberMeeting = nextWakeUp[0] - delta_120min_upperLimit + 1000; // next start point for meeting/ suggestion remember function
                Log.d("AlarmMeeting:", "ALARM in 2h");
            }
            else if ((nextWakeUp[0]-delta_15min_upperLimit) > currentSystemTime) {
                startRememberMeeting = nextWakeUp[0] - delta_15min_upperLimit + 1000; // next start point for meeting/ suggestion remember function
                Log.d("AlarmMeeting:", "ALARM in 15 min");
            }

            // set alarm manager only when next alarm is in less or greater 15 min
            if (startRememberMeeting > 0L) {


                Long repeatingMeetingRemember = 24L * 60L * 60L * 1000L; // one day

                Log.d("AlarmMeeting", "Next Wake Up:" + startRememberMeeting);


                // get reference to alarm manager
                AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

                // create intent for backcall to broadcast receiver
                Intent rememberMeetingAlarmIntent = new Intent(mContext, AlarmReceiverMeeting.class);

                // create call (pending intent) for alarm manager
                pendingIntentRememberMeeting = PendingIntent.getBroadcast(mContext, 0, rememberMeetingAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // set alarm
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startRememberMeeting, repeatingMeetingRemember, pendingIntentRememberMeeting);
            }
        }

        // close db connection
        myDb.close();
    }


    // ++++++++++++++++++++++++++++++++ Notification for meeting ++++++++++++++++++++++++++++++++++++++++++++++++++
    
    void notificationMeetingIn15Minutes (String meetingTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get meeting remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextRemember15Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_meeting");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberMeeting, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(meetingTextIn15Min));
        mBuilder.setContentText(meetingTextIn15Min);

        // show notification
        mNotificationManager.notify(201, mBuilder.build());
    }



    void notificationMeetingIn120Minutes (String meetingTextIn120Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get meeting remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextRemember120Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_meeting");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberMeeting, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(meetingTextIn120Min));
        mBuilder.setContentText(meetingTextIn120Min);

        // show notification
        mNotificationManager.notify(202, mBuilder.build());
    }


    void notificationMeetingIn1440Minutes (String meetingTextIn1440Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get meeting remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextRemember1440Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_meeting");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberMeeting, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(meetingTextIn1440Min));
        mBuilder.setContentText(meetingTextIn1440Min);

        // show notification
        mNotificationManager.notify(203, mBuilder.build());
    }


    // ++++++++++++++++++++++++++++++++ Notification for suggestion start ++++++++++++++++++++++++++++++++++++++++++++++++++

    void notificationCoachSuggestionEndIn15Minutes (String suggestionEndTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextSuggestionEndIn15Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(suggestionEndTextIn15Min));
        mBuilder.setContentText(suggestionEndTextIn15Min);

        // show notification
        mNotificationManager.notify(204, mBuilder.build());
    }


    void notificationCoachSuggestionEndIn120Minutes (String suggestionEndTextIn120Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextSuggestionEndIn120Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(suggestionEndTextIn120Min));
        mBuilder.setContentText(suggestionEndTextIn120Min);

        // show notification
        mNotificationManager.notify(205, mBuilder.build());
    }


    void notificationCoachSuggestionEndIn1440Minutes (String suggestionEndTextIn1440Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextSuggestionEndIn1440Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(suggestionEndTextIn1440Min));
        mBuilder.setContentText(suggestionEndTextIn1440Min);

        // show notification
        mNotificationManager.notify(206, mBuilder.build());
    }





    // ++++++++++++++++++++++++++++++++ Notification for client suggestion start ++++++++++++++++++++++++++++++++++++++++++++++++++

    void notificationClientStartSuggestionIn15Minutes (String clientSuggestionStartTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion start remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionStartIn15Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionStartTextIn15Min));
        mBuilder.setContentText(clientSuggestionStartTextIn15Min);

        // show notification
        mNotificationManager.notify(207, mBuilder.build());
    }


    void notificationClientStartSuggestionIn120Minutes (String clientSuggestionStartTextIn120Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion start remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionStartIn120Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionStartTextIn120Min));
        mBuilder.setContentText(clientSuggestionStartTextIn120Min);

        // show notification
        mNotificationManager.notify(208, mBuilder.build());
    }


    void notificationClientStartSuggestionIn1440Minutes (String clientSuggestionStartTextIn1440Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion start remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionStartIn1440Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionStartTextIn1440Min));
        mBuilder.setContentText(clientSuggestionStartTextIn1440Min);

        // show notification
        mNotificationManager.notify(209, mBuilder.build());
    }


    // ++++++++++++++++++++++++++++++++ Notification for client suggestion end ++++++++++++++++++++++++++++++++++++++++++++++++++


    void notificationClientEndSuggestionIn15Minutes (String clientSuggestionEndTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionEndIn15Min);

        // set intent/ pending intent to end meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionEndTextIn15Min));
        mBuilder.setContentText(clientSuggestionEndTextIn15Min);

        // show notification
        mNotificationManager.notify(210, mBuilder.build());
    }


    void notificationClientEndSuggestionIn120Minutes (String clientSuggestionEndTextIn120Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionEndIn120Min);

        // set intent/ pending intent to end meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionEndTextIn120Min));
        mBuilder.setContentText(clientSuggestionEndTextIn120Min);

        // show notification
        mNotificationManager.notify(211, mBuilder.build());
    }


    void notificationClientEndSuggestionIn1440Minutes (String clientSuggestionEndTextIn1440Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get client suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverNotificationHeadlineTextClientSuggestionEndIn1440Min);

        // set intent/ pending intent to end meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
        notificationIntent.putExtra("com","show_client_suggestion");
        notificationIntent.putExtra("meeting_id", 0L);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // generate back stack for pending intent and add main activity
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainActivityIntent);

        // add intent for meeting
        stackBuilder.addNextIntent(notificationIntent);

        // generate pending intent
        PendingIntent contentPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // set notification attributes
        mBuilder.setContentTitle(notificationContentTitle);
        mBuilder.setContentIntent(contentPendingIntent);
        // sound on/off for meeting?
        if (prefs.getBoolean(ConstansClassSettings.namePrefsNotificationAcousticSignal_RememberSuggestion, true)) {
            mBuilder.setSound(alarmSound);
        }

        // show long text in notification
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(clientSuggestionEndTextIn1440Min));
        mBuilder.setContentText(clientSuggestionEndTextIn1440Min);

        // show notification
        mNotificationManager.notify(212, mBuilder.build());
    }


}

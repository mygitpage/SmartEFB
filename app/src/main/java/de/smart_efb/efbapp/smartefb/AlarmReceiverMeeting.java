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

import java.util.Calendar;

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


        Log.d("MEETING ALARM ->", "IN ALARM RECEIVER!");

        mContext = context;
        
        Long noWakeUpPointSetValue = 9999999900000L;
        
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
        
        Long firstWakeUpMeeting = 0L;
        Long firstWakeUpSuggestion = 0L;
        Long firstWakeUpClientStartSuggestion = 0L;
        Long firstWakeUpClientEndSuggestion = 0L;

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

            // check meeting in 15 minutes
            Cursor rememberMeeting_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_meeting_15min", nowTime);
            if (rememberMeeting_15min != null && rememberMeeting_15min.getCount() > 0) {

                Log.d("MEETING ALARM ->", "IN 15 Min Meeting!");


                rememberMeeting_15min.moveToFirst();
                do {
                    String tmpMeetingNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextRemember15Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_15min.getLong(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_15min.getInt(rememberMeeting_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
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

                Log.d("MEETING ALARM ->", "IN 120 Min Meeting!");

                rememberMeeting_120min.moveToFirst();
                do {
                    String tmpMeetingNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextRemember120Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_120min.getLong(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_120min.getLong(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_120min.getInt(rememberMeeting_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
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

                Log.d("MEETING ALARM ->", "IN 1440 Min Meeting!");

                rememberMeeting_1440min.moveToFirst();
                do {
                    String tmpMeetingNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextRemember1440Min), EfbHelperClass.timestampToDateFormat(rememberMeeting_1440min.getLong(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberMeeting_1440min.getLong(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1)), "HH:mm"), meetingPlaceNames[rememberMeeting_1440min.getInt(rememberMeeting_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_PLACE1))]);
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

                Log.d("MEETING ALARM ->", "Next WakeUp Point");

                rememberMeeting_nextWakeUp.moveToFirst();
                firstWakeUpMeeting = rememberMeeting_nextWakeUp.getLong(rememberMeeting_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_DATE1));
                    
            }
            else { // no wake up point in suggestion -> set wake up point in far future!
                firstWakeUpMeeting = noWakeUpPointSetValue;
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

                Log.d("MEETING ALARM ->", "END SUGGESTION in 15 Min!");


                rememberSuggestionEnd_15min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextSuggestionEndIn15Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_15min.getLong(rememberSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_15min.getLong(rememberSuggestionEnd_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
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

                Log.d("MEETING ALARM ->", "END SUGGESTION in 120 Min!");


                rememberSuggestionEnd_120min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText120 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextSuggestionEndIn120Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_120min.getLong(rememberSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_120min.getLong(rememberSuggestionEnd_120min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
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

                Log.d("MEETING ALARM ->", "END SUGGESTION in 1440 Min!");


                rememberSuggestionEnd_1440min.moveToFirst();
                do {
                    String tmpSuggestionEndNotificationText1440 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextSuggestionEndIn1440Min), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_1440min.getLong(rememberSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberSuggestionEnd_1440min.getLong(rememberSuggestionEnd_1440min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME)), "HH:mm"));
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

                Log.d("Suggestion ALARM ->", "Next WakeUp Point");

                rememberSuggestion_nextWakeUp.moveToFirst();
                firstWakeUpSuggestion = rememberSuggestion_nextWakeUp.getLong(rememberSuggestion_nextWakeUp.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_RESPONSE_TIME));

            } 
            else { // no wake up point in suggestion -> set wake up point in far future!
                firstWakeUpSuggestion = noWakeUpPointSetValue; 
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









            // check client suggestion start time in 15 minutes
            Cursor rememberClientSuggestionStart_15min = myDb.getAllRowsRememberMeetingsAndSuggestion("remember_client_suggestion_start_15min", nowTime);
            if (rememberClientSuggestionStart_15min != null && rememberClientSuggestionStart_15min.getCount() > 0) {

                Log.d("MEETING ALARM ->", "Start Client SUGGESTION in 15 Min!");


                rememberClientSuggestionStart_15min.moveToFirst();
                do {
                    String tmpClientSuggestionStartNotificationText15 = String.format(mContext.getResources().getString(R.string.alarmReceiverMotificationSubTextClientSuggestionStartIn15Min), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_STARTDATE)), "HH:mm"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "dd.MM.yyyy"), EfbHelperClass.timestampToDateFormat(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.MEETING_SUGGESTION_KEY_MEETING_CLIENT_SUGGESTION_ENDDATE)), "HH:mm"));
                    clientSuggestionStartTextIn15Min += tmpClientSuggestionStartNotificationText15 + lineFeed;
                    lineFeed = "\n";
                    // update suggestion remember status
                    myDb.updateStatusRememberMeetingAndSuggestion(rememberClientSuggestionStart_15min.getLong(rememberClientSuggestionStart_15min.getColumnIndex(DBAdapter.KEY_ROWID)), 15); // 0 = no remember so far; 5 = remember 24 h; 10 = remember 2 hours; 15 = remember 15 minutes
                } while (rememberClientSuggestionStart_15min.moveToNext());
                // reset line feed
                lineFeed = "";
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
        }
        
        
        
        
        
        // next wake up point for alarm receiver
        //if ()
        // firstWakeUpMeeting
            // firstWakeUpSuggestion
        Log.d("Next Wake UP->", "Meeting:"+firstWakeUpMeeting);
        Log.d("Next Wake UP->", "Suggestion:"+firstWakeUpSuggestion);
        
        
        
        
        
        
        
        

        // close db connection
        myDb.close();


    }
    
    
    
    void notificationMeetingIn15Minutes (String meetingTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get meeting remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextRemember15Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextRemember120Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextRemember1440Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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






    

    void notificationCoachSuggestionEndIn15Minutes (String suggestionEndTextIn15Min, Intent mainActivityIntent, NotificationCompat.Builder mBuilder, Uri alarmSound, NotificationManager mNotificationManager) {
        // get suggestion end remember notification string
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextSuggestionEndIn15Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextSuggestionEndIn120Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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
        String notificationContentTitle = mContext.getResources().getString(R.string.alarmReceiverMotificationHeadlineTextSuggestionEndIn1440Min);

        // set intent/ pending intent to start meeting
        Intent notificationIntent = new Intent(mContext, ActivityMeeting.class);
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
    
    
    
    
    
    
    
    
    
    
    
    
}

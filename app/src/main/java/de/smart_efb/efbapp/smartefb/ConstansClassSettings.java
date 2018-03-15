package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 10.02.2017.
 */
class ConstansClassSettings {

    // prefs name for connecting status
    static final String namePrefsConnectingStatus = "connectingStatus";

    // prefs name for random number for connection to server
    static final String namePrefsRandomNumberForConnection = "randomNumberForConnection";

    // prefs name for client id
    static final String namePrefsClientId = "clientId";

    // prefs name for contact id (is used when no client id is not set -> user is anonym)
    static final String namePrefsContactId = "contactId";

    // prefs name for client name
    static final String namePrefsClientName = "clientName";

    // prefs name for last error messages
    static final String namePrefsLastErrorMessages = "lastError";

    // prefs name for case close
    static final String namePrefsCaseClose = "caseClose";

    // connection timeout for connection that is established in millisec
    static final int connectionEstablishedTimeOut = 15000;

    // connection timeout for connection that is read in millisec
    static final int connectionReadTimeOut = 15000;

    // minimum and maximum for random number to connect to server
    static final int randomNumberForConnectionMin = 10000;
    static final int randomNumberForConnectionMax = 99999;


    // URL ask server for time
    static final String urlConnectionSendAskForTimeToServer = "http://192.168.178.28/index.php?com=exchange&subcom=asktime";

    // URL first connect to server
    static final String urlFirstConnectToServer = "http://192.168.178.28/index.php?com=exchange&subcom=establish";

    // URL connection established ok
    static final String urlConnectionEstablishedToServer = "http://192.168.178.28/index.php?com=exchange&subcom=establishedok";

    // URL connection ask for new data
    static final String urlConnectionAskForNewDataToServer = "http://192.168.178.28/index.php?com=exchange&subcom=asknewdata";

    // URL connection send comment now arrangement to server
    static final String urlConnectionSendNewCommentArrangementToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendarrangementcomment";

    // URL connection send comment sketch arrangement to server
    static final String urlConnectionSendNewSketchCommentArrangementToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendsketcharrangementcomment";

    // URL connection send evaluation result arrangement to server
    static final String urlConnectionSendEvaluationResultArrangementToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendevaluationresultarrangement";

    // URL connection send comment jointly goals to server
    static final String urlConnectionSendNewCommentJointlyGoalsToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendjointlygoalscomment";

    // URL connection send evaluation result jointly goals to server
    static final String urlConnectionSendEvaluationResultJointlyGoalsToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendevaluationresultgoals";

    // URL connection send comment debetable goals to server
    static final String urlConnectionSendNewCommentDebetableGoalsToServer = "http://192.168.178.28/index.php?com=exchange&subcom=senddebetablegoalscomment";

    // URL connection send connect book message to server
    static final String urlConnectionSendConnectBookMessageToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendconnectbookmessage";

    // URL connection send meeting data to server
    static final String urlConnectionSendMeetingDataToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendmeetingdata";

    // URL connection send message data to server (associated and not associated)
    static final String urlConnectionSendMessageToServer = "http://192.168.178.28/index.php?com=exchange&subcom=sendmessage";





    // Setting for Visuals and visual notification
    static final String namePrefsNotificationVisualSignal_ConnectBook = "notificationVisualSignal_ConnectBook";
    static final String namePrefsNotificationVisualSignal_OurArrangement = "notificationVisualSignal_OurArrangment";
    static final String namePrefsNotificationVisualSignal_OurArrangementEvaluation = "notificationVisualSignal_OurArrangmentEvaluation";
    static final String namePrefsNotificationVisualSignal_OurGoal = "notificationVisualSignal_OurGoal";
    static final String namePrefsNotificationVisualSignal_OurGoalEvaluation = "notificationVisualSignal_OurGoalEvaluation";
    static final String namePrefsNotificationVisualSignal_Message = "notificationVisualSignal_Message";
    static final String namePrefsNotificationVisualSignal_RememberMeeting = "notificationVisualSignal_RememberMeeting";
    static final String namePrefsNotificationVisualSignal_RememberSuggestion = "notificationVisualSignal_RememberSuggestion";

    
    // Setting for acoustics notification
    static final String namePrefsNotificationAcousticSignal_ConnectBook = "notificationAcousticSignal_ConnectBook";
    static final String namePrefsNotificationAcousticSignal_OurArrangement = "notificationAcousticSignal_OurArrangment";
    static final String namePrefsNotificationAcousticSignal_OurArrangementEvaluation = "notificationAcousticSignal_OurArrangmentEvaluation";
    static final String namePrefsNotificationAcousticSignal_OurGoal = "notificationAcousticSignal_OurGoal";
    static final String namePrefsNotificationAcousticSignal_OurGoalEvaluation = "notificationAcousticSignal_OurGoalEvaluation";
    static final String namePrefsNotificationAcousticSignal_Message = "notificationAcousticSignal_Message";
    static final String namePrefsNotificationAcousticSignal_RememberMeeting = "notificationAcousticSignal_RememberMeeting";
    static final String namePrefsNotificationAcousticSignal_RememberSuggestion = "notificationAcousticSignal_RememberSuggestion";











}

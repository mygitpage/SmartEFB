package de.smart_efb.efbapp.smartefb;

/**
 * Created by ich on 10.02.2017.
 */
public class ConstansClassSettings {

    // prefs name for connecting status
    public static final String namePrefsConnectingStatus = "connectingStatus";

    // prefs name for random number for connection to server
    public static final String namePrefsRandomNumberForConnection = "randomNumberForConnection";


    // prefs name for client id
    public static final String namePrefsClientId = "clientId";

    // prefs name for last error messages
    public static final String namePrefsLastErrorMessages = "lastError";


    // connection timeout for connection that is established in millisec
    public static final int connectionEstablishedTimeOut = 15000;

    // connection timeout for connection that is read in millisec
    public static final int connectionReadTimeOut = 15000;




    // URL first connect to server
    public static final String urlFirstConnectToServer = "http://192.168.0.126/index.php?com=exchange&subcom=establish";

    // URL connection established ok
    public static final String urlConnectionEstablishedToServer = "http://192.168.0.126/index.php?com=exchange&subcom=establishedok";

    // URL connection ask for new data
    public static final String urlConnectionAskForNewDataToServer = "http://192.168.0.126/index.php?com=exchange&subcom=asknewdata";

    // URL connection send comment now arrangement to server
    public static final String urlConnectionSendNewCommentArrangementToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendarrangementcomment";

    // URL connection send comment sketch arrangement to server
    public static final String urlConnectionSendNewSketchCommentArrangementToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendsketcharrangementcomment";

    // URL connection send evaluation result arrangement to server
    public static final String urlConnectionSendEvaluationResultArrangementToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendevaluationresultarrangement";

    // URL connection send comment jointly goals to server
    public static final String urlConnectionSendNewCommentJointlyGoalsToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendjointlygoalscomment";

    // URL connection send evaluation result jointly goals to server
    public static final String urlConnectionSendEvaluationResultJointlyGoalsToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendevaluationresultgoals";

    // URL connection send comment debetable goals to server
    public static final String urlConnectionSendNewCommentDebetableGoalsToServer = "http://192.168.0.126/index.php?com=exchange&subcom=senddebetablegoalscomment";

    // URL connection send connect book message to server
    public static final String urlConnectionSendConnectBookMessageToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendconnectbookmessage";

    // URL connection send meeting data to server
    public static final String urlConnectionSendMeetingDataToServer = "http://192.168.0.126/index.php?com=exchange&subcom=sendmeetingdata";





}

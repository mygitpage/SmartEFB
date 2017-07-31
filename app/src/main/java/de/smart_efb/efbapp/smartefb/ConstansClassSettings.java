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

    // prefs name for case id
    public static final String namePrefsCaseId = "caseId";

    // prefs name for last error messages
    public static final String namePrefsLastErrorMessages = "lastError";


    // connection timeout for connection that is established in millisec
    public static final int connectionEstablishedTimeOut = 15000;

    // connection timeout for connection that is read in millisec
    public static final int connectionReadTimeOut = 15000;




    // URL first connect to server
    public static final String urlFirstConnectToServer = "http://192.168.1.119/index.php?com=exchange&subcom=establish";
    //public static final String urlFirstConnectToServer = "http://192.168.178.28/index.php?com=exchange&subcom=establish";

    // URL connection established ok
    public static final String urlConnectionEstablishedToServer = "http://192.168.1.119/index.php?com=exchange&subcom=establishedok";
    //public static final String urlConnectionEstablishedToServer = "http://192.168.178.28/index.php?com=exchange&subcom=establishedok";




}
